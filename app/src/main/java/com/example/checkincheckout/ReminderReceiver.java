package com.example.checkincheckout;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String type = intent.getStringExtra("type");


        if ("checkin".equals(type)) {
            NotificationHelper.showNotification(
                    context,
                    "Check-in Reminder",
                    "Your check-in time is at 9:00 AM"
            );

        } else if ("checkout".equals(type)) {
            NotificationHelper.showNotification(
                    context,
                    "Check-out Reminder",
                    "Your check-out time is approaching"
            );
        }
    }

public static void showNotification(Context context, String title, String message) {

    NotificationManager manager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    String channelId = "reminder_channel";

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel channel = new NotificationChannel(
                channelId,
                "Reminders",
                NotificationManager.IMPORTANCE_HIGH
        );
        manager.createNotificationChannel(channel);
    }

    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH);

    manager.notify((int) System.currentTimeMillis(), builder.build());
}
}