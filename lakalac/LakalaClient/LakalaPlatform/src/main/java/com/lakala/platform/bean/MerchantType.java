package com.lakala.platform.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by More on 15/3/22.
 */
public enum MerchantType {

    //TODO  根据文档完善枚举

    POS("POS"),
    MPOS("MPOS");
    private String value;

    public String getValue() {
        return value;
    }

    MerchantType(String value) {
        this.value = value;
    }

    private static final Map<String, MerchantType> stringToEnum = new HashMap<String, MerchantType>();
    static {
        // Initialize map from constant name to enum constant
        for(MerchantType merchantType : values()) {
            stringToEnum.put(merchantType.toString(), merchantType);
        }
    }

    public static MerchantType fromString(String symbol) {
        return stringToEnum.get(symbol);
    }

    @Override
    public String toString() {
        return value;
    }
}
