package com.lakala.platform.swiper.devicemanager;

/**
 * Created by More on 14-4-22.
 */
public enum SwiperProcessState {

    /**
     * 设备被拔出
     */
    DEVICE_UNPLUGGED,

    /**
     * 设备连接上
     */
    DEVICE_PLUGGED,

    /**
     * 刷卡超时
     */
    SWIPE_TIMEOUT,

    /**
     * 刷卡结束
     */
    SWIPE_END,

    /**
     * 密码输入超时
     */
    PIN_INPUT_TIMEOUT,

    /**
     * 等待刷卡
     */
    WAITING_FOR_CARD_SWIPE,

    /**
     * 等待密码输入
     */
    WAITING_FOR_PIN_INPUT,

    /**
     * 设备状态异常, 密码输入与刷卡的随机数不匹配
     */
    RND_ERROR,

    NORMAL,

    /**
     * 密码输入结束
     */
    PIN_INPUT_COMPLETE,

    /**
     * pboc交易结束
     */
    EMV_FINISH,

    /**
     * 等待写卡(二次授权)
     */
    REQUEST_SEC_ISS,

    //------------------- 设备连接验证相关的---------------//

    /**
     * 请求打开蓝牙
     */
    REQUEST_BLUETOOTH_OPEN,

    /**
     * 打开蓝牙
     */
    OPENING_BLUETOOTH,

    /**
     * 请求蓝牙失败
     */
    FAILED_OPEN_BLUETOOTH,

    /**
     * 开始终端验证
     */
    VALIDATION_START,
    /**
     * 搜索蓝牙设备
     */
    SEARCHING,
    /**
     * 结束搜索
     */
    FINISH_SEARCHING,
    /**
     * 签到失败
     */
    SIGN_UP_FAILED,
    /**
     * 签到成功
     */
    SIGN_UP_SUCCESS,

    /**
     * 联网验证中
     */
    ONLINE_VALIDATING,

    /**
     * 没有可用音频设备, 蓝牙设备
     */
    NONE_AVAILABLE_AUDIO_DEVICE_WITH_BLUETOOTH_CLOSED,

    /**
     * 降级
     */
    ON_FALL_BACK,

    /**
     * 发现未绑定新设备
     */
    NEW_DEVICE,

    /**
     * 正在绑定
     */
    BINDING,

    /**
     * 刷了复合卡
     */
    SWIPE_ICCARD_DENIED,

    /**
     * QPBOC读卡失败
     */
    QPBOC_DENIED,

    /**
     * EMV cancel
     */
    EMV_CANCEL,

}
