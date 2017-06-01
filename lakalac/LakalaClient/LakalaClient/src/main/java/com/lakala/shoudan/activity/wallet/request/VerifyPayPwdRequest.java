package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

import com.lakala.shoudan.common.CommonBaseRequest;

/**
 * 校验支付密码参数
 * Created by huangjp on 2015/12/19.
 */
public class VerifyPayPwdRequest extends CommonBaseRequest {
    private String trsPassword;
    public VerifyPayPwdRequest(Context context) {
        super(context);
    }

    public String getTrsPassword() {
        return trsPassword;
    }

    public void setTrsPassword(String trsPassword) {
        this.trsPassword = trsPassword;
    }
    @Override
    public boolean isNeedMac() {
        return true;
    }
}
