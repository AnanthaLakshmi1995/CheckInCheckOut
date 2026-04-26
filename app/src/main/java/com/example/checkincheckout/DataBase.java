package com.example.checkincheckout;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

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

         super(context, DB_NAME, null, 6);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE admin(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email Text, password TEXT)");

        db.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, email TEXT,phone TEXT,password TEXT, face_data BLOB)");

        db.execSQL("CREATE TABLE attendance (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "check_in TEXT, " +
                "check_out TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("Drop table if exists attendance");
        db.execSQL("Drop table if exists admin");
        if (oldVersion < 6) {
db.execSQL("Alter table admin add column email text");
            db.execSQL("ALTER TABLE users ADD COLUMN phone TEXT");
            db.execSQL("ALTER TABLE users ADD COLUMN password Text ");
        }

        onCreate(db);
    }


    public boolean insertUser(String username, String email, byte[] face_data,String phone,String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("email", email);
        cv.put("phone",phone);
        cv.put("password",password);
        cv.put("face_data", face_data);
        long res = db.insert("users", null, cv);
        return res != -1;
    }
    public void insertAttendance(String username, String checkIn) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("check_in", checkIn);
        values.put("check_out", (String) null);

        db.insert("attendance", null, values);
    }
    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users", null);

    }
    public boolean loginUser(String name, String pass) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username=? AND password=?",
                new String[]{name, pass});
        return cursor.getCount() > 0;
    }

    public Cursor getAllAttendance() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM attendance", null);
    }
    public boolean loginAdmin(String name, String password) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM admin WHERE name=? AND password=?",
                new String[]{name, password}
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();

        return exists;
    }


    public boolean insertAdmin(String name, String  email,String password) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("email" , email);
        cv.put("password", password);


        long result = db.insert("admin", null, cv);

        return result != -1;
    }
    public void updateCheckOut(String username, String checkOutTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(
                "UPDATE attendance SET check_out=? WHERE id = (" +
                        "SELECT id FROM attendance WHERE username=? AND check_out IS NULL ORDER BY id DESC LIMIT 1" +
                        ")",
                new Object[]{checkOutTime, username}
        );
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

}