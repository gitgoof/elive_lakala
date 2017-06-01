package com.lakala.core.swiper;

import android.bluetooth.BluetoothDevice;

import com.newland.mtype.module.common.emv.EmvTransInfo;

import java.util.List;

/**
 * Created by wangchao on 14-4-22.
 */
public interface SwiperControllerBluetoothListener extends SwiperControllerCollectionListener{
    /**
     * 获取设备地址列表
     *
     * @param macs
     */
    void deviceAddressList(List<BluetoothDevice> macs, BluetoothDevice newMacs);

    /**
     * 异常
     *
     * @param id
     * @param data
     */
    void detectorError(String id, Object data);

    /**
     * 当设备要求app响应一个应用选择时发生
     * 若设备自己完成选择或者强制应用选择,该事件不触发
     * @param context
     */
    public void onRequestSelectApplication(EmvTransInfo context);

    /**
     * 当设备要求app响应一个客户交易信息确认时发生。
     * 若在设备上完成交易信息确认，则该事件不触发。
     * @param context
     */
    public void onRequestTransferConfirm(EmvTransInfo context);

    /**
     * 当设备要求app完成一个密码输入过程时发生
     * 若在设备上完成密码输入，则该事件不触发
     * @param context
     */
    public void onRequestPinEntry(EmvTransInfo context);

    /**
     * 当设备要求联机交易时发生
     * @param icCardInfo
     */
    public void onRequestOnline(ICCardInfo icCardInfo);

    /**
     * 当emv交易正常结束时发生。
     * @param isSuccess
     * @param icCardInfo
     */
    public void onEmvFinished(boolean isSuccess,ICCardInfo icCardInfo);

    /**
     * 当emv流程要求一个fallback交易的时候进行处理
     * @param context
     */
    public void onFallback(EmvTransInfo context);

    /**
     * 当有任何错误时发生
     * @param e
     */
    public void onError(String e);

    /**
     *
     * @param icCardInfo
     */
    public void onQPBOCFinished(ICCardInfo icCardInfo);

    public void onQPBOCDenied();
}
