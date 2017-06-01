package com.lakala.elive.market.base;

import android.app.Activity;
import android.os.Bundle;


/**
 * 
 * User Activity基础类放一些公共的方法
 * 
 */
public abstract class BaseActivity extends Activity {

    /** 应用Session */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

}
