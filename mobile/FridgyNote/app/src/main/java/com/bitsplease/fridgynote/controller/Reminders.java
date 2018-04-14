package com.bitsplease.fridgynote.controller;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.bitsplease.fridgynote.R;
import com.bitsplease.fridgynote.utils.Constants;
import com.bitsplease.fridgynote.utils.PreferenceUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Reminders {
    private static Reminders instance;

    private Map<String, String> reminders;

    private Reminders() {
        reminders = new HashMap<>();

        SharedPreferences preferences = PreferenceUtils.getPrefs();
        String reminders = preferences.getString(Constants.KEY_REMINDERS, "");
        if (reminders.length() > 0) {
            parseString(reminders);
        }
    }

    public String getReminder(String tag) {
        if (!reminders.containsKey(tag)) {
            return null;
        }
        return reminders.get(tag);
    }

    public void addReminder(String tag, String value) {
        addReminder(tag, value, true);
    }

    private void addReminder(String tag, String value, boolean updatePrefs) {
        if (reminders.containsKey(tag)) {
            reminders.remove(tag);
        }
        reminders.put(tag, value);
        if (updatePrefs) {
            updatePreferences();
        }
    }

    public void deleteReminder(String tag) {
        deleteReminder(tag, true);
    }

    private void deleteReminder(String tag, boolean updatePrefs) {
        if (reminders.containsKey(tag)) {
            reminders.remove(tag);
        }
        if (updatePrefs) {
            updatePreferences();
        }
    }

    private void updatePreferences() {
        SharedPreferences preferences = PreferenceUtils.getPrefs();
        SharedPreferences.Editor editor = preferences.edit();
        String res = toString();
        editor.putString(Constants.KEY_REMINDERS, res);
        editor.apply();
    }

    private void parseString(String str) {
        if (str == null || str.isEmpty()) {
            return;
        }

        String[] tokens = str.split("#");
        for (String t : tokens) {
            if (t != null && !t.isEmpty()) {
                String[] keyValue = t.split(";");
                if (keyValue.length != 2) {
                    continue;
                }
                addReminder(keyValue[0], keyValue[1], false);
            }
        }

        updatePreferences();
    }

    public boolean hasReminder(String tag) {
        return reminders.containsKey(tag);
    }

    public static Reminders getReminders() {
        if (instance != null) {
            return instance;
        }
        instance = new Reminders();
        return instance;
    }

    public static void setReminder(Context context, String value) {
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Constants.KEY_REMINDERS)
//                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
//                .setContentTitle("FridgyNote")
//                .setContentText(value)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        notificationManager.notify(value.hashCode(), mBuilder.build());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            String id = "w01", name = "FridgyNote Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String desc = "Reminders for the FridgyNote app.";

            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(desc);
            notificationManager.createNotificationChannel(channel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, id);
            builder.setAutoCancel(false);
            builder.setContentTitle("FridgyNote Reminder");
            builder.setContentText(value);
            //builder.setStyle(new Notification.BigTextStyle().bigText(data));
            builder.setSmallIcon(R.drawable.ic_notifications_black_24dp);
            //builder.setContentIntent(pendingIntent);
            //if (Build.VERSION.SDK_INT >= 24) builder.setColor(Color.parseColor("#ff0000"));
            Notification notification = builder.build();
            notificationManager.notify(0, notification);
        } else {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Constants.KEY_REMINDERS)
                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setContentTitle("FridgyNote")
                    .setContentText(value)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(value.hashCode(), mBuilder.build());
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        Set<String> keySet = reminders.keySet();
        for (String s : keySet) {
            builder.append(s);
            builder.append(";");
            builder.append(reminders.get(s));
            builder.append("#");
        }
        return builder.toString();
    }
}
