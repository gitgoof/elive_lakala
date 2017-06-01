package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

import com.lakala.shoudan.common.CommonBaseRequest;

/**
 * Created by fengxuan on 2015/12/17.
 */
public class WalletCardInfoRequest extends CommonBaseRequest{

    private String payeeAcNo;

    private String payeeName;

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public String getPayeeAcNo() {
        return payeeAcNo;
    }

    public void setPayeeAcNo(String payeeAcNo) {
        this.payeeAcNo = payeeAcNo;
    }

    public WalletCardInfoRequest(Context context) {
        super(context);
    }
}
