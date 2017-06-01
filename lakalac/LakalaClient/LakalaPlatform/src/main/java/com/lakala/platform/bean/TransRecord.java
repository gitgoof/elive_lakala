package com.lakala.platform.bean;

import java.io.Serializable;

/**
 * Created by More on 15/2/5.
 */
public class TransRecord implements Serializable {

    /**
     * 交易类型
     */
    private String type;

    /**
     * 商户(门店)
     */
    private String merchantName;

    /**
     * 单个金额
     */
    private String amount;

    /**
     * 总金额
     */
    private String totalAmount;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
