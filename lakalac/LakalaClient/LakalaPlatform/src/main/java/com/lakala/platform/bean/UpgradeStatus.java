package com.lakala.platform.bean;

/**
 * Created by linmq on 2016/3/17.
 */
public enum UpgradeStatus {
    NONE("没有申请升级"),
    PROCESSING("升级审核中"),
    COMPLETED("升级成功"),
    FAILURE("升级失败");

    /**描述*/
    String desc;

    UpgradeStatus(String desc) {
        this.desc = desc;
    }
    public static UpgradeStatus fromString(String name){
        UpgradeStatus status = null;
        try {
            status = valueOf(name);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        if(status == null){
            status = NONE;
        }
        return status;
    }
}
