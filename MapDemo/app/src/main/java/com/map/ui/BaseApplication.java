package com.map.ui;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.facebook.stetho.Stetho;

/**
 * Created by xiaogu on 2017/3/14.
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //百度地图初始化
        SDKInitializer.initialize(this);
        //facebook调试工具
        Stetho.initializeWithDefaults(this);
    }
}
