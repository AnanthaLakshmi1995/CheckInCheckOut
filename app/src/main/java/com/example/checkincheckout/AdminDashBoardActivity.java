package com.example.checkincheckout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminDashBoardActivity extends AppCompatActivity {
    Button viewUsers, viewAttendance, logout, reports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dash_board);
        viewUsers = findViewById(R.id.ViewUsers);
        viewAttendance = findViewById(R.id.ViewAttendance);
        logout = findViewById(R.id.Logout);
        reports = findViewById(R.id.Report);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                new androidx.appcompat.app.AlertDialog.Builder(AdminDashBoardActivity.this)
                        .setTitle("Exit App")
                        .setMessage("Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, which) -> finish())
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
        viewUsers.setOnClickListener(v -> {
            startActivity(new Intent(this, ViewUsersActivity.class));
        });

        viewAttendance.setOnClickListener(v -> {

            startActivity(new Intent(this, ViewAttendanceActivity.class));
        });
        reports.setOnClickListener(v ->
        {
            startActivity(new Intent(this, ViewAttendanceActivity.class));

        });
        logout.setOnClickListener(v ->
        {
            Toast.makeText(this, "Admin Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, DashBoardActivity.class);
            startActivity(intent);
            finish();
        });
    }

}