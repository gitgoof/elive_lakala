package com.lakala.platform.swiper.mts;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;

import com.lakala.core.http.HttpRequest;
import com.lakala.core.http.IHttpRequestEvents;
import com.lakala.core.swiper.ESwiperType;
import com.lakala.core.swiper.ICCardInfo;
import com.lakala.core.swiper.SwiperDefine;
import com.lakala.library.DebugConfig;
import com.lakala.library.encryption.RSAEncrypt;
import com.lakala.library.exception.BaseException;
import com.lakala.library.jni.LakalaNative;
import com.lakala.library.util.DeviceUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.R;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.CommonEncrypt;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.statistic.StatisticManager;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.platform.swiper.mts.protocal.EProtocalType;
import com.lakala.platform.swiper.mts.protocal.ProtocalActivity;
import com.lakala.ui.dialog.mts.AlertDialog;
import com.newland.mtype.common.Const;
import com.newland.mtype.module.common.emv.SecondIssuanceRequest;
import com.newland.mtype.tlv.SimpleTLVPackage;
import com.newland.mtype.tlv.TLVPackage;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 刷卡逻辑控制器，通用的刷卡逻辑处理
 * Created by xyz on 14-1-18.
 */
public class SwipeLauncher implements SwiperManagerListener,
        SwipeDialogManager.SwipeDialogClickListener,
        TerminalSignInTask.TerminalSignInListener,
        GetSwipeKsnTask.GetKsnListener,
        Handler.Callback{

    /**
     * 刷卡器状态
     */
    //启动刷卡器状态
    private static final int SWIPE_START_STATUS          = 700;
    //刷卡超时状态
    private static final int SWIPE_TIMEOUT_STATUS        = 701;
    //刷卡器失败状态
    private static final int SWIPE_SWIPE_ERROR_STATUS    = 702;
    //等待刷卡状态
    private static final int SWIPE_WAITING_STATUS        = 703;
    //inputPin timeout
    private static final int INPUT_PIN_STATUS            = 704;
    //异常状态
    private static final int SWIPE_ERROR_STATUS          = 705;
    //刷卡器中断状态
    private static final int SWIPE_INTERRUPTED_STATUS    = 706;
    //onReadCardPin 执行结束状态
    private static final int ON_READ_CARD_PIN_FINISHED_STATUS = 707;
    //执行onReadCardPin状态
    private static final int RUN_READ_CARD_PIN_STATUS    = 708;
    //onEmvFinished
    private static final int ON_EMV_FINISHED_STATUS      = 709;
    //onFallback
    private static final int ON_FALLBACK_STATUS          = 710;
    //刷卡器开始读取卡信息
    private static final int ON_CARD_SWIPE_DETECTED      = 711;
    //ic卡降级使用
    private static final int IC_CARD_DEMOTION_USED       = 712;

    /**
     * 提示信息
     */
    //刷卡逻辑控制器实例
    private static final int SWIPE_ERROR_HINT         = 444;
    //通讯异常提示
    private static final int COMMUNICATION_ERROR_HINT = 445;
    //开通刷卡器完成提示
    private static final int BIND_SWIPE_FINISH_HINT   = 447;
    //绑定刷卡器失败提示
    private static final int BIND_SWIPE_ERROR_HINT    = 446;
    //刷卡器验证失败
    private static final int SWIPE_SIGN_IN_ERROR_HINT = 448;


    //刷卡逻辑控制器实例
    private static SwipeLauncher instance;
    //刷卡状态处理器
    private static Handler swipeStatusHandler;
    //刷卡管理实例
    private SwiperManager swiperManager;
    //刷卡dialog管理
    private SwipeDialogManager dialogManager;
    //终端登录
    private static TerminalSignInTask terminalSignInTask;
    //Activity
    private FragmentActivity activity;
    //ksn
    private String ksn = "";
    //刷卡监听
    private SwipeListener swipeListener;



    //是否连接刷卡器
    private boolean isConnection;
    //是否显示拔出刷卡器dialog
    private boolean isShowDialog;
    //刷卡失败标识
    private boolean isSwipeError;
    //等待刷卡标识
    private boolean isWaitingForSwipe;
    //设置当前刷卡器连接回调是否有效(处理逻辑)
    private boolean isCurrentConnectInvalid;
    //刷卡器异常次数
    private int swipeErrorCount;
    //当前设备数量
    private int currentDeviceSize;
    //当前刷卡状态
    private int currentSwipeStatus = 0;
    //上一个刷卡状态
    private int previousSwipeStatus = 0;
    //当前启动刷卡器金额
    private String currentAmount;
    //当前启动刷卡器title
    private String currentTitle;
    //收款宝交易类型
    private SwipeDefine.SwipeCollectionType collectionType;
    //刷卡交易实体,CardInfo
    private CardInfo cardInfo;
    //刷卡类型，这里指刷卡器是否有键盘以及所刷银行卡类型
    private SwipeDefine.SwipeCardType swipeCardType;
    //发卡行脚本2
    private boolean has_ISSUER_SCRIPT_TEMPLATE_2;
    //B端传过来的业务实体，用于对个该业务进行不同的刷卡定制
    private JSONObject business;

    private SwipeLauncher() {
        if(swiperManager == null){
            swiperManager = SwiperManager.getInstance(this);
        }
        if (swipeStatusHandler == null){
            swipeStatusHandler = new Handler(this);
        }
    }

    /**
     * 获取实例
     */
    public static SwipeLauncher getInstance() {
        if (instance == null) {
            instance = new SwipeLauncher();
        }
        return instance;
    }

    /**
     * 设置刷卡完成回调
     *
     * @param swipeListener
     */
    public void setSwipeListener(SwipeListener swipeListener) {
        this.swipeListener = swipeListener;
    }

    /**
     * 设置activity
     */
    public void setActivity(FragmentActivity activity) {
        this.activity = activity;
        this.dialogManager = new SwipeDialogManager(this);
        terminalSignInTask = new TerminalSignInTask(activity);
        terminalSignInTask.setListener(this);
    }

    /**
     * 设置启动刷卡器的金额和title
     * 设置交易类型
     *
     * @param amount
     * @param title
     */
    public void setStartParameters(String title, String amount, SwipeDefine.SwipeCollectionType collectionType){
        this.currentAmount  = amount;
        this.currentTitle   = title;
        this.collectionType = collectionType;
    }

    /**
     * 刷卡器启动
     */
    public void launch(){
        launch(business);
    }

    /**
     * 刷卡器启动
     *
     * @param business 用于区分不同业务的相关定制服务
     */
    public void launch(JSONObject business) {
        this.business = business;

        if (isSwipeError) {
            isSwipeError = false;
        }

        if (swiperManager == null){
            return;
        }

        if (!swiperManager.isListenerEnable()) {
            swiperManager.setEnableListener(true);
        }

        if (!swiperManager.isDeviceConnected() && dialogManager!=null) {
            dialogManager.showInsertSwipeDialog(business);
            return;
        }

        if (swiperManager.swipePortTypeSize() < 2) {

            if (swiperManager.getCurrentSwipeType() == null){
                swiperManager.setSwiperType("");
            }

            if (StringUtil.isEmpty(ksn)) {
                new GetSwipeKsnTask(this, swiperManager).execute();
            } else {
                if (swiperManager.isLastSwiper(ksn))
                    startSwipe();
                 else
                    checkSwiperStatus();
            }
        } else {
            if (StringUtil.isEmpty(ksn)) {
                start2SelectSwipe();
            } else {
                if (swiperManager.isLastSwiper(ksn))
                    startSwipe();
                else
                    checkSwiperStatus();
            }
        }
    }

    /**
     * 停止监听
     */
    public void stopMonitor() {

        if(swiperManager == null) {
            return;
        }

        swiperManager.stopSwipe();
        //如果监听有效则关闭监听，否则重复关闭监听，会导致反注册监听时异常
        if (swiperManager.isListenerEnable()) {
            swiperManager.setEnableListener(false);
        }
    }

    /**
     * 设置刷卡监听是否可用
     *
     * @param b
     */
    public void setMonitorEnable(boolean b) {
        if(swiperManager == null){
            return;
        }
        swiperManager.setEnableListener(b);
    }

    /**
     * 启动刷卡
     */
    private void startSwipe() {

        if(swiperManager == null){
            return;
        }

        sendSwipeStatus(SWIPE_START_STATUS);

        isWaitingForSwipe     = false;
        swiperManager.stopSwipe();
        swiperManager.startSwipe(currentTitle, currentAmount, collectionType);
    }

    /**
     * 启动收款宝键盘
     */
    private void startInputPIN() {
        if(swiperManager == null){
            return;
        }
        swiperManager.startInputPIN();
    }

    /**
     * 停止刷卡器
     */
    public void stopSwipe() {
        this.activity = null;
        if(swiperManager == null){
            return;
        }
        swiperManager.stopSwipe();
    }

    /**
     * 调用sendPin会触发
     * 一般需要用手机输入密码时，会调用此方法
     *
     * @param pin
     * @return
     */
    public void sendPin(String pin){
        if(StringUtil.isEmpty(pin)) return ;
        if(cardInfo != null){
            cardInfo.setPinkey(CommonEncrypt.pinKeyDesRsaEncrypt(ksn, pin));
        }
        sendSwipeStatus(RUN_READ_CARD_PIN_STATUS, swipeCardType);
    }

    /**
     * 设置当前连接的蓝牙名称
     */
    public void setConnectionBluetooth(String name){
        if(swiperManager == null) return;
        swiperManager.setConnectionBluetooth(name);
    }

    /**
     * 应用撤销插卡或刷卡调用
     */
    public void cancelCardRead(){
        if(swiperManager == null) return;
        swiperManager.cancelCardRead();
        swiperManager.resetScreen();
    }


    /**
     * 结束EMV
     * true : 正常结束
     * false : 中断
     *
     * @param b
     */
    public void cancelEmv(boolean b){
        if(swiperManager == null) return;
        swiperManager.cancelEmv(b);
        swiperManager.resetScreen();
    }

    /**
     * 二次授权
     */
    public void doSecondIssuance(String authCode, String icc55) {
        if(swiperManager == null) return;

        SecondIssuanceRequest sir = new SecondIssuanceRequest();
        sir.setAuthorisationResponseCode(authCode);//8a.length== 2

        try {

            TLVPackage tlvPackage = new SimpleTLVPackage();
            Base64 base64 = new Base64();

            if (StringUtil.isNotEmpty(icc55)) {
                byte[] rslt = (base64.decode(icc55.getBytes()));
                tlvPackage.unpack(rslt);//00 00 00

                byte[] authrisationCode = tlvPackage.getValue(Const.EmvStandardReference.AUTHORISATION_CODE);//89  length ==6
                byte[] isssuerAuthenticationData = tlvPackage.getValue(Const.EmvStandardReference.ISSUER_AUTHENTICATION_DATA);//0x91，发卡行认证数据
                byte[] issuerScriptTemplate1 = tlvPackage.getValue(Const.EmvStandardReference.ISSUER_SCRIPT_TEMPLATE_1);//0x71，发卡行脚本1
                byte[] issuerScriptTemplate2 = tlvPackage.getValue(Const.EmvStandardReference.ISSUER_SCRIPT_TEMPLATE_2);//0x72，发卡行脚本2

                if (null != isssuerAuthenticationData && isssuerAuthenticationData.length != 0) {
                    sir.setIssuerAuthenticationData(isssuerAuthenticationData);
                }

                if (null != issuerScriptTemplate1 && issuerScriptTemplate1.length != 0) {
                    sir.setIssuerScriptTemplate1(issuerScriptTemplate1);
                }

                if (null != issuerScriptTemplate2 && issuerScriptTemplate2.length != 0) {
                    sir.setIssuerScriptTemplate2(issuerScriptTemplate2);
                    has_ISSUER_SCRIPT_TEMPLATE_2 = true;
                } else {
                    has_ISSUER_SCRIPT_TEMPLATE_2 = false;
                }

                if (null != authrisationCode && authrisationCode.length == 6) {
                    sir.setAuthorisationCode(authrisationCode);
                }
            }

        } catch (Exception e) {
            LogUtil.print(e);
        } finally {
            swiperManager.doSecondIssuance(sir);
        }

    }

    /**
     * 设置刷卡器类型
     */
    public void setSwipeType(String type) {
        if(swiperManager == null){
            return;
        }
        swiperManager.setSwiperType(type);
    }

    /**
     * 暴露给外部调用,获取ksn,验证刷卡器是否匹配
     */
    public void getKsn(GetSwipeKsnTask.GetKsnListener listener) {
        if(swiperManager == null){
            return;
        }
        new GetSwipeKsnTask(listener, swiperManager).execute();
    }

    /**
     * 获取
     * @return
     */
    public String getKsn(){
        return ksn;
    }

    /**
     * 设置ksn
     *
     * @param ksn
     */
    public void setKsn(String ksn) {
        this.ksn = ksn;
    }

    /**
     * 设置当前刷卡器连接回调是否有效
     *
     * @param b
     */
    public void setCurrentConnectInvalid(boolean b) {
        this.isCurrentConnectInvalid = b;
    }

    /**
     * 暴露给外部调用接口onCancel方法
     */
    public void cancel(CancelMode mode) {
        if (null != swipeListener) {
            swipeListener.onCancel(mode);
        }
        if (null != dialogManager) {
            dialogManager.onDestroy();
        }
    }

    /**
     * 设置是否显示拔出刷卡器提示
     */
    public void setShowDialog(boolean show) {
        this.isShowDialog = show;
    }

    /**
     * 是否为拉风
     *
     * @return
     */
    public boolean isLaPhone() {
        return false;
    }

    /**
     * activity退出时，销毁相应资源
     */
    public void onDestroy() {
        ksn = "";
        if(swiperManager != null){
            swiperManager.onDestroy();
        }
        if(instance != null){
            instance = null;
        }
        if(swipeStatusHandler != null){
            swipeStatusHandler = null;
        }
    }

    /**
     * 获取当前业务 Key
     *
     * @return String
     */
    public String getCurrentBusinessKey(){
        //由于现在只有信用卡还款有特殊处理，所以再次进行直接判断 boolean 返回
        if (business != null){
            return business.optString("busId", "");
        }
        return "";
    }

    public JSONObject getBusiness(){
        return business;
    }

    /**
     * 统计获取 KSN 状态
     *
     * @param status    状态
     * @param context   Context
     */
    void statisticGetKSNStatus(String status, Context context){
        Map<String,String> stringHashMap = new HashMap<>();
//            device-osversion: 是用户系统版本
//            device-type: 设备类型
        String userMobile = ApplicationEx.getInstance().getUser().getLoginName();
        String deviceType = DeviceUtil.getPhoneManufacturer() + "-" + DeviceUtil.getPhoneType();
        stringHashMap.put("device-osversion", Build.VERSION.RELEASE);
        stringHashMap.put("device-type", deviceType);
        if (swiperManager != null){
            ESwiperType swiperType = swiperManager.getCurrentSwipeType();
            if (swiperType != null){
                stringHashMap.put("shk-model", swiperType.toString());
            }
        }
        if(context != null){
            StatisticManager.getInstance().onEvent(StatisticManager.swiperStatus, status, "1", "", userMobile, stringHashMap, context);
        }
    }

    /**
     * 发送刷卡状态
     *
     * @param status
     */
    private void sendSwipeStatus(int status){
        if(swipeStatusHandler == null) {
            return;
        }
        Message message = swipeStatusHandler.obtainMessage(status);
        swipeStatusHandler.sendMessage(message);
    }

    /**
     * 发送刷卡状态
     *
     * @param status
     * @param obj
     */
    private void sendSwipeStatus(int status, Object obj){
        if(swipeStatusHandler == null) {
            return;
        }
        Message message = swipeStatusHandler.obtainMessage(status, obj);
        swipeStatusHandler.sendMessage(message);
    }

    /**
     * 联网验证刷卡器状态
     */
    private void checkSwiperStatus() {
        if (TerminalKey.hasKey(ksn) && swiperManager !=null) {
            swiperManager.setCurrentValidKsn();
            startSwipe();
        } else {
            signInUserSwipe();
        }
    }

    /**
     * 终端绑定
     */
    private void bindUserSwipe() {
        String IMSI = activity != null ? DeviceUtil.getIMSI(activity) : "";//IMSI 串号
        String TelecomOperators = activity != null ? DeviceUtil.getPhoneISP(activity) : "";//运营商号段
        String MobileModel = DeviceUtil.getPhoneModel();//手机标识
        String MobileProduct = DeviceUtil.getPhoneType();//手机型号
        String MobileManuFacturer = DeviceUtil.getPhoneManufacturer();//手机厂商
        BusinessRequest request = SwipeRequest.bindTerminal(activity, ksn, IMSI, TelecomOperators, MobileModel, MobileProduct, MobileManuFacturer);
        request.setIHttpRequestEvents(new IHttpRequestEvents(){
            @Override
            public void onSuccess(HttpRequest request) {
                super.onSuccess(request);
                DialogController.getInstance().showAlertDialog(activity,
                        getString(R.string.plat_swipe_bind_finish),
                        getDialogMsg(null, BIND_SWIPE_FINISH_HINT),
                        "确定",
                        new AlertDialog.Builder.AlertDialogClickListener() {
                            @Override
                            public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
                                alertDialog.dismiss();
                                signInUserSwipe();
                            }
                        }
                );
            }

            @Override
            public void onFailure(HttpRequest request, BaseException exception) {
                super.onFailure(request, exception);
                DialogController.getInstance().showAlertDialog(activity,
                        "",
                        getDialogMsg(null, BIND_SWIPE_ERROR_HINT),
                        getString(R.string.plat_swipe_cancel),
                        getString(R.string.plat_swipe_retry),
                        new AlertDialog.Builder.AlertDialogClickListener() {
                            @Override
                            public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
                                switch (typeEnum) {
                                    case LEFT_BUTTON:
                                        alertDialog.dismiss();
                                        cancel(CancelMode.SWIPE_PLUGGED);
                                        break;
                                    case RIGHT_BUTTON:
                                        bindUserSwipe();
                                        break;
                                }
                            }
                        }
                );
            }
        });
        request.execute();
    }

    /**
     * 终端签到
     */
    private void signInUserSwipe() {
        if(terminalSignInTask == null){
            return;
        }
        terminalSignInTask.execute(ksn);
    }

    /**
     * 跳转登录界面
     */
    private void start2Login() {
        if(swipeListener == null){
            return;
        }
        swipeListener.reLogin();
    }

    /**
     * 跳转购买刷卡器界面
     */
    private void start2BuySwipe() {
//        if(activity != null){
//            PlatFormBroadcaster.send(activity, PlatFormBroadcaster.LAUNCHER, "buySwiper");
//        }
    }

    /**
     * 跳转帮助界面
     */
    private void start2Help() {

        if(activity == null) return;

        Intent intent = new Intent(activity, ProtocalActivity.class);
        intent.putExtra(ProtocalActivity.PROTOCAL_KEY, EProtocalType.SWIPE_INTRODUCE);
        activity.startActivity(intent);
    }

    /**
     * 跳转刷卡器类型选择界面
     */
    private void start2SelectSwipe() {
        this.isCurrentConnectInvalid = true;
        if(activity != null){
            final Intent intent = new Intent(activity, SetSwipeTypeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivityForResult(intent, 100);
        }
    }

    /**
     * -------------------刷卡回调------------------------
     */

    @Override
    public void onTimeOut() {

        sendSwipeStatus(SWIPE_TIMEOUT_STATUS);
    }

    @Override
    public void onWaitingForSwipe() {

        sendSwipeStatus(SWIPE_WAITING_STATUS);

        swipeErrorCount = 0;

        isWaitingForSwipe = true;

    }

    @Override
    public void onSwipeSuccess(String encTracks,
                               String randomNumber,
                               String maskedPANString,
                               SwipeDefine.SwipeKeyBoard swipeKeyBoard) {
        DialogController.getInstance().dismiss();

        if(dialogManager != null){
            dialogManager.stopSwitchGif();
        }

        User user = ApplicationEx.getInstance().getUser();
        String userMobile = "";
        if (user != null) {
            userMobile = user.getLoginName();
        }
        Map<String,String> stringHashMap = new HashMap<String, String>();
        if (swiperManager != null){
            stringHashMap.put("shk-model", swiperManager.getCurrentSwipeType().toString());
            stringHashMap.put("shk-device", ksn);
        }
        if(activity != null){
            //刷卡状态---刷卡成功数据统计
            StatisticManager.getInstance().onEvent(StatisticManager.swiperStatus, StatisticManager.OK, "1", "", userMobile, stringHashMap,activity);
        }

        //生成CardInfo实体
        cardInfo = new CardInfo(encTracks.toUpperCase(), randomNumber.toUpperCase(), maskedPANString, swipeKeyBoard);

        //判断刷卡器是否有键盘
        switch (swipeKeyBoard){
            case YES:
                //提示用户用硬键盘键入密码
                sendSwipeStatus(INPUT_PIN_STATUS, swipeKeyBoard);
                startInputPIN();
                break;
            case NO:
                if(swipeListener != null){
                    swipeListener.onRequestPin(maskedPANString);
                }
                swipeCardType = SwipeDefine.SwipeCardType.MAGNETIC_NORMAL;
                break;
        }

        isWaitingForSwipe = false;
    }

    @Override
    public void onReadICCardCompleted(ICCardInfo icCardInfo, SwipeDefine.SwipeKeyBoard swipeKeyBoard) {

        DialogController.getInstance().dismiss();

        if(dialogManager != null){
            dialogManager.stopSwitchGif();
        }

        User user = ApplicationEx.getInstance().getUser();
        String userMobile = "";
        if (user != null) {
            userMobile = user.getLoginName();
        }
        Map<String,String> stringHashMap = new HashMap<String, String>();
        if (swiperManager != null){
            stringHashMap.put("shk-model", swiperManager.getCurrentSwipeType().toString());
            stringHashMap.put("shk-device", ksn);
        }

        if(activity != null){
            //刷卡状态---刷卡成功数据统计
            StatisticManager.getInstance().onEvent(StatisticManager.swiperStatus, StatisticManager.OK, "1", "", userMobile, stringHashMap, activity);
        }

        //生成CardInfo
        cardInfo = new CardInfo(icCardInfo, swipeKeyBoard);

        //判断刷卡器是否有键盘
        switch (swipeKeyBoard){
            case YES:
                //提示用户用硬键盘键入密码
                sendSwipeStatus(INPUT_PIN_STATUS, swipeKeyBoard);
                startInputPIN();
                break;
            case NO:
                if(swipeListener != null){
                    swipeListener.onRequestPin(icCardInfo.getMaskedPan());
                }
                //在无键盘IC卡时候,得不到随机数,所以生成一个16字节16位随机数
                Random random = new Random();
                StringBuffer randomNumber = new StringBuffer();
                for(int i=0 ; i<16; i++){
                    randomNumber.append(Integer.toHexString(random.nextInt(16)));
                }
                cardInfo.setRandom(randomNumber.toString().toUpperCase());
                swipeCardType = SwipeDefine.SwipeCardType.IC_NORMAL;
                break;
        }

    }

    @Override
    public void onPinInputCompleted(String randomNumber, String pin, int length) {

        RSAEncrypt rsaEncrypt = new RSAEncrypt(LakalaNative.getPasswordPublicKey(DebugConfig.DEV_ENVIRONMENT));

        cardInfo.setPinkey(rsaEncrypt.encrypt(pin.getBytes()).toUpperCase());
        cardInfo.setRandom(randomNumber);
        cardInfo.setPinLength(length);
        //02101 硬键盘键入密码时
        cardInfo.setChnType("02101");

        if(cardInfo.getCardType().equals(CardInfo.MAGNETIC_CARD)){
            sendSwipeStatus(RUN_READ_CARD_PIN_STATUS, SwipeDefine.SwipeCardType.MAGNETIC_KEYBOARD);
        }else if(cardInfo.getCardType().equals(CardInfo.IC_CARD)){
            sendSwipeStatus(RUN_READ_CARD_PIN_STATUS, SwipeDefine.SwipeCardType.IC_KEYBOARD);
        }

    }

    @Override
    public void onEmvFinished(boolean b, ICCardInfo icCardInfo) {

        sendSwipeStatus(ON_EMV_FINISHED_STATUS, b);

        icCardInfo.setHasScpic55(has_ISSUER_SCRIPT_TEMPLATE_2);

        JSONObject tc = new JSONObject();
        try {
            tc.put("Tcicc55", icCardInfo.createTcicc55());
            tc.put("Scpic55", icCardInfo.createScpic55());
        } catch (JSONException e) {
            LogUtil.print(e);
        }

        if(swipeListener != null){
            swipeListener.onFinish(b, tc);
        }
    }

    @Override
    public void onSwipeError() {

        sendSwipeStatus(SWIPE_SWIPE_ERROR_STATUS);

        User user = ApplicationEx.getInstance().getUser();
        String userMobile = "";
        if (user != null){
            userMobile = user.getLoginName();
        }
        Map<String,String> stringHashMap = new HashMap<String, String>();
        if (swiperManager != null){
            stringHashMap.put("shk-model", swiperManager.getCurrentSwipeType().toString());
            stringHashMap.put("shk-device", ksn);
        }

        if(activity != null){
            //刷卡状态---刷卡失败数据统计
            StatisticManager.getInstance().onEvent(StatisticManager.swiperStatus, StatisticManager.Failed, "1", "", userMobile, stringHashMap, activity);
        }

        isSwipeError = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startSwipe();
            }
        }, 2000);

    }

    @Override
    public void onDeviceConnected(SwiperDefine.SwiperPortType type) {
        if(swiperManager == null){
            return;
        }
        LogUtil.print(" onDeviceConnected : type : " + type.toString() + " , current : " + swiperManager.getCurrentPortType());
        if(type != SwiperDefine.SwiperPortType.TYPE_BLUETOOTH){
            swiperManager.addSwipePortType(type);
        }
        if (currentDeviceSize < swiperManager.swipePortTypeSize()) {
            ksn = "";
            currentDeviceSize = swiperManager.swipePortTypeSize();
        }
    }

    @Override
    public void onDeviceDisconnected(SwiperDefine.SwiperPortType type) {
        if(swiperManager == null){
            return;
        }
        LogUtil.print(" onDeviceDisconnected : type : " + type.toString());
        if (swiperManager.swipePortTypeSize() > 1 && type != SwiperDefine.SwiperPortType.TYPE_BLUETOOTH){
            swiperManager.removeSwipePortType(type);
        }
        if (currentDeviceSize > swiperManager.swipePortTypeSize()) {
            ksn = "";
            currentDeviceSize = swiperManager.swipePortTypeSize();
        }
    }

    @Override
    public void onCurrentDisconnected() {
        LogUtil.print(" --- onCurrentDisconnected --- ");
        if(swiperManager == null){
            return;
        }

        if(swiperManager.getCurrentSwipeType() == ESwiperType.Bluetooth){
            //如果蓝牙断开，设置当前保存的刷卡器类型
            swiperManager.setSwiperType("");
        }
        //刷卡器拔出后，将ksn清除
        ksn = "";

        if (!isConnection)
            return;

        isConnection = false;

        if (activity == null)
            return;

        activity.sendBroadcast(new Intent(SetSwipeTypeActivity.SWIPE_PULL_UP));
        //当前显示为刷卡器类型选择页面
        if (isCurrentConnectInvalid)
            return;

        if (isWaitingForSwipe) swiperManager.stopSwipe();

        if (!isShowDialog)
            return;

        if (swiperManager.swipePortTypeSize() > 1) {
            start2SelectSwipe();
        } else {
            launch();
        }
    }

    @Override
    public void onCurrentConnected() {

        isConnection = true;

        isShowDialog = true;

        isSwipeError = false;

        if (activity == null)
            return;

        activity.sendBroadcast(new Intent(SetSwipeTypeActivity.SWIPE_PULL_DOWN));
        //当前显示为刷卡器类型选择页面
        if (isCurrentConnectInvalid)
            return;

        DialogController.getInstance().dismiss();

        launch();
    }

    @Override
    public void onNoDeviceDetected() {
        launch();
    }

    @Override
    public void onInterrupted() {
        sendSwipeStatus(SWIPE_INTERRUPTED_STATUS);
    }

    @Override
    public void deviceAddressList(List<BluetoothDevice> macs, BluetoothDevice newMacs) {

    }

    @Override
    public void onFallback() {
        sendSwipeStatus(ON_FALLBACK_STATUS);
    }

    @Override
    public void onCardSwipeDetected() {
        sendSwipeStatus(ON_CARD_SWIPE_DETECTED);
    }

    @Override
    public void icCardDemotionUsed() {
        sendSwipeStatus(IC_CARD_DEMOTION_USED);
    }

    @Override
    public void otherError(int errorCode, String errorMessage) {

        if (StringUtil.isEmpty(ksn))
            return;

        sendSwipeStatus(SWIPE_ERROR_STATUS, errorCode);
//        if(errorCode == SwiperManager.BLUETOOTH_ERROR){
//            //todo 消息暂时只处理蓝牙刷卡器
//        }
//
//        if (swipeErrorCount == 2 && errorCode == -2) {
//            DialogController.getInstance().showAlertDialog(activity,
//                    "",
//                    getDialogMsg(null, SWIPE_ERROR_HINT),
//                    getString(R.string.plat_swipe_cancel),
//                    getString(R.string.plat_swipe_retry),
//                    new AlertDialog.Builder.AlertDialogClickListener() {
//                        @Override
//                        public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
//                            alertDialog.dismiss();
//                            switch (typeEnum) {
//                                case LEFT_BUTTON:
//                                    cancel(CancelMode.SWIPE_PLUGGED);
//                                    break;
//                                case RIGHT_BUTTON:
//                                    startSwipe();
//                                    break;
//                            }
//                        }
//                    }
//            );
//            swipeErrorCount = 0;
//            return;
//        }
//        switch (errorCode) {
//            case -2:
//                swipeErrorCount++;
//                startSwipe();
//                break;
//            //蓝牙异常
//            case SwiperManager.BLUETOOTH_ERROR:
//                break;
//            //蓝牙检测error
//            case SwiperManager.BLUETOOTH_DETECTOR_ERROR:
//                break;
//        }
    }

    /**
     * SwipeDialogManager Listener
     */
    @Override
    public void onClick(SwipeDialogManager.ClickMode type, SwipeDialogManager.CurrentMode currentMode) {
        switch (type) {
            case LEFT:
                if(swiperManager == null) {
                    return;
                }
                switch (currentMode){
                    case INPUT_PIN:
                        //提示使用硬键盘，点击取消触发
                        swiperManager.cancelPininput();
                        cancel(CancelMode.SWIPE_PLUGGED);
                        break;
                    default:
                        if (isWaitingForSwipe) {
                            swiperManager.stopSwipe();
                            isWaitingForSwipe = false;
                        }
                        cancel(isConnection ? CancelMode.SWIPE_PLUGGED : CancelMode.SWIPE_UNPLUGGED);
                        break;
                }
                swiperManager.resetScreen();
                break;
            case RIGHT:
                switch (currentMode) {
                    case INSERT:
                        setBluetoothSwiperType();
                        break;
                    case STOP:
                        startSwipe();
                        break;
                }
                break;
            case LEFT_TEXT:
                if (currentMode == SwipeDialogManager.CurrentMode.INSERT_CREDIT){
                    goNoCardPayPage();
                }else {
                    start2Help();
                }
                break;
            case RIGHT_TEXT:
                switch (currentMode) {
                    case INSERT:
                        start2BuySwipe();
                        break;
                    case INSERT_CREDIT:
                        goSwiperGeneralProblemPage();
                        break;
                    case SWIPE:
                    case STOP:
                        start2Help();
                        break;
                }
                break;
        }
    }

    /**
     * 设置刷卡模式为蓝牙模式,并开始搜寻设备
     */
    private void setBluetoothSwiperType(){
        BluetoothAdapter.getDefaultAdapter().enable();
        if(activity != null){
            activity.startActivity(new Intent(activity, SwipeChooseBlueToothActivity.class));
        }
    }

    /**
     * 跳转无卡支付页面
     */
    private void goNoCardPayPage(){
        try{
            if(null == activity){
                return ;
            }
            Intent intent =new Intent(activity, ProtocalActivity.class);
            Bundle bundle =new Bundle();
            JSONObject data =new JSONObject();
            data.put(ProtocalActivity.PROTOCAL_TITLE,"无卡还款");
            data.put(ProtocalActivity.PROTOCAL_URL,"yjhk.html");
            bundle.putString(ProtocalActivity.DATA, data.toString());
            intent.putExtra(ProtocalActivity.BUSINESS_BUNDLE_KEY,bundle);
            activity.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *跳转常见问题
     */
    private void goSwiperGeneralProblemPage(){
        try{
            if(null == activity){
                return ;
            }
            Intent intent =new Intent(activity, ProtocalActivity.class);
            Bundle bundle =new Bundle();
            JSONObject data =new JSONObject();
            data.put(ProtocalActivity.PROTOCAL_TITLE,"常见问题");
            data.put(ProtocalActivity.PROTOCAL_URL,"swiper_help/list/list.html");
            bundle.putString(ProtocalActivity.DATA, data.toString());
            intent.putExtra(ProtocalActivity.BUSINESS_BUNDLE_KEY,bundle);
            activity.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Activity getActivity() {
        return activity;
    }


    /**
     * 终端签到回调
     */
    @Override
    public void signInSuccess(TerminalSignInTask.Status status) {
        if(swiperManager == null){
            return;
        }
        if (!swiperManager.isDeviceConnected()) return;
        switch (status) {
            case Bind:
                swiperManager.setCurrentValidKsn();
                startSwipe();
                break;
            case UnBind:
                DialogController.getInstance().showAlertDialog(activity,
                        getString(R.string.plat_swipe_bind),
                        getDialogMsg(status),
                        getString(R.string.plat_swipe_cancel),
                        getString(R.string.plat_swipe_bind_now),
                        new AlertDialog.Builder.AlertDialogClickListener() {
                            @Override
                            public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
                                switch (typeEnum) {
                                    case LEFT_BUTTON:
                                        alertDialog.dismiss();
                                        cancel(CancelMode.SWIPE_PLUGGED);
                                        break;
                                    case RIGHT_BUTTON:
                                        bindUserSwipe();
                                        break;
                                }
                            }
                        }
                );
                break;
            case Disabled:
            case Conflict:
                // 若果用户冲突(Conflict) 且检测到当前连接大于1个刷卡设备时,rightButton显示为“重新选择”
                final boolean isConflict = status == TerminalSignInTask.Status.Conflict && swiperManager.swipePortTypeSize() > 1;
                DialogController.getInstance().showAlertDialog(activity,
                        getString(R.string.plat_swipe_disabled),
                        getDialogMsg(status),
                        getString(R.string.plat_swipe_cancel),
                        isConflict ? getString(R.string.plat_swipe_retry_select) : "重新登录",
                        new AlertDialog.Builder.AlertDialogClickListener() {
                            @Override
                            public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
                                switch (typeEnum) {
                                    case LEFT_BUTTON:
                                        alertDialog.dismiss();
                                        cancel(CancelMode.SWIPE_PLUGGED);
                                        break;
                                    case RIGHT_BUTTON:
                                        if (isConflict) {
                                            alertDialog.dismiss();
                                            start2SelectSwipe();
                                            return;
                                        }
                                        start2Login();
                                        break;
                                }
                            }
                        }
                );
                break;
        }

    }

    @Override
    public void signInFailure() {
        if(swiperManager == null){
            return;
        }
        if (!isConnection)
            return;
        DialogController.getInstance().showAlertDialog(activity,
                "",
                getDialogMsg(null, SWIPE_SIGN_IN_ERROR_HINT),
                getString(R.string.plat_swipe_cancel),
                getString(R.string.plat_swipe_retry),
                new AlertDialog.Builder.AlertDialogClickListener() {
                    @Override
                    public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
                        switch (typeEnum) {
                            case LEFT_BUTTON:
                                alertDialog.dismiss();
                                cancel(CancelMode.SWIPE_PLUGGED);
                                break;
                            case RIGHT_BUTTON:
                                checkSwiperStatus();
                                break;
                        }
                    }
                }
        );
    }


    /**
     * 获取ksn回调
     */
    @Override
    public void GetKsnSuccess(String ksn) {
        statisticGetKSNStatus(StatisticManager.getKsnSuccess, activity);

        this.ksn = ksn;
//        ApplicationEx.getInstance().getUser().setSwiperId(ksn);
        checkSwiperStatus();
    }

    @Override
    public void GetKsnFailure() {
        start2SelectSwipe();

        statisticGetKSNStatus(StatisticManager.getKSNFailure, activity);
        statisticGetKSNStatus(StatisticManager.getKSNFailureDefault, activity);

    }

    @Override
    public FragmentActivity ksnActivity() {
        return activity;
    }

    @Override
    public boolean handleMessage(Message msg) {
        int status = msg.what;

        if (currentSwipeStatus == status){
            return false;
        }

        if (currentSwipeStatus == 0){
            currentSwipeStatus = status;
        }

        previousSwipeStatus = currentSwipeStatus;
        currentSwipeStatus  = status;

        //如果该引用为空,则return
        if (dialogManager == null || swipeListener == null){
            return false;
        }

        switch (currentSwipeStatus){
            //启动刷卡器
            case SWIPE_START_STATUS:
                //启动刷卡回调
                swipeListener.onStartSwiper();
                if(previousSwipeStatus == SWIPE_SWIPE_ERROR_STATUS){
                    currentSwipeStatus = SWIPE_SWIPE_ERROR_STATUS;
                    dialogManager.setTitle(getString(R.string.plat_swipe_starting_card));
                    dialogManager.setStopImage(true);
                }else if(previousSwipeStatus == IC_CARD_DEMOTION_USED){
                    currentSwipeStatus = IC_CARD_DEMOTION_USED;
                    dialogManager.setTitle(getString(R.string.plat_swipe_starting_card));
                }else {

                    //蓝牙刷卡器，交替显示刷卡和插卡动画
                    if(swiperManager.getCurrentSwipeType() == ESwiperType.Bluetooth){
                        dialogManager.showSwipeDialog();
                    } else {
                        //其他刷卡器只显示刷卡动画
                        dialogManager.showOnlySwipeDialog();
                    }
                    dialogManager.setTitle(getString(R.string.plat_swipe_starting_card));
                    dialogManager.setStopImage(true);
                }
                break;
            //等待刷卡
            case SWIPE_WAITING_STATUS:
                if (previousSwipeStatus == SWIPE_SWIPE_ERROR_STATUS){
                    dialogManager.setTitle(getString(R.string.plat_swipe_error_hint));
                }else if(previousSwipeStatus == IC_CARD_DEMOTION_USED){
                    dialogManager.setTitle(getString(R.string.plat_swipe_iccard_demotion_used));
                    dialogManager.stopSwitchGif();
                    dialogManager.setContent(SwipeDialogManager.CurrentMode.INSERT_IC_CARD, "", getString(R.string.plat_swipe_help));
                }else {

                    //如果是蓝牙刷卡器，刷卡对话框title显示"请刷卡或插入磁条卡"
                    if(swiperManager.getCurrentSwipeType() == ESwiperType.Bluetooth){
                        dialogManager.setTitle(getString(R.string.plat_swipe_please_swipe));
                    }else {
                        //如果是音频刷卡器，刷卡对话框title为“请刷磁条卡”
                        dialogManager.setTitle(getString(R.string.plat_swipe_citiaoka));
                    }
                }
                dialogManager.setStopImage(false);
                break;
            //刷卡超时
            case SWIPE_TIMEOUT_STATUS:
                //前一个状态为等待刷卡，则为刷卡超时
                if(previousSwipeStatus == SWIPE_WAITING_STATUS){
                    dialogManager.setTitle(getString(R.string.plat_swipe_timeout_title));
                    dialogManager.setContent(SwipeDialogManager.CurrentMode.STOP, "", getString(R.string.plat_swipe_help));
                    //若为输入pin，则为输入pin超时
                }else if(previousSwipeStatus == INPUT_PIN_STATUS){
                    dialogManager.showInputPinTimeoutDialog();
                    //刷卡超时回调
                    swipeListener.onWaitInputPinTimeout();
                }

                break;
            //刷卡失败
            case SWIPE_SWIPE_ERROR_STATUS:
                //@王炜光 确认修改
                dialogManager.setTitle(getString(R.string.plat_swipe_reading_card));
                dialogManager.setStopImage(true);
                break;
            //input pin timeout
            case INPUT_PIN_STATUS:
                dialogManager.showKeyBoardHintDialog();
                break;
            //刷卡器异常状态
            case SWIPE_ERROR_STATUS:
                int errorCode = Integer.parseInt(msg.obj.toString());
                if(errorCode == SwiperManager.BLUETOOTH_ERROR){
                    switch (previousSwipeStatus) {
                        //如果onReadCardPin执行后异常则回调onSecondIssuanceFail
                        case ON_READ_CARD_PIN_FINISHED_STATUS:
                            swipeListener.onSecondIssuanceFail();
                            break;
                        //前一个状态为检测到读卡状态，提示读卡失败对话框
                        case ON_CARD_SWIPE_DETECTED:
                            dialogManager.setTitle(getString(R.string.plat_read_card_error));
                            dialogManager.setContent(SwipeDialogManager.CurrentMode.STOP, "", getString(R.string.plat_swipe_help));
                            break;
                    }
                }
                //非蓝牙
                else {
                    dialogManager.showNormalErrorDialog();
                }
                break;
            //刷卡器中断状态
            case SWIPE_INTERRUPTED_STATUS:
                //
                if(previousSwipeStatus == ON_READ_CARD_PIN_FINISHED_STATUS){
                    swipeListener.onSecondIssuanceFail();
                }else {
                    DialogController.getInstance().dismiss();
                    cancel(CancelMode.SWIPE_PLUGGED);
                    //清屏
                    swiperManager.resetScreen();
                }
                break;
            //readCardPin执行结束状态
            case ON_READ_CARD_PIN_FINISHED_STATUS:

                break;
            //执行onReadCardPin
            case RUN_READ_CARD_PIN_STATUS:
                swipeListener.onReadCardPin(cardInfo, (SwipeDefine.SwipeCardType)msg.obj);
                //
                sendSwipeStatus(ON_READ_CARD_PIN_FINISHED_STATUS);
                break;
            //onEmvFinished
            case ON_EMV_FINISHED_STATUS:
                boolean isEMVFinished = (Boolean)msg.obj;

                //如果b为false，且前一个状态为等待刷卡，则为拔出ic卡error，提示用户读卡失败，重试
                if(isEMVFinished == false && previousSwipeStatus == SWIPE_WAITING_STATUS ){
                    startSwipe();
                }

                break;
            //onFallback
            case ON_FALLBACK_STATUS:
                //如果正在读卡，则提示读卡失败，请重新刷卡
                if(previousSwipeStatus == ON_CARD_SWIPE_DETECTED){
                    dialogManager.setTitle(getString(R.string.plat_swipe_readIC_error));
                    dialogManager.setContent(SwipeDialogManager.CurrentMode.STOP, "", getString(R.string.plat_swipe_help));
                }
                break;
            //开始读取卡
            case ON_CARD_SWIPE_DETECTED:
                dialogManager.setTitle(getString(R.string.plat_swipe_reading_card));
                break;
            //IC卡降级使用提示
            case IC_CARD_DEMOTION_USED:
                startSwipe();
                break;
        }
        return false;
    }



    /**
     * 获取dialog提示信息
     */
    private String getDialogMsg(TerminalSignInTask.Status status, int... errorCode) {
        if (status == null) {
            switch (errorCode[0]) {
                case SWIPE_ERROR_HINT:
                    return getString(R.string.plat_swipe_prompt_swipe_error_hint);
                case COMMUNICATION_ERROR_HINT:
                    return getString(R.string.plat_swipe_prompt_communication_error_hint);
                case BIND_SWIPE_FINISH_HINT:
                    return getString(R.string.plat_swipe_prompt_bind_finish_hint);
                case BIND_SWIPE_ERROR_HINT:
                    return getString(R.string.plat_swipe_prompt_bind_swipe_error_hint);
                case SWIPE_SIGN_IN_ERROR_HINT:
                    return getString(R.string.plat_swipe_prompt_swipe_signin_error_hint);
            }
        }
        switch (status) {
            case UnBind:
                return getString(R.string.plat_swipe_prompt_unbind);
            case Disabled:
                return getString(R.string.plat_swipe_prompt_disabled);
            case Conflict:
                return getString(R.string.plat_swipe_prompt_conflict0) + status.getUserId() + getString(R.string.plat_swipe_prompt_conflict1);
        }
        return "";
    }

    /**
     * 获取文字
     *
     * @param id
     * @return
     */
    private String getString(int id) {
        if(activity == null) return "";
        return activity.getResources().getString(id);
    }

    public enum CancelMode {
        /**
         * 刷卡器已插入
         */
        SWIPE_PLUGGED("0"),
        /**
         * 刷卡器未插入
         */
        SWIPE_UNPLUGGED("1"),
        /**
         * 设置刷卡界面返回
         */
        SET_SWIPE("2");

        private String value;

        private CancelMode(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

}
