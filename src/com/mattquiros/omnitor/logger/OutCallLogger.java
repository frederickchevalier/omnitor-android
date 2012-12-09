package com.mattquiros.omnitor.logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.mattquiros.omnitor.DB;
import com.mattquiros.omnitor.bean.OutCallLog;
import com.mattquiros.omnitor.util.Logger;

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
                        Logger.d("STARTED: OutCallLogger");
                        timeStarted = System.currentTimeMillis();
                        simNumber = tm.getLine1Number();
                        isRoaming = tm.isNetworkRoaming();
                        return;
                    }
                    
                    if (state == TelephonyManager.CALL_STATE_IDLE && timeStarted != -1L) {
                        timeEnded = System.currentTimeMillis();
                        new Thread() {
                            public void run() {
                                DB.getInstance(context).addCallLog(new OutCallLog(
                                        timeStarted, timeEnded, isRoaming, number, simNumber));
                                // reset to -1 because android may invoke this call state
                                // even when the call hasn't even started yet
                                timeStarted = -1L;
                            };
                        }.start();
                    }
                }
            }, PhoneStateListener.LISTEN_CALL_STATE);
            noCallListenerYet = false;
        }
    }

}
