package com.example.checkincheckout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        DataBase db = DataBase.getInstance(context);

        String type = intent.getStringExtra("type");

        String today = new SimpleDateFormat("dd-MM-yyyy",
                Locale.getDefault()).format(new Date());

        Cursor cursor = db.getAllUserEmails();

        if (cursor != null && cursor.moveToFirst()) {

            do {
                String email = cursor.getString(0);
                String username = cursor.getString(1);

                if ("checkin".equals(type)) {

                    if (!db.hasCheckedInToday(username, today)) {

                        EmailHelper.sendMail(
                                email,
                                "Check-In Reminder",
                                "Hi " + username + ", please check in today."
                        );
                    }

                } else if ("checkout".equals(type)) {

                    if (!db.hasCheckedOutToday(username, today)) {

                        EmailHelper.sendMail(
                                email,
                                "Check-Out Reminder",
                                "Hi " + username + ", please check out today."
                        );
                    }
                }

            } while (cursor.moveToNext());

            cursor.close();
        }
    }
}