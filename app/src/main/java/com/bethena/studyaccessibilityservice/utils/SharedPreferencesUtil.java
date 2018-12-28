package com.bethena.studyaccessibilityservice.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesUtil {
    private static SharedPreferences INSTANCE;


    public static void init(Context context, String spName) {
        if (INSTANCE == null) {
                INSTANCE = context.getSharedPreferences(spName, MODE_PRIVATE);
        }
    }


    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = INSTANCE.edit().putString(key, value);
        editor.commit();
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = INSTANCE.edit().putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return INSTANCE.getBoolean(key, defValue);
    }
}
