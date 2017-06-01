package com.lakala.platform.swiper.devicemanager.controller;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;

import com.lakala.core.swiper.ESwiperType;
import com.lakala.core.swiper.ICCardInfo;
import com.lakala.core.swiper.ICFieldConstructor;
import com.lakala.core.swiper.SwiperDefine;
import com.lakala.library.exception.SwiperException;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.CommonEncrypt;
import com.lakala.platform.common.MutexThreadManager;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.platform.swiper.devicemanager.SwiperManagerCallback;
import com.lakala.platform.swiper.devicemanager.SwiperProcessState;
import com.lakala.platform.swiper.devicemanager.connection.base.BaseConnectionManager;
import com.lakala.platform.swiper.devicemanager.connection.base.ConnectionStateListener;
import com.newland.mtype.common.Const;
import com.newland.mtype.module.common.emv.SecondIssuanceRequest;
import com.newland.mtype.tlv.SimpleTLVPackage;
import com.newland.mtype.util.ISOUtils;

import org.apache.commons.codec.binary.Base64;

import java.util.List;

/**
 * Created by More on 14-4-22.
 * <p/>
 * Modify by More on 14-7-23.
 * <p/>
 * Make Sure swiper is connected and validated
 */
public class SwiperManagerHandler2 implements SwiperManagerListener {
    private final String TAG = SwiperManagerHandler2.class.getSimpleName();
    /**
     * 刷卡器过滤,只有匹配该类型的刷卡器才能被识别
     */
    private SwiperManager swiperManager;

    private String amount = null;

    private String type = null;

    private SwiperManagerCallback swiperManagerCallback;

    private SwiperProcessState swiperProcessState = SwiperProcessState.NORMAL;

    private boolean isPinInputSeparate = false;

    private boolean isResetable = true;

    private boolean isDoingSecIss = false;

    private boolean isOnlineHappen = false;

    private Handler mainLooperHandler = new Handler(Looper.getMainLooper());

    public SwiperInfo getSwiperInfo() {
        return swiperInfo;
    }

    public void setSwiperInfo(SwiperInfo swiperInfo) {
        this.swiperInfo = swiperInfo;
    }

    private SwiperInfo swiperInfo = new SwiperInfo();

    private Context context;

    private boolean isPbocSupported = true;


    public SwiperManagerHandler2(Context context) {
        swiperManager = SwiperManager.getInstance(this);
        isPinInputSeparate = false;
        isOnlineHappen = false;

        registerListener();
    }

    private BaseConnectionManager connectionManager;

    private ConnectionStateListener listener = new ConnectionStateListener() {
        @Override
        public void onSwiperTypeValidate() {
            swiperManager = SwiperManager.getInstance(SwiperManagerHandler2.this);
        }

        @Override
        public void onConnectionState(SwiperProcessState state) {
            LogUtil.print(getClass().getName(), "Connection State" + state);
            onProcessEvent(state);
            if(state == SwiperProcessState.SIGN_UP_SUCCESS){
                swiperManager = SwiperManager.getInstance(SwiperManagerHandler2.this);
                swiperManager.setListener(SwiperManagerHandler2.this);
            }
        }
    };

    public SwiperManagerHandler2(Context context, Class<? extends BaseConnectionManager>  connection){

        if(! (context instanceof Activity)){
            throw new RuntimeException("Context should be Activity");
        }

        try{
            this.connectionManager =  (BaseConnectionManager)connection.getConstructors()[0].newInstance(context, listener);
        }catch (Exception e){
            LogUtil.print(getClass().getName(), "Create new Instance failed :" + context == null ? "null" : "not null", e);
        }
        isPinInputSeparate = false;
        isOnlineHappen = false;
        registerListener();
    }

    public void checkConnection(Context context){


        if(isScreenOn && getSwiperProcessState() !=SwiperProcessState.PIN_INPUT_COMPLETE && getSwiperProcessState() != SwiperProcessState.EMV_FINISH
                && !isPinInputSeparate){
            if (connectionManager != null) {
                connectionManager.resume();
                connectionManager.checkConnection(context);

            } else {
                throw new RuntimeException("connectionManger hasn't init");
            }
        }

    }

    /**
     * 注册监听回调
     */
    public void registerListener() {
        swiperManager = SwiperManager.getInstance(this);
    }

