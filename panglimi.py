#-*-coding: utf-8-*-

import pymysql
import spidev
import RPi.GPIO as gpio
import time
from gpiozero import PWMOutputDevice
from time import sleep
from subprocess import call
from datetime import datetime
import serial
import string
import pynmea2
import datetime
import socket
import time
import threading

btn_pin = 3
shutdown_sec = 1
led = 18

gpio.setmode(gpio.BCM)
gpio.setup(btn_pin, gpio.IN)
gpio.setup(led, gpio.OUT)
gpio.setwarnings(False)

#led sensor
delay = 0.5

ldr_channel = 0

spi = spidev.SpiDev()

spi.open(0,0)

spi.max_speed_hz = 100000

stickNum = "192.168.137.43"


#vibe module
motor = PWMOutputDevice(14, active_high=True, frequency=100)
motor.off()

#ultra sensor

trig_arr = [19,5,16]
#trig_arr = [19]
echo_arr = [26,6,20]
#echo_arr = [26]
result_arr = [0.0,0.0,0.0]
#result_arr = [0.0]
#connect db
connect = pymysql.connect(host="panglimi.c7ncomcsu95f.ap-northeast-2.rds.amazonaws.com",
                          port=3306, user="root", password="nsk5231^^", db="panglimi")
cursor = connect.cursor()

#socket
t = []
index = 0
s_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
ufsize = 1024
host = ''
port = 9999
s_socket.bind((host, port))
s_socket.listen(1)

class Cserver(threading.Thread):
    def __init__(self, socket):
        super(Cserver, self).__init__()
        self.s_socket = socket

    def run(self):
        global index
	global t
        self.c_socket, addr = self.s_socket.accept()
        print(addr[0], addr[1], "이 연결되었습니다")
        index = index + 1
        creat_thread(self.s_socket)
        recv = threading.Thread(target=self.c_recv)
        recv.start()

    def c_recv(self):
        while True:
            try:
                get_data = self.c_socket.recv(1024)
                print(get_data.decode('utf-8'))
                if len(get_data.decode('utf-8')) == 0:
			break
            except Exception as e:
                pass


    def c_send(self, put_data):
        try:
		self.c_socket.send(repr(put_data).encode("utf-8"))
        except Exception as e:
		pass


def creat_thread(s_socket):
    global index
    t.append(Cserver(s_socket))
    t[index].start()

creat_thread(s_socket)

#led sensor setting
def readadc(adcnum):
    if adcnum > 7 or adcnum < 0:
        return -1
    r = spi.xfer2([1,8+adcnum << 4,0])
    data = ((r[1]&3) << 8) +r[2]
    return data

#shut down and up
press_time = None

def button_state_changed(pin):
    global press_time
    if gpio.input(pin) == 0:
        if press_time is None:
            print("btn down")
            press_time = datetime.datetime.now()
    else:
        if press_time is not None:
		print("btn up")
		elapsed = (datetime.datetime.now() - press_time).total_seconds()
		press_time = None
        if elapsed >= shutdown_sec:
		t[index-1].c_send("exit")
		t[index-1].c_socket.close()
		t[index-1].s_socket.close()
		print("shut down")
		call(["shutdown","-h","now"], shell=False)


#subscribe to button presses
gpio.add_event_detect(btn_pin, gpio.BOTH, callback=button_state_changed)

#ultra sensor and vibe module setting
def setup():
	for i in range(0,3):
		gpio.setup(trig_arr[i], gpio.OUT)
		gpio.setup(echo_arr[i], gpio.IN)
		gpio.output(trig_arr[i], False)

def check_distance(index):
	gpio.output(trig_arr[index],True)
	time.sleep(0.00001)
	gpio.output(trig_arr[index],False)

	stop = 0
	start = 0

	while gpio.input(echo_arr[index])==0:

		start = time.time()

	while gpio.input(echo_arr[index])==1:

		stop = time.time()	

	check_time = stop - start
	distance = check_time * 17000
	time.sleep(0.0001)
	return distance

def main_run():
	global connect
	global s_socket
	global t
	try:
		setup()
		while True:
			ldr_value = readadc(ldr_channel)
			print("----------------------------------------")
			print("LDR Value : %d" % ldr_value)
			time.sleep(delay)
			if ldr_value < 800:
				gpio.output(led,gpio.HIGH)          
			else:
				gpio.output(led,gpio.LOW)
	
			#GPS
			ser = serial.Serial("/dev/ttyS0",baudrate=9600,timeout=3.0)
			dataout = pynmea2.NMEAStreamReader()
			data = ser.readline()
	
			if data[0:6] == "$GPRMC":
				newmsg=pynmea2.parse(data)
				lat=newmsg.latitude
				lng=newmsg.longitude
		
				gps =str(lat) + "," + str(lng) 
				
				print(gps + "\n")
				for i in t:
					i.c_send("cur,"+gps)  
       
 
			for i in range(0,3):
				result_arr[i] = check_distance(i)
				print("distance: %.1f cm" % result_arr[i])
				time.sleep(0.001)

				if result_arr[i] > 30 and result_arr[i] < 70 :
					speed = 0.3
					motor.value = speed
					time.sleep(0.3)
				elif result_arr[i] > 10 and result_arr[i] <= 30 :
					speed = 0.5
					motor.value = speed
					time.sleep(0.3)
				elif result_arr[i] > 5 and result_arr[i] <= 10 :
					speed = 0.7
					motor.value = speed
					time.sleep(0.3)
				elif result_arr[i] <= 5  :
					speed = 0.9
					motor.value = speed
					select_sql = "SELECT * from location WHERE lc_longtitude="+str(lng) +" AND lc_latitude="+str(lat)+" AND lc_stickNum="+stickNum
					cursor.execute(select_sql)
					result = cursor.fetchall() 
					
					if (33<=lat and lat<=43) and (124<=lng and lat<=132):
						if cursor.rowcount <= 0:
							cursor.execute("INSERT INTO location (lc_longtitude,lc_latitude,lc_stickNum) VALUES ("+str(lng)+", "+str(lat)+", "+stickNum+")") 
						else:
							cursor.execute("DELETE FROM location WHERE lc_longtitude="+str(lng)+" AND lc_latitude="+str(lat)+" AND lc_stickNum="+stickNum)
							cursor.execute("INSERT INTO location (lc_longtitude,lc_latitude,lc_stickNum) VALUES ("+str(lng)+", "+str(lat)+", "+stickNum+")") 

						connect.commit() 
					
					for i in t:
						i.c_send("crush")
					print "{:%Y-%m-%d %H:%M:%S}".format(datetime.datetime.now())
					time.sleep(0.3)
				elif result_arr[i] >= 100 :
					speed = 0
					motor.value = speed
					time.sleep(0.3)

	finally:
		gpio.cleanup()
		connect.close()
		s_socket.close()
		exit(0)
		
main_run = threading.Thread(target=main_run)
main_run.daemon = True
main_run.start()

while True:
    sleep(1)
