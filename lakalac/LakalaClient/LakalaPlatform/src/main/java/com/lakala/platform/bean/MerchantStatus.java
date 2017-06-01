package com.lakala.platform.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by More on 15/3/22.
 */
public enum  MerchantStatus {
    // 商户状态,0:未开通;1:正在开通;2:冻结;3:审核未通过;4:修改资料并且审核通过
    FROZEN("2"),
    PROCESSING("1"),
    COMPLETED("4"),
    FAILURE("3"),
    NONE("0");

    private String value;

    public String getValue() {
        return value;
    }

    MerchantStatus(String value) {
        this.value = value;
    }

    private static final Map<String, MerchantStatus> stringToEnum = new HashMap<String, MerchantStatus>();
    static {
        // Initialize map from constant name to enum constant
        for(MerchantStatus merchantStatus : values()) {
            stringToEnum.put(merchantStatus.toString(), merchantStatus);
        }
    }

    public static MerchantStatus fromString(String symbol) {
        return stringToEnum.get(symbol);
    }

    @Override
    public String toString() {
        return value;
    }
}
