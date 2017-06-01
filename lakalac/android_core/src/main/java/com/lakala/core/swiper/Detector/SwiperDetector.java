package com.lakala.core.swiper.Detector;

import android.content.BroadcastReceiver;
import android.content.Context;

import com.lakala.core.swiper.SwiperDefine;

/**
 * Created by Vinchaos api on 14-1-4.
 */
public abstract class SwiperDetector extends BroadcastReceiver {

    public static SwiperDetector initWithType(SwiperDefine.SwiperPortType type,Context context) {
        switch (type) {
            case TYPE_AUDIO:
                return new SwiperDetectorAudio(context);
            case TYPE_WIFI:
                return new SwiperDetectorWIFI(context);
            case TYPE_BLUETOOTH:
                return new SwiperDetectorBluetooth(context);
            case TYPE_LKLMOBILE:
                return new SwiperDetectorLKLMobile(context);
        }
        return null;
    }

    public abstract boolean isStartup();

    public abstract void start();

    public abstract void stop();

    public abstract boolean isConnected();

    public abstract SwiperDefine.SwiperPortType getSwiperPortType();

    public abstract void setListener(SwiperDetectorListener listener);
}
