package com.example.checkincheckout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import java.text.BreakIterator;

public class UserLoginActivity extends AppCompatActivity {

    EditText userName, userPassword;
    Button loginBtn;

    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        userName = findViewById(R.id.Username);
        userPassword = findViewById(R.id.Password);
       loginBtn = findViewById(R.id.Login);
       db = DataBase.getInstance(this);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                new androidx.appcompat.app.AlertDialog.Builder(UserLoginActivity.this)
                        .setTitle("Exit App")
                        .setMessage("Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, which) -> finish())
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
        loginBtn.setOnClickListener(v ->
        {
           String name = userName.getText().toString().trim();
            String pass = userPassword.getText().toString().trim();

            if (db.loginUser(name, pass))
            {
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                //SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                prefs.edit().putString("username", name).apply();
                //SharedPreferences prefs = context.getSharedPreferences("user", Context.MODE_PRIVATE);
               // prefs.edit().putString("username", actualUsername).apply();
                //ReminderReceiver.scheduleReminders(context);
             // startActivity(new Intent(this, UserCheckinActivity.class));
                Intent intent = new Intent(this, UserCheckinActivity.class);
               intent.putExtra("username", name);
               startActivity(intent);
               finish();
          }
            else
            {
              Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
          }
       });
   }
}