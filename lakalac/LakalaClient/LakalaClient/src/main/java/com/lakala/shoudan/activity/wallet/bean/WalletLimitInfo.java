package com.lakala.shoudan.activity.wallet.bean;

import java.io.Serializable;

/**
 * Created by fengxuan on 2015/12/14.
 */
public class WalletLimitInfo implements Serializable{

    private String type;
    private double perLimit;
    private double dailyLimitAmt;
    private double monthlyLimitAmt;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPerLimit() {
        return perLimit;
    }

    public void setPerLimit(double perLimit) {
        this.perLimit = perLimit;
    }

    public double getDailyLimitAmt() {
        return dailyLimitAmt;
    }

    public void setDailyLimitAmt(double dailyLimitAmt) {
        this.dailyLimitAmt = dailyLimitAmt;
    }

    public double getMonthlyLimitAmt() {
        return monthlyLimitAmt;
    }

    public void setMonthlyLimitAmt(double monthlyLimitAmt) {
        this.monthlyLimitAmt = monthlyLimitAmt;
    }
}
