package com.mattquiros.omnitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.mattquiros.omnitor.util.Logger;
import com.mattquiros.omnitor.util.This;

public class GeneralBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Logger.d("GeneralBroadcastReceiver received action: " + action);
        
        if (action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            return;
        }
        
        if (action.equals(This.ACTION_CHECK_OUT_SMS)) {
            return;
        }
        
        if (action.equals(This.ACTION_LOG_DATA)) {
            return;
        }
        
        if (action.equals(This.ACTION_UPLOAD_LOGS)) {
            return;
        }
        
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            // avoid starting the alarm scheduler if the app hasn't even been run yet
            SharedPreferences prefs = context.getSharedPreferences(
                    This.PREFS, Context.MODE_MULTI_PROCESS);
            if (!prefs.getBoolean(This.KEY_FIRST_RUN, true)) {
                new AlarmScheduler(context, false).start();
            }
            return;
        }
    }

}