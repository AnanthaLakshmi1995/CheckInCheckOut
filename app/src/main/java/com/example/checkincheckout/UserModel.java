package com.example.checkincheckout;

public class UserModel {
    int id;
    String name, email;

    public UserModel(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public String getEmail() { return email; }

}
