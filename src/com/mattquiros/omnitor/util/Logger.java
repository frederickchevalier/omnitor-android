package com.mattquiros.omnitor.util;

import android.util.Log;

public abstract class Logger {
    
    public static final String TAG = "omnitor-android";
    
    public static void d(String log) {
        Log.d(TAG, log);
    }
    
}
