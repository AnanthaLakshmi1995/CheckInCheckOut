package com.example.checkincheckout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DashBoardActivity extends AppCompatActivity {
    Button admin, user;
    Button checkincheckout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dash_board);
        admin = findViewById(R.id.Admin);
        user = findViewById(R.id.User);
        checkincheckout = findViewById(R.id.CheckinCheckout);
      //  String userName = getIntent().getStringExtra("username");
        String userName = getSharedPreferences("MyApp", MODE_PRIVATE)
                .getString("username", null);
        admin.setOnClickListener(v -> {
            Intent intent = new Intent(DashBoardActivity.this, AdminRegisterActivity.class);
            startActivity(intent);
        });
        user.setOnClickListener(v -> {
            Intent intent = new Intent(DashBoardActivity.this, UserRegisterActivity.class);
            startActivity(intent);
        });
        checkincheckout.setOnClickListener(v -> {

           if(userName == null) {
            Toast.makeText(this, "Username missing", Toast.LENGTH_SHORT).show();
             return;}
            Intent intent = new Intent(DashBoardActivity.this, UserCheckinActivity.class);
            intent.putExtra("username", userName);
            startActivity(intent);
        });

    }
}
