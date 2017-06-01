package com.lakala.shoudan.activity.wallet.bean;

import java.io.Serializable;

/**
 * Created by fengxuan on 2015/12/14.
 */
public class WalletInfo implements Serializable{

    private String walletNo;
    private String customerName;
    private String walletWithdrawBalance;
    private int authFlag;
    private String walletBalance;
    private String noPwdAmount;
    private int noPwdFlag;
    private int trsPasswordFlag;
    private int questionFlag;

    public int getQuestionFlag() {
        return questionFlag;
    }

    public void setQuestionFlag(int questionFlag) {
        this.questionFlag = questionFlag;
    }

    public String getWalletNo() {
        return walletNo;
    }

    public void setWalletNo(String walletNo) {
        this.walletNo = walletNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getWalletWithdrawBalance() {
        return walletWithdrawBalance;
    }

    public void setWalletWithdrawBalance(String walletWithdrawBalance) {
        this.walletWithdrawBalance = walletWithdrawBalance;
    }

    public String getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(String walletBalance) {
        this.walletBalance = walletBalance;
    }

    public String getNoPwdAmount() {
        return noPwdAmount;
    }

    public void setNoPwdAmount(String noPwdAmount) {
        this.noPwdAmount = noPwdAmount;
    }

    public int getAuthFlag() {
        return authFlag;
    }

    public void setAuthFlag(int authFlag) {
        this.authFlag = authFlag;
    }

    public int getNoPwdFlag() {
        return noPwdFlag;
    }

    public void setNoPwdFlag(int noPwdFlag) {
        this.noPwdFlag = noPwdFlag;
    }

    public int getTrsPasswordFlag() {
        return trsPasswordFlag;
    }

    public void setTrsPasswordFlag(int trsPasswordFlag) {
        this.trsPasswordFlag = trsPasswordFlag;
    }
}
