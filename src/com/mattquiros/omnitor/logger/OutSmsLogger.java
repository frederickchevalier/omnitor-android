package com.mattquiros.omnitor.logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;
import com.mattquiros.omnitor.DB;
import com.mattquiros.omnitor.bean.SmsLog;
import com.mattquiros.omnitor.util.Logger;
import com.mattquiros.omnitor.util.This;

public class OutSmsLogger extends Thread {
    
    private static final Uri SMS_URI = Uri.parse("content://sms");
    private static final String[] COLUMNS = new String[] {"date", "address", "body", "type"};
    private static final String WHERE = "type = 2";
    private static final String ORDER = "date DESC";
    
    private Context context;
    private SharedPreferences prefs;
    private TelephonyManager tm;
    private long timeLastChecked;
    private boolean roaming;
    
    public OutSmsLogger(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(This.PREFS, Context.MODE_MULTI_PROCESS);
        roaming = prefs.getBoolean(This.KEY_ROAMING_STATE, tm.isNetworkRoaming());
    }
    
    public OutSmsLogger(Context context, boolean roamingStateToUse) {
        this.context = context;
        prefs = context.getSharedPreferences(This.PREFS, Context.MODE_MULTI_PROCESS);
        roaming = roamingStateToUse;
    }
    
    @Override
    public void run() {
        Logger.d("STARTED: OutSmsLogger");
        timeLastChecked = prefs.getLong(This.KEY_TIME_LAST_CHECKED_OUT_SMS, This.DEFAULT_LONG);
        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String uuid = prefs.getString(This.KEY_UUID, This.NULL);
        
        Cursor cursor = context.getContentResolver().query(SMS_URI, COLUMNS,
                WHERE + " AND date > " + timeLastChecked, null, ORDER);
        Set<SmsLog> smsLogs = null;
        long date;
        String address, body, simNumber;
        SmsLog outSmsLog;
        if (cursor.moveToNext()) {
            smsLogs = new HashSet<SmsLog>();
            timeLastChecked = cursor.getLong(cursor.getColumnIndex("date"));
            simNumber = tm.getLine1Number();
            do {
                date = cursor.getLong(cursor.getColumnIndex("date"));
                address = cursor.getString(cursor.getColumnIndex("address"));
                body = cursor.getString(cursor.getColumnIndex("body"));
                outSmsLog = new SmsLog(uuid, This.TYPE_OUT_SMS,
                        address, simNumber, date, body.length(), roaming);
                
                if (smsLogs.contains(outSmsLog)) {
                    continue;
                }
                
                smsLogs.add(outSmsLog);
            } while (cursor.moveToNext());
        }
        
        if (smsLogs == null) {
            Logger.d("no sent SMSes found");
        } else {
            List<SmsLog> smsLogsList = new ArrayList<SmsLog>();
            smsLogsList.addAll(smsLogs);
            DB db = DB.getInstance(context);
            db.addSmsLogs(smsLogsList);
            Logger.d("added: " + new Gson().toJson(smsLogsList));
        }
        
        cursor.close();
        Editor editor = prefs.edit();
        editor.putLong(This.KEY_TIME_LAST_CHECKED_OUT_SMS, timeLastChecked);
        editor.commit();
        
        Logger.d("FINISHED: OutSmsLogger");
    }
    
}
