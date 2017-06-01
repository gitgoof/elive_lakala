package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

/**
 * Created by LMQ on 2015/12/24.
 */
public class TreasureDoPayParams extends WalletBaseRequest {
    public TreasureDoPayParams(Context context) {
        super(context);
    }
    private String billId;
    private String trsPassword;
    private String lastPaymentType;
    private String terminalId;
    private String notifyURL;
    private String orderToPayFlag;
    private String lakalaOrderId;

    public String getBillId() {
        return billId;
    }

    public TreasureDoPayParams setBillId(String billId) {
        this.billId = billId;
        return this;
    }

    public String getTrsPassword() {
        return trsPassword;
    }

    public TreasureDoPayParams setTrsPassword(String trsPassword) {
        this.trsPassword = trsPassword;
        return this;
    }

    public String getLastPaymentType() {
        return lastPaymentType;
    }

    public TreasureDoPayParams setLastPaymentType(String lastPaymentType) {
        this.lastPaymentType = lastPaymentType;
        return this;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public TreasureDoPayParams setTerminalId(String terminalId) {
        this.terminalId = terminalId;
        return this;
    }

    public String getNotifyURL() {
        return notifyURL;
    }

    public TreasureDoPayParams setNotifyURL(String notifyURL) {
        this.notifyURL = notifyURL;
        return this;
    }

    public String getOrderToPayFlag() {
        return orderToPayFlag;
    }

    public TreasureDoPayParams setOrderToPayFlag(String orderToPayFlag) {
        this.orderToPayFlag = orderToPayFlag;
        return this;
    }

    public String getLakalaOrderId() {
        return lakalaOrderId;
    }

    public TreasureDoPayParams setLakalaOrderId(String lakalaOrderId) {
        this.lakalaOrderId = lakalaOrderId;
        return this;
    }

    @Override
    public boolean isNeedMac() {
        return true;
    }
}
