package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

import com.lakala.shoudan.common.CommonBaseRequest;

/**
 * Created by huangjp on 2015/12/19.
 */
public class QuickUnsignRequest extends CommonBaseRequest {
    private String accountNo;//账户号
    private String accountType;//账户类型
    private String sid;//报文标识ID
    private String sMSCode;//短信验证码
    private String cardId;//卡片序号
    private String signFlag="1";//快捷签约标识

    @Override
    public boolean isNeedMac() {
        return true;
    }

    public QuickUnsignRequest(Context context) {
        super(context);
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

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSMSCode() {
        return sMSCode;
    }

    public void setSMSCode(String sMSCode) {
        this.sMSCode = sMSCode;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getSignFlag() {
        return signFlag;
    }

    public void setSignFlag(String signFlag) {
        this.signFlag = signFlag;
    }
}
