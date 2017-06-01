package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

import com.lakala.shoudan.common.CommonBaseRequest;

/**
 * 设置、修改支付密码参数
 * Created by huangjp on 2015/12/17.
 */
public class SetPwdRequest extends CommonBaseRequest {
    private String trsPassword;
    private String confirmPassword;
    private String newTrsPassword;
    private String busid;
    public SetPwdRequest(Context context) {
        super(context);
    }

    public String getTrsPassword() {
        return trsPassword;
    }

    public void setTrsPassword(String trsPassword) {
        this.trsPassword = trsPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getNewTrsPassword() {
        return newTrsPassword;
    }

    public void setNewTrsPassword(String newTrsPassword) {
        this.newTrsPassword = newTrsPassword;
    }

    public String getBusid() {
        return busid;
    }

    public void setBusid(String busid) {
        this.busid = busid;
    }

    @Override
    public boolean isNeedMac() {
        return true;
    }

}
