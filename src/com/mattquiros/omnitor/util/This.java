package com.mattquiros.omnitor.util;

public abstract class This {
    
    public static final String PREFS = "omnitor.prefs";
    public static final String LOG_TAG = "omnitor-android";
    public static final String NULL = "null";
    public static final long DEFAULT_LONG = -1;
    public static final String URL = "https://api.mongolab.com/api/1/databases/omnitor-android" +
            "/collections/logs/?apiKey=" + API.key;
    
    public static final String KEY_FIRST_RUN = "fr";
    public static final String KEY_SIGNED_UP = "su";
    public static final String KEY_UUID = "uid";
    public static final String KEY_ROAMING_STATE = "rs";
    public static final String KEY_TIME_FIRST_RUN = "frt";
    public static final String KEY_TIME_LAST_CHECKED_OUT_SMS = "tos";
    public static final String KEY_DEVICE_LOG = "dl";
    
}
