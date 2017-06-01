package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by fengx on 2015/11/4.
 */
public class QueryRegularCardInfoRequest extends BaseRequest{

    private String BusId = "1GB";
    private String ProductId;
    private String ContractId;

    public String getBusId() {
        return BusId;
    }

    public void setBusId(String busId) {
        BusId = busId;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getContractId() {
        return ContractId;
    }

    public void setContractId(String contractId) {
        ContractId = contractId;
    }
}
