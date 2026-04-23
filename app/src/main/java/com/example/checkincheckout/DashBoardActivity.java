package com.example.checkincheckout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DashBoardActivity extends AppCompatActivity {
    Button admin, user,login;
    EditText username,password;
    //Button checkin;
    DataBase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dash_board);
        username=findViewById(R.id.Username);
        password=findViewById(R.id.Password);
        login=findViewById(R.id.Login);
        admin = findViewById(R.id.Admin);
        user = findViewById(R.id.User);
       // checkin = findViewById(R.id.Checkin);
       // checkout=findViewById(R.id.Checkout);

        db = new DataBase(this);
      //  String userName = getIntent().getStringExtra("username");
        String userName = getSharedPreferences("MyApp", MODE_PRIVATE)
                .getString("username", null);
        login.setOnClickListener(v -> {

            String name = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (name.equals("admin") && pass.equals("12345")) {
                Toast.makeText(this, "Admin Login Successfully", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(this, AdminDashBoardActivity.class));
            }
            else if (db.loginUser(name, pass)) {
                Toast.makeText(this, "User Login Successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, UserCheckinActivity.class);
                intent.putExtra("username", name);
                startActivity(intent);
            }
            else {
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });

        admin.setOnClickListener(v -> {
            Intent intent = new Intent(DashBoardActivity.this, AdminRegisterActivity.class);
            startActivity(intent);
        });
        user.setOnClickListener(v -> {
            Intent intent = new Intent(DashBoardActivity.this, UserRegisterActivity.class);
            startActivity(intent);
        });
        //checkin.setOnClickListener(v -> {
         //   if(userName == null) {
          //  Toast.makeText(this, "Username missing", Toast.LENGTH_SHORT).show();
             return;}
          //  Intent intent = new Intent(DashBoardActivity.this, UserCheckinActivity.class);
          //  intent.putExtra("username", userName);
           // startActivity(intent);});}
}
