package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

import com.lakala.shoudan.common.CommonBaseRequest;

/**
 * 查询快捷签约验证码参数、查询快捷签约是否成功参数
 * Created by huangjp on 2015/12/17.
 */
public class QuickSignRequest extends CommonBaseRequest {
    public QuickSignRequest(Context context) {
        super(context);
    }
    private String customerName;//签约人名称
    private String identifierType;//证件类型   （验证码 默认，00：身份证）（成功与否 默认，01：身份证）
    private String identifier;//证件号码 01：身份证
    private String bankName;//银行名称
    private String bankCode;//银行行号
    private String mobileInBank;//银行预留手机号
    private String supportSMS="1";//是否支持短信发送 1：支持；0：不支持
    private String accountNo;//账户号
    private String accountType;//签约卡类型  1:借记卡 2:信用卡
    private String cVN2;//信用卡CVN2/CVV2 信用卡必送
    private String cardExp;//信用卡有效期 信用卡必送

    //是否成功
    private String oTPPassword;//短信验证码
    private String srcid;//查询的SID值

    //验证码
    private  String isShortcutSign;//是否签约快捷 1：是；0：否
    private String authFlag;//实名认证标识  实名认证时必传，0：已认证；1：未认证

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getMobileInBank() {
        return mobileInBank;
    }

    public void setMobileInBank(String mobileInBank) {
        this.mobileInBank = mobileInBank;
    }

    public String getSupportSMS() {
        return supportSMS;
    }

    public void setSupportSMS(String supportSMS) {
        this.supportSMS = supportSMS;
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

    public String getCVN2() {
        return cVN2;
    }

    public void setCVN2(String cVN2) {
        this.cVN2 = cVN2;
    }

    public String getCardExp() {
        return cardExp;
    }

    public void setCardExp(String cardExp) {
        this.cardExp = cardExp;
    }

    public String getOTPPassword() {
        return oTPPassword;
    }

    public void setOTPPassword(String oTPPassword) {
        this.oTPPassword = oTPPassword;
    }

    public String getSrcid() {
        return srcid;
    }

    public void setSrcid(String srcid) {
        this.srcid = srcid;
    }

    public String getIsShortcutSign() {
        return isShortcutSign;
    }

    public void setIsShortcutSign(String isShortcutSign) {
        this.isShortcutSign = isShortcutSign;
    }

    public String getAuthFlag() {
        return authFlag;
    }

    public void setAuthFlag(String authFlag) {
        this.authFlag = authFlag;
    }

    @Override
    public boolean isNeedMac() {
        return true;
    }
}
