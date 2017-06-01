package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

import com.lakala.shoudan.common.CommonBaseRequest;

/**
 * Created by LMQ on 2015/12/28.
 */
public class PayTypeParams extends CommonBaseRequest {
    public PayTypeParams(Context context) {
        super(context);
    }
    private String billId;
    private String title = "随便";

    public String getBillId() {
        return billId;
    }

    public PayTypeParams setBillId(String billId) {
        this.billId = billId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public PayTypeParams setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public boolean isNeedMac() {
        return true;
    }
}
