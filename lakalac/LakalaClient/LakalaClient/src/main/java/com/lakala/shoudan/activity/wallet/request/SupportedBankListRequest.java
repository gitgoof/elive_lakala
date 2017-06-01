package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

import com.lakala.shoudan.common.CommonBaseRequest;

/**
 * Created by huangjp on 2015/12/18.
 */
public class SupportedBankListRequest extends CommonBaseRequest{
    private String busId;
    public SupportedBankListRequest(Context context) {
        super(context);
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }
}
