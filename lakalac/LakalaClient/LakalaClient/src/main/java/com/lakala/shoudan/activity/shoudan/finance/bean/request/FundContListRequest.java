package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by fengx on 2015/11/5.
 */
public class FundContListRequest extends BaseRequest{

    private String ProductId;
    private String Period;
    private String ContractId;
    private String ContractState;

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getPeriod() {
        return Period;
    }

    public void setPeriod(String period) {
        Period = period;
    }

    public String getContractId() {
        return ContractId;
    }

    public void setContractId(String contractId) {
        ContractId = contractId;
    }

    public String getContractState() {
        return ContractState;
    }

    public void setContractState(String contractState) {
        ContractState = contractState;
    }
}
