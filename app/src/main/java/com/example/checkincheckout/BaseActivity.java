package com.example.checkincheckout;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    protected SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("theme", MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyTheme(isDarkMode());
    }

    protected boolean isDarkMode() {
        return prefs.getBoolean("darkMode", false);
    }

    protected void toggleTheme() {
        boolean newMode = !isDarkMode();
        prefs.edit().putBoolean("darkMode", newMode).apply();
        applyTheme(newMode);
    }
    protected void setupThemeIcon(android.widget.ImageView themeIcon) {
        if (themeIcon == null) return;

        // Set the correct icon for current mode
        themeIcon.setImageResource(isDarkMode()
                ? R.drawable.ic_dark_mode
                : R.drawable.ic_light_mode);

        // Toggle on click
        themeIcon.setOnClickListener(v -> {
            toggleTheme();
            themeIcon.setImageResource(isDarkMode()
                    ? R.drawable.ic_dark_mode
                    : R.drawable.ic_light_mode);
        });
    }
    protected void applyTheme(boolean darkMode) {
        int bgColor = darkMode
                ? getResources().getColor(R.color.dark_bg)
                : getResources().getColor(R.color.light_bg);

        View root = findViewById(android.R.id.content);
        if (root != null) {
            root.setBackgroundColor(bgColor);
        }

        getWindow().setStatusBarColor(bgColor);
        getWindow().setNavigationBarColor(bgColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                int appearance = darkMode
                        ? 0
                        : WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS;
                controller.setSystemBarsAppearance(
                        appearance,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
            }
        }
    }
}