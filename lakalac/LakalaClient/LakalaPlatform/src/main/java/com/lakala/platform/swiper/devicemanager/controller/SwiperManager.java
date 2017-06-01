package com.lakala.platform.swiper.devicemanager.controller;

import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.lakala.core.swiper.Adapter.SwiperAdapterBluetooth;
import com.lakala.core.swiper.ESwiperType;
import com.lakala.core.swiper.ICCardInfo;
import com.lakala.core.swiper.SwiperController;
import com.lakala.core.swiper.SwiperControllerBluetoothListener;
import com.lakala.core.swiper.SwiperDefine;
import com.lakala.cswiper6.bluetooth.CSwiperController;
import com.lakala.cswiper6.bluetooth.CommandProtocolVer;
import com.lakala.library.exception.SwiperException;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.swiper.devicemanager.bluetooth.NLDevice;
import com.newland.mtype.ModuleType;
import com.newland.mtype.module.common.emv.AIDConfig;
import com.newland.mtype.module.common.emv.CAPublicKey;
import com.newland.mtype.module.common.emv.EmvTransInfo;
import com.newland.mtype.module.common.emv.SecondIssuanceRequest;
import com.newland.mtype.module.common.security.GetDeviceInfo;
import com.newland.mtype.util.ISOUtils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Vinchaos api on 14-1-11.
 */
public class SwiperManager implements SwiperControllerBluetoothListener {

    //蓝牙探测error
    public static final int BLUETOOTH_DETECTOR_ERROR = 999;
    //蓝牙error
    public static final int BLUETOOTH_ERROR          = 998;

    //二次授信
    private static SecondIssuanceRequest secondIssuanceRequest;
    static{
        secondIssuanceRequest = new SecondIssuanceRequest();
        secondIssuanceRequest.setAuthorisationResponseCode("00");
        secondIssuanceRequest.setIssuerScriptTemplate1(new byte[] {0x01, 0x01, 0x01, 0x01, 0x01, 0x01,0x01, 0x01, 0x01 });
        secondIssuanceRequest.setIssuerScriptTemplate2(new byte[] {0x01, 0x01, 0x01, 0x01, 0x01, 0x01,0x01, 0x01, 0x01 });
        secondIssuanceRequest.setIssuerAuthenticationData(new byte[] {0x01, 0x01, 0x01, 0x01, 0x01, 0x01,0x01, 0x01, 0x01 });
    }

    private static final String Q201        = "0001";
    private static final String Q202        = "0002";
    private static final String Q203        = "0003";
    private static final String Q206        = "0008";
    private static final String PAYFI       = "0007";
    private static final String QV30E       = "0009";
    private static final String LKLMobile   = "0010";
    private static final String Bluetooth   = "0011";
    /**
     * 默认刷卡器类型Key
     */
    private static final String DEFAULT_KEY = "default_swiper";

    private static SwiperManager instance;

    private SwiperController swiperController;
    /**
     * 保存默认刷卡器类型
     */
    private SharedPreferences defaultSwiper;

    //刷卡器监听是否有效
    private boolean isListenerEnable = true;

    private SwiperManagerListener listener;

    private String validateKSN = "";

    private ApplicationEx applicationEx;

    private List<SwiperDefine.SwiperPortType> swipePortTypes = new ArrayList<SwiperDefine.SwiperPortType>();

    private boolean forceQpboc = false;

    //连接的蓝牙名字
    private String connectedBluetoothName;

    private SwiperManager(SwiperManagerListener listener) {
        this.listener           = listener;
        this.applicationEx      = ApplicationEx.getInstance();
        this.defaultSwiper      = PreferenceManager.getDefaultSharedPreferences(applicationEx);
        this.swiperController   = SwiperController.getInstance(this, applicationEx);
        setSwiperType(ESwiperType.Bluetooth);

    }

    public static synchronized SwiperManager getInstance(SwiperManagerListener listener) {
        if (instance == null) {
            instance = new SwiperManager(listener);
        }
        return instance;
    }


