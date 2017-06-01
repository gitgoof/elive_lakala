package com.lakala.elive.qcodeenter.request;

import java.io.Serializable;

/**
 * Q码绑定列表
 */
public class QCodeListRequ implements Serializable {


    public String merchantNo; //商户号

    public String authToken;

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

}
