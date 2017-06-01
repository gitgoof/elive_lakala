package com.lakala.platform.bean;

/**
 * Created by More on 15/12/8.
 */
public enum T0Status {
    UNKNOWN("未知状态"),
    NOTSUPPORT("不支持"),
    SUPPORT("支持/未申请"),
    PROCESSING("处理中"),
    COMPLETED("完成"),
    ONEDAYLOAN("完成"),
    FAILURE("失败");
    private String errMsg;

    /**
     *
     * @param describe 状态描述
     */
    T0Status(String describe) {
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
