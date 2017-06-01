package com.lakala.core.swiper.Adapter;

import android.content.Context;

import com.lakala.core.swiper.ESwiperType;
import com.lakala.core.swiper.SwiperDefine;
import com.lakala.cswiper2.CSwiperController;

/**
 * Created by Vinchaos api on 14-1-3.
 */
public class SwiperAdapterQ202 extends SwiperAdapter implements CSwiperController.CSwiperStateChangedListener {
    private CSwiperController controller;
    private SwiperAdapterListener listener;

    public SwiperAdapterQ202(Context context) {
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
            controller.startCSwiper();
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
        return ESwiperType.Q202;
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
        if (listener == null) return;
        listener.onDevicePlugged();
    }

    @Override
    public void onDeviceUnplugged() {
        if (listener == null) return;
        listener.onDeviceUnplugged();
    }

    @Override
    public void onWaitingForDevice() {
        if (listener == null) return;
        listener.onWaitingForDevice();
    }

    @Override
    public void onNoDeviceDetected() {
        if (listener == null) return;
        listener.onNoDeviceDetected();
    }

    @Override
    public void onWaitingForCardSwipe() {
        if (listener == null) return;
        listener.onWaitingForCardSwipe();
    }

    @Override
    public void onCardSwipeDetected() {
        if (listener == null) return;
        listener.onCardSwipeDetected();
    }

    @Override
    public void onDecodingStart() {
        if (listener == null) return;
        listener.onDecodingStart();
    }

    @Override
    public void onError(int errorCode, String errorMessage) {
        if (listener == null) return;
        listener.onError(errorCode, errorMessage);
    }

    @Override
    public void onInterrupted() {
        if (listener == null) return;
        listener.onInterrupted();
    }

    @Override
    public void onTimeout() {
        if (listener == null) return;
        listener.onTimeout();
    }

    @Override
    public void onDecodeCompleted(String formatID,
                                  String ksn,
                                  String encTracks,
                                  int track1Length,
                                  int track2Length,
                                  int track3Length,
                                  String randomNumber,
                                  String maskedPANString,
                                  String expiryDate,
                                  String cardHolderName) {
        if (listener == null) return;
        listener.onDecodeCompleted(formatID, ksn, encTracks, track1Length, track2Length, track3Length, randomNumber, maskedPANString, expiryDate, cardHolderName);
    }

    @Override
    public void onDecodeError(CSwiperController.DecodeResult decodeResult) {
        if (listener == null) return;
        listener.onDecodeError(SwiperDefine.SwiperControllerDecodeResult.valueOf(decodeResult.toString()));
    }


}
