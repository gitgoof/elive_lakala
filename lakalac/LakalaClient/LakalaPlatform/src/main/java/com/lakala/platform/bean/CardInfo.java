package com.lakala.platform.bean;

import java.io.Serializable;

/**
 * Created by HUASHO on 2015/1/19.
 * 借记卡或信用卡信息
 */
public class CardInfo implements Serializable{

    private String bankCode;
    private String subBankFullNameCode;
    private String accountName;
    private String bankFullNameCode;
    private String cardId;
    private String bankFullName;
    private String bankName;
    private String cardNo;

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public void setSubBankFullNameCode(String subBankFullNameCode) {
        this.subBankFullNameCode = subBankFullNameCode;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setBankFullNameCode(String bankFullNameCode) {
        this.bankFullNameCode = bankFullNameCode;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public void setBankFullName(String bankFullName) {
        this.bankFullName = bankFullName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getSubBankFullNameCode() {
        return subBankFullNameCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getBankFullNameCode() {
        return bankFullNameCode;
    }

    public String getCardId() {
        return cardId;
    }

    public String getBankFullName() {
        return bankFullName;
    }

    public String getBankName() {
        return bankName;
    }

    public String getCardNo() {
        return cardNo;
    }
}
