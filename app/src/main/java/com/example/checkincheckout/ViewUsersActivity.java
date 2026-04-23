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

import java.util.ArrayList;

public class ViewUsersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    UserAdapter adapter;
    EditText searchuser;
    Button logout;

    ArrayList<UserModel> userList = new ArrayList<>();
    ArrayList<UserModel> displayList = new ArrayList<>();

    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);
        searchuser = findViewById(R.id.searchUser);
        recyclerView = findViewById(R.id.recycleUsers);
        logout=findViewById(R.id.Logout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        db = new DataBase(this);
          loadUsers();
        logout.setOnClickListener(v ->
        {
            Context context = null;
            Toast.makeText(context, "Admin Logged out successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(context, AdminDashBoardActivity.class);
            startActivity(intent);
        });
        searchuser.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

                String text = s.toString().toLowerCase();
                displayList.clear();

                for (UserModel user : userList)
                {
                    if (user.getUsername().toLowerCase().contains(text))
                    {
                        displayList.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

    }
    private void loadUsers()
    {
        Cursor cursor = db.getAllUsers();

        if (cursor.getCount() == 0)
        {
            Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show();
            return;
        }

        userList.clear();
        while (cursor.moveToNext())
        {
            userList.add(new UserModel(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("username")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("password")),
                    cursor.getString(cursor.getColumnIndexOrThrow("phone"))
            ));
        }

        displayList.clear();
        displayList.addAll(userList);
        adapter = new UserAdapter(this, displayList);
        recyclerView.setAdapter(adapter);
    }
}