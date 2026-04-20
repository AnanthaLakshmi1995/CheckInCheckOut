package com.example.checkincheckout;

public class AttendanceModel {
    int id;
    String name, checkIn, checkOut,status;

    public AttendanceModel(int id, String name, String checkIn, String checkOut) {
        this.id = id;
        this.name = name;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public String getUsername() { return name; }
    public String getCheckIn() { return checkIn; }
    public String getCheckOut() { return checkOut; }
    public String getStatus()
    {
        return status;
    }
}

