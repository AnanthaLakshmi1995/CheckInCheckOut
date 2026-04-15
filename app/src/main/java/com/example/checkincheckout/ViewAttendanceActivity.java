package com.example.checkincheckout;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewAttendanceActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    AttendanceAdapter adapter;
    ArrayList<AttendanceModel> attendanceList = new ArrayList<>();
    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_attendance);
        recyclerView = findViewById(R.id.recyclerAttendance);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = new DataBase(this);

        loadAttendance();
    }

    private void loadAttendance() {
        Cursor cursor = db.getAllAttendance();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No attendance records found", Toast.LENGTH_SHORT).show();
            return;
        }

        while (cursor.moveToNext()) {
            attendanceList.add(new AttendanceModel(
                    cursor.getInt(0),      // ID
                    cursor.getString(1),   // Username
                    cursor.getString(2),   // CheckIn
                    cursor.getString(3)    // CheckOut
            ));
        }

        adapter = new AttendanceAdapter(this, attendanceList);
        recyclerView.setAdapter(adapter);
    }
}