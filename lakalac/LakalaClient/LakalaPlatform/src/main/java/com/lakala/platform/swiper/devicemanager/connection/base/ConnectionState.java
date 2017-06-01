package com.lakala.platform.swiper.devicemanager.connection.base;

/**
 * Created by More on 14-8-25.
 */
public enum ConnectionState {

    OPENING_BLUETOOTH,
    SIGN_UP_START,
    SEARCHING,
    FINISH_SEARCHING,
    SIGN_UP_FAILED_AUDIO,
    SIGN_UP_FAILED_BLUETOOTH,
    SIGN_UP_SUCCESS_AUDIO,
    SIGN_UP_SUCCESS_BLUETOOTH,
    NONE_AVAILABLE_AUDIO_DEVICE_WITH_BLUETOOTH_CLOSED,
    ONLINE_VALIDATING,

}
