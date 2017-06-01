package com.lakala.core.swiper.Adapter;

/**
 * Created by Vinchaos api on 14-1-4.
 * 收款宝
 */
public abstract class SwiperCollectionAdapter extends SwiperAdapter {
    /**
     * 启动刷卡，并有刷卡器的显示屏上显示金额。
     *
     * @param amount 　金额
     */
    public abstract void startSwiper(String amount);

    /**
     * 启动刷卡器，等待用户输入密码。
     */
    public abstract void startInputPIN();

    /**
     * 设置启动刷卡器时的扩展参数。
     *
     * @param index 参数索引，参见以 PARAM_INDEX_ 开头的常量。
     * @param data  参数，参数的类型取决于index。
     */
    public abstract void setStartParameter(int index, Object data);

    /**
     * 复位刷卡器上显示屏，显示欢迎页面。当复位完成后会触发
     * onResetScreenCompleted 事件。
     */
    public abstract void resetScreen();
}
