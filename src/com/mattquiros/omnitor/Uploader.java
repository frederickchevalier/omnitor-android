package com.mattquiros.omnitor;

import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.mattquiros.omnitor.bean.JsonLog;
import com.mattquiros.omnitor.util.Logger;
import com.mattquiros.omnitor.util.This;

public class Uploader extends Thread {
    
    private Context context;
    
    public Uploader(Context context) {
        this.context = context;
    }
    
    @Override
    public void run() {
        Logger.d("STARTED: Uploader");
        DB db = DB.getInstance(context);
        SQLiteDatabase lock = db.getReadableDatabase();
        lock.beginTransaction();
        List<JsonLog> logs = db.getAllLogs();
        String json = new Gson().toJson(logs);
        
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(This.URL);
        post.setHeader("content-type", "application/json");
        try {
            Logger.d("to upload: " + json);
            StringEntity se = new StringEntity(json);
            post.setEntity(se);
            StatusLine status = client.execute(post).getStatusLine();
            if (status.getStatusCode() == HttpStatus.SC_OK) {
                db.clearTables();
            } else {
                Logger.d("Uploader failed. " + status.getStatusCode() + ": " + status.getReasonPhrase());
            }
            lock.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }
        lock.endTransaction();
        Logger.d("FINISHED: Uploader");
    }
    
}
