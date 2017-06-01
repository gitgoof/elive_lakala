package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

import com.lakala.shoudan.common.CommonBaseRequest;

/**
 * Created by fengxuan on 2015/12/21.
 */
public class WithdrawToBillRequest extends CommonBaseRequest{

    private String payeeAcNo;
    private String payeeName;
    private String bankCode;
    private String bankName;
    private String amount;
    private String arrivalType;
    private String payeeMobile;
    private String billno;
    private String busid;

    public String getBusid() {
        return busid;
    }

    public void setBusid(String busid) {
        this.busid = busid;
    }

    public String getBillno() {
        return billno;
    }

    public void setBillno(String billno) {
        this.billno = billno;
    }

    public String getPayeeAcNo() {
        return payeeAcNo;
    }

    public void setPayeeAcNo(String payeeAcNo) {
        this.payeeAcNo = payeeAcNo;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getArrivalType() {
        return arrivalType;
    }

    public void setArrivalType(String arrivalType) {
        this.arrivalType = arrivalType;
    }

    public String getPayeeMobile() {
        return payeeMobile;
    }

    public void setPayeeMobile(String payeeMobile) {
        this.payeeMobile = payeeMobile;
    }

    public WithdrawToBillRequest(Context context) {
        super(context);
    }

    @Override
    public boolean isNeedMac() {
        return true;
    }
}
