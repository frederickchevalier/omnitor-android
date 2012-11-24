package com.mattquiros.omnitor;

import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.mattquiros.omnitor.util.This;

public class MainActivity extends Activity {
    
    private Toast signupToast;
    
    @TargetApi(11)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            getActionBar().hide();
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = getSharedPreferences(This.PREFS, MODE_PRIVATE);
        Editor editor = prefs.edit();
        
        if (prefs.getBoolean(This.KEY_SIGNED_UP, false)) {
            
        } else {
            setContentView(R.layout.main);
        }
        
        if (prefs.getBoolean(This.KEY_FIRST_RUN, true)) {
            boolean roaming = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).isNetworkRoaming();
            long currentTime = System.currentTimeMillis();
            
            editor.putBoolean(This.KEY_FIRST_RUN, false);
            editor.putString(This.KEY_UUID, UUID.randomUUID().toString());
            editor.putBoolean(This.KEY_ROAMING_STATE, roaming);
            editor.putLong(This.KEY_TIME_FIRST_RUN, currentTime);
            editor.putLong(This.KEY_TIME_LAST_CHECKED_OUT_SMS, currentTime);
            editor.commit();
            
            Intent initialUpload = new Intent(this, InitialUploadService.class);
            startService(initialUpload);
        }
    }
    
    public void go(View view) {
        String email = ((EditText) findViewById(R.id.main_email)).getText().toString();
        String toastMessage = null;
        
        if (email.equals("") || !email.matches("[^\\s]+@[^\\s]+([.][^\\s^\\.]+)+")) {
            toastMessage = getString(R.string.signup_error_email);
            if (signupToast != null) {
                signupToast.setText(toastMessage);
            }
        }
        
        if (toastMessage == null) {
            new SignUpTask(this, email, signupToast).execute();
        } else {
            if (signupToast == null) {
                signupToast = Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT);
            }
            signupToast.show();
        }
    }
    
}
