package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

/**
 * Created by huangjp on 2015/12/18.
 */
public class SmallAmountCancelPwdRequest extends WalletBaseRequest {
    private String state;
    private String amount;
    private String trsPassword;

    public SmallAmountCancelPwdRequest(Context context) {
        super(context);
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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
