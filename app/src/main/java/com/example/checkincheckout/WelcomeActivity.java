package com.example.checkincheckout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    ImageView Image;
    Button start;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        Image = findViewById(R.id.Image);
        start = findViewById(R.id.Start);
        start.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashBoardActivity.class);
            startActivity(intent);
        });

    }

}