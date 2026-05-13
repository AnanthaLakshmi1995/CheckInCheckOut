package com.example.checkincheckout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        final PendingResult result = goAsync();   // ★ keeps receiver alive

        new Thread(() -> {
            try {
                String type = intent.getStringExtra("type");
                Log.d("REMINDER", "Triggered → " + type);

                Calendar now = Calendar.getInstance();
                int day = now.get(Calendar.DAY_OF_WEEK);

                if (day == Calendar.FRIDAY || day == Calendar.SATURDAY) {
                    Log.d("REMINDER", "Weekend skip");
                    scheduleNext(context, type);
                    return;
                }

                DataBase db = DataBase.getInstance(context);
                String today = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        .format(new Date());

                Log.d("REMINDER", "today string = '" + today + "'");

                // Dump all attendance rows for debugging
                Cursor dbg = db.getReadableDatabase().rawQuery(
                        "SELECT username, date, check_in, check_out FROM attendance",
                        null);
                while (dbg.moveToNext()) {
                    Log.d("REMINDER",
                            "ATTENDANCE row: user=" + dbg.getString(0) +
                                    " date='" + dbg.getString(1) + "'" +
                                    " in=" + dbg.getString(2) +
                                    " out=" + dbg.getString(3));
                }
                dbg.close();

                Cursor cursor = db.getAllUserEmails();

                if (cursor == null) {
                    Log.d("REMINDER", "Cursor is NULL");
                } else {
                    Log.d("REMINDER", "Cursor count: " + cursor.getCount());
                }

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String email = cursor.getString(0);
                        String username = cursor.getString(1);
                        Log.d("REMINDER", "Processing: " + username + " / " + email);

                        if ("checkin".equals(type)) {

                            boolean checkedIn = db.hasCheckedInToday(username, today);
                            Log.d("REMINDER",
                                    username + " already checked in? " + checkedIn);

                            if (!checkedIn) {
                                EmailHelper.sendEmailBlocking(email,
                                        "Check-In Reminder",
                                        "Hello " + username + ",\nPlease check in.");
                                Log.d("REMINDER",
                                        "Check-in mail sent to → " + email);
                            }

                        } else if ("checkout".equals(type)) {

                            boolean checkedOut = db.hasCheckedOutToday(username, today);
                            Log.d("REMINDER",
                                    username + " already checked out? " + checkedOut);

                            if (!checkedOut) {
                                EmailHelper.sendEmailBlocking(email,
                                        "Check-Out Reminder",
                                        "Hello " + username + ",\nPlease check out.");
                                Log.d("REMINDER",
                                        "Check-out mail sent to → " + email);
                            }
                        }

                    } while (cursor.moveToNext());
                    cursor.close();
                }

                scheduleNext(context, type);

            } catch (Exception e) {
                Log.e("REMINDER", "Error: " + e.getMessage(), e);
            } finally {
                result.finish();   // ★ release receiver
            }
        }).start();
    }

    private void scheduleNext(Context context, String type) {

        Calendar calendar = Calendar.getInstance();

        if ("checkin".equals(type)) {
            calendar.set(Calendar.HOUR_OF_DAY, 8);    // ★ FIXED: was 11
            calendar.set(Calendar.MINUTE, 45);         // ★ FIXED: was 3
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 17);
            calendar.set(Calendar.MINUTE, 45);
        }
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        while (day == Calendar.FRIDAY || day == Calendar.SATURDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            day = calendar.get(Calendar.DAY_OF_WEEK);
        }

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("type", type);
        intent.setAction(type);

        int requestCode = type.equals("checkin") ? 1 : 2;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent);

        Log.d("REMINDER", "Next " + type + " scheduled for → " + calendar.getTime());
    }
}