package com.lakala.platform.swiper.devicemanager.connection;

import android.bluetooth.BluetoothDevice;

import com.lakala.core.swiper.ICCardInfo;
import com.lakala.core.swiper.SwiperDefine;
import com.lakala.platform.swiper.devicemanager.controller.SwipeDefine;
import com.lakala.platform.swiper.devicemanager.controller.SwiperManagerListener;
import com.newland.mtype.module.common.emv.EmvTransInfo;

import java.util.List;

/**
 * Created by More on 15/1/15.
 */
public class SwiperDetection implements SwiperManagerListener {


    @Override
    public void onTimeOut() {

    }

    @Override
    public void onWaitingForSwipe() {

    }

    @Override
    public void onSwipeSuccess(String encTracks, String randomNumber, String maskedPANString, SwipeDefine.SwipeKeyBoard swipeKeyBoard) {

    }

    @Override
    public void onReadICCardCompleted(ICCardInfo icCardInfo, SwipeDefine.SwipeKeyBoard swipeKeyBoard) {

    }

    @Override
    public void onPinInputCompleted(String randomNumber, String pin, int length, boolean bool, byte[] b, byte[] c) {

    }

    @Override
    public void onEmvFinished(boolean b, ICCardInfo emvTransInfo) {

    }

    @Override
    public void onSwipeError() {

    }

    @Override
    public void onDeviceConnected(SwiperDefine.SwiperPortType type) {

    }

    @Override
    public void onDeviceDisconnected(SwiperDefine.SwiperPortType type) {

    }

    @Override
    public void otherError(int errorCode, String errorMessage) {

    }

    @Override
    public void onCurrentDisconnected() {

    }

    @Override
    public void onCurrentConnected() {

    }

    @Override
    public void onNoDeviceDetected() {

    }

    @Override
    public void onInterrupted() {

    }

    @Override
    public void deviceAddressList(List<BluetoothDevice> macs, BluetoothDevice newMacs) {

    }

    @Override
    public void onFallback() {

    }

    @Override
    public void onCardSwipeDetected() {

    }

    @Override
    public void onWaitingForPinEnter() {

    }

    @Override
    public void icCardDemotionUsed() {

    }

    @Override
    public void onQPBOCFinished(ICCardInfo icCardInfo) {

    }

    @Override
    public void onQPBOCDenied() {

    }
}
