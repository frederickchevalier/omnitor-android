package com.mattquiros.omnitor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class HelloActivity extends Activity {
    
    @TargetApi(11)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            getActionBar().hide();
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hello);
    }
    
}
