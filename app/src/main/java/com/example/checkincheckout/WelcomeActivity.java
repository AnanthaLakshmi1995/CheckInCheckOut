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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Calendar;

public class WelcomeActivity extends AppCompatActivity {

    ImageView Image;
    Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 🔥 THEME FIRST
        SharedPreferences themePrefs = getSharedPreferences("theme", MODE_PRIVATE);
        boolean isDark = themePrefs.getBoolean("darkMode", false);

        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);




        Image = findViewById(R.id.Image);
        start = findViewById(R.id.Start);
        ImageView themeIcon = findViewById(R.id.themeIcon);

        // ✅ Set correct icon
        themeIcon.setImageResource(
                isDark ? R.drawable.ic_dark_mode : R.drawable.ic_light_mode
        );

        // 🔥 Theme toggle
        themeIcon.setOnClickListener(v -> {

            boolean current = themePrefs.getBoolean("darkMode", false);
            boolean newMode = !current;

            themePrefs.edit().putBoolean("darkMode", newMode).apply();

            AppCompatDelegate.setDefaultNightMode(
                    newMode ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );

            recreate();
        });

        start.setOnClickListener(v -> {
            startActivity(new Intent(this, DashBoardActivity.class));
        });
    }


}