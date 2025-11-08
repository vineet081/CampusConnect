package com.vineet.campusconnect.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.vineet.campusconnect.models.Task;
import com.vineet.campusconnect.receivers.AlertReceiver;

public class AlarmUtils {

    @SuppressLint("ScheduleExactAlarm") // Suppress warning as we asked for permission in Manifest
    public static void scheduleAlarm(Context context, Task task) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // 1. Prepare the intent that will launch the AlertReceiver
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.putExtra("TASK_TITLE", task.title);
        // We use the created timestamp as a unique ID for the alarm
        int uniqueId = (int) (task.createdTimestamp / 1000);
        intent.putExtra("TASK_ID", uniqueId);

        // 2. Create the PendingIntent
        // FLAG_IMMUTABLE is required for Android 12+
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                uniqueId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 3. Check for exact alarm permission on Android 12+ (API 31+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(context, "Please allow standard alarms in settings for reminders", Toast.LENGTH_LONG).show();
                // In a real app, you would open the settings screen here:
                // Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                // context.startActivity(intent);
                return;
            }
        }

        // 4. Set the alarm
        // setExactAndAllowWhileIdle ensures it fires even if the phone is in "doze" mode
        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.dueDate, pendingIntent);
        } catch (SecurityException e) {
            Toast.makeText(context, "Cannot schedule alarm: Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    // Optional: Method to cancel an alarm (useful for "Delete Task" later)
    public static void cancelAlarm(Context context, Task task) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        int uniqueId = (int) (task.createdTimestamp / 1000);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                uniqueId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
    }
}