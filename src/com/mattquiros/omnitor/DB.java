package com.mattquiros.omnitor;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mattquiros.omnitor.bean.DataLog;
import com.mattquiros.omnitor.bean.InCallLog;
import com.mattquiros.omnitor.bean.JsonLog;
import com.mattquiros.omnitor.bean.OutCallLog;
import com.mattquiros.omnitor.bean.SmsLog;
import com.mattquiros.omnitor.util.This;

public class DB extends SQLiteOpenHelper {
    
    private static final String NAME = "omnitor-android-db";
    private static final int VERSION = 1;
    
    private static final String TABLE_SMS = "SMS";
    private static final String TABLE_IN_CALL = "IN_CALL";
    private static final String TABLE_OUT_CALL = "OUT_CALL";
    private static final String TABLE_DATA = "DATA";
    
    private static DB instance = null;
    private static String uuid = null;
    
    private DB(Context context) {
        super(context, NAME, null, VERSION);
        uuid = context.getSharedPreferences(This.PREFS,
                Context.MODE_MULTI_PROCESS).getString(This.KEY_UUID, This.NULL);
    }
    
    public static DB getInstance(Context context) {
        if (instance == null) {
            instance = new DB(context);
        }
        return instance;
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_SMS = "CREATE TABLE " + TABLE_SMS +
                "(UUID TEXT, TYPE TEXT, NUMBER TEXT, SIM_NUMBER TEXT, " +
                "TIME INTEGER, LENGTH INTEGER, ROAMING NUMERIC)";
        String CREATE_TABLE_IN_CALL = "CREATE TABLE " + TABLE_IN_CALL +
                "(UUID TEXT, TYPE TEXT, TIME_STARTED INTEGER, " +
                "TIME_ANSWERED INTEGER, TIME_ENDED INTEGER, ROAMING NUMERIC, " +
                "NUMBER TEXT, SIM_NUMBER TEXT)";
        String CREATE_TABLE_OUT_CALL = "CREATE TABLE " + TABLE_OUT_CALL +
                "(UUID TEXT, TYPE TEXT, TIME_STARTED INTEGER, " +
                "TIME_ENDED INTEGER, ROAMING NUMERIC, NUMBER TEXT, SIM_NUMBER TEXT)";
        String CREATE_TABLE_DATA = "CREATE TABLE " + TABLE_DATA +
                "(UUID TEXT, TYPE TEXT, TIME INTEGER, MOBILE_SENT INTEGER, " +
                "MOBILE_RECEIVED INTEGER, NETWORK_SENT INTEGER, NETWORK_RECEIVED INTEGER, " +
                "ROAMING NUMERIC)";
        db.execSQL(CREATE_TABLE_SMS);
        db.execSQL(CREATE_TABLE_IN_CALL);
        db.execSQL(CREATE_TABLE_OUT_CALL);
        db.execSQL(CREATE_TABLE_DATA);
    }
    
    public void addSmsLogs(List<SmsLog> smsLogs) {
        SQLiteDatabase db = getWritableDatabase();
        InsertHelper ih = new InsertHelper(db, TABLE_SMS);
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
    
    public void addCallLog(OutCallLog callLog) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("UUID", callLog.getUuid());
        values.put("TYPE", callLog.getType());
        values.put("TIME_STARTED", callLog.getTime_started());
        values.put("TIME_ENDED", callLog.getTime_ended());
        values.put("ROAMING", callLog.isRoaming());
        values.put("NUMBER", callLog.getNumber());
        values.put("SIM_NUMBER", callLog.getSim_number());
        if (callLog instanceof InCallLog) {
            values.put("TIME_ANSWERED", ((InCallLog) callLog).getTime_answered());
            db.insert(TABLE_IN_CALL, null, values);
        } else {
            db.insert(TABLE_OUT_CALL, null, values);
        }
    }
    
