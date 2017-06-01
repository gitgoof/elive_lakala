package com.lakala.core.swiper;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.lakala.core.swiper.Adapter.SwiperAdapter;
import com.lakala.core.swiper.Adapter.SwiperAdapterBluetooth;
import com.lakala.core.swiper.Adapter.SwiperAdapterQV30E;
import com.lakala.core.swiper.Adapter.SwiperCollectionAdapter;
import com.lakala.core.swiper.Adapter.SwiperEmvAdapter;
import com.lakala.core.swiper.Adapter.SwiperEmvAdapterListener;
import com.lakala.core.swiper.Detector.SwiperBluetoothDetectorListener;
import com.lakala.core.swiper.Detector.SwiperDetector;
import com.lakala.core.swiper.Detector.SwiperDetectorAudio;
import com.lakala.core.swiper.Detector.SwiperDetectorBluetooth;
import com.lakala.core.swiper.Detector.SwiperDetectorLKLMobile;
import com.lakala.core.swiper.Detector.SwiperDetectorWIFI;
import com.lakala.cswiper6.bluetooth.CommandProtocolVer;
import com.lakala.library.encryption.Base64;
import com.lakala.library.exception.SwiperException;
import com.lakala.library.util.LogUtil;
import com.newland.mtype.ModuleType;
import com.newland.mtype.module.common.emv.AIDConfig;
import com.newland.mtype.module.common.emv.CAPublicKey;
import com.newland.mtype.module.common.emv.EmvTransInfo;
import com.newland.mtype.module.common.emv.SecondIssuanceRequest;
import com.newland.mtype.module.common.security.GetDeviceInfo;
import com.newland.mtype.util.ISOUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by Vinchaos api on 14-1-4.
 */
public class SwiperController implements SwiperEmvAdapterListener, SwiperBluetoothDetectorListener {
    private boolean isEnableListener;
    private SwiperControllerListener listener;
    private SwiperAdapter adapter;
    private ESwiperType swiperType;
    private SwiperDetector detectorAudio, detectorWiFi, detectorLKLMobile, detectorBluetooth;
    private Context context;

    private SwiperController(SwiperControllerListener listener, Context context) {
        this.context            = context;
        this.listener           = listener;
        this.swiperType         = ESwiperType.Q203;
        this.detectorAudio      = SwiperDetectorAudio.initWithType(SwiperDefine.SwiperPortType.TYPE_AUDIO, context);
        this.detectorWiFi       = SwiperDetectorWIFI.initWithType(SwiperDefine.SwiperPortType.TYPE_WIFI, context);
        this.detectorLKLMobile  = SwiperDetectorLKLMobile.initWithType(SwiperDefine.SwiperPortType.TYPE_LKLMOBILE, context);
        this.detectorBluetooth  = SwiperDetectorBluetooth.initWithType(SwiperDefine.SwiperPortType.TYPE_BLUETOOTH, context);
        this.detectorAudio.setListener(this);
        this.detectorWiFi.setListener(this);
        this.detectorLKLMobile.setListener(this);
        this.detectorBluetooth.setListener(this);
        this.enableListener(true);
    }

//======================================= throw SwiperException ======================================

    //刷卡驱动未加载
    private static final String SWIPE_DRIVER_NOT_LOAD;
    //刷卡状态非闲置状态
    private static final String SWIPE_NOT_IDLE;
    //无效的参数
    private static final String SWIPE_INVALID_PARAM;
    //无效的刷卡器类型
    private static final String SWIPE_INVALID_TYPE;
    //刷卡器驱动加载失败
    private static final String SWIPE_DRIVER_LOAD_FAIL;
    //无效的刷卡器驱动
    private static final String SWIPE_DRIVER_INVALID;

    static {
        SWIPE_DRIVER_NOT_LOAD   = "swipe driver is not load";
        SWIPE_NOT_IDLE          = "swipe state is not IDLE";
        SWIPE_INVALID_PARAM     = "invalid parameter";
        SWIPE_INVALID_TYPE      = "swipe type is invalid";
        SWIPE_DRIVER_LOAD_FAIL  = "swipe driver load fail";
        SWIPE_DRIVER_INVALID    = "current swipe driver is invalid";
    }

    private void throwSwipeException(String errorMessage) throws SwiperException {
        throwSwipeException("", errorMessage);
    }

