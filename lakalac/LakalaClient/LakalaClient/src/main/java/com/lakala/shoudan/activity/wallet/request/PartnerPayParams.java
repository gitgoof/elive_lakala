package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

import com.lakala.platform.common.Utils;
import com.lakala.shoudan.common.CommonBaseRequest;

/**
 * Created by LMQ on 2015/12/23.
 */
public class PartnerPayParams extends CommonBaseRequest {
    public PartnerPayParams(Context context) {
        super(context);
    }
    private String busid = "1H2";
    private String params;
    private String fee = Utils.yuan2Fen("0");

    public String getParams() {
        return params;
    }

    public PartnerPayParams setParams(String params) {
        this.params = params;
        return this;
    }

    public String getBusid() {
        return busid;
    }

    @Override
    public boolean isNeedMac() {
        return true;
    }

    @Override
    public boolean isNeedRnd() {
        return false;
    }

    public PartnerPayParams setBusid(String busid) {
        this.busid = busid;
        return this;
    }

    public String getFee() {
        return fee;
    }

    public PartnerPayParams setFee(String fee) {
        this.fee = fee;
        return this;
    }
}
