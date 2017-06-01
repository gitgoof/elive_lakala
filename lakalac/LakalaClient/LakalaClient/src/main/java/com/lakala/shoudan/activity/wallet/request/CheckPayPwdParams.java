package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

import com.lakala.shoudan.common.CommonBaseRequest;

/**
 * Created by LMQ on 2016/1/11.
 */
public class CheckPayPwdParams extends CommonBaseRequest {
    public CheckPayPwdParams(Context context) {
        super(context);
    }
    private String trsPassword;
    private String mac = "sdlgkfask";

    public String getMac() {
        return mac;
    }

    public CheckPayPwdParams setMac(String mac) {
        this.mac = mac;
        return this;
    }

    public String getTrsPassword() {
        return trsPassword;
    }

    public CheckPayPwdParams setTrsPassword(String trsPassword) {
        this.trsPassword = trsPassword;
        return this;
    }
}
