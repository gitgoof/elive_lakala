package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by LMQ on 2015/10/21.
 */
public class VerifySMSCodeRequest extends BaseRequest {
    private String SMSCode;
    private int BusinessType = 0;
    private int BusinessCode = 228202;

    public String getSMSCode() {
        return SMSCode;
    }

    public VerifySMSCodeRequest setSMSCode(String SMSCode) {
        this.SMSCode = SMSCode;
        return this;
    }

    public int getBusinessType() {
        return BusinessType;
    }

    public VerifySMSCodeRequest setBusinessType(int businessType) {
        BusinessType = businessType;
        return this;
    }

    public int getBusinessCode() {
        return BusinessCode;
    }

    public VerifySMSCodeRequest setBusinessCode(int businessCode) {
        BusinessCode = businessCode;
        return this;
    }
}
