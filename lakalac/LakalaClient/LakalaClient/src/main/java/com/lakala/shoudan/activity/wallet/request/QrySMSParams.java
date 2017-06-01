package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

import com.lakala.shoudan.common.CommonBaseRequest;

/**
 * Created by LMQ on 2015/12/29.
 */
public class QrySMSParams extends CommonBaseRequest {
    public QrySMSParams(Context context) {
        super(context);
    }
    private String isFirstSub = "0";//0:非第一次，1：第一次
    private String payerAcNo;
    private String mobileInBank;
    private String billAmount;
    private String lotteryMerchantFlag = "1";//上送时 0为彩票收银台 其他为普通收银台
    private String shortCardFlag = "0";
    private String billId;

    public String getBillId() {
        return billId;
    }

    public QrySMSParams setBillId(String billId) {
        this.billId = billId;
        return this;
    }

    public String getShortCardFlag() {
        return shortCardFlag;
    }

    public QrySMSParams setShortCardFlag(String shortCardFlag) {
        this.shortCardFlag = shortCardFlag;
        return this;
    }

    public String getIsFirstSub() {
        return isFirstSub;
    }

    public QrySMSParams setIsFirstSub(String isFirstSub) {
        this.isFirstSub = isFirstSub;
        return this;
    }

    public String getPayerAcNo() {
        return payerAcNo;
    }

    public QrySMSParams setPayerAcNo(String payerAcNo) {
        this.payerAcNo = payerAcNo;
        return this;
    }

    public String getMobileInBank() {
        return mobileInBank;
    }

    public QrySMSParams setMobileInBank(String mobileInBank) {
        this.mobileInBank = mobileInBank;
        return this;
    }

    public String getBillAmount() {
        return billAmount;
    }

    public QrySMSParams setBillAmount(String billAmount) {
        this.billAmount = billAmount;
        return this;
    }

    public String getLotteryMerchantFlag() {
        return lotteryMerchantFlag;
    }

    public QrySMSParams setLotteryMerchantFlag(String lotteryMerchantFlag) {
        this.lotteryMerchantFlag = lotteryMerchantFlag;
        return this;
    }
}
