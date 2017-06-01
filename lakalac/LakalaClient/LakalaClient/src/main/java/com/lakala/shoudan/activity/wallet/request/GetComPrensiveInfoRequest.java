package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

import com.lakala.shoudan.common.CommonBaseRequest;

/**
 * Created by fengxuan on 2015/12/21.
 */
public class GetComPrensiveInfoRequest extends CommonBaseRequest{

    private String busid = "1GB";

    public String getBusid() {
        return busid;
    }

    public void setBusid(String buiid) {
        this.busid = buiid;
    }

    public GetComPrensiveInfoRequest(Context context) {
        super(context);
    }

    @Override
    public boolean isNeedMac() {
        return true;
    }
}
