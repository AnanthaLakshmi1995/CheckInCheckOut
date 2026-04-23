package com.example.checkincheckout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminLoginActivity extends AppCompatActivity {
    EditText adminName, adminPassword;
    Button loginBtn;

    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        adminName = findViewById(R.id.Username);
        adminPassword = findViewById(R.id.Password);
        loginBtn = findViewById(R.id.Login);

        db = DataBase.getInstance(this);

        loginBtn.setOnClickListener(v ->
        {
            String name = adminName.getText().toString().trim();
            String pass = adminPassword.getText().toString().trim();

            if (db.loginAdmin(name, pass))
            {
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this,AdminDashBoardActivity.class);
                intent.putExtra("username", name);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
