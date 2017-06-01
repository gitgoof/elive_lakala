package com.lakala.core.swiper;

/**
 * Created by Vinchaos api on 14-1-6.
 */
public interface SwiperControllerListener {
    /**
     * 检测到刷卡动作，刷卡后将接收到该通知。
     */
    void onCardSwipeDetected();

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
    void onDecodeCompleted(String formatID,
                           String ksn,
                           String encTracks,
                           int track1Length,
                           int track2Length,
                           int track3Length,
                           String randomNumber,
                           String maskedPANString,
                           String expiryDate,
                           String cardHolderName);

    /**
     * 解码磁道数据失败时触发
     *
     * @param decodeState 解码失败原因
     */
    void onDecodeError(SwiperDefine.SwiperControllerDecodeResult decodeState);

    /**
     * 开始解码磁道数据。
     */
    void onDecodingStart();

    /**
     * 发生了错误。可能偶然的错误，设备与手机的适配问题，或者设备与驱动不符。
     *
     * @param errorCode    错误代码。
     * @param errorMessage 错误信息。
     */
    void onError(int errorCode, String errorMessage);

    /**
     * 操作裭中断
     * 由于设备拔出或者其它错误导致刷卡器操作中断。
     */
    void onInterrupted();

    /**
     * 未检测到设备，对刷卡器进行操作时在指定时间内没有检测到刷卡器。
     */
    void onNoDeviceDetected();

    /**
     * 操作超时
     * 主要针对刷卡指令，在特定的时间内未刷卡，该方法被调用。对于电池版本刷卡器，这可以避免刷卡器误用导致电池无谓损耗。
     */
    void onTimeout();

    /**
     * 等待用户刷卡
     * 调用 startSwiper 方法后，已经检测到刷卡器，进入等待刷卡。
     */
    void onWaitingForCardSwipe();

    /**
     * 等待设备就绪
     */
    void onWaitingForDevice();

    /**
     * 获取KSN成功
     *
     * @param ksn KSN
     */
    void onGetKsnCompleted(String ksn);

    /**
     * 刷卡设备插入
     */
    void onDeviceConnected(SwiperDefine.SwiperPortType type);

    /**
     * 刷卡设备插出
     */
    void onDeviceDisconnected(SwiperDefine.SwiperPortType type);

    /**
     * 当前选择的刷卡器类型的刷卡器插入
     */
    void onCurrentSwiperConnected();

    /**
     * 当前刷卡器被移除
     */
    void onCurrentSwiperDisconnected();

}