    public void addDataLog(DataLog dataLog) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("UUID", dataLog.getUuid());
        values.put("TYPE", dataLog.getType());
        values.put("TIME", dataLog.getTime());
        values.put("MOBILE_SENT", dataLog.getMobile_sent());
        values.put("MOBILE_RECEIVED", dataLog.getMobile_received());
        values.put("NETWORK_SENT", dataLog.getNetwork_sent());
        values.put("NETWORK_RECEIVED", dataLog.getNetwork_received());
        values.put("ROAMING", dataLog.isRoaming());
        db.insert(TABLE_DATA, null, values);
    }
    
    public List<JsonLog> getAllLogs() {
        List<JsonLog> allLogs = new ArrayList<JsonLog>();
        allLogs.addAll(getSmsLogs());
        allLogs.addAll(getCallLogs());
        allLogs.addAll(getDataLogs());
        return allLogs;
    }
    
    private List<JsonLog> getSmsLogs() {
        List<JsonLog> smsLogs = new ArrayList<JsonLog>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SMS, null);
        if (cursor.moveToFirst()) {
            do {
                smsLogs.add(new SmsLog(uuid, cursor.getString(cursor.getColumnIndex("TYPE")),
                    cursor.getString(cursor.getColumnIndex("NUMBER")),
                    cursor.getString(cursor.getColumnIndex("SIM_NUMBER")),
                    cursor.getLong(cursor.getColumnIndex("TIME")),
                    cursor.getInt(cursor.getColumnIndex("LENGTH")),
                    cursor.getInt(cursor.getColumnIndex("ROAMING")) == 1 ? true : false));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return smsLogs;
    }
    
    private List<JsonLog> getCallLogs() {
        List<JsonLog> callLogs = new ArrayList<JsonLog>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_IN_CALL, null);
        if (cursor.moveToFirst()) {
            do {
                callLogs.add(new InCallLog(uuid,
                        cursor.getLong(cursor.getColumnIndex("TIME_STARTED")),
                        cursor.getLong(cursor.getColumnIndex("TIME_ANSWERED")),
                        cursor.getLong(cursor.getColumnIndex("TIME_ENDED")),
                        cursor.getInt(cursor.getColumnIndex("ROAMING")) == 1 ? true : false,
                        cursor.getString(cursor.getColumnIndex("NUMBER")),
                        cursor.getString(cursor.getColumnIndex("SIM_NUMBER"))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        
        cursor = db.rawQuery("SELECT * FROM " + TABLE_OUT_CALL, null);
        if (cursor.moveToFirst()) {
            do {
                callLogs.add(new OutCallLog(uuid,
                        cursor.getLong(cursor.getColumnIndex("TIME_STARTED")),
                        cursor.getLong(cursor.getColumnIndex("TIME_ENDED")),
                        cursor.getInt(cursor.getColumnIndex("ROAMING")) == 1 ? true : false,
                        cursor.getString(cursor.getColumnIndex("NUMBER")),
                        cursor.getString(cursor.getColumnIndex("SIM_NUMBER"))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return callLogs;
    }
    
    private List<JsonLog> getDataLogs() {
        List<JsonLog> dataLogs = new ArrayList<JsonLog>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DATA, null);
        if (cursor.moveToFirst()) {
            do {
                dataLogs.add(new DataLog(uuid,
                        cursor.getLong(cursor.getColumnIndex("TIME")),
                        cursor.getLong(cursor.getColumnIndex("MOBILE_SENT")),
                        cursor.getLong(cursor.getColumnIndex("MOBILE_RECEIVED")),
                        cursor.getLong(cursor.getColumnIndex("NETWORK_SENT")),
                        cursor.getLong(cursor.getColumnIndex("NETWORK_RECEIVED")),
                        cursor.getInt(cursor.getColumnIndex("ROAMING")) == 1 ? true : false));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return dataLogs;
    }
    
    public void clearTables() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_SMS, null, null);
        db.delete(TABLE_IN_CALL, null, null);
        db.delete(TABLE_OUT_CALL, null, null);
        db.delete(TABLE_DATA, null, null);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IN_CALL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OUT_CALL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
        onCreate(db);
    }

}
