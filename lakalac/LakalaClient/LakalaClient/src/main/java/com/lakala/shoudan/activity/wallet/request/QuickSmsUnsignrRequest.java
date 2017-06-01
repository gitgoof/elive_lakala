package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

import com.lakala.shoudan.common.CommonBaseRequest;

/**
 * 查询快捷解约短信验证码参数
 * Created by huangjp on 2015/12/19.
 */
public class QuickSmsUnsignrRequest extends CommonBaseRequest{
    private String cardId;//银行卡ID
    private String mobile;//用户手机号
    private String mobileInBank;//银行预留手机号
    private String accountNo;//签约卡卡号
    private String accountType;//签约卡类型

    @Override
    public boolean isNeedMac() {
        return true;
    }

    public QuickSmsUnsignrRequest(Context context) {
        super(context);
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobileInBank() {
        return mobileInBank;
    }

    public void setMobileInBank(String mobileInBank) {
        this.mobileInBank = mobileInBank;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
