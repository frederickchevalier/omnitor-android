package com.mattquiros.omnitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.telephony.TelephonyManager;

import com.mattquiros.omnitor.logger.DataLogger;
import com.mattquiros.omnitor.logger.InSmsLogger;
import com.mattquiros.omnitor.logger.OutSmsLogger;
import com.mattquiros.omnitor.util.Logger;
import com.mattquiros.omnitor.util.This;

public class GeneralBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Logger.d("GeneralBroadcastReceiver received action: " + action);
                
        if (action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            new InSmsLogger(context, intent.getExtras()).start();
            return;
        }
        
        if (action.equals(This.ACTION_INITIAL_UPLOAD)) {
            new InitialUploadThread(context).start();
            return;
        }
        
        if (action.equals(This.ACTION_CHECK_OUT_SMS)) {
            new OutSmsLogger(context).start();
            return;
        }
        
        if (action.equals(This.ACTION_LOG_DATA)) {
            new DataLogger(context).start();
            return;
        }
        
        if (action.equals(This.ACTION_UPLOAD_LOGS)) {
            new Uploader(context).start();
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
        
        // UNTESTED CODE: detecting change in network roaming state
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            boolean newRoamingState = tm.isNetworkRoaming();
            boolean oldRoamingState = context.getSharedPreferences(This.PREFS,
                    Context.MODE_MULTI_PROCESS).getBoolean(This.KEY_ROAMING_STATE, newRoamingState);
            
            if (newRoamingState != oldRoamingState) {
                Logger.d("DETECTED roaming state change: " + oldRoamingState + " to " + newRoamingState);
                new OutSmsLogger(context, oldRoamingState).start();
                if (TrafficStats.getMobileRxBytes() != TrafficStats.UNSUPPORTED) {
                    new DataLogger(context, oldRoamingState).start();
                }
            }
            return;
        }
    }

}
