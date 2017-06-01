package com.lakala.elive.user.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.lakala.elive.Session;


public class BaseFragmentActivity extends FragmentActivity {

    /** 应用Session */
    protected Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = getApplicationContext();
        mSession = Session.get(context);
    }


    @Override
    protected void onResume() {
        super.onResume();
        final Context context = getApplicationContext();
        mSession = Session.get(context);
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
