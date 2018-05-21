package com.nissan.alldriverguide.utils;

import android.util.Log;

/**
 * Created by shubha on 11/20/17.
 */

public class Logger {

    public static void error(String tag, String msg) {
        if (AppConfig.IS_DEBUGGABLE)
            Log.e(tag, msg);
    }

    public void weird(String tag, String msg) {
        if (AppConfig.IS_DEBUGGABLE)
            Log.w(tag, msg);
    }

    public void information(String tag, String msg) {
        if (AppConfig.IS_DEBUGGABLE)
            Log.i(tag, msg);
    }

    public static void debugging(String tag, String msg) {
        if (AppConfig.IS_DEBUGGABLE)
            Log.d(tag, msg);
    }

    public void veryMuchAll(String tag, String msg) {
        if (AppConfig.IS_DEBUGGABLE)
            Log.v(tag, msg);
    }

}
