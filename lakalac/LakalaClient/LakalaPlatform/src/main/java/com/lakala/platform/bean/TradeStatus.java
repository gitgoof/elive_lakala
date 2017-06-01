package com.lakala.platform.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by More on 15/3/24.
 */
public enum TradeStatus implements Serializable{

    RECEIVE_SUCCEED("FF","收款失败"),
    RECEIVE_FAILED("00", "收款成功"),
    REVOCATION_FAILED("CF","撤销失败"),
    REVOCATION_SUCCEED("C0","撤销成功");

    private String value;

    private String name;

    TradeStatus(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private static final Map<String, TradeStatus> stringToEnum = new HashMap<String, TradeStatus>();
    static {
        for(TradeStatus tradeStatus : values()) {
            stringToEnum.put(tradeStatus.getValue(), tradeStatus);
        }
    }

    public static TradeStatus fromString(String s){
        return stringToEnum.get(s);
    }

    @Override
    public String toString() {
        return "TradeStatus{" +
                "value='" + value + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
