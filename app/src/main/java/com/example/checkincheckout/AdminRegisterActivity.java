package com.example.checkincheckout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class  AdminRegisterActivity extends AppCompatActivity {

    EditText Username, Password,email;
    Button register;
    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);
        Username = findViewById(R.id.Username);
        Password = findViewById(R.id.Password);
        email=findViewById(R.id.Emailid);
        register = findViewById(R.id.Register);
        db = new DataBase(this);
        register.setOnClickListener(v -> {

            String uname = Username.getText().toString().trim();
            String pass = Password.getText().toString().trim();
           String mail= email.getText().toString().trim();
            if (uname.isEmpty() || pass.isEmpty() || mail.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }



                boolean result = db.registerAdmin(uname,mail ,pass);

                if (result) {
                    Toast.makeText(this, "Admin Registered Successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, AdminLoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Already Registered or Error", Toast.LENGTH_SHORT).show();
                }

        });

    }
}