    private void initStartParameter(String title, SwipeDefine.SwipeCollectionType collectionType){

        switch(collectionType){
            case QUERY:
                setStartParameter(1, "3");
                setStartParameter(4, 31);// TRADE_TYPE_8583
                setStartParameter(5, 25);// TRADE_TYPE_CUSTOM
                setStartParameter(6, false);// ONLINE_FLAG
                break;
            case CONSUMPTION:
                setStartParameter(1, title);
                setStartParameter(4, 0);
                setStartParameter(5, 1);
                setStartParameter(6, false);
                break;
        }

    }

    /**
     * 启动刷卡器,等待刷卡
     */
    public void startSwipe(String title, String amount, SwipeDefine.SwipeCollectionType collectionType) {
        if (swiperController == null) return;
        stopSwipe();

        //如果是蓝牙刷卡器，则调用蓝牙刷卡器启动方法，需要传递参数，否则使用默认启动方法
        if(swiperController.getCurrentAdapter() instanceof SwiperAdapterBluetooth){
            initStartParameter(title, collectionType);
            startSwipe(amount, new ModuleType[]{ModuleType.COMMON_SWIPER, ModuleType.COMMON_ICCARD, ModuleType.COMMON_QPBOC});

        }else {
            try {
                swiperController.startSwiper();
            } catch (SwiperException e) {
                LogUtil.print(e);
            }
        }

    }

    public void starSwieper(){

        if (swiperController == null) return;
        stopSwipe();

        try {
            swiperController.startSwiper();
        } catch (SwiperException e) {
            LogUtil.print(e);
        }

    }

    /**
     * 启动刷卡器with amount
     *
     * @param amount
     */
    public void startSwipe(String amount){


        if (swiperController == null) return;
        try {
            swiperController.stopSwiper();
            swiperController.startSwipe(amount);
        } catch (SwiperException e) {
            e.printStackTrace();
        }
    }

