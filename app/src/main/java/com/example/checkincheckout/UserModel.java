package com.example.checkincheckout;

public class UserModel {
    int id;
    String name, email,phone;

    public UserModel(int id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone=phone;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public String getEmail() { return email; }
    public String getPhone()
    {
        return phone;
    }

}
