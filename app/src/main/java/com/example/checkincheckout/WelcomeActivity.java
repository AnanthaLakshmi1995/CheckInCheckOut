package com.example.checkincheckout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessaging;

public class WelcomeActivity extends AppCompatActivity {

    ImageView Image, themeIcon;
    Button start;
    LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Image = findViewById(R.id.Image);
        start = findViewById(R.id.Start);
        themeIcon = findViewById(R.id.themeIcon);
        mainLayout = findViewById(R.id.mainLayout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            requestPermissions(
                    new String[]{
                            android.Manifest.permission.POST_NOTIFICATIONS
                    },
                    1
            );
        }
        FirebaseApp.initializeApp(this);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        android.util.Log.d(
                                "FCM",
                                "TOKEN FAILED"
                        );

                        return;
                    }

                    String token = task.getResult();

                    android.util.Log.d(
                            "FCM",
                            "TOKEN = " + token
                    );
                });
        SharedPreferences prefs =
                getSharedPreferences("theme", MODE_PRIVATE);

        // INITIAL THEME
        boolean darkMode =
                prefs.getBoolean("darkMode", false);

        if (darkMode) {

            mainLayout.setBackgroundColor(
                    getResources().getColor(R.color.dark_bg));

            themeIcon.setImageResource(R.drawable.ic_dark_mode);

        } else {

            mainLayout.setBackgroundColor(
                    getResources().getColor(R.color.light_bg));

            themeIcon.setImageResource(R.drawable.ic_light_mode);
        }

        // THEME SWITCH
        themeIcon.setOnClickListener(v -> {

            boolean isDark =
                    prefs.getBoolean("darkMode", false);

            if (isDark) {

                // LIGHT MODE
                mainLayout.setBackgroundColor(
                        getResources().getColor(R.color.light_bg));

                themeIcon.setImageResource(R.drawable.ic_light_mode);

                prefs.edit()
                        .putBoolean("darkMode", false)
                        .apply();

            } else {

                // DARK MODE
                mainLayout.setBackgroundColor(
                        getResources().getColor(R.color.dark_bg));

                themeIcon.setImageResource(R.drawable.ic_dark_mode);

                prefs.edit()
                        .putBoolean("darkMode", true)
                        .apply();
            }
        });

        // START BUTTON
        start.setOnClickListener(v -> {
            startActivity(new Intent(
                    WelcomeActivity.this,
                    DashBoardActivity.class));
        });

        // BACK PRESS
        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {

                        new AlertDialog.Builder(WelcomeActivity.this)
                                .setTitle("Exit App")
                                .setMessage("Are you sure you want to exit?")
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        (dialog, which) -> finish())
                                .setNegativeButton("No",
                                        (dialog, which) -> dialog.dismiss())
                                .show();
                    }
                });
    }
}