package com.mattquiros.omnitor.util;

import com.google.gson.Gson;
import com.mattquiros.omnitor.DB;

import android.content.Context;
import android.util.Log;

public abstract class Logger {
    
    public static final String TAG = "omnitor-android";
    
    public static void d(String log) {
        Log.d(TAG, log);
    }
    
    public static void printAll(Context context) {
        Log.d(TAG, "logs: " + new Gson().toJson(DB.getInstance(context).getAllLogs()));
    }
    
}
