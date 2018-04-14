package com.bitsplease.fridgynote.controller;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.bitsplease.fridgynote.R;
import com.bitsplease.fridgynote.utils.Constants;
import com.bitsplease.fridgynote.utils.PreferenceUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ShoppingItems implements Serializable {
    private static ShoppingItems instance;

    private Map<String, String> shoppingItems;

    private ShoppingItems() {
        shoppingItems = new HashMap<>();

        SharedPreferences preferences = PreferenceUtils.getPrefs();
        String shoppingItems = preferences.getString(Constants.KEY_SHOPPING_ITEMS, "");
        if (shoppingItems.length() > 0) {
            parseString(shoppingItems);
        }
    }

    public String getShoppingItem(String tag) {
        if (!shoppingItems.containsKey(tag)) {
            return null;
        }
        return shoppingItems.get(tag);
    }

    public void addShoppingItem(String tag, String value) {
        addShoppingItem(tag, value, true);
    }

    private void addShoppingItem(String tag, String value, boolean updatePrefs) {
        if (shoppingItems.containsKey(tag)) {
            shoppingItems.remove(tag);
        }
        shoppingItems.put(tag, value);
        if (updatePrefs) {
            updatePreferences();
        }
    }

    public void deleteShoppingItem(String tag) {
        deleteShoppingItem(tag, true);
    }

    private void deleteShoppingItem(String tag, boolean updatePrefs) {
        if (shoppingItems.containsKey(tag)) {
            shoppingItems.remove(tag);
        }
        if (updatePrefs) {
            updatePreferences();
        }
    }

    private void updatePreferences() {
        SharedPreferences preferences = PreferenceUtils.getPrefs();
        SharedPreferences.Editor editor = preferences.edit();
        String res = toString();
        editor.putString(Constants.KEY_SHOPPING_ITEMS, res);
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
                addShoppingItem(keyValue[0], keyValue[1], false);
            }
        }

        updatePreferences();
    }

    public boolean hasShoppingItem(String tag) {
        return shoppingItems.containsKey(tag);
    }

    public static ShoppingItems getShoppingItems() {
        if (instance != null) {
            return instance;
        }
        instance = new ShoppingItems();
        return instance;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        Set<String> keySet = shoppingItems.keySet();
        for (String s : keySet) {
            builder.append(s);
            builder.append(";");
            builder.append(shoppingItems.get(s));
            builder.append("#");
        }
        return builder.toString();
    }
}
