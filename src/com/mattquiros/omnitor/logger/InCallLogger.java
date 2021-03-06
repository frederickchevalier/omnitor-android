package com.mattquiros.omnitor.logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;
import com.mattquiros.omnitor.DB;
import com.mattquiros.omnitor.bean.InCallLog;
import com.mattquiros.omnitor.util.Logger;
import com.mattquiros.omnitor.util.This;

public class InCallLogger extends BroadcastReceiver {
    
    private static long timeStarted = -1L;
    private static long timeAnswered = -1L;
    private static long timeEnded;
    private static boolean isRoaming;
    private static String number;
    private static String simNumber;
    
    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) return;
        String state = extras.getString(TelephonyManager.EXTRA_STATE);
        if (state == null) return;
        
        // ringing
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            timeStarted = System.currentTimeMillis();

            number = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            TelephonyManager tm = (TelephonyManager) context.getSystemService(
                    Context.TELEPHONY_SERVICE);
            simNumber = tm.getLine1Number();
            isRoaming = tm.isNetworkRoaming();
            timeAnswered = -1L; // reset
            return;
        }
        
        // answered
        if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) && timeStarted != -1L) {
            timeAnswered = System.currentTimeMillis();
            return;
        }
        
        // ended
        if (state.equals(TelephonyManager.EXTRA_STATE_IDLE) && timeStarted != -1L) {
            timeEnded = System.currentTimeMillis();
            final String uuid = context.getSharedPreferences(This.PREFS,
                    Context.MODE_MULTI_PROCESS).getString(This.KEY_UUID, This.NULL);
            new Thread() {
                public void run() {
                    Logger.d("STARTED: InCallLogger");
                    InCallLog callLog = new InCallLog(uuid, timeStarted,
                            timeAnswered, timeEnded, isRoaming, number, simNumber);
                    DB db = DB.getInstance(context);
                    db.addCallLog(callLog);
                    Logger.d("added: " + new Gson().toJson(callLog));
                    
                    // reset timeStarted to -1 because android may invoke this
                    // state even when the call is outgoing and without going
                    // through EXTRA_STATE_RINGING first
                    timeStarted = -1L;
                    Logger.d("FINISHED: InCallLogger");
                };
            }.start();
            return;
        }
    }

}
