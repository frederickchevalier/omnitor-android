package com.mattquiros.omnitor.logger;

import java.io.RandomAccessFile;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.TrafficStats;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;
import com.mattquiros.omnitor.DB;
import com.mattquiros.omnitor.bean.DataLog;
import com.mattquiros.omnitor.util.Logger;
import com.mattquiros.omnitor.util.This;

public class DataLogger extends Thread {
    
    private Context context;
    private long oldMobileTxBytes;
    private long oldMobileRxBytes;
    private long oldNetworkTxBytes;
    private long oldNetworkRxBytes;
    
    public DataLogger(Context context) {
        this.context = context;
    }
    
    @Override
    public void run() {
        Logger.d("STARTED: DataLogger");
        SharedPreferences prefs = context.getSharedPreferences(This.PREFS, Context.MODE_MULTI_PROCESS);
        Editor editor = prefs.edit();
        oldMobileTxBytes = prefs.getLong(This.KEY_MOBILE_TX, This.DEFAULT_LONG);
        boolean oldRoamingState = prefs.getBoolean(This.KEY_ROAMING_STATE,
                ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).isNetworkRoaming());
        String uuid = prefs.getString(This.KEY_UUID, This.NULL);
        
        // if it's not the first run
        if (oldMobileTxBytes != This.DEFAULT_LONG) {
            oldMobileRxBytes = prefs.getLong(This.KEY_MOBILE_RX, This.DEFAULT_LONG);
            oldNetworkTxBytes = prefs.getLong(This.KEY_NETWORK_TX, This.DEFAULT_LONG);
            oldNetworkRxBytes = prefs.getLong(This.KEY_NETWORK_RX, This.DEFAULT_LONG);
            
            long currentMobileTxBytes = TrafficStats.getMobileTxBytes();
            long currentMobileRxBytes = TrafficStats.getMobileRxBytes();
            long currentNetworkTxBytes = this.getNetworkTxBytes();
            long currentNetworkRxBytes = this.getNetworkRxBytes();
            
            long mobileSent = currentMobileTxBytes - oldMobileTxBytes < 0 ? 
                    currentMobileTxBytes : currentMobileTxBytes - oldMobileTxBytes;
            long mobileReceived = currentMobileRxBytes - oldMobileRxBytes < 0 ?
                    currentMobileRxBytes : currentMobileRxBytes - oldMobileRxBytes;
            long networkSent = currentNetworkTxBytes - oldNetworkTxBytes < 0 ?
                    currentNetworkTxBytes : currentNetworkTxBytes - oldNetworkTxBytes;
            long networkReceived = currentNetworkRxBytes - oldNetworkRxBytes < 0 ?
                    currentNetworkRxBytes : currentNetworkRxBytes - oldNetworkRxBytes;
            
            DataLog dataLog = new DataLog(uuid,
                    System.currentTimeMillis(),
                    mobileSent,
                    mobileReceived,
                    networkSent,
                    networkReceived,
                    oldRoamingState);
            DB.getInstance(context).addDataLog(dataLog);
            Logger.d("added: " + new Gson().toJson(dataLog));
            
            editor.putLong(This.KEY_MOBILE_TX, currentMobileTxBytes);
            editor.putLong(This.KEY_MOBILE_RX, currentMobileRxBytes);
            editor.putLong(This.KEY_NETWORK_TX, currentNetworkTxBytes);
            editor.putLong(This.KEY_NETWORK_RX, currentNetworkRxBytes);
            editor.commit();
        } else {
            oldMobileTxBytes = TrafficStats.getMobileTxBytes();
            oldMobileRxBytes = TrafficStats.getMobileRxBytes();
            oldNetworkTxBytes = this.getNetworkTxBytes();
            oldNetworkRxBytes = this.getNetworkRxBytes();
            
            editor.putLong(This.KEY_MOBILE_TX, oldMobileTxBytes);
            editor.putLong(This.KEY_MOBILE_RX, oldMobileRxBytes);
            editor.putLong(This.KEY_NETWORK_TX, oldNetworkTxBytes);
            editor.putLong(This.KEY_NETWORK_RX, oldNetworkRxBytes);
            editor.commit();
        }
        
        Logger.d("FINISHED: DataLogger");
    }
    
    private long getNetworkTxBytes() {
        String txFile = "sys/class/net/" + this.getWifiInterface() + "/statistics/tx_bytes";
        return readLongFromFile(txFile);
    }
    
    private long getNetworkRxBytes() {
        String rxFile = "sys/class/net/" + this.getWifiInterface() + "/statistics/rx_bytes";
        return readLongFromFile(rxFile);
    }
    
    private String getWifiInterface() {
        String wifiInterface = null;
        try {
            Class<?> system = Class.forName("android.os.SystemProperties");
            Method getter = system.getMethod("get", String.class);
            wifiInterface = (String) getter.invoke(null, "wifi.interface");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (wifiInterface == null || wifiInterface.length() == 0) {
            wifiInterface = "eth0";
        }
        return wifiInterface;
    }
    
    private long readLongFromFile(String filename) {
        RandomAccessFile f = null;
        try {
            f = new RandomAccessFile(filename, "r");
            String contents = f.readLine();
            if (contents != null && contents.length() > 0) {
                return Long.parseLong(contents);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (f != null) try { f.close(); } catch (Exception e) { e.printStackTrace(); }
        }
        return -1L;
    }

}