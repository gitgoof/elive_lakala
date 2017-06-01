package com.lakala.shoudan.activity.webbusiness;

/**
 *
 * 业务类型码
 * Created by More on 15/12/19.
 */
public enum BusType {

    /**
     * 结束页面
     */
    FINISH("0"),
    /**
     * 展示对话框
     */
    ALERT_DIALOG("1"),
    /**
     * 请求交易
     */
    REQUEST_TRADE("2");

    private String value;

    BusType(String value) {
        this.value = value;
    }



}
