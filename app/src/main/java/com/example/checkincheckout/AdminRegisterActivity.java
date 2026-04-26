package com.example.checkincheckout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class  AdminRegisterActivity extends AppCompatActivity {

    EditText AdminName, Password,Adminemail;
    Button register;
    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);
        AdminName = findViewById(R.id.AdminName);
        Password = findViewById(R.id.AdminPassword);
        Adminemail=findViewById(R.id.AdminEmailid);
        register = findViewById(R.id.Register);
        db = new DataBase(this);
        register.setOnClickListener(v -> {

            String name = AdminName.getText().toString().trim();
            String password = Password.getText().toString().trim();
           String email= Adminemail.getText().toString().trim();
            if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
                boolean result = db.insertAdmin(name,email ,password);

                if (result) {
                    Toast.makeText(this, "Admin Registered Successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, AdminLoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Already Registered", Toast.LENGTH_SHORT).show();
                }

        });

    }
}