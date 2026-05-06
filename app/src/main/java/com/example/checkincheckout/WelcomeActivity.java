package com.example.checkincheckout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Calendar;

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

        ImageView themeIcon = findViewById(R.id.themeIcon);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                new androidx.appcompat.app.AlertDialog.Builder(WelcomeActivity.this)
                        .setTitle("Exit App")
                        .setMessage("Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, which) -> finish())
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
        SharedPreferences prefs = getSharedPreferences("theme", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("darkMode", false);
        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );
// set icon initially
        themeIcon.setImageResource(isDark ? R.drawable.ic_dark_mode : R.drawable.ic_light_mode);

        themeIcon.setOnClickListener(v -> {

            boolean currentMode = prefs.getBoolean("darkMode", false);
            boolean newMode = !currentMode;

            prefs.edit().putBoolean("darkMode", newMode).apply();

            AppCompatDelegate.setDefaultNightMode(
                    newMode ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );

            // 🔥 IMPORTANT: update icon
            themeIcon.setImageResource(
                    newMode ? R.drawable.ic_dark_mode : R.drawable.ic_light_mode
            );
        });

        start.setOnClickListener(v -> {
            startActivity(new Intent(this, DashBoardActivity.class));
        });
    }


}