package com.lakala.core.swiper.Adapter;

import android.content.Context;

import com.lakala.core.swiper.ESwiperType;
import com.lakala.core.swiper.SwiperDefine;
import com.lakala.cswiper6.CSwiperController;
import com.lakala.library.util.LogUtil;

/**
 * Created by Vinchaos api on 14-1-4.
 */
public class SwiperAdapterQV30E extends SwiperCollectionAdapter implements CSwiperController.CSwiperStateChangedListener {

    private CSwiperController controller;
    private SwiperAdapterListener listener;

    public SwiperAdapterQV30E(Context context) {
        controller = new CSwiperController(context, this);
    }

    /**
     * SwiperCollectionAdapter
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
        return ESwiperType.QV30E;
    }

    @Override
    public void setListener(SwiperAdapterListener listener) {
        this.listener = listener;
    }

    @Override
    public void startSwiper(String amount) {
        if (controller == null) return;
        if (controller.getCSwiperState() == CSwiperController.CSwiperControllerState.STATE_IDLE)
            controller.startCSwiper(amount);
    }

    @Override
    public void startInputPIN() {
        if (controller == null) return;

        controller.startInputPIN();

    }

    @Override
    public void setStartParameter(int index, Object data) {
        if (controller == null) return;
        controller.setStartParameter(index, data);
    }

    @Override
    public void resetScreen() {
        if (controller == null) return;
        controller.resetScreen();
    }

    /**
     * CSwiperController.CSwiperStateChangedListener
     */
    @Override
    public void onInterrupted() {
        if (listener == null) return;
        listener.onInterrupted();
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
    public void onWaitingForPinEnter() {
        if (listener == null || !(listener instanceof SwiperCollectionAdapterListener)) return;
        ((SwiperCollectionAdapterListener) listener).onWaitingForPinEnter();
    }

    @Override
    public void onWaitingForDevice() {
        if (listener == null) return;
        listener.onWaitingForDevice();
    }

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
    public void onError(int errorCode, String errorMessage) {
        if (listener == null) return;
        listener.onError(errorCode, errorMessage);
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
    public void onPinInputCompleted(String randomNumber, String PIN, int length) {
        if (listener == null || !(listener instanceof SwiperCollectionAdapterListener)) return;
        ((SwiperCollectionAdapterListener) listener).onPinInputCompleted(randomNumber, PIN, length, null, null);
    }

    @Override
    public void onDecodeError(CSwiperController.DecodeResult decodeResult) {
        if (listener == null) return;
        listener.onDecodeError(SwiperDefine.SwiperControllerDecodeResult.valueOf(decodeResult.toString()));
    }

    @Override
    public void onDecodingStart() {
        if (listener == null) return;
        listener.onDecodingStart();
    }

    @Override
    public void onCardSwipeDetected() {
        if (listener == null) return;
        listener.onCardSwipeDetected();
    }

    @Override
    public void onResetScreenCompleted() {
        if (listener == null || !(listener instanceof SwiperCollectionAdapterListener)) return;
        ((SwiperCollectionAdapterListener) listener).onResetScreenCompleted();
    }


}
