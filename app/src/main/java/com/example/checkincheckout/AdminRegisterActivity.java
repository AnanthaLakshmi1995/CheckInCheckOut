package com.example.checkincheckout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class  AdminRegisterActivity extends AppCompatActivity {

    EditText Username, Password;
    Button register;
    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);
        Username = findViewById(R.id.Username);
        Password = findViewById(R.id.Password);
        register = findViewById(R.id.Register);
        db = new DataBase(this);
        register.setOnClickListener(v -> {
            String uname = Username.getText().toString().trim();
            String pass = Password.getText().toString().trim();

            if (uname.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (uname.equals("admin") && pass.equals("12345")) {

                Toast.makeText(this, "Admin Registered Successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, AdminDashBoardActivity.class);
                startActivity(intent);
                finish();
            } else
            {
                Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }
}