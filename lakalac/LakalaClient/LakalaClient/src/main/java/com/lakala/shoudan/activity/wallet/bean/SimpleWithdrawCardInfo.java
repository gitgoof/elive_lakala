package com.lakala.shoudan.activity.wallet.bean;

import java.io.Serializable;

/**
 * Created by fengxuan on 2015/12/26.
 */
public class SimpleWithdrawCardInfo implements Serializable{

    private String bankName;
    private String bankCard;
    private String bankCode;
    private String bankType ;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }
}
