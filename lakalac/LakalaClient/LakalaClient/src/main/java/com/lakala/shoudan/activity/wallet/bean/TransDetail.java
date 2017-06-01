package com.lakala.shoudan.activity.wallet.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fengx on 2015/11/23.
 */
public class TransDetail implements Serializable{

    private String tranAmount;
    private String walletBalance;
    private String transName;
    private String transRetdesc;
    private String transDate;
    private String transTime;
    private String transRes;
    private String transType;

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getTranAmount() {
        return tranAmount;
    }

    public void setTranAmount(String tranAmount) {
        this.tranAmount = tranAmount;
    }

    public String getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(String walletBalance) {
        this.walletBalance = walletBalance;
    }

    public String getTransName() {
        return transName;
    }

    public void setTransName(String transName) {
        this.transName = transName;
    }

    public String getTransRetdesc() {
        return transRetdesc;
    }

    public void setTransRetdesc(String transRetdesc) {
        this.transRetdesc = transRetdesc;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public String getTransTime() {
        return transTime;
    }

    public void setTransTime(String transTime) {
        this.transTime = transTime;
    }

    public String getTransRes() {
        return transRes;
    }

    public void setTransRes(String transRes) {
        this.transRes = transRes;
    }
}
