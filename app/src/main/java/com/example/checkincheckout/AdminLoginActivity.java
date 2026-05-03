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
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_admin_login);

            adminName = findViewById(R.id.Username);
            adminPassword = findViewById(R.id.Password);
            loginBtn = findViewById(R.id.Login);

            db = DataBase.getInstance(this);
           // db.insertAdmin("admin", "12345");

            loginBtn.setOnClickListener(v -> {

                String name = adminName.getText().toString().trim();
                String pass = adminPassword.getText().toString().trim();

                // ✅ Correct method
                if (db.loginAdmin(name, pass)) {

                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, AdminDashBoardActivity.class);
                    intent.putExtra("username", name);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

