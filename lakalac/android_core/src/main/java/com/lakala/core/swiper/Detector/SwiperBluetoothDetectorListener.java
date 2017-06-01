package com.lakala.core.swiper.Detector;

import android.bluetooth.BluetoothDevice;

import java.util.List;

/**
 * Created by wangchao on 14-4-21.
 */
public interface SwiperBluetoothDetectorListener extends SwiperDetectorListener{

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

}