    private void throwSwipeException(String errData, String errorMessage) throws SwiperException {
        throw (new SwiperException(errData, errorMessage));
    }

//======================================= Controller 公共方法 ==========================================


    public static SwiperController getInstance(SwiperControllerListener listener, Context context) {
        return new SwiperController(listener, context);
    }


    public void onDestroy() {
        if (adapter != null)
            adapter.deleteSwiper();

        if (isEnableListener)
            enableListener(false);

        if (detectorBluetooth.isStartup())
            detectorBluetooth.stop();
        adapter = null;
    }

    /**
     * 判断当前是否已经插入刷卡设备
     *
     * @return PayFi、音频刷卡器、蓝牙刷卡器，至少有一个接入才返回 true。
     */
    public boolean hasSwiperConnected() {
        return this.swiperIsConnected(SwiperDefine.SwiperPortType.TYPE_AUDIO) ||
                this.swiperIsConnected(SwiperDefine.SwiperPortType.TYPE_WIFI) ||
                this.swiperIsConnected(SwiperDefine.SwiperPortType.TYPE_BLUETOOTH) ||
                this.swiperIsConnected(SwiperDefine.SwiperPortType.TYPE_LKLMOBILE);
    }

    /**
     * 获取刷卡器状态
     *
     * @return 状态值
     */
    public SwiperDefine.SwiperControllerState getSwiperState() throws SwiperException {
        if (adapter == null){
            throwSwipeException(SWIPE_DRIVER_NOT_LOAD);
        }
        return adapter.getCSwiperState();
    }

    /**
     * 获取刷卡器的 KSN，获取成功后会在 onGetKsnCompleted 事件中返回KSN字符串。
     */
    public String getKsn() throws SwiperException {
        if (adapter == null) {
            throwSwipeException(SWIPE_DRIVER_NOT_LOAD);
        }
        if (adapter.getCSwiperState() == SwiperDefine.SwiperControllerState.STATE_IDLE) {
            try {
                return adapter.getKSN();
            } catch (Exception e){
                throwSwipeException(e.getLocalizedMessage(),e.getMessage());
                return "";
            }
        }

        throwSwipeException(SWIPE_NOT_IDLE);
        return "";
    }

    /**
     * 获取当前设置的刷卡器类型。
     *
     * @return 刷卡器类型。
     */
    public ESwiperType getSwiperType() {
        return swiperType;
    }

    /**
     * 获取当前刷卡适配器
     *
     * @return
     */
    public SwiperAdapter getCurrentAdapter(){
        return adapter;
    }

    /**
     * 设置刷卡器类型并加载相应的刷卡器驱动，如果之前已经设置了刷卡器类型， 则旧的刷卡器驱动会被卸载。
     * 默认为 LKLSwiperTypes.Q203。
     * 如果设置的刷卡器类型无效则不会改变刷卡器类型。
     *
     * @param type 刷卡器类型。
     */
    public void setSwiperType(ESwiperType type) throws SwiperException {
        //非法参数
        if (type == null){
            throwSwipeException(SWIPE_INVALID_PARAM);
        }

        //验证传入的刷卡器类型是否正确
        boolean typeValid = false;
        ESwiperType[] types = ESwiperType.values();
        for (ESwiperType t : types) {
            typeValid = t == type;
            if (typeValid)
                break;
        }

        if (!typeValid){
            throwSwipeException(SWIPE_INVALID_TYPE);
        }

        //要设置的刷卡器类型和当前刷卡器类型相同并且已经加载驱动成功。
        if (type == swiperType && adapter != null)
            return;

        //卸载旧的刷卡器驱动。
        if (adapter != null) {
            adapter.deleteSwiper();
        }
        adapter = null;
        swiperType = type;

        //加载刷卡器驱动
        adapter = SwiperAdapter.initWithType(swiperType, context);
        adapter.setListener(this);

        //adapter 为空表示加载刷卡器驱动失败了。
        if (adapter == null){
            throwSwipeException(SWIPE_DRIVER_LOAD_FAIL);
        }
    }

    /**
     * 检测指定接口类型的刷卡器是否已经连接。
     *
     * @param type 刷卡器接口类型
     * @return 连接了返回 true。
     */
    public boolean swiperIsConnected(SwiperDefine.SwiperPortType type) {
        switch (type) {
            case TYPE_AUDIO:
                return detectorAudio.isConnected();
            case TYPE_WIFI:
                return detectorWiFi.isConnected();
            case TYPE_LKLMOBILE:
                return detectorLKLMobile.isConnected();
            case TYPE_BLUETOOTH:
                return detectorBluetooth.isConnected();
            default:
                return false;
        }
    }

