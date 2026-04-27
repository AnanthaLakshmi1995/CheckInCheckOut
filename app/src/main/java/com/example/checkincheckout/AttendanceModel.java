package com.example.checkincheckout;

public class AttendanceModel {
    int id;
    String name, checkIn, checkOut,date,working_hours;

    public AttendanceModel(int id, String name,  String date,String checkIn, String checkOut,String working_hours) {
        this.id = id;
        this.name = name;
        this.date=date;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.working_hours=working_hours;
    }

    public String getUsername() { return name; }
    public String getDate()
    {
        return date;
    }
    public String getCheckIn() { return checkIn; }
    public String getCheckOut() { return checkOut; }

    public String getWorkingHours()
    {
        return working_hours;
    }
}

