package com.lakala.platform.response;

/**
 * Created by More on 15/2/9.
 */
public enum HttpConnectEvent {

    /**
     * 连接超时等异常
     */
    EXCEPTION("连接超时等异常"),

    /**
     * 发送不成功等错误
     */
    ERROR("发送不成功等错误"),

    /**
     * HTTP应答错误
     */
    RESPONSE_ERROR("HTTP应答错误");

    String describe;

    HttpConnectEvent(String describe) {
        this.describe = describe;
    }

    public String getDescribe() {
        return describe;
    }
}
