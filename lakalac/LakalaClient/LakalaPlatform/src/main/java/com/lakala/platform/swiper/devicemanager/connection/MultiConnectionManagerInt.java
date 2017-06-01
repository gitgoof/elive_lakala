package com.lakala.platform.swiper.devicemanager.connection;

import com.lakala.core.swiper.SwiperDefine;
import com.lakala.platform.swiper.devicemanager.connection.devicevalidate.DeviceOnlineValidateListener;

/**
 * Created by More on 15/1/15.
 */
public interface MultiConnectionManagerInt {

    boolean isPhyDeviceInsert();

    void onPhyDeviceInsert();

    boolean isDeviceValidate();

    SwiperDefine.SwiperPortType getPreDeviceType();

    boolean isDevicePre();

    void onlineValidate(DeviceOnlineValidateListener deviceOnlineValidateListener );



}
