package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by LMQ on 2015/10/16.
 */
public class FundTransferInRequest extends BaseRequest{
    private String busid = "1G0";
    private String PayeeAcNo;
    private String ProductId;
    private String Amount;

    public String getBusid() {
        return busid;
    }

    public FundTransferInRequest setBusid(String busid) {
        this.busid = busid;
        return this;
    }

    public String getAmount() {
        return Amount;
    }

    public FundTransferInRequest setAmount(String amount) {
        Amount = amount;
        return this;
    }

    public String getPayeeAcNo() {
        return PayeeAcNo;
    }

    public FundTransferInRequest setPayeeAcNo(String payeeAcNo) {
        PayeeAcNo = payeeAcNo;
        return this;
    }

    public String getProductId() {
        return ProductId;
    }

    public FundTransferInRequest setProductId(String productId) {
        ProductId = productId;
        return this;
    }
}
