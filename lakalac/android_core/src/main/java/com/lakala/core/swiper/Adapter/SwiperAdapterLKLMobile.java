package com.lakala.core.swiper.Adapter;

import android.content.Context;

import com.lakala.core.swiper.ESwiperType;
import com.lakala.core.swiper.SwiperDefine;
import com.lakala.lphone.CSwiperController;

/**
 * Created by wangchao on 14-3-5.
 */
public class SwiperAdapterLKLMobile extends SwiperAdapter implements CSwiperController.CSwiperStateChangedListener {

    private CSwiperController controller;
    private SwiperAdapterListener listener;

    public SwiperAdapterLKLMobile(Context context) {
        controller = new CSwiperController(context, this);
    }

    /**
     * SwiperAdapter
     */
    @Override
    public boolean isDevicePresent() {
        if (controller == null) return false;
        return controller.isDevicePresent();
    }

    @Override
    public void startSwiper() {
        if (controller == null) return;
        if (controller.getCSwiperState() == CSwiperController.CSwiperControllerState.STATE_IDLE)
            try {
                controller.startCSwiper();
            } catch (CSwiperController.IllegalStateException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void stopSwiper() {
        if (controller == null) return;
        controller.stopCSwiper();
    }

    @Override
    public void deleteSwiper() {
        if (controller == null) return;
        controller.deleteCSwiper();
    }

    @Override
    public SwiperDefine.SwiperControllerState getCSwiperState() {
        if (controller == null) return null;
        return SwiperDefine.SwiperControllerState.valueOf(controller.getCSwiperState().toString());
    }

    @Override
    public String getKSN() {
        if (controller == null) return "";
        return controller.getCSwiperKsn();
    }

    @Override
    public ESwiperType getSwiperType() {
        return ESwiperType.LKLMobile;
    }

    @Override
    public void setListener(SwiperAdapterListener listener) {
        this.listener = listener;
    }


    /**
     * CSwiperController.CSwiperStateChangedListener
     */
    @Override
    public void onDevicePlugged() {
        if(controller == null)return;
        listener.onDevicePlugged();
    }

    @Override
    public void onDeviceUnplugged() {
        if(controller == null) return;
        listener.onDeviceUnplugged();
    }

    @Override
    public void onWaitingForDevice() {
        if (controller == null) return;
        listener.onWaitingForDevice();
    }

    @Override
    public void onNoDeviceDetected() {
        if (controller == null) return;
        listener.onNoDeviceDetected();
    }

    @Override
    public void onWaitingForCardSwipe() {
        if(controller == null) return;
        listener.onWaitingForCardSwipe();
    }

    @Override
    public void onCardSwipeDetected() {
        if(controller == null) return;
        listener.onCardSwipeDetected();
    }

    @Override
    public void onDecodingStart() {
        if(controller == null) return;
        listener.onDecodingStart();
    }

    @Override
    public void onError(int i, String s) {
        if(controller == null) return;
        listener.onError(i, s);
    }

    @Override
    public void onInterrupted() {
        if(controller == null) return;
        listener.onInterrupted();
    }

    @Override
    public void onTimeout() {
        if(controller == null) return;
        listener.onTimeout();
    }

    @Override
    public void onDecodeCompleted(String s, String s2, String s3, int i, int i2, int i3, String s4, String s5, String s6, String s7) {
        if(controller == null) return;
        listener.onDecodeCompleted(s, s2, s3, i, i2, i3, s4, s5, s6, s7);
    }

    @Override
    public void onDecodeError(CSwiperController.DecodeResult decodeResult) {
        if(controller == null) return;
        listener.onDecodeError(SwiperDefine.SwiperControllerDecodeResult.valueOf(decodeResult.toString()));
    }

    @Override
    public void OnTestReport(int i, int i2, int i3, int i4) {

    }
}
