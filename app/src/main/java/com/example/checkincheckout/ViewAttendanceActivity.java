package com.example.checkincheckout;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ViewAttendanceActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AttendanceAdapter adapter;
    EditText searchuser;
    ImageButton filter;
    Button logout;

    ArrayList<AttendanceModel> attendanceList = new ArrayList<>();   // original
    ArrayList<AttendanceModel> displayList = new ArrayList<>();      // filtered

    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);

        recyclerView = findViewById(R.id.recyclerAttendance);
        searchuser = findViewById(R.id.searchUser);
        filter = findViewById(R.id.filter);
logout=findViewById(R.id.Logout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = new DataBase(this);

        loadAttendance();
        logout.setOnClickListener(v -> {

            Context context = null;
            Toast.makeText(ViewAttendanceActivity.this, "Admin Logged out Successfully", Toast.LENGTH_SHORT).show();
           // Toast.makeText(context, "Admin Logged out successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, DashBoardActivity.class);
            startActivity(intent);
            finish();
        });
        searchuser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String text = s.toString().toLowerCase();
                displayList.clear();

                for (AttendanceModel item : attendanceList) {
                    if (item.getUsername().toLowerCase().contains(text)) {
                        displayList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        filter.setOnClickListener(v -> {
            String[] options = {"All", "Check-In", "Check-Out"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Filter By");

            builder.setItems(options, (dialog, which) -> {

                displayList.clear();

                if (which == 0) {
                    displayList.addAll(attendanceList);

                } else if (which == 1) {
                    for (AttendanceModel item : attendanceList) {
                        if (item.getCheckIn() != null && !item.getCheckIn().isEmpty()) {
                            displayList.add(item);
                        }
                    }

                } else {
                    for (AttendanceModel item : attendanceList) {
                        if (item.getCheckOut() != null && !item.getCheckOut().isEmpty()) {
                            displayList.add(item);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            });

            builder.show();
        });
    }
    private void loadAttendance() {

        Cursor cursor = db.getAllAttendance();

        attendanceList.clear();

        while (cursor.moveToNext()) {

            int id = cursor.getInt(0);
            String username = cursor.getString(1);
            String date = cursor.getString(2);
            String checkIn = cursor.getString(3);
            String checkOut = cursor.getString(4);

            String workingHours = calculateWorkingHours(checkIn, checkOut);

            AttendanceModel model = new AttendanceModel(
                    id,
                    username,
                    date,
                    checkIn,
                    checkOut,
                    workingHours
            );

            //model.setWorkingHours(workingHours);

            attendanceList.add(model);
        }

        displayList.clear();
        displayList.addAll(attendanceList);

        adapter = new AttendanceAdapter(this, displayList);
        recyclerView.setAdapter(adapter);
    }
    private String calculateWorkingHours(String checkIn, String checkOut) {

        if (checkOut == null || checkOut.isEmpty()) {
            return "Not checked out";
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());

            Date inTime = sdf.parse(checkIn);
            Date outTime = sdf.parse(checkOut);

            long diff = outTime.getTime() - inTime.getTime();

            long hours = diff / (1000 * 60 * 60);
            long minutes = (diff / (1000 * 60)) % 60;

            return hours + " hrs " + minutes + " mins";

        } catch (Exception e) {
            return "Error";
        }
    }

}