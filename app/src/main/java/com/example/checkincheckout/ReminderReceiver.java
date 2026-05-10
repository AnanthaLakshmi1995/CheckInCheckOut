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

        // Skip Friday & Saturday
        if (day == Calendar.FRIDAY ||
                day == Calendar.SATURDAY) {

            Log.d("REMINDER", "Weekend skip");

            scheduleNext(context, type);

            return;
        }

        DataBase db = DataBase.getInstance(context);

        String today = new SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault()
        ).format(new Date());

        Cursor cursor = db.getAllUserEmails();

        if (cursor != null && cursor.moveToFirst()) {

            do {

                String email = cursor.getString(0);

                String username = cursor.getString(1);

                if ("checkin".equals(type)) {

                    boolean checkedIn =
                            db.hasCheckedInToday(
                                    username,
                                    today
                            );

                    if (!checkedIn) {

                        EmailHelper.sendEmail(
                                email,
                                "Check-In Reminder",
                                "Hello " + username +
                                        ",\nPlease check in."
                        );

                        Log.d("REMINDER",
                                "Check-in mail sent to → " + email);
                    }

                } else if ("checkout".equals(type)) {

                    boolean checkedOut =
                            db.hasCheckedOutToday(
                                    username,
                                    today
                            );

                    if (!checkedOut) {

                        EmailHelper.sendEmail(
                                email,
                                "Check-Out Reminder",
                                "Hello " + username +
                                        ",\nPlease check out."
                        );

                        Log.d("REMINDER",
                                "Check-out mail sent to → " + email);
                    }
                }

            } while (cursor.moveToNext());

            cursor.close();
        }

        scheduleNext(context, type);
    }
    private void scheduleNext(Context context, String type) {

        Calendar calendar = Calendar.getInstance();
        if ("checkin".equals(type)) {
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE,45 );
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 17);
            calendar.set(Calendar.MINUTE, 45);
        }


        // If time already passed today → move to next day
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        int day = calendar.get(Calendar.DAY_OF_WEEK);

        // Skip Friday and Saturday
        while (day == Calendar.FRIDAY || day == Calendar.SATURDAY) {

            calendar.add(Calendar.DAY_OF_MONTH, 1);

            day = calendar.get(Calendar.DAY_OF_WEEK);
        }

        Log.d("REMINDER", "Next scheduled for → " + calendar.getTime());

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, ReminderReceiver.class);

        intent.putExtra("type", type);

        intent.setAction(type);

        int requestCode = type.equals("checkin") ? 1 : 2;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );

        Log.d("REMINDER", "Alarm set for → " + calendar.getTime());
    }


}