package com.lakala.platform.bean;

import java.io.Serializable;

/**
 * Created by LMQ on 2015/3/10.
 */
public class TransferFeeInfo implements Serializable {

    /**
     * isfixed : 0  //是否固定
     * feeChannel : 90CNMB
     * transName : 9月1日18:00前到账   //支付名
     * chargeRate : 0.003  //按比例收费
     * feeLevel : 0
     * fixFee : 0  //固定手续费
     * feeType : 17O
     * maxFee : 1000  //最大手续费
     * feeId : 1  //支付类型
     * minFee : 200  //最少手续费
     */
    private int isfixed;
    private String feeChannel;
    private String transName;
    private double chargeRate;
    private int feeLevel;
    private int fixFee;
    private String feeType;
    private int maxFee;
    private int feeId;
    private int minFee;

    public void setIsfixed(int isfixed) {
        this.isfixed = isfixed;
    }

    public void setFeeChannel(String feeChannel) {
        this.feeChannel = feeChannel;
    }

    public void setTransName(String transName) {
        this.transName = transName;
    }

    public void setChargeRate(double chargeRate) {
        this.chargeRate = chargeRate;
    }

    public void setFeeLevel(int feeLevel) {
        this.feeLevel = feeLevel;
    }

    public void setFixFee(int fixFee) {
        this.fixFee = fixFee;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public void setMaxFee(int maxFee) {
        this.maxFee = maxFee;
    }

    public void setFeeId(int feeId) {
        this.feeId = feeId;
    }

    public void setMinFee(int minFee) {
        this.minFee = minFee;
    }

    public int getIsfixed() {
        return isfixed;
    }

    public String getFeeChannel() {
        return feeChannel;
    }

    public String getTransName() {
        return transName;
    }

    public double getChargeRate() {
        return chargeRate;
    }

    public int getFeeLevel() {
        return feeLevel;
    }

    public int getFixFee() {
        return fixFee;
    }

    public String getFeeType() {
        return feeType;
    }

    public int getMaxFee() {
        return maxFee;
    }

    public int getFeeId() {
        return feeId;
    }

    public int getMinFee() {
        return minFee;
    }
}