    /**
     * 设置事件侦听器
     *
     * @param listener 侦听器
     */
    public void setListener(SwiperControllerListener listener) {
        this.listener = listener;
    }

    /**
     * 启用/禁用 事件侦听器，禁用事件侦听器时侦听器暂时不会收到任何事件。
     * 默认为启用事件侦听器。
     */
    public void enableListener(boolean enable) {
        isEnableListener = enable;


        //干掉wifi 跟拉风德检测 收款宝用不到
        if (enable) {
            detectorAudio.start();
//            detectorWiFi.start();
//            detectorLKLMobile.start();
        } else {
            detectorAudio.stop();
//            detectorWiFi.stop();
//            detectorLKLMobile.stop();
        }
    }

    /**
     * 设置连接参数，蓝牙用到
     * @param params
     */
    public boolean setConnectParams(String[] params){
        if (adapter instanceof SwiperAdapterBluetooth)
            return ((SwiperAdapterBluetooth) adapter).setConnectParams(params);
        return false;
    }


    /**
     * 启动刷卡器等待用户刷卡。
     */
    public void startSwiper() throws SwiperException {
        if (adapter == null) {
            throwSwipeException(SWIPE_DRIVER_NOT_LOAD);
        }
        if (adapter instanceof SwiperAdapterBluetooth){
            startSwipe("");
        } else {
            adapter.startSwiper();
        }
    }

    /**
     * 结束刷卡等待。
     */
    public void stopSwiper() throws SwiperException {
        if (adapter == null){
            throwSwipeException(SWIPE_DRIVER_NOT_LOAD);
        }
        if (adapter.getCSwiperState() == SwiperDefine.SwiperControllerState.STATE_IDLE)
            return;
        adapter.stopSwiper();
    }

    /**
     * 收款宝启动刷卡器
     *
     * @param amount
     * @throws SwiperException
     */
    public void startSwipe(String amount) throws SwiperException {
        startSwipe(amount, null, null, null, null);
    }

    /**
     * 收款宝启动刷卡器
     *
     * @param amount
     * @param cardReaderModuleTypes
     * @throws SwiperException
     */
    public void startSwipe(String amount, ModuleType[] cardReaderModuleTypes, final byte[] businessCode, final byte[] serviceCode, final byte[] additionalMsg) throws SwiperException {
        if (adapter == null) {
            throwSwipeException(SWIPE_DRIVER_NOT_LOAD);
        }
        if (adapter instanceof SwiperAdapterBluetooth){
            if(cardReaderModuleTypes == null){
                cardReaderModuleTypes = new ModuleType[]{ModuleType.COMMON_SWIPER,ModuleType.COMMON_ICCARD};
            }
            ((SwiperAdapterBluetooth) adapter).startSwiper(amount, cardReaderModuleTypes, businessCode, serviceCode, additionalMsg);
            return;
        }
        else if (adapter instanceof SwiperAdapterQV30E){
            ((SwiperAdapterQV30E) adapter).startSwiper(amount);
            return;
        }

        throwSwipeException(SWIPE_DRIVER_INVALID);
    }

    public void startSwipe(String amount, ModuleType[] cardReaderModuleTypes) throws SwiperException {
        startSwipe(amount, cardReaderModuleTypes, null, null, null);
    }

    /**
     * 收款宝startInputPIN
     *
     * @throws SwiperException
     */
    public void startInputPIN() throws SwiperException {
        if (adapter == null) {
            throwSwipeException(SWIPE_DRIVER_NOT_LOAD);
        }
        if(adapter instanceof SwiperCollectionAdapter){
            ((SwiperCollectionAdapter) adapter).startInputPIN();
        }
    }

    /**
     * 收款宝setStartParameter
     *
     * @param index
     * @param data
     * @throws SwiperException
     */
    public void setStartParameter(int index, Object data) throws SwiperException {
        if (adapter == null) {
            throwSwipeException(SWIPE_DRIVER_NOT_LOAD);
        }
        if(adapter instanceof SwiperCollectionAdapter){
            ((SwiperCollectionAdapter) adapter).setStartParameter(index, data);
        }
    }

