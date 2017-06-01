package com.lakala.platform.statistic;

/**
 * 大额收款-成功-入口
 * Created by huangjp on 2015/12/30.
 */
public enum LargeAmountEnum {
    LargeAmount;
    String advertId;

    public String getAdvertId() {
        return advertId;
    }

    public void setAdvertId(String advertId) {
        this.advertId = advertId;
    }
}
