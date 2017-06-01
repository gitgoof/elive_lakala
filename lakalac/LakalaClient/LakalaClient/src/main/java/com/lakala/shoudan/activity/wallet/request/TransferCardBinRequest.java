package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

import com.lakala.shoudan.common.CommonBaseRequest;

/**
 * Created by fengxuan on 2015/12/19.
 */
public class TransferCardBinRequest extends CommonBaseRequest{

    private String payeeAcNo;

    public String getPayeeAcNo() {
        return payeeAcNo;
    }

    public void setPayeeAcNo(String payeeAcNo) {
        this.payeeAcNo = payeeAcNo;
    }

    public TransferCardBinRequest(Context context) {
        super(context);
    }
}
