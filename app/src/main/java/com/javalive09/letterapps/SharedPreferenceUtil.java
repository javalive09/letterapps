package com.javalive09.letterapps;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtil {

    public static final String FAVORITE = "favorite";

    public static boolean getBoolean(Context context, String name, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public static void putBoolean(Context context, String name, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public static void removeKey(Context context, String name, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(key).apply();
    }

    public static void registerListener(Context context, String name, SharedPreferences
            .OnSharedPreferenceChangeListener listener) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterListener(Context context, String name, SharedPreferences
            .OnSharedPreferenceChangeListener listener) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public static Map<String, ?> getAll(Context context, String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences.getAll();
    }

}
