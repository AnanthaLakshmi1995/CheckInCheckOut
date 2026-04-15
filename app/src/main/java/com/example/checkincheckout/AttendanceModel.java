package com.example.checkincheckout;

public class AttendanceModel {
    int id;
    String name, checkIn, checkOut;

    public AttendanceModel(int id, String name, String checkIn, String checkOut) {
        this.id = id;
        this.name = name;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public String getName() { return name; }
    public String getCheckIn() { return checkIn; }
    public String getCheckOut() { return checkOut; }
}

