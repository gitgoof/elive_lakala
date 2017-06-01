package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

import com.lakala.shoudan.common.CommonBaseRequest;

/**
 * Created by huangjp on 2015/12/17.
 */
public class MyBankCardRequest extends CommonBaseRequest {
    public MyBankCardRequest(Context context) {
        super(context);
    }
    private String signType="1";

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }
}