    /**
     * 清除回调释放资源
     */
    public void destroy() {
        release();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    swiperManager.onDestroy();
                }catch (Exception e){
                    LogUtil.print(e);
                }
            }
        }).start();


    }

    /**
     * 释放监听,通知蓝牙搜索停止
     */
    public void release(){

        if(connectionManager != null){
            connectionManager.destroy();
            connectionManager = null;
        }

    }

    @Override
    public void onReadICCardCompleted(ICCardInfo icCardInfo, SwipeDefine.SwipeKeyBoard swipeKeyBoard) {
        LogUtil.print(TAG, " onRequestOnline and isPinInputSeparate is " + isPinInputSeparate);

        swiperInfo = new SwiperInfo();
        swiperInfo.setEmvTransInfo(icCardInfo.getEmvTransInfo());

        swiperInfo.setIcc55(icCardInfo.getIcc55());
        swiperInfo.setTrack2(icCardInfo.getTrack2());
        swiperInfo.setCardsn(icCardInfo.getCardSn());
        swiperInfo.setEncTracks("");
        swiperInfo.setRandomNumber("");
        swiperInfo.setMaskedPan(icCardInfo.getPan());
        swiperInfo.setCardType(SwiperInfo.CardType.ICCard);
        swiperInfo.setPosemc("1");
        swiperInfo.setIcinfo(icCardInfo.getIcinfo());

        onProcessEvent(SwiperProcessState.SWIPE_END);

        if (!isPinInputSeparate) {
            startInputPin();
        }
    }

    private void onProcessEvent(final SwiperProcessState state) {
        mainLooperHandler.post(new Runnable() {
            @Override
            public void run() {
                if (null != swiperManagerCallback) {
                    swiperManagerCallback.onProcessEvent(state, swiperInfo);
                }
                swiperProcessState = state;
            }
        });
    }

    public void startInputPin() {
        LogUtil.print(TAG,"startInputPin");

        MutexThreadManager.runThread("stat pin", new Runnable() {
            @Override
            public void run() {
                //!debug true 需要密码
                swiperManager.setStartParameter(7, isInputPinNeeded());
                swiperManager.startInputPIN();
            }
        });

    }

    private boolean isInputPinNeeded = true;

    public boolean isInputPinNeeded(){

        return isInputPinNeeded;

    }

    public void setInputPinNeeded(boolean inputPinNeeded) {
        isInputPinNeeded = inputPinNeeded;
    }

    @Override
    public void onEmvFinished(boolean b, ICCardInfo cardInfo) {
        LogUtil.print(TAG,"onEmvFinished");
        LogUtil.print("<AS>","onEmvFinished");
        // 刷卡成功,跳转页面,不做页面重置
        isDoingSecIss = false;

        if (!isOnlineHappen) {
            isOnlineHappen = false;
            LogUtil.print("<AS>","交易中断:isOnlineHappen");
            notifyFinish("交易中断");
            return;
        }
        //UploadTc
        swiperInfo.setTCValue(b);
        swiperInfo.setTcIcc55(ICFieldConstructor.createTcicc55(cardInfo.getEmvTransInfo()));
        swiperInfo.setScpicc55(ICFieldConstructor.createScpic55(cardInfo.getEmvTransInfo()));

        requestUploadTC();

    }

    private void notifyFinish(String error) {
        if (null != swiperManagerCallback) {
            swiperManagerCallback.onNotifyFinish(error);
        }
    }

    private void requestUploadTC() {
        if (null != swiperManagerCallback) {
            swiperProcessState = SwiperProcessState.EMV_FINISH;
            swiperManagerCallback.requestUploadTc(swiperInfo);
        }
    }

    private boolean isQv30e(){
        return swiperManager.getCurrentSwipeType() == ESwiperType.QV30E;
    }

    public void collection(String amount) {


        if (amount == null) {
            throw new RuntimeException("Amount can't be null");
        }

        if(isQv30e()){
            swiperManager.setStartParameter(1, 1);
            swiperManager.startSwipe(amount);
            return;
        }

        this.amount = amount;
        this.type = "1";
        swiperManager.setStartParameter(1, "1");
        swiperManager.setStartParameter(4, 0);
        swiperManager.setStartParameter(5, 1);
        swiperManager.setStartParameter(6, true);
        startMscAndIC(amount);

    }

    public void revocation() {
        if(swiperManager.getCurrentSwipeType() == ESwiperType.QV30E){
            swiperManager.setStartParameter(1, 2);
            swiperManager.starSwieper();
            return;
        }
        this.type = "2";
        swiperManager.setStartParameter(1, "2");
        swiperManager.setStartParameter(4, 2);// TRADE_TYPE_8583
        swiperManager.setStartParameter(5, 25);// TRADE_TYPE_CUSTOM
        swiperManager.setStartParameter(6, true);// ONLINE_FLAG
        startMscAndIC(null);
    }


    public void balanceQuery() {

        if(isQv30e()){
            swiperManager.setStartParameter(1, 3);
            swiperManager.starSwieper();
            return;
        }

        this.type = "3";
        swiperManager.setStartParameter(1, "3");
        swiperManager.setStartParameter(4, 31);// TRADE_TYPE_8583
        swiperManager.setStartParameter(5, 25);// TRADE_TYPE_CUSTOM
        swiperManager.setStartParameter(6, true);// ONLINE_FLAG
        startMscAndIC(null);
    }

    public void startMsc(String type, String amount) {
        if (type == null || amount == null) {
            throw new RuntimeException("Trans type or amount can't be null");
        }

        this.type = type;
        this.amount = amount;

        if(isQv30e()){
            swiperManager.startSwipe(amount);
            return;
        }


        swiperManager.setStartParameter(1, type);
        startMsc(amount);

    }

    public void startCommonTrans(final TransFactor transFactor){
        MutexThreadManager.runThread("startCommonTrans", new Runnable() {
            @Override
            public void run() {
                try {

                    if (isQv30e()) {
                        swiperManager.startSwipe(transFactor.getAmount());
                        return;
                    } else {
                        swiperManager.startTrans(transFactor);
                    }

                } catch (SwiperException e) {
                    LogUtil.print(e);
                }
            }
        });


    }

    public void startPboc(String type, String amount) {
        if (type == null || amount == null) {
            throw new RuntimeException("Trans type or amount can't be null");
        }
        this.amount = amount;

        this.type = type;
        if(isQv30e()){
            swiperManager.startSwipe(amount);
            return;
        }

        swiperManager.setStartParameter(1, type);
        swiperManager.setStartParameter(4, 0);
        swiperManager.setStartParameter(5, 1);
        swiperManager.setStartParameter(6, true);
        startMscAndIC(amount);

    }

    private void startMsc(String amount){
        isPbocSupported = false;
        swiperManager.startMsc(amount);
    }

    private void startMscAndIC(String amount){
        isPbocSupported = true;
        swiperManager.startMscAndIC(amount);
    }


    /**
     * 结束PBOC交易
     *
     * @param b
     */
    public void cancelEmv(final boolean b) {
        if (swiperInfo.getCardType() == SwiperInfo.CardType.ICCard) {

            try {
                swiperProcessState = SwiperProcessState.EMV_CANCEL;
                swiperManager.cancelEmv(b);
            } catch (Exception e) {
                LogUtil.print(e);
            }

        }
    }


    @Override
    public void onPinInputCompleted(String randomNumber, String pin, int length, boolean isCommandProVerTwo, byte[] macRandom, byte[] mac) {
        LogUtil.print(TAG, "onPinInputCompleted");

        if (length == 0 || length == 255) {
            swiperInfo.setPin("");
        } else {
            swiperInfo.setPin(CommonEncrypt.rsaPinKeyEncrypt(pin));
        }

        swiperInfo.setRandomNumber(randomNumber);

        swiperInfo.setIsNewCommandProtocol(isCommandProVerTwo);

        swiperInfo.setDeviceInfo(swiperManager.getDeviceInfo());

        LogUtil.print("SwiperInfo = " + swiperInfo.toString());

        if(isCommandProVerTwo){

            if(macRandom != null && mac.length >0){
                swiperInfo.setMacRandom(ISOUtils.hexString(macRandom));
            }

            if(mac != null && mac.length >0){
                swiperInfo.setMac(ISOUtils.hexString(mac));
            }

        }


        if (!swiperInfo.getRandomNumber().equals(randomNumber)) {
            // Re swipe Dialog
            onProcessEvent(SwiperProcessState.RND_ERROR);
            return;
        }
        swiperProcessState = SwiperProcessState.PIN_INPUT_COMPLETE;
        //Swipe Process end
        if (swiperInfo.getCardType() == SwiperInfo.CardType.MSC) {
            mscProcessEnd();
        } else {
            if(swiperInfo.getCardType() == SwiperInfo.CardType.ICCard)
                onProcessEvent(SwiperProcessState.REQUEST_SEC_ISS);
            requestUploadIC55();
        }
    }

    private void requestUploadIC55() {
        if (null != swiperManagerCallback) {
            swiperManagerCallback.onRequestUploadIC55(swiperInfo);
        }
    }

    private void mscProcessEnd() {
        if (null != swiperManagerCallback) {
            swiperManagerCallback.onMscProcessEnd(swiperInfo);
        }
    }

    /**
     * IC card is broken, goto MSC steps
     */
    @Override
    public void onFallback() {

        onProcessEvent(SwiperProcessState.ON_FALL_BACK);


    }



    public void downGradeSwipe() {
        if (type == null) {
            throw new RuntimeException("DowGrade error, Trans type or amount can't be null");
        }

        swiperManager.setStartParameter(1, type);
        swiperManager.setStartParameter(4, 31);// TRADE_TYPE_8583
        swiperManager.setStartParameter(5, 25);// TRADE_TYPE_CUSTOM
        swiperManager.setStartParameter(6, true);// ONLINE_FLAG
        startMsc(amount);
    }

    @Override
    public void otherError(int errorCode, String s) {
        LogUtil.print(getClass().getName(), "Error " + s);

        if (isDoingSecIss) {
            isDoingSecIss = false;
            swiperInfo.secIssError();
            swiperManagerCallback.requestUploadTc(swiperInfo);
        } else if (s != null && !"设备连接失败!".equals(s)) {
            isDoingSecIss = false;
            //Util.toastCenter(s == null?" Null" : s);
            if ("打开读卡器失败!".equals(s)) {
                return;
            }
            LogUtil.print("<AS>","otherError设备连接失败:"+s);
            notifyFinish(s);
            return;
        }
//        if(swiperProcessState != null){
//            switch (swiperProcessState){
//                case WAITING_FOR_CARD_SWIPE:
//                    onFallback();
//                    break;
//            }
//        }

        isDoingSecIss = false;
    }

    @Override
    public void onWaitingForPinEnter() {
        onProcessEvent(SwiperProcessState.WAITING_FOR_PIN_INPUT);
    }


    @Override
    public void onCurrentDisconnected() {

        onProcessEvent(SwiperProcessState.DEVICE_UNPLUGGED);
        LogUtil.print("<AS>","设备中断:onCurrentDisconnected");
        notifyFinish("设备中断");
    }

    @Override
    public void onCurrentConnected() {
        if (swiperManager.isPhyConnectSwiper()) {
            onProcessEvent(SwiperProcessState.DEVICE_PLUGGED);
        }
    }


    @Override
    public void onWaitingForSwipe() {
        //State : Ready For Swipe Card
        onProcessEvent(SwiperProcessState.WAITING_FOR_CARD_SWIPE);
    }

    @Override
    public void onCardSwipeDetected() {

    }

    @Override
    public void onInterrupted() {
        LogUtil.print(TAG,"onInterrupted");
//history code        if (!swiperManager.isPhyConnectSwiper() || swiperManager.isSwiperValid()) {
//            notifyFinish("设备中断");
//        }
        LogUtil.print("<AS>","设备中断:onInterrupted");
        notifyFinish("设备中断");
    }

    @Override
    public void onTimeOut() {
        if (swiperProcessState == SwiperProcessState.WAITING_FOR_CARD_SWIPE) {
            onProcessEvent(SwiperProcessState.SWIPE_TIMEOUT);
        } else if (swiperProcessState == SwiperProcessState.WAITING_FOR_PIN_INPUT) {
            onProcessEvent(SwiperProcessState.PIN_INPUT_TIMEOUT);
        } else {
           LogUtil.print("Exception", "Unknown SwiperProcessState");
        }
    }

    public void reset() {

        connectionManager.pause();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isResetable && swiperManager.isDeviceConnected()) {

                    if (swiperManager.isPbocSupportedSwiper()) {
                        if (swiperProcessState == SwiperProcessState.WAITING_FOR_CARD_SWIPE) {
                            swiperManager.cancelCardRead();
                        }
                        if (swiperProcessState == SwiperProcessState.WAITING_FOR_PIN_INPUT) {
                            swiperManager.cancelPininput();
                        }

                        if(swiperProcessState == SwiperProcessState.REQUEST_SEC_ISS){
                            cancelEmv(false);
                        }

                    }
                    swiperManager.resetScreen();
            }
        }}).start();

    }

    /**
     * Do Second Issuance
     *
     * @param transRslt transfer success or failed
     * @param icc55     icc55field
     * @param authCode  authorisation code
     */
    public void doSecondIssuance(boolean transRslt, String icc55, String authCode) {
        LogUtil.print("" + transRslt + icc55);
        LogUtil.print("<AS>","00");
        LogUtil.print("<AS>","icc55:"+icc55+"\n"+"authCode:"+authCode);
        SecondIssuanceRequest sir = new SecondIssuanceRequest();
        LogUtil.print("<AS>","01");
        isDoingSecIss = true;
        if (transRslt)
            sir.setAuthorisationResponseCode("00");//8a.length== 2
        else
            sir.setAuthorisationResponseCode("01");
        try {

            SimpleTLVPackage tlvPackage = new SimpleTLVPackage();
            Base64 base64 = new Base64();
            LogUtil.print("<AS>","02");
            if (null != icc55 && !"".equals(icc55) && !"null".equals(icc55)) {
                LogUtil.print("<AS>","03");
                String s="9F2608C972D4F99211B2B39F2701809F101307010103A0A802010A01000000000025C39ECB9F3704E8B67BD39F3602008F9505000004E8009A031607299C01009F02060000000001005F2A02015682027C009F1A0201569F03060000000000009F3303E0E1C89F34034203009F3501229F1E084E65776C616E64318408A0000003330101019F090200209F410400000159";
                byte[] rslt = (base64.decode(icc55.getBytes()));
//                byte[] rslt = (base64.decode(s.getBytes()));
                LogUtil.print("<AS>","04");
                String sss="";
                for (int i=0;i<rslt.length;i++){
                    sss=sss+rslt[i]+"/";
                }
                LogUtil.print("<AS>",""+sss);
                tlvPackage.unpack(rslt);//00 00 00
                LogUtil.print("<AS>","05");
                //byte[] authorisationCode = tlvPackage.getValue(Const.EmvStandardReference.AUTHORISATION_CODE);//89  length ==6
                byte[] isssuerAuthenticationData = tlvPackage.getValue(Const.EmvStandardReference.ISSUER_AUTHENTICATION_DATA);                    //0x91，发卡行认证数据
                byte[] issuerScriptTemplate1 = tlvPackage.getValue(Const.EmvStandardReference.ISSUER_SCRIPT_TEMPLATE_1);                        //0x71，发卡行脚本1
                byte[] issuerScriptTemplate2 = tlvPackage.getValue(Const.EmvStandardReference.ISSUER_SCRIPT_TEMPLATE_2);
                LogUtil.print("<AS>",isssuerAuthenticationData.toString());
                LogUtil.print("<AS>",issuerScriptTemplate1.toString());
                LogUtil.print("<AS>",issuerScriptTemplate1.toString());
                LogUtil.print("<AS>","06");
                if (null != isssuerAuthenticationData && isssuerAuthenticationData.length != 0)
                    sir.setIssuerAuthenticationData(isssuerAuthenticationData);
                LogUtil.print("<AS>","07");
                if (null != issuerScriptTemplate1 && issuerScriptTemplate1.length != 0)
                    sir.setIssuerScriptTemplate1(issuerScriptTemplate1);
                LogUtil.print("<AS>","08");
                if (null != issuerScriptTemplate2 && issuerScriptTemplate2.length != 0) {
                    sir.setIssuerScriptTemplate2(issuerScriptTemplate2);
                    LogUtil.print("<AS>","09");
                    swiperInfo.setIfRetScpic55(true);
                } else {
                    swiperInfo.setIfRetScpic55(false);
                    LogUtil.print("<AS>","10");
                }
            }
            LogUtil.print("<AS>","12");
            //0x72，发卡行脚本2
            if (null != authCode && !"".equals(authCode) && authCode.length() == 6) {
                sir.setAuthorisationCode(authCode.getBytes());
                LogUtil.print("<AS>","13");
            }

        } catch (Exception e) {
            LogUtil.print("<AS>","14");
            LogUtil.print("SwiperManagerHandler", "Unpack Response Error", e);
        } finally {
            LogUtil.print("<AS>","15");
            swiperManager.doSecondIssuance(sir);

        }

    }

    @Override
    public void onDeviceDisconnected(SwiperDefine.SwiperPortType type) {
        LogUtil.print(TAG,"onPhysicalDeviceRemoved");
    }

    public boolean isOnlineHappen() {
        return isOnlineHappen;
    }

    public void setOnlineHappen(boolean isOnlineHappen) {
        this.isOnlineHappen = isOnlineHappen;
    }

    public boolean isPinInputSeparate() {
        return isPinInputSeparate;
    }

    public void setPinInputSeparate(boolean isPinInputSeparate) {
        this.isPinInputSeparate = isPinInputSeparate;
    }

    public SwiperProcessState getSwiperProcessState() {
        return swiperProcessState;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public SwiperManagerCallback getSwiperManagerCallback() {
        return swiperManagerCallback;
    }

    public void setSwiperManagerCallback(SwiperManagerCallback swiperManagerCallback) {
        this.swiperManagerCallback = swiperManagerCallback;
    }

    public boolean isResetable() {
        return isResetable;
    }

    public void setResetable(boolean isResetable) {
        this.isResetable = isResetable;
    }

    public void deleteSwiper(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    swiperManager.onDestroy();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onSwipeSuccess(String encTracks, String randomNumber, String maskedPANString, SwipeDefine.SwipeKeyBoard swipeKeyBoard) {
        LogUtil.print(TAG,"onDecodeCompleted");

        swiperInfo = new SwiperInfo();
        swiperInfo.setCardType(SwiperInfo.CardType.MSC);
        swiperInfo.setPosemc("0");
        String track1 = "";
        String[] tracks = encTracks.split("@");
        if (tracks.length == 1) {
            swiperInfo.setEncTracks(encTracks);
            swiperInfo.setTrack1(track1);
        } else {
            swiperInfo.setTrack1(tracks[0]);
            swiperInfo.setEncTracks(tracks[1]);
        }

        swiperInfo.setRandomNumber(randomNumber);
        swiperInfo.setMaskedPan((maskedPANString).replaceAll(" ", "").replaceAll("-", "").replaceAll(",", "").replace("*", "X"));

        onProcessEvent(SwiperProcessState.SWIPE_END);
        if (!isPinInputSeparate) {
            startInputPin();
        }
    }

    @Override
    public void onSwipeError() {

    }

    @Override
    public void onDeviceConnected(SwiperDefine.SwiperPortType type) {

    }

    @Override
    public void onNoDeviceDetected() {

    }

    @Override
    public void deviceAddressList(List<BluetoothDevice> macs, BluetoothDevice newMacs) {

    }

    @Override
    public void icCardDemotionUsed() {
        onProcessEvent(SwiperProcessState.SWIPE_ICCARD_DENIED);
    }


    /*************  用于获取屏幕是否点亮 **************/

    protected boolean isScreenOn = true;

    protected void registerScreenReceiver(){

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        context.registerReceiver(screenReceiver, intentFilter);
    }

    protected BroadcastReceiver screenReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(Intent.ACTION_SCREEN_ON.equals(action)){
                isScreenOn = true;
            }else if(Intent.ACTION_SCREEN_OFF.equals(action)){
                isScreenOn = false;
            }
        }
    };

    public ESwiperType getPresentSwiperType(){
        return swiperManager.getCurrentSwipeType();
    }

    public boolean isPbocSupported(){

        if(isQv30e()){
            return false;
        }

        return isPbocSupported;
    }

    public boolean isQPbocSupported(){

        return swiperManager.isQpbocSupported();
    }

    @Override
    public void onQPBOCFinished(ICCardInfo icCardInfo) {
        swiperInfo = new SwiperInfo();
        swiperInfo.setEmvTransInfo(icCardInfo.getEmvTransInfo());

        swiperInfo.setIcc55(icCardInfo.getIcc55());
        swiperInfo.setTrack2(icCardInfo.getTrack2());
        swiperInfo.setCardsn(icCardInfo.getCardSn());
        swiperInfo.setEncTracks("");
        swiperInfo.setRandomNumber("");
        swiperInfo.setMaskedPan(icCardInfo.getPan());
        swiperInfo.setCardType(SwiperInfo.CardType.QPBOC);
        swiperInfo.setPosemc("1");
        swiperInfo.setIcinfo(icCardInfo.getIcinfo());

        onProcessEvent(SwiperProcessState.SWIPE_END);

        if (!isPinInputSeparate) {

            startInputPin();
        }
    }

    @Override
    public void onQPBOCDenied() {
        onProcessEvent(SwiperProcessState.QPBOC_DENIED);
    }


    public boolean isForceQpboc() {
        return (swiperManager.isForceQpboc());
    }

    public void setForceQpboc(boolean forceQpboc) {
        swiperManager.setForceQpboc(forceQpboc);
    }
}
