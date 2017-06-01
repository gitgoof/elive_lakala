package com.lakala.shoudan.activity.shoudan.finance.bean;

/**
 * Created by LMQ on 2015/10/21.
 */
public class BankCard {
    private String BankName;
    private String BankCode;
    private String AccountNo;
    private String AccountType;
    private String BankUrl;
    private String MobileInBank;

    public String getBankName() {
        return BankName;
    }

    public BankCard setBankName(String bankName) {
        BankName = bankName;
        return this;
    }

    public String getBankCode() {
        return BankCode;
    }

    public BankCard setBankCode(String bankCode) {
        BankCode = bankCode;
        return this;
    }

    public String getAccountNo() {
        return AccountNo;
    }

    public BankCard setAccountNo(String accountNo) {
        AccountNo = accountNo;
        return this;
    }

    public String getAccountType() {
        return AccountType;
    }

    public BankCard setAccountType(String accountType) {
        AccountType = accountType;
        return this;
    }

    public String getBankUrl() {
        return BankUrl;
    }

    public BankCard setBankUrl(String bankUrl) {
        BankUrl = bankUrl;
        return this;
    }

    public String getMobileInBank() {
        return MobileInBank;
    }

    public BankCard setMobileInBank(String mobileInBank) {
        MobileInBank = mobileInBank;
        return this;
    }
}
