package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by fengx on 2015/10/19.
 */
public class GenerateBillIdRequest extends BaseRequest{


    /**
     * BankName : 民生银行
     * PayeeAcNo : 6226150066399885
     * Amount : 100
     * PayeeName : 测试
     * ArrivalType : 3
     * BankCode : 305
     * AccountType : 1
     */
    private String BankName;
    private String PayeeAcNo;
    private String Amount;
    private String PayeeName;
    private String ArrivalType;
    private String BankCode;
    private String AccountType;
    private String BusId = "1GB";

    public String getBusId() {
        return BusId;
    }

    public GenerateBillIdRequest setBusId(String busId) {
        BusId = busId;
        return this;
    }

    public void setBankName(String BankName) {
        this.BankName = BankName;
    }

    public void setPayeeAcNo(String PayeeAcNo) {
        this.PayeeAcNo = PayeeAcNo;
    }

    public void setAmount(String Amount) {
        this.Amount = Amount;
    }

    public void setPayeeName(String PayeeName) {
        this.PayeeName = PayeeName;
    }

    public void setArrivalType(String ArrivalType) {
        this.ArrivalType = ArrivalType;
    }

    public void setBankCode(String BankCode) {
        this.BankCode = BankCode;
    }

    public void setAccountType(String AccountType) {
        this.AccountType = AccountType;
    }

    public String getBankName() {
        return BankName;
    }

    public String getPayeeAcNo() {
        return PayeeAcNo;
    }

    public String getAmount() {
        return Amount;
    }

    public String getPayeeName() {
        return PayeeName;
    }

    public String getArrivalType() {
        return ArrivalType;
    }

    public String getBankCode() {
        return BankCode;
    }

    public String getAccountType() {
        return AccountType;
    }
}
