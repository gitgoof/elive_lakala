package com.lakala.shoudan.activity.shoudan.finance.bean;

/**
 * Created by LMQ on 2015/10/21.
 */
public class Bank {
    private String BankCode;
    private String LogoName;
    private String BankName;
    private String AccountType;

    public String getBankCode() {
        return BankCode;
    }

    public Bank setBankCode(String bankCode) {
        BankCode = bankCode;
        return this;
    }

    public String getLogoName() {
        return LogoName;
    }

    public Bank setLogoName(String logoName) {
        LogoName = logoName;
        return this;
    }

    public String getBankName() {
        return BankName;
    }

    public Bank setBankName(String bankName) {
        BankName = bankName;
        return this;
    }

    public String getAccountType() {
        return AccountType;
    }

    public Bank setAccountType(String accountType) {
        AccountType = accountType;
        return this;
    }
}
