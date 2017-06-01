package com.lakala.shoudan.activity.merchant.upgrade;

import java.util.HashMap;
import java.util.Map;

/**
 * 升级状态
 * Created by huangjp on 2016/5/27.
 */
public enum  UpgradeStatus {
    NONE,//没有申请升级
    PROCESSING,//升级审核中
    COMPLETED,//升级成功
    FAILURE;//升级失败
    private static final Map<String, UpgradeStatus> stringToEnum = new HashMap<String, UpgradeStatus>();
    static {
        // Initialize map from constant name to enum constant
        for(UpgradeStatus accountType : values()) {
            stringToEnum.put(accountType.toString(), accountType);
        }
    }
    public static UpgradeStatus fromString(String symbol) {
        return stringToEnum.get(symbol);
    }

}
