package com.vineet.campusconnect.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.vineet.campusconnect.MainActivity;
import com.vineet.campusconnect.R;

public class AlertReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "task_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // 1. Get the task title from the intent that woke us up
        String taskTitle = intent.getStringExtra("TASK_TITLE");
        int taskId = intent.getIntExtra("TASK_ID", 0);

        // 2. Create the action (what happens when they tap the notification)
        // We want it to open the TaskManagerActivity
        Intent tapIntent = new Intent(context, MainActivity.class);
        tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, tapIntent, PendingIntent.FLAG_IMMUTABLE);

        // 3. Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_task) // Use your task icon
                .setContentTitle("Task Reminder")
                .setContentText(taskTitle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // Close notification when tapped

        // 4. Show the notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // For Android 8.0+ (Oreo), we must create a Notification Channel first
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Task Reminders", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for task due dates");
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(taskId, builder.build());
    }
}