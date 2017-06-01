package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

import com.lakala.shoudan.common.CommonBaseRequest;

/**
 * Created by fengxuan on 2015/12/17.
 */
public class RedPackageRequest extends CommonBaseRequest{

    private String page;
    private String busid;
    private String giftStat;

    public String getGiftStat() {
        return giftStat;
    }

    public void setGiftStat(String giftStat) {
        this.giftStat = giftStat;
    }

    public String getBusid() {
        return busid;
    }

    public void setBusid(String busid) {
        this.busid = busid;
    }

    public RedPackageRequest(Context context) {
        super(context);
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    @Override
    public boolean isNeedMac() {
        return true;
    }

    @Override
    protected String getMacRnd() {
        return "";
    }

    @Override
    public boolean isNeedRnd() {
        return false;
    }
}
