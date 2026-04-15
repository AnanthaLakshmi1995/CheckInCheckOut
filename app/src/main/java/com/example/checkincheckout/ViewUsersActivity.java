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

public class ViewUsersActivity extends AppCompatActivity {

     RecyclerView recyclerView;
    UserAdapter adapter;
    ArrayList<UserModel> userList = new ArrayList<>();
    DataBase db;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_users);
        recyclerView = findViewById(R.id.recyclerUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = new DataBase(this);

        loadUsers();
    }

    private void loadUsers() {
        Cursor cursor = db.getAllUsers();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show();
            return;
        }

        while (cursor.moveToNext()) {
            userList.add(new UserModel(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2)
            ));
        }

        adapter = new UserAdapter(this, userList);
        recyclerView.setAdapter(adapter);
    }
}