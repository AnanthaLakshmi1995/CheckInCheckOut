package com.example.checkincheckout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UserLoginActivity extends AppCompatActivity {

    EditText userName, userPassword;
    Button loginBtn;

    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

       userName = findViewById(R.id.UserName);
        userPassword = findViewById(R.id.Password);
       loginBtn = findViewById(R.id.Login);

       db = DataBase.getInstance(this);

        loginBtn.setOnClickListener(v -> {
           String name = userName.getText().toString().trim();
            String pass = userPassword.getText().toString().trim();

            if (db.loginUser(name, pass)) {
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
              startActivity(new Intent(this, AdminDashBoardActivity.class));
          } else {
              Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
          }
       });
   }
}