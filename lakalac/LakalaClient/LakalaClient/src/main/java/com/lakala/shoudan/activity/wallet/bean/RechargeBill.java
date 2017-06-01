package com.lakala.shoudan.activity.wallet.bean;

import java.io.Serializable;

/**
 * Created by fengxuan on 2015/12/17.
 */
public class RechargeBill implements Serializable{

    private String billid;
    private String fee;

    public String getBillid() {
        return billid;
    }

    public void setBillid(String billid) {
        this.billid = billid;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }
}
