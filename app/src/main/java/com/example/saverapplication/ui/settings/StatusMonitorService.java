package com.example.saverapplication.ui.settings;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


import com.example.saverapplication.MainActivity;
import com.example.saverapplication.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StatusMonitorService extends Service {

    private static final long POLL_INTERVAL = 12 * 60 * 60 * 1000;
    private Handler handler;
    private Runnable statusCheckRunnable;
    private static final int NOTIFICATION_ID = 1001;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Build a minimal foreground notification and cancel it immediately
        Notification notification = new NotificationCompat.Builder(this, NotificationHelper.CHANNEL_ID)
                .setSmallIcon(R.drawable.whats) // Make sure this icon exists in res/drawable
                .setPriority(NotificationCompat.PRIORITY_MIN) // Very low priority
                .build();

        startForeground(1, notification);
        stopForeground(true); // Hide it immediately

        handler = new Handler();
        statusCheckRunnable = new Runnable() {
            @Override
            public void run() {
                checkForNewStatuses();
                handler.postDelayed(this, POLL_INTERVAL);
            }
        };
        handler.post(statusCheckRunnable);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && statusCheckRunnable != null) {
            handler.removeCallbacks(statusCheckRunnable);
        }
    }

    private void checkForNewStatuses() {
        List<File> whatsappStatuses = new ArrayList<>();
        List<File> whatsappBusinessStatuses = new ArrayList<>();

        // WhatsApp personal
        File waDir = new File(Environment.getExternalStorageDirectory(),
                "Android/media/com.whatsapp/WhatsApp/Media/.Statuses");

        // WhatsApp Business
        File waBizDir = new File(Environment.getExternalStorageDirectory(),
                "Android/media/com.whatsapp.w4b/WhatsApp Business/Media/.Statuses");

        if (waDir.exists()) {
            whatsappStatuses.addAll(getNewStatuses(waDir));
        }

        if (waBizDir.exists()) {
            whatsappBusinessStatuses.addAll(getNewStatuses(waBizDir));
        }

        int totalNew = whatsappStatuses.size() + whatsappBusinessStatuses.size();

        if (totalNew > 0) {
            StringBuilder message = new StringBuilder();

            if (!whatsappStatuses.isEmpty()) {
                message.append(whatsappStatuses.size()).append(" from WhatsApp");
            }
            if (!whatsappBusinessStatuses.isEmpty()) {
                if (message.length() > 0) message.append(" and ");
                message.append(whatsappBusinessStatuses.size()).append(" from WhatsApp Business");
            }

            sendStatusNotification("You have " + totalNew + " new status(es): " + message + ".");
        }
    }



    private List<File> getNewStatuses(File statusDirectory) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Set<String> seenFiles = prefs.getStringSet("seen_status_files", new HashSet<>());

        File[] allFiles = statusDirectory.listFiles();
        List<File> newStatuses = new ArrayList<>();

        if (allFiles != null) {
            for (File file : allFiles) {
                if (!seenFiles.contains(file.getAbsolutePath()) && file.isFile()) {
                    newStatuses.add(file);
                }
            }
        }

        // Save updated seen files
        Set<String> updatedSeen = new HashSet<>(seenFiles);
        for (File file : newStatuses) {
            updatedSeen.add(file.getAbsolutePath());
        }
        prefs.edit().putStringSet("seen_status_files", updatedSeen).apply();

        return newStatuses;
    }

    private void sendStatusNotification(String message) {
        NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
        notificationHelper.createNotificationChannel();

        Intent viewIntent = new Intent(this, MainActivity.class); // Open your main activity
        viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        viewIntent.putExtra("from_notification", true);

        PendingIntent viewPendingIntent = PendingIntent.getActivity(
                this, 0, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, NotificationHelper.CHANNEL_ID)
                .setContentTitle("New Status Update")
                .setContentText(message)
                .setSmallIcon(R.drawable.whats)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_view, "View Now", viewPendingIntent)
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, notification);
    }

}
