package com.lakala.platform.bean;

/**
 * Created by LMQ on 2015/6/16.
 * 实名认证状态
 */
public enum AuthenticationStatus {
    UNKNOWN("未知状体"),NONE("未申请"),PASS("通过"),FAILURE("未通过");
    private String desc;
    AuthenticationStatus(String desc) {
        this.desc = desc;
    }
}
