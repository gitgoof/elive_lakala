package com.lakala.shoudan.activity.shoudan.bankitcard.bean;

import java.io.Serializable;

/**
 * Created by huangjp on 2015/12/17.
 */
public class QuickCardBinBean implements Serializable {
    private String mobileInBank;
    private String cardId;

    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 银行行号
     */
    private String bankCode;
    /**
     * 卡号
     */
    private String accountNo;
    /**
     * 卡类型 1：借记卡；2：信用卡
     */
    private String accountType;
    /**
     * 持卡人姓名
     */
    private String customerName;


    /**
     * 签约人证件类型
     */
    private String identifierType;
    /**
     * 签约人证件号
     */
    private String identifier;
    /**
     * 是否支持短信发送
     */
    private String supportSMS="1";
    /**
     * 是否签约快捷
     */
    private String isShortcutSign;
    /**
     * 实名认证标识
     */
    private String authFlag;
    /**
     * 信用卡CVN2/CVV2
     */
    private String cVN2;
    /**
     * 信用卡有效期
     */
    private String cardExp;

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

    public String getMobileInBank() {
        return mobileInBank;
    }

    public void setMobileInBank(String mobileInBank) {
        this.mobileInBank = mobileInBank;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
}
