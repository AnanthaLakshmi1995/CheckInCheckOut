package com.example.checkincheckout;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class UserCheckinActivity extends AppCompatActivity {

    ImageView capturedImage;
    Button btnCheckIn, btnCheckOut, cameraBtn;

    DataBase db;
    String userName;

    private static final int CAMERA_REQUEST = 100;
    Bitmap capturedFaceBitmap = null;
    FusedLocationProviderClient fusedLocationClient;
    double officeLat = 16.5907263;
    double officeLng = 82.018323;

    boolean isCheckIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_checkin);

        capturedImage = findViewById(R.id.capturedImage);
        btnCheckIn = findViewById(R.id.CheckIn);
        btnCheckOut = findViewById(R.id.CheckOut);
        cameraBtn = findViewById(R.id.Camera);
        //tickMark = findViewById(R.id.tickMark);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                new androidx.appcompat.app.AlertDialog.Builder(UserCheckinActivity.this)
                        .setTitle("Exit App")
                        .setMessage("Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, which) -> finish())
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        db = DataBase.getInstance(this);

        userName = getIntent().getStringExtra("username");

        if (userName == null || userName.isEmpty()) {
            Toast.makeText(this, "Username not received!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cameraBtn.setOnClickListener(v -> openCamera());

        btnCheckIn.setOnClickListener(v -> {
            isCheckIn = true;
            validateFaceAndProceed();

        });

        btnCheckOut.setOnClickListener(v -> {
            isCheckIn = false;
            validateFaceAndProceed();
        });



    }
    private void performAction() {

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String time = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(new Date());

        if (isCheckIn) {

            db.insertAttendance(userName, time, currentDate, "");

            Toast.makeText(this, "Check-In Successfully", Toast.LENGTH_SHORT).show();
            setReminder(this, 8, 45, 1, "checkin");


        } else {

            String checkInTime = db.getCheckInTime(userName);

            if (checkInTime == null) {
                Toast.makeText(this, "No check-in found!", Toast.LENGTH_SHORT).show();
                return;
            }
            String workingHours = calculateWorkingHours(checkInTime, time);
            db.updateCheckOut(userName, time, workingHours);

            setReminder(this, 17, 45, 2, "checkout");
            //Toast.makeText(this, "Checked Out\nWorking Hours: " + workingHours, Toast.LENGTH_LONG).show();
           Toast.makeText(this, "Checked Out Successfully", Toast.LENGTH_SHORT).show();

        }

        startActivity(new Intent(this, DashBoardActivity.class));
    }

    private void validateFaceAndProceed() {

        if (capturedFaceBitmap == null) {
            Toast.makeText(this, "Capture face first", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap registeredFace = getRegisteredFaceFromDB(userName);

        if (registeredFace == null) {
            Toast.makeText(this, "No registered face found", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isMatch = compareFaces(capturedFaceBitmap, registeredFace);

        if (isMatch) {
            getUserLocation();
        } else {
            Toast.makeText(this, "Face Not Matched", Toast.LENGTH_SHORT).show();
        }
    }

    private void getUserLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        checkOfficeRange(location.getLatitude(), location.getLongitude());
                    } else {
                        Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkOfficeRange(double userLat, double userLng) {

        float[] results = new float[1];

        Location.distanceBetween(
                officeLat, officeLng,
                userLat, userLng,
                results
        );

        float distance = results[0];

        if (distance <= 100) {
            performAction();
        } else {
            Toast.makeText(this, "You are outside office!", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            Bitmap original = (Bitmap) data.getExtras().get("data");
            capturedFaceBitmap = processFace(original);
            capturedImage.setImageBitmap(capturedFaceBitmap);
            //tickMark.setVisibility(View.VISIBLE);
        }
    }

    public boolean compareFaces(Bitmap bmp1, Bitmap bmp2) {

        bmp1 = resize(bmp1);
        bmp2 = resize(bmp2);

        int total = bmp1.getWidth() * bmp1.getHeight();

        long sum1 = 0;
        long sum2 = 0;

        for (int i = 0; i < bmp1.getWidth(); i++) {
            for (int j = 0; j < bmp1.getHeight(); j++) {

                int p1 = bmp1.getPixel(i, j);
                int p2 = bmp2.getPixel(i, j);

                int gray1 = (int)(0.3 * ((p1 >> 16) & 0xff) +
                        0.59 * ((p1 >> 8) & 0xff) +
                        0.11 * (p1 & 0xff));

                int gray2 = (int)(0.3 * ((p2 >> 16) & 0xff) +
                        0.59 * ((p2 >> 8) & 0xff) +
                        0.11 * (p2 & 0xff));

                sum1 += gray1;
                sum2 += gray2;
            }
        }

        float avg1 = sum1 / (float) total;
        float avg2 = sum2 / (float) total;

        float diff = Math.abs(avg1 - avg2);

        Log.d("FACE", "Avg1: " + avg1 + " Avg2: " + avg2 + " Diff: " + diff);

        // 🔥 KEY CHANGE (tolerance)
        return diff < 25;
    }
    private Bitmap resize(Bitmap bmp1) {
        return bmp1;
    }

    private Bitmap processFace(Bitmap original) {
        Bitmap rotated = rotateBitmap(original, -90);

        int size = Math.min(rotated.getWidth(), rotated.getHeight());
        int x = (rotated.getWidth() - size) / 2;
        int y = (rotated.getHeight() - size) / 2;

        Bitmap cropped = Bitmap.createBitmap(rotated, x, y, size, size);
        return Bitmap.createScaledBitmap(cropped, 200, 200, true);
    }

    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0,
                source.getWidth(), source.getHeight(), matrix, true);
    }
    private Bitmap getRegisteredFaceFromDB(String username) {
        byte[] faceBytes = db.getUserFace(username);
        if (faceBytes == null) return null;

        return android.graphics.BitmapFactory.decodeByteArray(faceBytes, 0, faceBytes.length);
    }

    private String calculateWorkingHours(String checkIn, String checkOut) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());

            Date inTime = sdf.parse(checkIn);
            Date outTime = sdf.parse(checkOut);

            long diff = outTime.getTime() - inTime.getTime();

            long hours = diff / (1000 * 60 * 60);
            long minutes = (diff / (1000 * 60)) % 60;

            return hours + " hrs " + minutes + " mins";

        } catch (Exception e) {
            return "0 hrs";
        }
    }
    private void setReminder(Context context, int hour, int minute, int requestCode, String type) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("type", type);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

}