package com.bitsplease.fridgynote.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.bitsplease.fridgynote.R;
import com.bitsplease.fridgynote.utils.Constants;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Reminders implements Serializable {
    private static Reminders instance;

    private SharedPreferences preferences;
    private Map<String, String> reminders;

    private Reminders(SharedPreferences appPreferences) {
        preferences = appPreferences;
        reminders = new HashMap<>();

        Log.d("FN-", "parsing");
        if (preferences.contains(Constants.KEY_REMINDERS)) {
            parseString(preferences.getString(Constants.KEY_REMINDERS, ""));
        }
        Log.d("FN-", "stuff " + toString());
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

    public void addReminder(String tag, String value, boolean updatePrefs) {
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

    public void deleteReminder(String tag, boolean updatePrefs) {
        if (reminders.containsKey(tag)) {
            reminders.remove(tag);
        }
        if (updatePrefs) {
            updatePreferences();
        }
    }

    private void updatePreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.KEY_REMINDERS, toString());
        editor.apply();
    }

    private void parseString(String str) {
        if (str == null || str.isEmpty()) {
            return;
        }

        String[] tokens = str.split("#");
        for (String t : tokens) {
            if (t != null && !t.isEmpty()) {
                String[] keyValue = t.split("|");
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

    public static Reminders getReminders(SharedPreferences preferences) {
        if(instance != null) {
            return instance;
        }
        instance = new Reminders(preferences);
        return instance;
    }

    public static void setReminder(Context context, String value) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Constants.KEY_REMINDERS)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("FridgyNote")
                .setContentText(value)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(value.hashCode(), mBuilder.build());
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        Set<String> keySet = reminders.keySet();
        for (String s : keySet) {
            builder.append(s);
            builder.append("|");
            builder.append(reminders.get(s));
            builder.append("#");
        }
        Log.d("FN-", "toStr " + builder.toString());
        return builder.toString();
    }
}