    /**
     * 收款宝resetScreen
     *
     * @throws SwiperException
     */
    public void resetScreen() throws SwiperException {
        if (adapter == null) {
            throwSwipeException(SWIPE_DRIVER_NOT_LOAD);
        }
        if(adapter instanceof SwiperCollectionAdapter){
            ((SwiperCollectionAdapter) adapter).resetScreen();
        }
    }

    public Map<String, Object> getSwipeCardExtendData() throws SwiperException {
        if (adapter == null) {
            throwSwipeException(SWIPE_DRIVER_LOAD_FAIL);
        }
        if (adapter instanceof SwiperAdapterBluetooth) {
            return ((SwiperAdapterBluetooth) adapter).getSwipeCardExtendData();
        }
        return null;
    }
    /**
     * 添加AID
     *
     * @param aidConfig
     */
    public void addAID(AIDConfig aidConfig) throws SwiperException {
        if (adapter == null) {
            throwSwipeException(SWIPE_DRIVER_NOT_LOAD);
        }
        if(adapter instanceof SwiperEmvAdapter){
            ((SwiperEmvAdapter) adapter).addAID(aidConfig);
        }
    }

    /**
     * 删除AID
     *
     * @param aid
     */
    public void deleteAID(byte[] aid) throws SwiperException {
        if (adapter == null) {
            throwSwipeException(SWIPE_DRIVER_NOT_LOAD);
        }
        if(adapter instanceof SwiperEmvAdapter){
            ((SwiperEmvAdapter) adapter).deleteAID(aid);
        }
    }

    /**
     * 清空所有AID
     *
     * @throws SwiperException
     */
    public void clearAllAID() throws SwiperException {
        if(adapter == null) {
            throwSwipeException(SWIPE_DRIVER_NOT_LOAD);
        }
        if(adapter instanceof SwiperEmvAdapter) {
            ((SwiperEmvAdapter) adapter).clearAllAID();
        }
    }

    /**
     * 添加公钥
     *
     * @param rid
     * @param capk
     * @throws SwiperException
     */
    public void addCAPublicKey(byte[] rid, CAPublicKey capk) throws SwiperException {
        if (adapter == null){
            throwSwipeException(SWIPE_DRIVER_LOAD_FAIL);
        }
        if(adapter instanceof SwiperEmvAdapter){
            ((SwiperEmvAdapter) adapter).addCAPublicKey(rid, capk);
        }
    }

    /**
     * 删除公钥
     *
     * @param rid
     * @param index
     * @throws SwiperException
     */
    public void deleteCAPublicKey(byte[] rid, int index) throws SwiperException {
        if(adapter == null) {
            throwSwipeException(SWIPE_DRIVER_NOT_LOAD);
        }
        if(adapter instanceof SwiperEmvAdapter){
            ((SwiperEmvAdapter) adapter).deleteCAPublicKey(rid, index);
        }
    }

    /**
     * 清空所有公钥
     *
     * @param rid
     * @throws SwiperException
     */
    public void clearAllCAPublicKey(byte[] rid) throws SwiperException {
        if(adapter == null){
            throwSwipeException(SWIPE_DRIVER_NOT_LOAD);
        }
        if(adapter instanceof SwiperEmvAdapter){
            ((SwiperEmvAdapter) adapter).clearAllCAPublicKey(rid);
        }
    }

    /**
     * 二次授权
     *
     * @param request
     * @throws SwiperException
     */
    public void doSecondIssuance(SecondIssuanceRequest request) throws SwiperException {
        if(adapter == null){
            throwSwipeException(SWIPE_DRIVER_NOT_LOAD);
        }
        if(adapter instanceof SwiperEmvAdapter){
            ((SwiperEmvAdapter) adapter).doSecondIssuance(request);
        }
    }

    /**
     * 应用撤销密码输入调用
     *
     * @throws SwiperException
     */
    public void cancelPininput() throws SwiperException {
        if(adapter == null){
            throwSwipeException(SWIPE_DRIVER_NOT_LOAD);
        }
        if(adapter instanceof SwiperEmvAdapter){
            ((SwiperEmvAdapter) adapter).cancelPininput();
        }
    }

    /**
     * 应用撤销插卡或刷卡调用
     *
     * @throws SwiperException
     */
    public void cancelCardRead() throws SwiperException {
        if(adapter == null){
            throwSwipeException(SWIPE_DRIVER_NOT_LOAD);
        }
        if(adapter instanceof SwiperEmvAdapter){
            ((SwiperEmvAdapter) adapter).cancelCardRead();
        }
    }

