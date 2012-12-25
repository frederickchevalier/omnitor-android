package com.mattquiros.omnitor.logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;
import com.mattquiros.omnitor.DB;
import com.mattquiros.omnitor.bean.OutCallLog;
import com.mattquiros.omnitor.util.Logger;
import com.mattquiros.omnitor.util.This;

public class OutCallLogger extends BroadcastReceiver {

    private static long timeStarted = -1L;
    private static long timeEnded;
    private static boolean isRoaming;
    private static String number;
    private static String simNumber;
    private static boolean noCallListenerYet = true;
    
    @Override
    public void onReceive(final Context context, Intent intent) {
        number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        if (noCallListenerYet) {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(
                    Context.TELEPHONY_SERVICE);
            tm.listen(new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {                    
                    if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        timeStarted = System.currentTimeMillis();
                        simNumber = tm.getLine1Number();
                        isRoaming = tm.isNetworkRoaming();
                        return;
                    }
                    
                    if (state == TelephonyManager.CALL_STATE_IDLE && timeStarted != -1L) {
                        timeEnded = System.currentTimeMillis();
                        final String uuid = context.getSharedPreferences(This.PREFS,
                                Context.MODE_MULTI_PROCESS).getString(This.KEY_UUID, This.NULL);
                        new Thread() {
                            public void run() {
                                Logger.d("STARTED: OutCallLogger");
                                OutCallLog callLog = new OutCallLog(uuid,
                                        timeStarted, timeEnded, isRoaming, number, simNumber);
                                DB db = DB.getInstance(context);
                                db.addCallLog(callLog);
                                Logger.d("added: " + new Gson().toJson(callLog));
                                // reset to -1 because android may invoke this call state
                                // even when the call hasn't even started yet
                                timeStarted = -1L;
                                Logger.d("FINISHED: OutCallLogger");
                            };
                        }.start();
                    }
                }
            }, PhoneStateListener.LISTEN_CALL_STATE);
            noCallListenerYet = false;
        }
    }

}
