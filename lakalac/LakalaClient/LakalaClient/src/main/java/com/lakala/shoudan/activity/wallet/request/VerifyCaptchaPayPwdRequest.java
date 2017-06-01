package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

import com.lakala.shoudan.common.CommonBaseRequest;

/**
 * 验证验证码参数
 * Created by huangjp on 2015/12/19.
 */
public class VerifyCaptchaPayPwdRequest extends CommonBaseRequest {
    private String mobile;
    private String sMSCode;
    private String businessType="0";//业务类型 0:移动客户端1:PC端9：第三方;
    private String businessCode="228202";//业务编码 找回支付密码 228202
    public VerifyCaptchaPayPwdRequest(Context context) {
        super(context);
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSMSCode() {
        return sMSCode;
    }

    public void setSMSCode(String sMSCode) {
        this.sMSCode = sMSCode;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    @Override
    public boolean isNeedMac() {
        return true;
    }
}
