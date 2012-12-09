package com.mattquiros.omnitor.logger;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.mattquiros.omnitor.DB;
import com.mattquiros.omnitor.bean.SmsLog;
import com.mattquiros.omnitor.util.Logger;
import com.mattquiros.omnitor.util.This;

public class InSmsLogger extends Thread {
    
    private Context context;
    private Object[] pdus;
    
    public InSmsLogger(Context context, Bundle extras) {
        this.context = context;
        pdus = (Object[]) extras.get("pdus");
    }
    
    @Override
    public void run() {
        Logger.d("STARTED: InSmsLogger");
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        List<SmsLog> smsLogs = new ArrayList<SmsLog>();
        SmsMessage smsMessage;
        
        for (Object pdu : pdus) {
            smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
            smsLogs.add(new SmsLog(This.TYPE_IN_SMS, smsMessage.getOriginatingAddress(),
                    tm.getLine1Number(), System.currentTimeMillis(),
                    smsMessage.getMessageBody().length(), tm.isNetworkRoaming()));
        }
        
        DB.getInstance(context).addSmsLogs(smsLogs);
        Logger.printAll(context);
        Logger.d("FINISHED: InSmsLogger");
    }
    
}
