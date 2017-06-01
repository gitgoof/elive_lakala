package com.lakala.elive.market.merqcodebind;

import java.io.Serializable;

/**
 * 存量商户的账户
 */
public class MerAccountRequ implements Serializable {

    public String authToken;
    public String merchantCode;
    public String shopNo;

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getShopNo() {
        return shopNo;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

}
