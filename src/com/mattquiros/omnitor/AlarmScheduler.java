package com.mattquiros.omnitor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;

import com.mattquiros.omnitor.util.Logger;
import com.mattquiros.omnitor.util.This;

public class AlarmScheduler extends Thread {
    
    private static final long A = 1L;
    private static final long B = 1L;
    private static final long C = 1L;
    
    private Context parent;
    private boolean firstRun;
    
    public AlarmScheduler(Context parent, boolean firstRun) {
        this.parent = parent;
        this.firstRun = firstRun;
    }
    
    @Override
    public void run() {
        Logger.d("STARTED: AlarmScheduler");
        AlarmManager am = (AlarmManager) parent.getSystemService(Context.ALARM_SERVICE);
        PendingIntent outSmsLogger, dataLogger, uploader;
        outSmsLogger = PendingIntent.getBroadcast(parent, 0, new Intent(This.ACTION_CHECK_OUT_SMS), 0);
        uploader = PendingIntent.getBroadcast(parent, 0, new Intent(This.ACTION_UPLOAD_LOGS), 0);
        long currentTime = System.currentTimeMillis();
        am.setRepeating(AlarmManager.RTC_WAKEUP, currentTime + 60000L * A, 60000L * A, outSmsLogger);
        Logger.d("scheduled outgoing SMS logger");
        am.setRepeating(AlarmManager.RTC_WAKEUP, currentTime + 60000L * B, 60000L * B, uploader);
        Logger.d("scheduled uploader");
        
        if (TrafficStats.getMobileRxBytes() != TrafficStats.UNSUPPORTED) {
            Intent initialDataLogging = new Intent(This.ACTION_LOG_DATA);
            if (firstRun) {
                parent.sendBroadcast(initialDataLogging);
            }
            dataLogger = PendingIntent.getBroadcast(parent, 0, initialDataLogging, 0);
            am.setRepeating(AlarmManager.RTC_WAKEUP, currentTime + 60000L * C, 60000L * C, dataLogger);
            Logger.d("scheduled data logger");
        }
        Logger.d("FINISHED: AlarmScheduler");
    }
    
}
