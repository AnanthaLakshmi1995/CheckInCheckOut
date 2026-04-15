package com.example.checkincheckout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminDashBoardActivity extends AppCompatActivity {
    Button viewUsers, viewAttendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dash_board);
        viewUsers = findViewById(R.id.ViewUsers);
        viewAttendance = findViewById(R.id.ViewAttendance);

        viewUsers.setOnClickListener(v -> {
            startActivity(new Intent(this, ViewUsersActivity.class));
        });

        viewAttendance.setOnClickListener(v -> {

            startActivity(new Intent(this, ViewAttendanceActivity.class));
        });
    }

}