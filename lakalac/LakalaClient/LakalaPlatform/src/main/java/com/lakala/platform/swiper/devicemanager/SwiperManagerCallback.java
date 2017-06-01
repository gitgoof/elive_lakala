package com.lakala.platform.swiper.devicemanager;


/**
 * Created by More on 14-4-22.
 */
public interface SwiperManagerCallback {

    /**
     * Activity should be closed, make sure online transfer hasn't started
     *
     * 设备交易被中断或其他异常,
     */
    void onNotifyFinish(String error);

    /**
     * The swipe and pinInput process has end
     */
    void onMscProcessEnd(SwiperInfo swiperInfo);

    /**
     * Suppose to upload IC55 field(online transfer)
     * @param swiperInfo
     */
    void onRequestUploadIC55(SwiperInfo swiperInfo);

    /**
     * Suppose to upload TC(Online request)
     */
    void requestUploadTc(SwiperInfo swiperInfo);

    /**
     * Notify the special state of swiper
     * @param swiperProcessState
     */
    void onProcessEvent(SwiperProcessState swiperProcessState, SwiperInfo swiperInfo);

}
