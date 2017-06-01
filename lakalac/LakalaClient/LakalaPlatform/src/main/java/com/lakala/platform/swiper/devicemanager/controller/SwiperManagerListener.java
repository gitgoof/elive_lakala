package com.lakala.platform.swiper.devicemanager.controller;

import android.bluetooth.BluetoothDevice;

import com.lakala.core.swiper.ICCardInfo;
import com.lakala.core.swiper.SwiperDefine;
import com.newland.mtype.module.common.emv.EmvTransInfo;

import java.util.List;

/**
 * Created by Vinchaos api on 14-1-11.
 */
public interface SwiperManagerListener {

    /**
     * 超时
     */
    void onTimeOut();

    /**
     * 等待刷卡
     */
    void onWaitingForSwipe();

    /**
     * 刷卡成功
     */
    void onSwipeSuccess(String encTracks,
                        String randomNumber,
                        String maskedPANString,
                        SwipeDefine.SwipeKeyBoard swipeKeyBoard);


    /**
     * 插卡后，刷卡器读出卡信息后的回调
     */
    public void onReadICCardCompleted(ICCardInfo icCardInfo, SwipeDefine.SwipeKeyBoard swipeKeyBoard);

    /**
     * 硬件输入完卡密码后的回调
     */
    public void onPinInputCompleted(String randomNumber, String pin, int length, boolean isCommandProVerTwo, byte[] macRandom, byte[] mac);

    /**
     * 当emv交易正常结束时发生。<p>
     * 正常结束包括交易失败,或者是emv交易被拒绝等状态，应该要注意同{@link #otherError(int errorCode, String errorMessage)}的区别。<p>
     * {@link #otherError(int errorCode, String errorMessage)} 表示一个异常结束，而这个异常很可能使得这个交易没有正确处理完成。<p>
     * 而该事件则表示设备一定正常完成交易处理。不需要再执行{@link SwiperManager#cancelCardRead()} ()}等操作.但如果发生了二次授权,有可能需要发起一个冲正<p>
     *
     * @param emvTransInfo 交易处理结果，可能是TC或者AAC
     */
    public void onEmvFinished(boolean b, ICCardInfo emvTransInfo);

    /**
     * 刷卡失败
     */
    void onSwipeError();

    /**
     * 刷卡器插入
     */
    void onDeviceConnected(SwiperDefine.SwiperPortType type);

    /**
     * 刷卡器移除
     */
    void onDeviceDisconnected(SwiperDefine.SwiperPortType type);

    /**
     * 其他异常
     */
    void otherError(int errorCode, String errorMessage);

    /**
     * 当前刷卡器拔出
     */
    void onCurrentDisconnected();

    /**
     * 当前刷卡器插入
     */
    void onCurrentConnected();

    /**
     * 未检测到设备，对刷卡器进行操作时在指定时间内没有检测到刷卡器。
     */
    void onNoDeviceDetected();

    /**
     * 刷卡器中断
     */
    void onInterrupted();

    /**
     * 蓝牙设备列表
     *
     * @param macs
     */
    void deviceAddressList(List<BluetoothDevice> macs, BluetoothDevice newMacs);

    /**
     * 读取ic卡错误等,会造成回退
     */
    void onFallback();

    /**
     * 开始解码
     */
    void onCardSwipeDetected();

    /**
     * ic卡降级使用
     */
    void icCardDemotionUsed();

    /**
     * 等待pin输入
     */
    void onWaitingForPinEnter();


    /**
     *
     * @param icCardInfo
     */
    public void onQPBOCFinished(ICCardInfo icCardInfo);

    public void onQPBOCDenied();
}