    /**
     * 结束EMV
     * true : 正常结束
     * false : 中断
     *
     * @param b
     * @throws SwiperException
     */
    public void cancelEmv(boolean b) throws SwiperException {
        if(adapter == null) {
            throwSwipeException(SWIPE_DRIVER_LOAD_FAIL);
        }
        if(adapter instanceof SwiperEmvAdapter){
            ((SwiperEmvAdapter) adapter).cancelEmv(b);
        }
    }

    /**
     * 设置指令集版本号
     * @param commandProtocolVer
     * @throws SwiperException
     */
    public void setProtocolVer(CommandProtocolVer commandProtocolVer) throws SwiperException {
        if(adapter == null) {
            throwSwipeException(SWIPE_DRIVER_LOAD_FAIL);
        }
        if(adapter instanceof SwiperEmvAdapter){
            ((SwiperEmvAdapter) adapter).setCommandProtocolVer(commandProtocolVer);
        }
    }

    /**
     * 获取固件支持指令版本号
     * @return
     * @throws SwiperException
     */
    public CommandProtocolVer getCommandProtocolVer() throws SwiperException {
        if(adapter == null) {
            throwSwipeException(SWIPE_DRIVER_LOAD_FAIL);
        }
        if(adapter instanceof SwiperEmvAdapter){
            return ((SwiperEmvAdapter) adapter).getDeviceSupportedProtocol();
        }
        return CommandProtocolVer.VERSION_ONE;
    }

    /**
     * 获取设备能力
     *
     * @return
     * @throws SwiperException
     */
    public byte[] fetchProdAllocation() throws SwiperException {
        if (adapter == null){
            throwSwipeException(SWIPE_DRIVER_NOT_LOAD);
        }
        if (adapter instanceof  SwiperEmvAdapter){
            return ((SwiperEmvAdapter) adapter).fetchProdAllocation();
        }
        return null;
    }

//======================================= 回调方法 ==========================================
    /**
     * SwiperEmvAdapterListener  begin
     */
    @Override
    public void onPinInputCompleted(String randomNumber, String PIN, int length, byte[] macRandom, byte[] mac) {
        if (listener == null || !isEnableListener || !(listener instanceof SwiperControllerCollectionListener)) return;
        ((SwiperControllerCollectionListener) listener).onPinInputCompleted(randomNumber, PIN, length, macRandom, mac);
    }

    @Override
    public void onWaitingForPinEnter() {
        if (listener == null || !isEnableListener || !(listener instanceof SwiperControllerCollectionListener)) return;
        ((SwiperControllerCollectionListener) listener).onWaitingForPinEnter();
    }

    @Override
    public void onResetScreenCompleted() {
        if (listener == null || !isEnableListener || !(listener instanceof SwiperControllerCollectionListener)) return;
        ((SwiperControllerCollectionListener) listener).onResetScreenCompleted();
    }

    @Override
    public void onCardSwipeDetected() {
        if (listener == null || !isEnableListener) return;
        listener.onCardSwipeDetected();
    }

    /**
     * 刷卡完成并成功解码出刷卡数据时触发。
     *
     * @param formatID        格式ID;
     * @param ksn             刷卡器设备编码
     * @param encTracks       加密的磁道数据
     * @param track1Length    磁道1的长度（没有数据为0）
     * @param track2Length    磁道2的长度（没有数据为0）
     * @param track3Length    磁道3的长度（没有数据为0）
     * @param randomNumber    随机数
     * @param maskedPANString 账号（卡号）格式“ddddddddXXXXXXXXdddd”(隐藏卡号的中间的几位数字)d 数字，X 隐藏字符
     * @param expiryDate      到期日，格式ＹＹＭＭ
     * @param cardHolderName  持卡人姓名
     */
    @Override
    public void onDecodeCompleted(String formatID, String ksn, String encTracks, int track1Length, int track2Length, int track3Length, String randomNumber, String maskedPANString, String expiryDate, String cardHolderName) {
        if (listener == null || !isEnableListener) return;
        listener.onDecodeCompleted(formatID, ksn, encTracks, track1Length, track2Length, track3Length, randomNumber, maskedPANString, expiryDate, cardHolderName);
    }

