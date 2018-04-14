package com.bitsplease.fridgynote.utils;

import android.content.SharedPreferences;

public class PreferenceUtils {
    public static final String PACKAGE = "com.bitsplease.fridgynote";
    private static SharedPreferences instance;

    public static void setPreferences(SharedPreferences prefs) {
        instance = prefs;
    }

    public static SharedPreferences getPrefs() {
        return instance;
    }
}
