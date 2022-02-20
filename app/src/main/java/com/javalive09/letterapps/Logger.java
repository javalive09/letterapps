package com.javalive09.letterapps;

import android.util.Log;

/**
 * open log
 *
 * adb shell setprop log.tag.Launcher VERBOSE
 */
public class Logger {

    private static final String TAG = "Launcher";

    public static void i(String tag, String msg) {
        if (isLoggable()) {
            Log.i(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isLoggable()) {
            Log.e(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isLoggable()) {
            Log.d(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (isLoggable()) {
            Log.v(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isLoggable()) {
            Log.w(tag, msg);
        }
    }

    public static boolean isLoggable() {
        return Log.isLoggable(TAG, Log.DEBUG);
    }

}