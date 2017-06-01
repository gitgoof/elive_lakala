package com.lakala.elive.beans;

import java.io.Serializable;

/**
 * 进件--终端开通基本信息
 * Created by wenhaogu on 2017/1/9.
 */

public class TerminalInfo implements Serializable {
    private String applyId;
    private String deviceType;
    private String deviceDrawMethod;
    private String deviceDeposit;
    private String deviceRent;
    private String deviceSaleAmount;
    private String deviceCnt;
    private String commFee;//通讯费
    private String  settlePeriod;//结算周期

    public String getSettlePeriod() {
        return settlePeriod;
    }

    public void setSettlePeriod(String settlePeriod) {
        this.settlePeriod = settlePeriod;
    }

    public String getCommFee() {
        return commFee;
    }

    public void setCommFee(String commFee) {
        this.commFee = commFee;
    }

    public String getDeviceCnt() {
        return deviceCnt;
    }

    public void setDeviceCnt(String deviceCnt) {
        this.deviceCnt = deviceCnt;
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceDrawMethod() {
        return deviceDrawMethod;
    }

    public void setDeviceDrawMethod(String deviceDrawMethod) {
        this.deviceDrawMethod = deviceDrawMethod;
    }

    public String getDeviceDeposit() {
        return deviceDeposit;
    }

    public void setDeviceDeposit(String deviceDeposit) {
        this.deviceDeposit = deviceDeposit;
    }

    public String getDeviceRent() {
        return deviceRent;
    }

    public void setDeviceRent(String deviceRent) {
        this.deviceRent = deviceRent;
    }

    public String getDeviceSaleAmount() {
        return deviceSaleAmount;
    }

    public void setDeviceSaleAmount(String deviceSaleAmount) {
        this.deviceSaleAmount = deviceSaleAmount;
    }


}
