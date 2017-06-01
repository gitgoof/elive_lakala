package com.lakala.shoudan.activity.wallet.bean;

import java.io.Serializable;

/**
 * Created by fengxuan on 2015/12/19.
 * 零钱卡列表信息，判断是否有绑卡
 * 过时
 */
public class CardListInfo implements Serializable{


    private String innerBankCode;
    private String customerName;
    private String cardId;
    private String signFlag;
    private String accountType;
    private Object logoUrl;
    private String bankName;
    private String accountNo;
    private String bkMobile;
    private String logoName;
    private String bankCode;

    public void setInnerBankCode(String innerBankCode) {
        this.innerBankCode = innerBankCode;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public void setSignFlag(String signFlag) {
        this.signFlag = signFlag;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setLogoUrl(Object logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public void setBkMobile(String bkMobile) {
        this.bkMobile = bkMobile;
    }

    public void setLogoName(String logoName) {
        this.logoName = logoName;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getInnerBankCode() {
        return innerBankCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCardId() {
        return cardId;
    }

    public String getSignFlag() {
        return signFlag;
    }

    public String getAccountType() {
        return accountType;
    }

    public Object getLogoUrl() {
        return logoUrl;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public String getBkMobile() {
        return bkMobile;
    }

    public String getLogoName() {
        return logoName;
    }

    public String getBankCode() {
        return bankCode;
    }
}
