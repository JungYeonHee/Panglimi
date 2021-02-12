package com.example.panglimi;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.SupportMapFragment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

public class MyService extends Service {
    NotificationManager Notifi_M;
    private static final String CHANNEL_ID = "channel_id" ;
    NotificationCompat.Builder mBuilder;
    int cnt;
    NotificationManager notificationManager;
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    static public String ip = "";//"220.69.172.223";            // IP 번호
    private int port = 9999;
    private Handler mHandler;
    static double userLatitude = 0;
    static double userLongtitude = 0;
    boolean inside = true;
    String[] curUser = new String[7];
    String[] curUserk = new String[12];

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        connect();
        return START_STICKY;
    }

    //서비스가 종료될 때 할 작업
    public void onDestroy() {
        super.onDestroy();
    }

    void connect(){
        if(Login_Page.input_id.length()==0)
            ip = Kakaosticknum_Page.kakaosticknum;            //카카오로그인 대상자
        else
            ip = Sticknum_Page.stickNum;                       //일반로그인 대상자
        MainActivity.userStickNum = ip;
        mHandler = new Handler();
        Thread checkUpdate = new Thread() {
            public void run() {
                if (Login_Page.input_id.length() == 0) {     //카카오로그인일때
                    try {
                        final String[] result3 = {""};
                        String urlstr = "http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/sticknum_select.php?user_name=" + Login_Page.nickName; //서버로 보낼 url
                        result3[0] = new GetJSONObject().execute(urlstr).get(); //서버에서 json 파일 읽어오기
                        curUserk = GetJSONObject.jsonParser_address2(result3[0]); //json 파일 파싱하기
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(curUserk != null){
                        if(curUserk[0] != null && curUserk[1] != null){
                            if(curUserk[0].length() != 0 && curUserk[1].length() != 0){
                                if(!curUserk[0].equals("null") && !curUserk[1].equals("null")) {
                                    userLatitude = Double.parseDouble(curUserk[0]);
                                    userLongtitude = Double.parseDouble(curUserk[1]);
                                }
                            }
                        }
                    }
                } else {
                    final String[] result2 = {""};
                    try {
                        String urlsel = "http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/user_select_final.php?user_id=" + Login_Page.input_id; //서버로 보낼 url
                        result2[0] = new GetJSONObject().execute(urlsel).get(); //서버에서 json 파일 읽어오기
                        curUser = GetJSONObject.jsonParser_address(result2[0]); //json 파일 파싱하기
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(curUser != null){
                        if(curUser[0] != null && curUser[1] != null){
                            if(curUser[0].length() != 0 && curUser[1].length() != 0){
                                if(!curUser[0].equals("null") && !curUser[1].equals("null")) {
                                    userLatitude = Double.parseDouble(curUser[0]);
                                    userLongtitude = Double.parseDouble(curUser[1]);
                                }
                            }
                        }
                    }
                }

                try {
                    socket = new Socket(ip, port);
                } catch (IOException e1) {
                    Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.caution);
                    createNotificationChannel();
                    mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID).setSmallIcon(R.drawable.caution)
                            .setContentTitle("알림").setContentText("지팡이 전원을 킨 후 다시 시도해주세요")
                            .setDefaults(android.app.Notification.DEFAULT_VIBRATE).setLargeIcon(icon)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true);
                    notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(2, mBuilder.build());
                }
                if(socket != null){
                    try {
                        dos = new DataOutputStream(socket.getOutputStream());
                        dis = new DataInputStream(socket.getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    while(true) {
                        try {
                            String line = "";
                            while (true) {
                                byte[] receiver = new byte[35];
                                dis.read(receiver);
                                line = new String(receiver);
                                StringTokenizer tokenizer2 = new StringTokenizer(line, "',");
                                String state;
                                double latitude=0, longtitude=0;
                                if(tokenizer2.countTokens() == 4){
                                    state = tokenizer2.nextToken();
                                    latitude = Double.parseDouble(tokenizer2.nextToken());
                                    longtitude = Double.parseDouble(tokenizer2.nextToken());    }
                                else{
                                    state = tokenizer2.nextToken();
                                }

                                if(line.length()> 0) {
                                    dos.flush();
                                    if(state.equals("cur")){
                                        if(userLongtitude == 0 && userLatitude == 0){
                                            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.caution);
                                            PendingIntent addressIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), Address_Page.class), PendingIntent.FLAG_UPDATE_CURRENT);
                                            createNotificationChannel();
                                            mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID).setSmallIcon(R.drawable.caution)
                                                    .setContentTitle("알림").setContentText("주소를 설정해주셔야 알림을 받을 수 있습니다")
                                                    .setDefaults(android.app.Notification.DEFAULT_VIBRATE).setLargeIcon(icon)
                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true)
                                                    .setContentIntent(addressIntent);
                                            notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                                            if(MainActivity.alarmState.equals("on"))
                                                notificationManager.notify(1, mBuilder.build());
                                        }
                                        else if(userLatitude == latitude && userLongtitude == longtitude && inside == false){
                                            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.caution);
                                            createNotificationChannel();
                                            mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID).setSmallIcon(R.drawable.caution)
                                                    .setContentTitle("알림").setContentText("집에 도착했습니다")
                                                    .setDefaults(android.app.Notification.DEFAULT_VIBRATE).setLargeIcon(icon)
                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true);
                                            notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                                            if(MainActivity.alarmState.equals("on"))
                                                notificationManager.notify(1, mBuilder.build());
                                            inside = true;
                                        }
                                        else if(userLongtitude != latitude && userLatitude != latitude && inside == true){
                                            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.caution);
                                            createNotificationChannel();
                                            mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID).setSmallIcon(R.drawable.caution)
                                                    .setContentTitle("알림").setContentText("집에서 출발했습니다")
                                                    .setDefaults(android.app.Notification.DEFAULT_VIBRATE).setLargeIcon(icon)
                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true);
                                            notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                                            if(MainActivity.alarmState.equals("on"))
                                                notificationManager.notify(1, mBuilder.build());
                                            inside = false;
                                        }
                                    }
                                    else if(state.equals("crush")){
                                        System.out.println("새로운 위치 받아옴");
                                        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.caution);
                                        PendingIntent cautionIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                                        createNotificationChannel();
                                        mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID).setSmallIcon(R.drawable.caution)
                                                .setContentTitle("알림").setContentText("새로운 접촉 발생! 확인해보세요")
                                                .setDefaults(android.app.Notification.DEFAULT_VIBRATE).setLargeIcon(icon)
                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true)
                                                .setContentIntent(cautionIntent);
                                        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                                        if(MainActivity.alarmState.equals("on"))
                                            notificationManager.notify(0, mBuilder.build());
                                    }
                                }
                                if(line.equals("exit")) {
                                    socket.close();
                                    break;
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
        };

        checkUpdate.start();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}