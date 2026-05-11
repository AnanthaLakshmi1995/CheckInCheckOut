package com.example.checkincheckout;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService
        extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);

        NotificationManager manager =
                (NotificationManager)
                        getSystemService(NOTIFICATION_SERVICE);

        String channelId = "fcm_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            channelId,
                            "FCM Notifications",
                            NotificationManager.IMPORTANCE_HIGH);

            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, channelId)
                        .setContentTitle(
                                message.getNotification().getTitle())
                        .setContentText(
                                message.getNotification().getBody())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true);

        manager.notify(1, builder.build());
    }
}