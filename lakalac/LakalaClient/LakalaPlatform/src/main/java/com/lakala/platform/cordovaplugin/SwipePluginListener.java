package com.lakala.platform.cordovaplugin;


import com.lakala.platform.swiper.mts.CardInfo;
import com.lakala.platform.swiper.mts.SwipeDefine;

import org.json.JSONObject;

/**
 * Created by wangchao on 14-8-4.
 */
public interface SwipePluginListener {

    /**
     * 交易取消
     *
     * @param data
     */
    public void onCancel(String data);

    /**
     * 当需要用户在手机上输入pin 时触发该事件，如果需要在终端上输入则不会触发该事件。
     * 当用户输入完后需要调用 sendPin 方法将 pin 返回给交易流程。
     */
    public void onRequestPin(String maskPan);

    /**
     * 当完成读卡（刷卡）、输Pin 操作后解发该事件。
     */
    public void onReadCardPin(CardInfo cardInfo, SwipeDefine.SwipeCardType swipeCardType);

    /**
     * 刷卡业务完成,Emv 完成
     */
    public void onFinish(boolean isSuccess, JSONObject tc);

    /**
     * 二次授权失败
     */
    public void onSecondIssuanceFail();

    /**
     * 启动刷卡器时触发此事件，此事件只表示正在启动刷卡器，但并不表示一定会启动成功。
     */
    public void onStartSwiper();

    /**
     * 等待输PIN超时。
     */
    public void onWaitInputPinTimeout();

    /**
     * 处理特殊 action
     */
    public void handleAction(int action);

}
