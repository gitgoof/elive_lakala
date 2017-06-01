package com.lakala.core.swiper.Detector;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

import com.lakala.core.swiper.SwiperDefine;


/**
 * Created by Vinchaos api on 14-1-4.
 */
public class SwiperDetectorAudio extends SwiperDetector implements android.os.Handler.Callback {
    /**
     * 物理设备插入
     */
    private final static int HM_PHYSICAL_DEVICE_INSERTED = 201;
    /**
     * 物理设备拔出
     */
    private final static int HM_PHYSICAL_DEVICE_REMOVED = 202;
    /**
     * 耳机设备是否已经插入*
     */
    private boolean headsetIsDevicePresent = false;

    private boolean isDestroyed = false;
    private SwiperDetectorListener listener;
    private Context context;
    private Handler handler;
    private boolean isStartUp;

    public SwiperDetectorAudio(Context context) {
        handler = new Handler(this);
        this.context = context;
    }

    @Override
    public boolean isStartup() {
        return isStartUp;
    }

    @Override
    public void start() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);  //注册耳机插拔事件监听器
        this.context.registerReceiver(this, filter);//注册广播接受者
        isDestroyed = false;
        isStartUp = true;
    }

    @Override
    public void stop() {
        try {
            this.context.unregisterReceiver(this);
        } catch (Exception e) {}
        isDestroyed = true;
        isStartUp = false;
    }

    @Override
    public boolean isConnected() {
        return headsetIsDevicePresent;
    }

    @Override
    public SwiperDefine.SwiperPortType getSwiperPortType() {
        return SwiperDefine.SwiperPortType.TYPE_AUDIO;
    }

    @Override
    public void setListener(SwiperDetectorListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", 0); // 0 代表拔出，1代表插入
            int microphone = intent.getIntExtra("microphone", 0); //是否有麦克,0没有

            if (state == 1) {
                //插入设备
                if (microphone == 0) {
                    //没有麦克
                    headsetIsDevicePresent = false;
                } else {
                    //插入了一个耳麦设备
                    headsetIsDevicePresent = true;
                }
            } else {
                //拨出设备
                headsetIsDevicePresent = false;
            }
            //发送物理设备插拨事件
            raisePhysicalDevicePlugEvents();
        }
    }

    /**
     * 发送物理设备插拨事件
     */
    private void raisePhysicalDevicePlugEvents() {
        if (isDestroyed || handler == null)
            return;

        Message msg = handler.obtainMessage();
        msg.obj = SwiperDefine.SwiperPortType.TYPE_AUDIO;

        if (headsetIsDevicePresent)
            msg.what = HM_PHYSICAL_DEVICE_INSERTED;
        else
            msg.what = HM_PHYSICAL_DEVICE_REMOVED;

        //发送物理设备插拨事件
        handler.sendMessage(msg);

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            //物理设备插入
            case HM_PHYSICAL_DEVICE_INSERTED:
                listener.onConnected(this);
                break;
            //物理设备移除
            case HM_PHYSICAL_DEVICE_REMOVED:
                listener.onDisconnected(this);
                break;
        }
        return true;
    }
}
