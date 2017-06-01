
package com.lakala.elive.beans;

import java.io.Serializable;



 //进件--卡应用开通扣率设置列表


public class CardAppRateInfo implements Serializable {
    private String applyId;
    private String terminalId;
    private String cardType;
    private String baseFeeRate;
    private String baseFeeMax;
    private String hasTransAmt;
    private String perTransLimit;
    private String dayTransLimit;
    private String monthTransLimit;


    public CardAppRateInfo() {
    }

    public CardAppRateInfo(String cardType, String baseFeeRate, String baseFeeMax) {
        this.cardType = cardType;
        this.baseFeeRate = baseFeeRate;
        this.baseFeeMax = baseFeeMax;
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getBaseFeeRate() {
        return baseFeeRate;
    }

    public void setBaseFeeRate(String baseFeeRate) {
        this.baseFeeRate = baseFeeRate;
    }

    public String getBaseFeeMax() {
        return baseFeeMax;
    }

    public void setBaseFeeMax(String baseFeeMax) {
        this.baseFeeMax = baseFeeMax;
    }

    public String getHasTransAmt() {
        return hasTransAmt;
    }

    public void setHasTransAmt(String hasTransAmt) {
        this.hasTransAmt = hasTransAmt;
    }

    public String getPerTransLimit() {
        return perTransLimit;
    }

    public void setPerTransLimit(String perTransLimit) {
        this.perTransLimit = perTransLimit;
    }

    public String getDayTransLimit() {
        return dayTransLimit;
    }

    public void setDayTransLimit(String dayTransLimit) {
        this.dayTransLimit = dayTransLimit;
    }

    public String getMonthTransLimit() {
        return monthTransLimit;
    }

    public void setMonthTransLimit(String monthTransLimit) {
        this.monthTransLimit = monthTransLimit;
    }
}

