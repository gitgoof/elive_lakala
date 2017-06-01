package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by LMQ on 2015/10/12.
 */
public class SetPayPwdRequest extends BaseRequest {
    private String BusId = "110005";
    private String TrsPassword;
    private String NewTrsPassword;
    private String ConfirmTrsPassword;

    public String getBusId() {
        return BusId;
    }

    public SetPayPwdRequest setBusId(String busId) {
        this.BusId = busId;
        return this;
    }

    public String getTrsPassword() {
        return TrsPassword;
    }

    public SetPayPwdRequest setTrsPassword(String trsPassword) {
        TrsPassword = trsPassword;
        return this;
    }

    public String getNewTrsPassword() {
        return NewTrsPassword;
    }

    public SetPayPwdRequest setNewTrsPassword(String newTrsPassword) {
        NewTrsPassword = newTrsPassword;
        return this;
    }

    public String getConfirmTrsPassword() {
        return ConfirmTrsPassword;
    }

    public SetPayPwdRequest setConfirmTrsPassword(String confirmTrsPassword) {
        ConfirmTrsPassword = confirmTrsPassword;
        return this;
    }
}
