package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by LMQ on 2015/10/9.
 */
public class FundStateQryRequest extends BaseRequest {
    private String BusId = "1G1";
    private String ProductId;
    private String signType = "1";

    public String getSignType() {
        return signType;
    }

    public FundStateQryRequest setSignType(String signType) {
        this.signType = signType;
        return this;
    }

    public String getProductId() {
        return ProductId;
    }

    public FundStateQryRequest setProductId(String productId) {
        ProductId = productId;
        return this;
    }

    public String getBusId() {
        return BusId;
    }

    public FundStateQryRequest setBusId(String busId) {
        BusId = busId;
        return this;
    }
}
