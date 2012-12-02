package com.mattquiros.omnitor;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mattquiros.omnitor.pojo.JsonLog;
import com.mattquiros.omnitor.pojo.SmsLog;

public class DB extends SQLiteOpenHelper {
    
    private static final String NAME = "omnitor-android-db";
    private static final int VERSION = 1;
    
    private static final String TABLE_SMSLOG = "SMSLOG";
    
    private static DB instance = null;
    
    private DB(Context context) {
        super(context, NAME, null, VERSION);
    }
    
    public static DB getInstance(Context context) {
        if (instance == null) {
            instance = new DB(context);
        }
        return instance;
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_SMSLOG = "CREATE TABLE " + TABLE_SMSLOG +
                "(UUID TEXT PRIMARY KEY, TYPE TEXT, NUMBER TEXT, SIM_NUMBER TEXT, " +
                "TIME INTEGER, LENGTH INTEGER, ROAMING NUMERIC)";
        db.execSQL(CREATE_TABLE_SMSLOG);
    }
    
    public void addSmsLogs(List<SmsLog> smsLogs) {
        SQLiteDatabase db = this.getWritableDatabase();
        InsertHelper ih = new InsertHelper(db, TABLE_SMSLOG);
        try {
            // get column indices
            int uuid = ih.getColumnIndex("UUID");
            int type = ih.getColumnIndex("TYPE");
            int number = ih.getColumnIndex("NUMBER");
            int sim_number = ih.getColumnIndex("SIM_NUMBER");
            int time = ih.getColumnIndex("TIME");
            int length = ih.getColumnIndex("LENGTH");
            int roaming = ih.getColumnIndex("ROAMING");
            
            db.beginTransaction();
            for (SmsLog s : smsLogs) {
                ih.prepareForInsert();
                ih.bind(uuid, s.getUuid());
                ih.bind(type, s.getType());
                ih.bind(number, s.getNumber());
                ih.bind(sim_number, s.getSim_number());
                ih.bind(time, s.getTime());
                ih.bind(length, s.getLength());
                ih.bind(roaming, s.isRoaming());
                ih.execute();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    
    private List<SmsLog> getSmsLogs() {
        List<SmsLog> smsLogs = new ArrayList<SmsLog>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SMSLOG, null);
        if (cursor.moveToFirst()) {
            do {
                smsLogs.add(new SmsLog(cursor.getString(cursor.getColumnIndex("TYPE")),
                        cursor.getString(cursor.getColumnIndex("NUMBER")),
                        cursor.getString(cursor.getColumnIndex("SIM_NUMBER")),
                        cursor.getLong(cursor.getColumnIndex("TIME")),
                        cursor.getInt(cursor.getColumnIndex("LENGTH")),
                        cursor.getInt(cursor.getColumnIndex("ROAMING")) == 1 ? true : false));
            } while (cursor.moveToNext());
        }
        return smsLogs;
    }
    
    public List<JsonLog> getAllLogs() {
        List<JsonLog> allLogs = new ArrayList<JsonLog>();
        allLogs.addAll(getSmsLogs());
        return allLogs;
    }
    
    public void clearTables() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_SMSLOG, null, null);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMSLOG);
        onCreate(db);
    }

}
