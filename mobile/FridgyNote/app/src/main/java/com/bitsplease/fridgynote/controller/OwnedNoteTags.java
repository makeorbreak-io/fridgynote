package com.bitsplease.fridgynote.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.bitsplease.fridgynote.R;
import com.bitsplease.fridgynote.utils.Constants;
import com.bitsplease.fridgynote.utils.PreferenceUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by André on 14/04/2018.
 */

public class OwnedNoteTags {
    private static OwnedNoteTags instance;


    private Map<String, String> ownedTags;

    private OwnedNoteTags() {

        ownedTags = new HashMap<>();

        SharedPreferences preferences = PreferenceUtils.getPrefs();
        String owned = preferences.getString(Constants.KEY_OWNED_TAGS, "");
        if (owned.length() > 0) {
            parseString(owned);
        }
    }

    public String getOwnedTag(String tag) {
        if (!ownedTags.containsKey(tag)) {
            return null;
        }
        return ownedTags.get(tag);
    }

    public void addOwnedTag(String tag, String value) {
        addOwnedTag(tag, value, true);
    }

    private void addOwnedTag(String tag, String value, boolean updatePrefs) {
        Log.d("FN-test", "actually adding " + tag + " " + value);
        if (ownedTags.containsKey(tag)) {
            ownedTags.remove(tag);
        }
        ownedTags.put(tag, value);
        if (updatePrefs) {
            updatePreferences();
        }
    }

    public void deleteOwnedTag(String tag) {
        deleteOwnedTag(tag, true);
    }

    private void deleteOwnedTag(String tag, boolean updatePrefs) {
        if (ownedTags.containsKey(tag)) {
            ownedTags.remove(tag);
        }
        if (updatePrefs) {
            updatePreferences();
        }
    }

    private void updatePreferences() {
        SharedPreferences preferences = PreferenceUtils.getPrefs();
        SharedPreferences.Editor editor = preferences.edit();
        String res = toString();
        editor.putString(Constants.KEY_OWNED_TAGS, res);
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
                addOwnedTag(keyValue[0], keyValue[1], false);
            }
        }

        updatePreferences();
    }

    public boolean hasOwnedTag(String tag) {
        Log.e("FN-OWNEDS", ownedTags.toString());
        Log.e("FN-OWNEDS", tag);
        return ownedTags.containsKey(tag);
    }

    public static OwnedNoteTags getOwnedTags() {
        if(instance != null) {
            return instance;
        }
        instance = new OwnedNoteTags();
        return instance;
    }

    public static void setOwnedTag(Context context, String value) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Constants.KEY_OWNED_TAGS)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("FridgyNote")
                .setContentText(value)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(value.hashCode(), mBuilder.build());
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        Set<String> keySet = ownedTags.keySet();
        for (String s : keySet) {
            builder.append(s);
            builder.append(";");
            builder.append(ownedTags.get(s));
            builder.append("#");
        }
        Log.d("FN-", "toStr " + builder.toString());
        return builder.toString();
    }
}
