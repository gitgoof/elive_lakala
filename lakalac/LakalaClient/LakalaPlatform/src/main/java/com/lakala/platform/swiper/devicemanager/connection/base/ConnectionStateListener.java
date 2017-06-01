package com.lakala.platform.swiper.devicemanager.connection.base;


import com.lakala.platform.swiper.devicemanager.SwiperProcessState;

/**
 * Created by More on 14-8-25.
 */
public interface ConnectionStateListener {

    void onSwiperTypeValidate();

    void onConnectionState(SwiperProcessState state);
}