    /**
     * start swipe with amount and ModuleType
     *
     * @param amount
     * @param cardReaderModuleTypes
     */
    public void startSwipe(String amount, ModuleType[] cardReaderModuleTypes){
        if (swiperController == null) return;
        try {
            swiperController.stopSwiper();
            swiperController.startSwipe(amount, cardReaderModuleTypes);
        } catch (SwiperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束刷卡
     */
    public void stopSwipe() {
        if (swiperController == null) return;
        try {
            swiperController.stopSwiper();
        } catch (SwiperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动刷卡器，等待用户输入密码
     */
    public void startInputPIN(){
        if(swiperController == null) return;
        try {
            swiperController.startInputPIN();
        } catch (SwiperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置启动参数
     *
     * @param index
     * @param data
     */
    public void setStartParameter(int index, Object data){
        if(swiperController == null) return;
        try {
            swiperController.setStartParameter(index, data);
        } catch (SwiperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 收款宝重置屏幕
     */
    public void resetScreen(){
        if(swiperController == null) return;
        try {
            swiperController.resetScreen();
        } catch (SwiperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加AID
     * @param aidConfig
     */
    public void addAID(AIDConfig aidConfig){
        if(swiperController == null) return;
        try {
            swiperController.addAID(aidConfig);
        } catch (SwiperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除AID
     * @param aid
     */
    public void deleteAID(byte[] aid){
        if(swiperController == null) return;
        try {
            swiperController.deleteAID(aid);
        } catch (SwiperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空AID
     */
    public void clearAllAID(){
        if(swiperController == null) return;
        try {
            swiperController.clearAllAID();
        } catch (SwiperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加公钥
     * @param rid
     * @param capk
     */
    public void addCAPublicKey(byte[] rid, CAPublicKey capk){
        if (swiperController == null) return;
        try {
            swiperController.addCAPublicKey(rid, capk);
        } catch (SwiperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除公钥
     * @param rid
     * @param index
     */
    public void deleteCAPublicKey(byte[] rid, int index){
        if(swiperController == null) return;
        try {
            swiperController.deleteCAPublicKey(rid, index);
        } catch (SwiperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空公钥
     * @param rid
     */
    public void clearAllCAPublicKey(byte[] rid){
        if(swiperController == null) return;
        try {
            swiperController.clearAllCAPublicKey(rid);
        } catch (SwiperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 二次授权
     */
    public void doSecondIssuance(SecondIssuanceRequest request){
        if(swiperController == null) return;
        try {
            swiperController.doSecondIssuance(request);
        } catch (SwiperException e) {
            e.printStackTrace();
        }
    }

    /**
     *  应用撤销密码输入调用
     */
    public void cancelPininput() {
        if(swiperController == null) return;
        try {
            swiperController.cancelPininput();
        } catch (SwiperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 应用撤销插卡或刷卡调用
     */
    public  void cancelCardRead(){
        if(swiperController == null) return;
        try {
            swiperController.cancelCardRead();
        } catch (SwiperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束EMV
     * true : 正常结束
     * false : 中断
     *
     * @param b
     */
    public void cancelEmv(boolean b){
        if(swiperController == null) return;
        try {
            swiperController.cancelEmv(b);
        } catch (SwiperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设备是否连接
     */
    public boolean isDeviceConnected() {
        LogUtil.print("QB",swiperController.getSwiperType());
        return swiperController.swiperIsConnected(swiperController.getSwiperType() == ESwiperType.Bluetooth ? SwiperDefine.SwiperPortType.TYPE_BLUETOOTH : SwiperDefine.SwiperPortType.TYPE_AUDIO);
    }

    /**
     * 验证刷卡器
     */
    public boolean isLastSwiper(String newKsn) {
        return newKsn.equalsIgnoreCase(applicationEx.getSession().getCurrentKSN());
    }

    public void setCurrentValidKsn() {
        applicationEx.getSession().setCurrentKSN(validateKSN);
    }

    private CommandProtocolVer commandProtocolVer;

    private GetDeviceInfo deviceInfo;

    public void setDeviceInfo(GetDeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    /**
     * 获取验证时得到的KSN,该方法会阻塞线程
     */
    public String getKsn() {

        LogUtil.print("SwiperManager  exc method = " + "getKSN ");

        try {
            if (swiperController != null) {
                if (swiperController.getSwiperState() != SwiperDefine.SwiperControllerState.STATE_IDLE) {
                    swiperController.stopSwiper();
                }
                validateKSN = swiperController.getKsn();
                commandProtocolVer = swiperController.getCommandProtocolVer();
                initDeviceInfo();
            }
        } catch (SwiperException e) {
            LogUtil.print(e);
            validateKSN = "";
        }
        return validateKSN;
    }

    /**
     * 设置当前刷卡器
     *
     * @param type 枚举类型
     */
    public void setSwiperType(ESwiperType type) {
        if (swiperController == null){
            return;
        }
        if (type == null) {
            type = getSwipeType(defaultSwiper.getString(DEFAULT_KEY, Q203));
        }
        try {
            swiperController.setSwiperType(type);
            //蓝牙设备默认不保存
            if (type != ESwiperType.Bluetooth) {
                defaultSwiper.edit().putString(DEFAULT_KEY, type.toString()).commit();
            }
        } catch (SwiperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置刷卡器
     *
     * @param type 字符串类型
     */
    public void setSwiperType(String type) {
        //如果当前设备为lakalaMobile,且没有检测到其他刷卡器,则直接设置拉风驱动

        setSwiperType(getSwipeType(type));
    }

    /**
     * 设置现在手机检测到的刷卡器种类
     *
     * @param type
     */
    public void addSwipePortType(SwiperDefine.SwiperPortType type) {
        if(!swipePortTypes.contains(type)){
            swipePortTypes.add(type);
        }
        LogUtil.print("lakala", "add type size : " + swipePortTypes.size());
    }

    /**
     * 删除手机检测到的刷卡器类型
     *
     * @param type
     */
    public void removeSwipePortType(SwiperDefine.SwiperPortType type) {
        if(swipePortTypes.size() == 0) return;
        if(swipePortTypes.contains(type)){
            swipePortTypes.remove(type);
        }
        LogUtil.print("lakala", "remove type size : " + swipePortTypes.size());
    }

    /**
     * 清空检测到的刷卡器类型
     */
    public void clearSwipePortType() {
        swipePortTypes.clear();
    }

    /**
     * 是否检测到其他刷卡器类型
     *
     * @return
     */
    public boolean isSwipePortTypesEmpty() {
        return swipePortTypes.size() == 0;
    }

    /**
     * 检测到其他刷卡器类型列表size
     *
     * @return
     */
    public int swipePortTypeSize(){
        return swipePortTypes.size();
    }

    /**
     * 获取当前设置的刷卡器型号
     *
     * @return
     */
    public ESwiperType getCurrentSwipeType(){
        return swiperController != null ? swiperController.getSwiperType() : null;
    }

    /**
     * 获取当前刷卡器类型
     *
     * @return
     */
    public SwiperDefine.SwiperPortType getCurrentPortType(){
        return getCurrentSwipeType().getPortType();
    }

    /**
     * 设置监听是否可用
     */
    public void setEnableListener(boolean enable) {
        isListenerEnable = enable;
        swiperController.enableListener(enable);
    }

    private NLDevice nlDevice;

    public void setConnectionDevice(NLDevice nlDevice){
        if(nlDevice == null)
            return;
        this.nlDevice = nlDevice;
        setConnectionBluetooth(nlDevice.getName());
        connectBluetooth(nlDevice.getMacAddress());

    }


    /**
     * 连接蓝牙设备
     * @param mac
     */
    public boolean connectBluetooth(String mac){
        if (swiperController == null) {
            return false;
        }
        return swiperController.setConnectParams(new String[]{"btaddr:" + mac});
    }
    /**
     * 判断监听是否有效
     *
     * @return
     */
    public boolean isListenerEnable() {
        return isListenerEnable;
    }

    /**
     * 销毁
     */
    public void onDestroy() {

        //清空当前设备
        ApplicationEx.getInstance().getSession().setCurrentKSN("");

        if (null != swiperController) {
            swiperController.onDestroy();
            swiperController = null;
        }
        instance = null;
    }

    /**
     * 2014.7.1 sdk版本meSdk-1.0.2 加入ic卡判断 当3位服务代码的第一位的值为“2”或者“6”时表示该卡可能存在IC卡
     * @return 是否为存在ic卡
     */
    private boolean isICCard(){
        if (swiperController == null) {
            return false;
        }
        Map<String,Object> extendData = null;
        try {
            extendData = swiperController.getSwipeCardExtendData();
        } catch (SwiperException e) {
            return false;
        }

        if (extendData == null){
            return false;
        }

        Object serviceCode = extendData.get(CSwiperController.SERVICECODE);
        try {
            LogUtil.d(this.getClass().getSimpleName()," serviceCode is " + serviceCode);
            char s = serviceCode.toString().charAt(0);
            return s == '2'|| s == '6';
        } catch (Exception e){
            return false;
        }
    }

    /**
     * 获取刷卡器类型
     */
    private ESwiperType getSwipeType(String id) {
        if (id.equals(Q201)) {
            return ESwiperType.Q201;
        }
        if (id.equals(Q202)) {
            return ESwiperType.Q202;
        }
        if (id.equals(Q203)) {
            return ESwiperType.Q203;
        }
        if (id.equals(Q206)) {
            return ESwiperType.Q206;
        }
        if (id.equals(PAYFI)) {
            return ESwiperType.PayFi;
        }
        if (id.equals(QV30E)) {
            return ESwiperType.QV30E;
        }
        if (id.equals(LKLMobile)) {
            return ESwiperType.LKLMobile;
        }
        if (id.equals(Bluetooth)) {
            return ESwiperType.Bluetooth;
        }
        return null;
    }

    private boolean isListenerValidate(){
        return listener != null;
    }

    /**
     * 设置当前连接蓝牙名称
     *
     * @param name
     */
    public void setConnectionBluetooth(String name){
        this.connectedBluetoothName = name;
    }

    /**
     * 检查判断当前刷卡设备是否存在硬键盘
     *
     * @return
     */
    private SwipeDefine.SwipeKeyBoard checkKeyBoard(){

        return SwipeDefine.SwipeKeyBoard.YES;
        //收款宝目前的设备都支持密码输入
//        SwipeDefine.SwipeKeyBoard swipeKeyBoard = SwipeDefine.SwipeKeyBoard.NO;
//
//        //如果当前连接为蓝牙刷卡器则判断键盘
//        if(getCurrentSwipeType() == ESwiperType.Bluetooth){
//
//            char[] allocation = fetchProdAllocation();
//
//            //不支持获取设备能力,判断连入设备名称
//            if(allocation.length == 0){
//                if (connectedBluetoothName.startsWith("L-M35") ||
//                        connectedBluetoothName.startsWith("L-ME30")) {
//                    swipeKeyBoard = SwipeDefine.SwipeKeyBoard.YES;
//                }
//            }
//            //支持获取设备能力
//            else {
//                //7位为0 则没有键盘
//                swipeKeyBoard = String.valueOf(allocation[7]).equals("0") ?
//                        SwipeDefine.SwipeKeyBoard.NO : SwipeDefine.SwipeKeyBoard.YES;
//            }
//        }
//        return swipeKeyBoard;
    }

    /**
     * 获取设备能力
     * 第1位:屏幕
     * 第2位:内存
     * 第3位:卡功能
     * 第4位:扩展功能
     * 第5位:远距通信
     * 第6位:本地通信
     * 第7,8位:键盘,颜色
     *
     * @return
     */
    private char[] fetchProdAllocation(){
        char[] allocation = new char[0];
        try {
            byte[] temp = swiperController.fetchProdAllocation();
            //是否支持获取设备能力
            if(supportFetchAllocation(temp)){
                allocation = getChars(temp);
            }
        } catch (SwiperException e) {
            LogUtil.print(e);
        }
        if(allocation.length > 8){
            LogUtil.print("屏幕 : " + allocation[0] +
                    "\n内存 : " + allocation[1] +
                    "\n卡功能 : " + allocation[2] +
                    "\n扩展功能 : " + allocation[3] +
                    "\n远距通信 : " + allocation[4] +
                    "\n本地通信 : " + allocation[5] +
                    "\n键盘/颜色 : " + allocation[6] + " , " + allocation[7]);
        }

        return allocation;
    }

    /**
     * 是否支持获取设备能力
     *
     * @return
     */
    private boolean supportFetchAllocation(byte[] temp){
        if(temp == null) return false;

        boolean support = false;
        //如果每位的值都为0x00 则不支持获取设备能力
        for (int i= 0; i < temp.length; i++){
            if(temp[i] != 0x00){
                support = true;
                break;
            }
        }
        return support;
    }

    /**
     * bytes 2 chars
     *
     * @param bytes
     * @return
     */
    private char[] getChars (byte[] bytes) {
        Charset cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put (bytes);
        bb.flip();
        CharBuffer cb = cs.decode (bb);

        return cb.array();
    }


    /**
     * 刷卡器回调
     */
    @Override
    public void onCardSwipeDetected() {
        LogUtil.print("onCardSwipeDetected");
        if(!isListenerValidate()) return;
        listener.onCardSwipeDetected();
    }

    @Override
    public void onDecodeCompleted(String formatID, String ksn,
                                  String encTracks,
                                  int track1Length,
                                  int track2Length,
                                  int track3Length,
                                  String randomNumber,
                                  String maskedPANString,
                                  String expiryDate,
                                  String cardHolderName) {
        LogUtil.print("onDecodeCompleted",
                "\nformatID: " + formatID
                        + "\nksn: " + ksn
                        + "\nencTracks: " + encTracks
                        + "\ntrack1Length: " + track1Length
                        + "\ntrack2Length: " + track2Length
                        + "\ntrack3Length: " + track3Length
                        + "\nrandomNumber: " + randomNumber
                        + "\nmaskedPANString: " + maskedPANString
                        + "\nexpiryDate: " + expiryDate
                        + "\ncardHolderName: " + cardHolderName
        );
        if(!isListenerValidate()) return;
        //默认不用硬键盘
        SwipeDefine.SwipeKeyBoard swipeKeyBoard = SwipeDefine.SwipeKeyBoard.NO;

//        if (swiperController.getCurrentAdapter() instanceof SwiperAdapterBluetooth && isICCard()){
//            LogUtil.print("wcwcwc", "该卡是IC卡,请将卡插入卡槽使用");
//            listener.icCardDemotionUsed();
//            return;
//        }
        //若当前刷卡器为蓝牙设备，则需要判断是否需要用设备的硬键盘
        if (swiperController.getSwiperType() == ESwiperType.Bluetooth){
            swipeKeyBoard = checkKeyBoard();
        }

        String track1 = "";
        String encTrack = "";
        String[] tracks = encTracks.split("@");
        if (tracks.length == 1) {
            encTrack = encTracks;
        } else {
            track1 = tracks[0];
            encTrack = tracks[1];
        }

        listener.onSwipeSuccess(encTracks, randomNumber, maskedPANString.replace("X", "*"), swipeKeyBoard);
    }

    @Override
    public void onDecodeError(SwiperDefine.SwiperControllerDecodeResult decodeState) {
        LogUtil.print("onDecodeError");
        if(!isListenerValidate()) return;
        listener.onSwipeError();
    }

    @Override
    public void onDecodingStart() {
        LogUtil.print("onDecodingStart");
    }

    @Override
    public void onError(int errorCode, String errorMessage) {
        LogUtil.print("onError :" + errorMessage + " errorCode : " + errorCode);
        if(!isListenerValidate()) return;
        listener.otherError(errorCode, errorMessage);
    }

    @Override
    public void onInterrupted() {
        LogUtil.print("onInterrupted");
        if(!isListenerValidate()) return;
        listener.onInterrupted();
    }

    @Override
    public void onNoDeviceDetected() {
        LogUtil.print("onNoDeviceDetected");
        if(!isListenerValidate()) return;
        listener.onNoDeviceDetected();
    }

    @Override
    public void onTimeout() {
        LogUtil.print("onTimeout");
        if(!isListenerValidate()) return;
        listener.onTimeOut();
    }

    @Override
    public void onWaitingForCardSwipe() {
        LogUtil.print("onWaitingForCardSwipe");
        if(!isListenerValidate()) return;
        listener.onWaitingForSwipe();
    }

    @Override
    public void onWaitingForDevice() {
        LogUtil.print("onWaitingForDevice");
    }

    @Override
    public void onGetKsnCompleted(String ksn) {
        LogUtil.print("onGetKsnCompleted");
    }

    @Override
    public void onDeviceConnected(SwiperDefine.SwiperPortType type) {
        LogUtil.print("onDeviceConnected");
        if(!isListenerValidate()) return;
        listener.onDeviceConnected(type);
    }

    @Override
    public void onDeviceDisconnected(SwiperDefine.SwiperPortType type) {
        LogUtil.print("onDeviceDisconnected");
        if(!isListenerValidate()) return;

        listener.onDeviceDisconnected(type);
    }

    @Override
    public void onCurrentSwiperConnected() {
        LogUtil.print("onCurrentSwiperConnected");
        if(!isListenerValidate()) return;
        listener.onCurrentConnected();
    }

    @Override
    public void onCurrentSwiperDisconnected() {
        LogUtil.print("onCurrentSwiperDisconnected");
        if(!isListenerValidate()) return;
        listener.onCurrentDisconnected();

    }
    // bluetooth and collection
    @Override
    public void deviceAddressList(List<BluetoothDevice> macs, BluetoothDevice newMacs) {
        LogUtil.print("deviceAddressList" + " list : " + macs.size());
        if(!isListenerValidate()) return;
        listener.deviceAddressList(macs, newMacs);
    }

    @Override
    public void detectorError(String id, Object data) {
        LogUtil.print("detectorError" + " id : " + id + " list : " + data);
        if(!isListenerValidate()) return;
        listener.otherError(BLUETOOTH_DETECTOR_ERROR, id);
    }

    @Override
    public void onRequestSelectApplication(EmvTransInfo context) {
        LogUtil.print("onRequestSelectApplication" + " \n " + context);
    }

    @Override
    public void onRequestTransferConfirm(EmvTransInfo context) {
        LogUtil.print("onRequestTransferConfirm" + " \n " + context);
    }

    @Override
    public void onRequestPinEntry(EmvTransInfo context) {
        LogUtil.print("onRequestPinEntry" + " \n " + context);
    }

    @Override
    public void onRequestOnline(ICCardInfo icCardInfo) {
        LogUtil.print("onRequestOnline" + " \n cardNo : " + icCardInfo.getPan());
        if(!isListenerValidate()) return;

        //默认不用硬键盘
        SwipeDefine.SwipeKeyBoard swipeKeyBoard = SwipeDefine.SwipeKeyBoard.NO;

        //若当前刷卡器为蓝牙设备，则需要判断是否需要用设备的硬键盘
        if (swiperController.getSwiperType() == ESwiperType.Bluetooth){
            swipeKeyBoard = checkKeyBoard();
        }
        listener.onReadICCardCompleted(icCardInfo, swipeKeyBoard);
    }

    @Override
    public void onEmvFinished(boolean isSuccess, ICCardInfo icCardInfo) {
        LogUtil.print("onEmvFinished" + " isSuccess : " + isSuccess + " \n " + icCardInfo);
        if(!isListenerValidate()) return;
        listener.onEmvFinished(isSuccess, icCardInfo);
    }

    @Override
    public void onFallback(EmvTransInfo context) {
        LogUtil.print("onFallback" + " \n " + context);
        if(!isListenerValidate()) return;
        listener.onFallback();
    }

    @Override
    public void onError(String e) {
        LogUtil.print("onError" + " " + e);
        if(!isListenerValidate()) return;
        listener.otherError(BLUETOOTH_ERROR, e);
    }

    @Override
    public void onPinInputCompleted(String randomNumber, String PIN, int length, byte[] macRandom, byte[] mac) {
        LogUtil.print("onPinInputCompleted" + " randomNumber : " + randomNumber + " PIN : " + PIN + " length : " + length);
        if(!isListenerValidate()) return;
        listener.onPinInputCompleted(randomNumber, PIN, length, isCommandProVerTwo, macRandom, mac);
    }

    @Override
    public void onWaitingForPinEnter() {
        LogUtil.print("onWaitingForPinEnter");
        listener.onWaitingForPinEnter();
    }

    @Override
    public void onResetScreenCompleted() {
        LogUtil.print("onResetScreenCompleted");
    }


    public NLDevice getNlDevice(){
        return nlDevice;
    }

    public boolean isPhyConnectSwiper(){
        return getCurrentSwipeType() != ESwiperType.Bluetooth;
    }

    /**
     * 查询当前设备是否支持PBOC
     * @return
     */
    public boolean isPbocSupportedSwiper(){
        ESwiperType type = getCurrentSwipeType();
        return type !=ESwiperType.QV30E;
    }

    private boolean isCommandProVerTwo = false;


    private void setRevocationParameter(){

        setStartParameter(1, "2");
        setStartParameter(4, 2);// TRADE_TYPE_8583
        setStartParameter(5, 25);// TRADE_TYPE_CUSTOM
        setStartParameter(6, true);// ONLINE_FLAG
    }

    private void setBalanceParameter(){
        setStartParameter(1, "3");
        setStartParameter(4, 31);// TRADE_TYPE_8583
        setStartParameter(5, 25);// TRADE_TYPE_CUSTOM
        setStartParameter(6, true);// ONLINE_FLAG
    }

    private void setConsuptionParameter(String name){
        setStartParameter(1, name);
        setStartParameter(4, 0);
        setStartParameter(5, 1);
        setStartParameter(6, true);
    }


    public void startTrans(TransFactor transFactor) throws SwiperException {

        if (swiperController == null){
            throw new SwiperException("HHHH","swiperController is null");
        }

        stopSwipe();
        isCommandProVerTwo = (commandProtocolVer != CommandProtocolVer.VERSION_ONE);
        if(TransactionType.Revocation == transFactor.getTransactionType()){
            setRevocationParameter();
            transFactor.setAmount(null);
        }else if(TransactionType.Query == transFactor.getTransactionType()){

            setBalanceParameter();
            transFactor.setAmount(null);
        }else{

            setConsuptionParameter(transFactor.getTransactionType().getValue());

        }



        if(isCommandProVerTwo){

            setCommandProVer(commandProtocolVer);
            //当前设备指令版本为1.0或不支持新流程
            ModuleType[] moduleTypes;
            if(transFactor.isIcSupported() && !transFactor.isDownGrade()){

                if(transFactor.getTransactionType().isSupportQPBOC() && isQpbocSupported())
                    moduleTypes = forceQpboc? new ModuleType[]{ModuleType.COMMON_NCCARD} :new ModuleType[]{ModuleType.COMMON_SWIPER, ModuleType.COMMON_ICCARD, ModuleType.COMMON_NCCARD};
                else
                    moduleTypes  =  new ModuleType[]{ModuleType.COMMON_SWIPER, ModuleType.COMMON_ICCARD};

            }else{
                moduleTypes = new ModuleType[]{ModuleType.COMMON_SWIPER};
            }
            try {
                swiperController.startSwipe(transFactor.getAmount(),
                        moduleTypes,
                        transFactor.getTransactionType().getBusId().getBytes(), ISOUtils.str2bcd
                                (transFactor.getServiceCode(),false),
                        transFactor.getAdditionalMsg() == null ? null : transFactor.getAdditionalMsg().getBytes("GBK"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }else {
            setCommandProVer(CommandProtocolVer.VERSION_ONE);
            swiperController.startSwipe(transFactor.getAmount(), (transFactor.isIcSupported() && !transFactor.isDownGrade()) ? new ModuleType[]{ModuleType.COMMON_SWIPER, ModuleType.COMMON_ICCARD} : new ModuleType[]{ModuleType.COMMON_SWIPER});

        }

    }

    @Deprecated
    public void startMsc(String amount) {

        try{
            if (swiperController != null) {
                stopSwipe();
                swiperController.startSwipe(amount, new ModuleType[]{ModuleType.COMMON_SWIPER});
            }
        }catch (Exception e){

        }
    }

    @Deprecated
    public void startMscAndIC(String amount) {
        try{
            if (swiperController != null) {
                stopSwipe();
                swiperController.startSwipe(amount, new ModuleType[]{ModuleType.COMMON_SWIPER, ModuleType.COMMON_ICCARD});
            }
        }catch (Exception e){
            LogUtil.print(e);
        }

    }

    public void setListener(SwiperManagerListener listener) {
        this.listener = listener;
    }

    public void setNlDevice(NLDevice nlDevice) {
        this.nlDevice = nlDevice;
    }


    public void disconnect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{

                    //清空当前设备
                    ApplicationEx.getInstance().getSession().setCurrentKSN("");

                    final ESwiperType eSwiperType = swiperController.getSwiperType();
                    if(ESwiperType.Bluetooth == eSwiperType)
                        swiperController.deleteSwiper();


                }catch (Exception e){}
            }
        }).start();

    }

    public void setCommandProVer(CommandProtocolVer commandProVer) throws SwiperException {
        if(swiperController.getCurrentAdapter() instanceof SwiperAdapterBluetooth){
            swiperController.setProtocolVer(commandProVer);
        }

    }

    private boolean isQpbocSupported = false;

    public void initDeviceInfo(){
        if(swiperController.getCurrentAdapter() instanceof SwiperAdapterBluetooth){
            deviceInfo =  swiperController.getDeviceInfo();
            isQpbocSupported = swiperController.isSupportNCCARD();

        }


    }

    public boolean isQpbocSupported() {
        return isQpbocSupported;
    }

    public GetDeviceInfo getDeviceInfo(){

        return deviceInfo;

    }

    public boolean isSupportNCCARD(){
        return swiperController.isSupportNCCARD();
    }

    @Override
    public void onQPBOCFinished(ICCardInfo icCardInfo) {
        if(!isListenerValidate()) return;
        listener.onQPBOCFinished(icCardInfo);
    }

    @Override
    public void onQPBOCDenied() {
        if(!isListenerValidate()) return;
        listener.onQPBOCDenied();

    }

    public boolean isForceQpboc() {
        return forceQpboc;
    }

    public void setForceQpboc(boolean forceQpboc) {
        this.forceQpboc = forceQpboc;
    }
}
