package com.example.checkincheckout;

import static org.xmlpull.v1.XmlPullParser.TEXT;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBase extends SQLiteOpenHelper {

    public static final String DB_NAME = "checkinout.db";
    private static DataBase instance;

    public static synchronized DataBase getInstance(Context context) {
        if (instance == null) {
            instance = new DataBase(context.getApplicationContext());
        }
        return instance;
    }

    public DataBase(Context context) {
        super(context, DB_NAME, null, 9);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE admin(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, password TEXT)");

        db.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, email TEXT, phone TEXT, password TEXT, face_data BLOB)");

        db.execSQL("CREATE TABLE attendance (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "date TEXT, " +
                "check_in TEXT, " +
                "check_out TEXT, " +
                "working_hours TEXT" +
                ")");
        db.execSQL("INSERT INTO admin(name, password) VALUES('admin','12345')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS attendance");
        db.execSQL("DROP TABLE IF EXISTS admin");
        onCreate(db);
    }

    public void insertAttendance(String username, String checkIn, String date,String working_hours) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("check_in", checkIn);
        values.put("check_out", (String) null);
        values.put("date", date);
        values.put("working_hours",working_hours);

        db.insert("attendance", null, values);
    }
    public boolean insertAdmin(String name, String password) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("password", password);

        long result = db.insert("admin", null, cv);

        if (result == -1) {
            Log.e("DB", "Admin Insert Failed");
        } else {
            Log.d("DB", "Admin Insert Success ID: " + result);
        }

        return result != -1;
    }

    public void updateCheckOut(String username, String checkOutTime, String workingHours) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(
                "UPDATE attendance SET check_out=?, working_hours=? WHERE id = (" +
                        "SELECT id FROM attendance WHERE username=? AND check_out IS NULL ORDER BY id DESC LIMIT 1)",
                new Object[]{checkOutTime, workingHours, username}
        );
    }



    public String getCheckInTime(String username) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT check_in FROM attendance WHERE username=? AND check_out IS NULL ORDER BY id DESC LIMIT 1",
                new String[]{username}
        );

        if (cursor.moveToFirst()) {
            String checkIn = cursor.getString(0);
            cursor.close();
            return checkIn;
        }

        cursor.close();
        return null;
    }

    public byte[] getUserFace(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT face_data FROM users WHERE username=?",
                new String[]{username}
        );

        if (cursor != null && cursor.moveToFirst()) {
            byte[] faceBytes = cursor.getBlob(0);
            cursor.close();
            return faceBytes;
        }

        if (cursor != null) cursor.close();
        return null;
    }
    public boolean loginAdmin(String name, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM admin WHERE name=? AND password=?",
                new String[]{name, password});
        return cursor.getCount() > 0;
    }
    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users", null);

    }
    public boolean loginUser(String username, String password) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE username=? AND password=?",
                new String[]{username, password}
        );

        boolean isValid = cursor.getCount() > 0;

        cursor.close();

        return isValid;
    }

    public Cursor getAllAttendance() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM attendance", null);
    }

    public boolean insertUser(String username, String email, byte[] face_data,String phone,String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("phone",phone);
        cv.put("password",password);
        cv.put("email", email);
        cv.put("face_data", face_data);
        long res = db.insert("users", null, cv);
        return res != -1;
    }
    public String getUserEmail(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT email FROM users WHERE username=?",
                new String[]{username}
        );

        if (cursor.moveToFirst()) {
            String email = cursor.getString(0);
            cursor.close();
            return email;
        }

        cursor.close();
        return null;
    }
    public boolean hasCheckedInToday(String username, String date) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM attendance WHERE username=? AND date=? AND check_in IS NOT NULL",
                new String[]{username, date}
        );

        boolean exists = cursor.getCount() > 0;

        cursor.close();

        return exists;
    }
    public boolean hasCheckedOutToday(String username, String date) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM attendance WHERE username=? AND date=? AND check_out IS NOT NULL",
                new String[]{username, date}
        );

        boolean exists = cursor.getCount() > 0;

        cursor.close();

        return exists;
    }
    public Cursor getAllUserEmails() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT email, username FROM users", null);
    }
}