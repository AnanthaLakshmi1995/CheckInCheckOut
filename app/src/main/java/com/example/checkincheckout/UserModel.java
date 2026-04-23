package com.example.checkincheckout;

public class UserModel
{
    int id;
    String name, email,phone,password;

    public UserModel(int id, String name, String email, String phone,String pass)
    {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone=phone;
        this.password=pass;
    }

    public int getId()
    {
        return id;
    }

    public String getUsername()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }
    public String getPhone()
    {
        return phone;
    }

    public String getPassword()
    {
        return password;
    }

}
