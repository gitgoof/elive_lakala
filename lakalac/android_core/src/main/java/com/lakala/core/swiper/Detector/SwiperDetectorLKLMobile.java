package com.lakala.core.swiper.Detector;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.lakala.core.swiper.SwiperDefine;

/**
 * Created by wangchao on 14-3-5.
 */
public class SwiperDetectorLKLMobile extends SwiperDetector{

    private static final String LKLMobile = "laphone";

    private boolean isStartUp;

    private SwiperDetectorListener listener;

    public SwiperDetectorLKLMobile(Context context){

    }

    @Override
    public boolean isStartup() {
        return isStartUp;
    }

    @Override
    public void start() {
        isStartUp = true;
    }

    @Override
    public void stop() {
        isStartUp = false;
    }

    @Override
    public boolean isConnected() {
        return Build.DEVICE.equals(LKLMobile);
    }

    @Override
    public SwiperDefine.SwiperPortType getSwiperPortType() {
        return SwiperDefine.SwiperPortType.TYPE_LKLMOBILE;
    }

    @Override
    public void setListener(SwiperDetectorListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
