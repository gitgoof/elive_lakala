package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by LMQ on 2015/10/14.
 */
public class CheckPayPwdRequest extends BaseRequest {
    private String BusId = "110005";
    private String TrsPassword;

    public String getTrsPassword() {
        return TrsPassword;
    }

    public CheckPayPwdRequest setTrsPassword(String trsPassword) {
        TrsPassword = trsPassword;
        return this;
    }

    public String getBusId() {
        return BusId;
    }

    public CheckPayPwdRequest setBusId(String busId) {
        BusId = busId;
        return this;
    }
}
