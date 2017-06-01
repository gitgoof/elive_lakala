package com.lakala.core.swiper.Adapter;

/**
 * Created by Vinchaos api on 14-1-4.
 * 收款宝
 */
public interface SwiperCollectionAdapterListener extends SwiperAdapterListener {
    /**
     * PIN 输入完成
     *
     * @param randomNumber 随机数
     * @param PIN          PIN 密文
     * @param length       PIN 长度
     */
    void onPinInputCompleted(String randomNumber, String PIN, int length, byte[] macRandom, byte[] mac);

    /**
     * 设备准备就绪，可以输入PIN。该事件在startInputPin 之后触发。
     */
    void onWaitingForPinEnter();

    /**
     * 当调用　resetScreen 方法后，刷卡器完成屏幕复位后触发此事件。
     */
    void onResetScreenCompleted();
}
