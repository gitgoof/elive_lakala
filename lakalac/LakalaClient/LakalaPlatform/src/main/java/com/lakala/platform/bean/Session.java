package com.lakala.platform.bean;

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

import com.lakala.core.swiper.ICCardInfo;
import com.lakala.core.swiper.SwiperDefine;
import com.lakala.platform.common.AppUpgradeController;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.platform.swiper.devicemanager.controller.SwipeDefine;
import com.lakala.platform.swiper.devicemanager.controller.SwiperManager;
import com.lakala.platform.swiper.devicemanager.controller.SwiperManagerListener;
import com.newland.mtype.module.common.emv.EmvTransInfo;

import java.util.List;

/**
 * 会话相关bean
 * <p/>
 * Created by jerry on 14-1-10.
 */
public class Session {

    /** 用户是否登录标记 */
    private boolean isUserLogin;

    /** 实名认证时间 */
    private long realnameTime;

    /** 当前刷卡器ksn */
    private String currentKSN;

    private boolean isFromPosApply;//是否来自pos申请

    /** 用户信息 */
    private User user = new User();

    public boolean isUserLogin() {
        return isUserLogin;
    }

    public void setUserLogin(boolean isUserLogin) {
        this.isUserLogin = isUserLogin;
    }

    public String getCurrentKSN() {
        return currentKSN;
    }

    public void setCurrentKSN(String currentKSN) {
        this.currentKSN = currentKSN;
    }

    public long getRealnameTime() { return realnameTime; }

    public void setRealnameTiem(long realnameTime) { this.realnameTime = realnameTime; }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isFromPosApply() {
        return isFromPosApply;
    }

    public void setFromPosApply(boolean isFromPosApply) {
        this.isFromPosApply = isFromPosApply;
    }

    public String getCurrentLineNo() {

        if(!TextUtils.isEmpty(currentKSN)){
            return TerminalKey.getLineNo(currentKSN);
        }
        return "";
    }


    public void clear(){
        setUserLogin(false);
        TerminalKey.clear();
        setCurrentKSN("");
        setRealnameTiem(0);
//        AppUpgradeController.getInstance().setAppUpdate(false);
        //清除用户登陆数据
        user = new User();
        SwiperManager.getInstance(new SwiperManagerListener() {
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
            public void onPinInputCompleted(String randomNumber, String pin, int length, boolean isCommandProVerTwo, byte[] macRandom, byte[] mac) {

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
            public void icCardDemotionUsed() {

            }

            @Override
            public void onWaitingForPinEnter() {

            }

            @Override
            public void onQPBOCFinished(ICCardInfo icCardInfo) {

            }

            @Override
            public void onQPBOCDenied() {

            }
        }).disconnect();
    }
}
