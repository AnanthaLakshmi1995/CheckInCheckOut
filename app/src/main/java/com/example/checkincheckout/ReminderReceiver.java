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

        Cursor cursor = db.getAllUserEmails();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String email = cursor.getString(0);
                String username = cursor.getString(1);

                if ("checkin".equals(type)) {

                    boolean checkedIn = db.hasCheckedInToday(username, today);

                    if (!checkedIn) {
                        EmailHelper.sendEmail(
                                email,
                                "Check-In Reminder",
                                "Hello " + username + ",\n\nPlease check in."
                        );
                    }

                } else if ("checkout".equals(type)) {

                    boolean checkedOut = db.hasCheckedOutToday(username, today);

                    if (!checkedOut) {
                        EmailHelper.sendEmail(
                                email,
                                "Check-Out Reminder",
                                "Hello " + username + ",\n\nPlease check out."
                        );
                    }
                }

            } while (cursor.moveToNext());

            cursor.close();
        }


        scheduleNext(context, type);
    }

    private void scheduleNext(Context context, String type) {

        Calendar next = Calendar.getInstance();

        if ("checkin".equals(type)) {
            next.set(Calendar.HOUR_OF_DAY, 8);
            next.set(Calendar.MINUTE,45);
        } else {
            next.set(Calendar.HOUR_OF_DAY, 17);
            next.set(Calendar.MINUTE, 45);
        }

        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);

        // move to next day
        next.add(Calendar.DAY_OF_MONTH, 1);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("type", type);
        intent.setAction(type);

        int requestCode = type.equals("checkin") ? 1 : 2;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                next.getTimeInMillis(),
                pendingIntent
        );

        Log.d("REMINDER", "Next scheduled for → " + next.getTime());
    }
}