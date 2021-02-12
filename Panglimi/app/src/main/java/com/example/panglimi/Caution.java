package com.example.panglimi;

public class Caution {
    String address;
    String date;

    public Caution( String address, String date){
        this.address = address;
        this.date = date;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
