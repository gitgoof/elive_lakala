package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.common.CommonBaseRequest;

import org.json.JSONArray;

/**
 * Created by fengxuan on 2015/12/17.
 */
public class WalletTradeRequest extends CommonBaseRequest{

    private String billId;
    private String trsPassword;
    private String lastPaymentType;
    private String terminalId;
    private String paymentTypes;
    private String busid;
    private String amount;
    private String pan;
    private String billno;
    private String notifyURL;
    private String orderToPayFlag;
    private String lakalaOrderId;
    private String params;
    private String fee;

    public String getFee() {
        return fee;
    }

    public WalletTradeRequest setFee(String fee) {
        this.fee = fee;
        return this;
    }

    public String getParams() {
        return params;
    }

    public WalletTradeRequest setParams(String params) {
        this.params = params;
        return this;
    }

    public String getNotifyURL() {
        return notifyURL;
    }

    public WalletTradeRequest setNotifyURL(String notifyURL) {
        this.notifyURL = notifyURL;
        return this;
    }

    public String getOrderToPayFlag() {
        return orderToPayFlag;
    }

    public WalletTradeRequest setOrderToPayFlag(String orderToPayFlag) {
        this.orderToPayFlag = orderToPayFlag;
        return this;
    }

    public String getLakalaOrderId() {
        return lakalaOrderId;
    }

    public WalletTradeRequest setLakalaOrderId(String lakalaOrderId) {
        this.lakalaOrderId = lakalaOrderId;
        return this;
    }

    public String getBillno() {
        return billno;
    }

    public void setBillno(String billno) {
        this.billno = billno;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBusid() {
        return busid;
    }

    public void setBusid(String busid) {
        this.busid = busid;
    }

    public String getPaymentTypes() {
        return paymentTypes;
    }

    public void setPaymentTypes(String paymentTypes) {
        this.paymentTypes = paymentTypes;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getTrsPassword() {
        return trsPassword;
    }

    public void setTrsPassword(String trsPassword) {
        this.trsPassword = trsPassword;
    }

    public String getLastPaymentType() {
        return lastPaymentType;
    }

    public void setLastPaymentType(String lastPaymentType) {
        this.lastPaymentType = lastPaymentType;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public WalletTradeRequest(Context context) {
        super(context);
    }

    @Override
    public boolean isNeedMac() {
        return true;
    }

    @Override
    protected String getTermId() {
        return this.terminalId;
    }
}
