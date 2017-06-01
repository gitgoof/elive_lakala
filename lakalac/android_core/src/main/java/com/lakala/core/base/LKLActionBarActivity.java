package com.lakala.core.base;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.lakala.core.launcher.ActivityLauncher;

/**
 * Created by wangchao on 14/11/11.
 */
public abstract class LKLActionBarActivity extends ActionBarActivity {

    private Object TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaultOrientationPortrait();
        Intent intent = getIntent();
        if (intent != null){
            String title = intent.getStringExtra(ActivityLauncher.INTENT_PARAM_KEY_TITLE);
            if (title != null){
                this.setTitle(title);
            }
        }

        if(delegate() != null){
            delegate().onCreate(this, savedInstanceState);
        }
    }

    protected void setDefaultOrientationPortrait(){
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(delegate() != null){
            delegate().onStart(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(delegate() != null){
            delegate().onResume(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(delegate() != null){
            delegate().onPause(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(delegate() != null){
            delegate().onStop(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(delegate() != null){
            delegate().onDestroy(this);
        }
    }

    protected abstract LKLActivityDelegate delegate();

    public Object getTAG() {
        return TAG;
    }

    public void setTAG(Object TAG) {
        this.TAG = TAG;
    }
}
