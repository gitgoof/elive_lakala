package com.lakala.platform.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by More on 15/3/22.
 */
public enum  AccountType {

    PRIVATE("0"),

    PUBLIC("1");

    private String value;

    public String getValue() {
        return value;
    }

    AccountType(String value) {
        this.value = value;
    }

    private static final Map<String, AccountType> stringToEnum = new HashMap<String, AccountType>();
    static {
        // Initialize map from constant name to enum constant
        for(AccountType accountType : values()) {
            stringToEnum.put(accountType.toString(), accountType);
        }
    }

    public static AccountType fromString(String symbol) {
        return stringToEnum.get(symbol);
    }

    @Override
    public String toString() {
        return value;
    }
    
}
