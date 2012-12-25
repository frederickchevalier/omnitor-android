package com.mattquiros.omnitor;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.mattquiros.omnitor.bean.DeviceLog;
import com.mattquiros.omnitor.util.Logger;
import com.mattquiros.omnitor.util.This;

public class InitialUploadThread extends Thread {
    
    private Context context;
    
    public InitialUploadThread(Context context) {
        this.context = context;
    }
    
    public void run() {
        Logger.d("STARTED: InitialUploadThread");
        SharedPreferences prefs = context.getSharedPreferences(This.PREFS, Context.MODE_MULTI_PROCESS);
        Editor editor = prefs.edit();
        String uuid = prefs.getString(This.KEY_UUID, This.NULL);
        String deviceLog = prefs.getString(This.KEY_DEVICE_LOG, null);
        if (deviceLog == null) {
            deviceLog = new Gson().toJson(new DeviceLog(uuid,
                    android.os.Build.MANUFACTURER,
                    android.os.Build.MODEL,
                    prefs.getLong(This.KEY_TIME_FIRST_RUN, This.DEFAULT_LONG)));
            editor.putString(This.KEY_DEVICE_LOG, deviceLog);
            editor.commit();
        }
        
        Logger.d("deviceLog: " + deviceLog);
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(This.URL);
        post.setHeader("content-type", "application/json");
        try {
            post.setEntity(new StringEntity(deviceLog));
            HttpResponse response = client.execute(post);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == HttpStatus.SC_OK) {
                Logger.d("Initial upload successful! Removing device log from phone...");
                editor.remove(This.KEY_DEVICE_LOG);
                editor.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.d("Initial upload failed. Rescheduling...");
            
            // retry in 1 minute
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(This.ACTION_INITIAL_UPLOAD), 0);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000L * 1L, pi);
        } finally {
            Logger.d("FINISHED: InitialUploadThread");
        }
    }
    
}
