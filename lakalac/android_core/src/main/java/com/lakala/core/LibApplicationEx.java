package com.lakala.core;

import android.app.Application;

import com.lakala.library.DBHelper;

/**
 * 扩展Application类，挂载具有全局生命周期的一些参数
 * Created by xyz on 13-12-10.
 */
public class LibApplicationEx extends Application{

    private static LibApplicationEx instance;

    public static LibApplicationEx getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        onCreateStepOne();
        onCreateStepTwo();
    }


    protected void onCreateStepOne(){

    }

    protected void onCreateStepTwo(){
        DBHelper.getInstance(this).loadLibs();
    }
}
