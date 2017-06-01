package com.lakala.platform.bean;

/**
 * Created by More on 15/6/17.
 */
public enum LargeAmountAccess {

    UNKNOWN,//未知，接口没有获取到
    NOTSUPPORT,//不支持
    SUPPORT,// 支持/未申请
    PROCESSING,//处理中
    SUCCESS,// 成功
    FAILURE,//失败
    CLOSED,// 关闭


}
