package com.example.checkincheckout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DashBoardActivity extends AppCompatActivity {
    Button user,login;
    EditText username,password;
    //Button checkin;
    DataBase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dash_board);
        username = findViewById(R.id.Username);
        password = findViewById(R.id.Password);
        login = findViewById(R.id.Login);
        user = findViewById(R.id.User);
        //TextView title = findViewById(R.id.appTitle);
       // title.setText("Admin Dashboard");
        db = new DataBase(this);

        String userName = getSharedPreferences("MyApp", MODE_PRIVATE)
                .getString("username", null);
        login.setOnClickListener(v -> {

            String name = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (name.equals("admin") && pass.equals("12345")) {
                Toast.makeText(this, "Admin Login Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AdminDashBoardActivity.class));
                finish();
            } else if (db.loginUser(name, pass)) {
                Toast.makeText(this, "User Login Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, UserCheckinActivity.class);
                intent.putExtra("username", name);
                 startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });
        user.setOnClickListener(v -> {
            Intent intent = new Intent(DashBoardActivity.this, UserRegisterActivity.class);
            startActivity(intent);
        });

    }
}
