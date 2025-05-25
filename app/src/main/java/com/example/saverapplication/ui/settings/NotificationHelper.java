package com.example.saverapplication.ui.settings;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    public static final String CHANNEL_ID = "status_notify_channel";
    private static final String CHANNEL_NAME = "Status Notifications";

    private Context context;

    public NotificationHelper(Context context) {
        this.context = context;
    }

    // Method to create the notification channel
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Method to send a notification
    public void sendStatusNotification(String title, String message) {
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_dialog_info)  // You can customize the icon
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification); // You can use a unique ID for each notification
    }

    // Method to cancel all notifications (optional)
    public void cancelNotifications() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