    @Override
    public void onDecodeError(SwiperDefine.SwiperControllerDecodeResult decodeState) {
        if (listener == null || !isEnableListener) return;
        listener.onDecodeError(decodeState);
    }

    @Override
    public void onDecodingStart() {
        if (listener == null || !isEnableListener) return;
        listener.onDecodingStart();
    }

    @Override
    public void onError(int errorCode, String errorMessage) {
        if (listener == null || !isEnableListener) return;
        LogUtil.print("<AS>","errorMessage:"+errorMessage);
        listener.onError(errorCode, errorMessage);
    }

    @Override
    public void onInterrupted() {
        if (listener == null || !isEnableListener) return;
        listener.onInterrupted();
    }

    @Override
    public void onNoDeviceDetected() {
        if (listener == null || !isEnableListener) return;
        listener.onNoDeviceDetected();
    }

    @Override
    public void onTimeout() {
        if (listener == null || !isEnableListener) return;
        listener.onTimeout();
    }

    @Override
    public void onWaitingForCardSwipe() {
        if (listener == null || !isEnableListener) return;
        listener.onWaitingForCardSwipe();
    }

    @Override
    public void onWaitingForDevice() {
        if (listener == null || !isEnableListener) return;
        listener.onWaitingForDevice();
    }

    @Override
    public void onGetKsnCompleted(String ksn) {
        if (listener == null || !isEnableListener) return;
        listener.onGetKsnCompleted(ksn);
    }

    @Override
    public void onDevicePlugged() {
        if (listener == null || !isEnableListener) return;
        listener.onDeviceConnected(getSwiperType().getPortType());
        //如果是蓝牙则在这里调用
        if(getSwiperType().getPortType() == SwiperDefine.SwiperPortType.TYPE_BLUETOOTH){
            this.onConnected(detectorBluetooth);
        }
    }

    @Override
    public void onDeviceUnplugged() {
        if (listener == null || !isEnableListener) return;
        listener.onDeviceDisconnected(getSwiperType().getPortType());
        //如果是蓝牙则在这里调用
        if(getSwiperType().getPortType() == SwiperDefine.SwiperPortType.TYPE_BLUETOOTH){
            this.onDisconnected(detectorBluetooth);
        }
    }

    @Override
    public void onRequestSelectApplication(EmvTransInfo context) {
        if (listener == null || !isEnableListener || !(listener instanceof SwiperControllerBluetoothListener)) return;
        ((SwiperControllerBluetoothListener) listener).onRequestSelectApplication(context);
    }

    @Override
    public void onRequestTransferConfirm(EmvTransInfo context) {
        if (listener == null || !isEnableListener || !(listener instanceof SwiperControllerBluetoothListener)) return;
        ((SwiperControllerBluetoothListener) listener).onRequestTransferConfirm(context);
    }

    @Override
    public void onRequestPinEntry(EmvTransInfo context) {
        if (listener == null || !isEnableListener || !(listener instanceof SwiperControllerBluetoothListener)) return;
        ((SwiperControllerBluetoothListener) listener).onRequestPinEntry(context);
    }

    @Override
    public void onRequestOnline(EmvTransInfo context) {
        if (listener == null || !isEnableListener || !(listener instanceof SwiperControllerBluetoothListener)) return;


        ((SwiperControllerBluetoothListener) listener).onRequestOnline(handleEmvTransInfo(context));
    }

    @Override
    public void onEmvFinished(boolean isSuccess, EmvTransInfo context) {
        if (listener == null || !isEnableListener || !(listener instanceof SwiperControllerBluetoothListener)) return;
        ICCardInfo icCardInfo = new ICCardInfo();
        icCardInfo.setEmvTransInfo(context);
        ((SwiperControllerBluetoothListener) listener).onEmvFinished(isSuccess, icCardInfo);
    }

    @Override
    public void onFallback(EmvTransInfo context) {
        if (listener == null || !isEnableListener || !(listener instanceof SwiperControllerBluetoothListener)) return;
        ((SwiperControllerBluetoothListener) listener).onFallback(context);
    }

    @Override
    public void onError(String e) {
        if (listener == null || !isEnableListener || !(listener instanceof SwiperControllerBluetoothListener)) return;
        LogUtil.print("<AS>","e:"+e);
        ((SwiperControllerBluetoothListener) listener).onError(e);
    }
    /**
     * SwiperEmvAdapterListener  end
     */

