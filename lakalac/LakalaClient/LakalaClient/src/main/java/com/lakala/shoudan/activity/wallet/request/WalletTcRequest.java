package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

import com.lakala.shoudan.activity.wallet.request.WalletBaseRequest;
import com.lakala.shoudan.common.CommonBaseRequest;

/**
 * Created by fengxuan on 2015/12/25.
 */
public class WalletTcRequest extends CommonBaseRequest{

    private String tcicc55;
    private String scpicc55;
    private String tcvalue;
    private String srcSid;

    public String getTcicc55() {
        return tcicc55;
    }

    public void setTcicc55(String tcicc55) {
        this.tcicc55 = tcicc55;
    }

    public String getScpicc55() {
        return scpicc55;
    }

    public void setScpicc55(String scpicc55) {
        this.scpicc55 = scpicc55;
    }

    public String getTcvalue() {
        return tcvalue;
    }

    public void setTcvalue(String tcvalue) {
        this.tcvalue = tcvalue;
    }

    public String getSrcSid() {
        return srcSid;
    }

    public void setSrcSid(String srcSid) {
        this.srcSid = srcSid;
    }

    public WalletTcRequest(Context context) {
        super(context);
    }

    @Override
    public boolean isNeedMac() {
        return true;
    }

    @Override
    protected String getChntype() {
        return "02101";
    }
}
