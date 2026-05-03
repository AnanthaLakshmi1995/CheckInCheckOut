
package com.example.checkincheckout;

import android.graphics.Bitmap;

public class UserModel {

    int id;
    String name, email, phone, password;
    Bitmap face;

    public UserModel(int id, String name, String email, String phone, String password, Bitmap face) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.face = face;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public Bitmap getFace() {
        return face;
    }
}