    /**
     * SwiperDetectorListener
     */
    @Override
    public void onConnected(SwiperDetector detector) {
        if (listener == null || !isEnableListener) return;
        SwiperDefine.SwiperPortType portType = detector.getSwiperPortType();
        listener.onDeviceConnected(portType);

        if (portType == swiperType.getPortType())
            listener.onCurrentSwiperConnected();
    }

    @Override
    public void onDisconnected(SwiperDetector detector) {
        if (listener == null || !isEnableListener) return;
        SwiperDefine.SwiperPortType portType = detector.getSwiperPortType();
        listener.onDeviceDisconnected(portType);

        if (portType == swiperType.getPortType())
            listener.onCurrentSwiperDisconnected();

    }

    @Override
    public SwiperController getSwiperController() {
        return this;
    }


    @Override
    public void deviceAddressList(List<BluetoothDevice> macs, BluetoothDevice newMacs) {
        if (listener == null || !isEnableListener || !(listener instanceof SwiperControllerBluetoothListener)) return;
        ((SwiperControllerBluetoothListener) listener).deviceAddressList(macs, newMacs);
    }

    @Override
    public void detectorError(String id, Object data) {
        if (listener == null || !isEnableListener || !(listener instanceof SwiperControllerBluetoothListener)) return;
        ((SwiperControllerBluetoothListener) listener).detectorError(id, data);
    }

    public void deleteSwiper(){
        if(adapter != null){
            adapter.deleteSwiper();
        }
    }

    public GetDeviceInfo getDeviceInfo(){

        if (adapter instanceof SwiperAdapterBluetooth) {
            return ((SwiperAdapterBluetooth) adapter).getDeviceInfo();
        }
        return null;
    }

    @Override
    public void onQPBOCFinished(EmvTransInfo emvTransInfo) {
        if (listener == null || !isEnableListener || !(listener instanceof SwiperControllerBluetoothListener)) return;

        if (emvTransInfo.getExecuteRslt() == 0x0f || emvTransInfo.getExecuteRslt() == 0x0d ||emvTransInfo.getExecuteRslt() == 0x03 || emvTransInfo.getExecuteRslt() == 0x00 || emvTransInfo.getExecuteRslt() == 0x01) {

            ((SwiperControllerBluetoothListener) listener).onQPBOCFinished(handleEmvTransInfo(emvTransInfo));

        }else{
            //被交易被拒绝
            ((SwiperControllerBluetoothListener) listener).onQPBOCDenied();
        }
    }


    public ICCardInfo handleEmvTransInfo(EmvTransInfo context){
        int maskNum      = context.getCardNo().length()-10;
        String mask      = "";

        for(int i = 0 ; i< maskNum ; i++){
            mask = mask + "*";
        }
        //遮罩卡号
        String maskedPan = context.getCardNo().substring(0,6) + mask + context.getCardNo().substring(context.getCardNo().length()-4);

        ICCardInfo icCardInfo = new ICCardInfo();
        icCardInfo.setMaskedPan(maskedPan);
        icCardInfo.setPan(context.getCardNo());
        icCardInfo.setCardSn(context.getCardSequenceNumber());
        //从这个回调过来的肯定是插卡交易，故设置固定值1
        icCardInfo.setPosemc("1");

        //获取track2，二磁道信息
        byte[] tk2 = context.getTrack_2_eqv_data();
        if(tk2 != null){
            String hex = ISOUtils.hexString(tk2);
            byte[] bcd = ISOUtils.str2bcd(hex, false);
            String track2 = new String(Base64.encode(bcd,Base64.NO_WRAP));
            icCardInfo.setTrack2(track2);
        }

        //生成55域的内容
        byte[] icc55tlv = ICFieldConstructor.createIC55TLVPackage(context).pack();

        String icc55 = new String(Base64.encode(icc55tlv, Base64.NO_WRAP));
        icCardInfo.setIcc55(icc55);
        icCardInfo.setIcinfo(new String(Base64.encode(ICFieldConstructor.createIcInfo(context).pack(), Base64.NO_WRAP)));

        return icCardInfo;
    }


    public boolean isSupportNCCARD(){
        if (adapter instanceof SwiperAdapterBluetooth) {
            return ((SwiperAdapterBluetooth) adapter).isSupportNCCARD();
        }
        return false;
    }

}
