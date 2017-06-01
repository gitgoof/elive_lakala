package com.lakala.shoudan.activity.shoudan.records;

import org.json.JSONObject;

/**
 * Created by fengxuan on 2015/12/28.
 */
public class WithdrawInfo {

    private String dealTypeCode;
    private String dealTypeName;
    private String status;
    private String merchantName;
    private String merchantCode;
    private String pasm;
    private String posOgName;
    private String sysSeq;
    private String authCode;
    private String batchNo;
    private String voucherCode;
    private String paymentAccount;
    private String dealStartDateTime;
    private double dealAmount;
    private String sign;
    private String sid;
    private String busId;
    private String busName;
    private String tradeType;

    public String getDealTypeCode() {
        return dealTypeCode;
    }

    public void setDealTypeCode(String dealTypeCode) {
        this.dealTypeCode = dealTypeCode;
    }

    public String getDealTypeName() {
        return dealTypeName;
    }

    public void setDealTypeName(String dealTypeName) {
        this.dealTypeName = dealTypeName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getPasm() {
        return pasm;
    }

    public void setPasm(String pasm) {
        this.pasm = pasm;
    }

    public String getPosOgName() {
        return posOgName;
    }

    public void setPosOgName(String posOgName) {
        this.posOgName = posOgName;
    }

    public String getSysSeq() {
        return sysSeq;
    }

    public void setSysSeq(String sysSeq) {
        this.sysSeq = sysSeq;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getPaymentAccount() {
        return paymentAccount;
    }

    public void setPaymentAccount(String paymentAccount) {
        this.paymentAccount = paymentAccount;
    }

    public String getDealStartDateTime() {
        return dealStartDateTime;
    }

    public void setDealStartDateTime(String dealStartDateTime) {
        this.dealStartDateTime = dealStartDateTime;
    }

    public double getDealAmount() {
        return dealAmount;
    }

    public void setDealAmount(double dealAmount) {
        this.dealAmount = dealAmount;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public WithdrawInfo parseObject(JSONObject jsonObject){

        WithdrawInfo withdrawInfo = new WithdrawInfo();
        withdrawInfo.setDealTypeCode(jsonObject.optString("dealTypeCode"));
        withdrawInfo.setDealTypeName(jsonObject.optString("dealTypeName"));
        withdrawInfo.setStatus(jsonObject.optString("status"));
        withdrawInfo.setMerchantName(jsonObject.optString("merchantName"));
        withdrawInfo.setMerchantCode(jsonObject.optString("merchantCode"));
        withdrawInfo.setPasm(jsonObject.optString("pasm"));
        withdrawInfo.setPosOgName(jsonObject.optString("posOgName"));
        withdrawInfo.setSysSeq(jsonObject.optString("sysSeq"));
        withdrawInfo.setAuthCode(jsonObject.optString("authCode"));
        withdrawInfo.setBatchNo(jsonObject.optString("batchNo"));
        withdrawInfo.setVoucherCode(jsonObject.optString("voucherCode"));
        withdrawInfo.setPaymentAccount(jsonObject.optString("paymentAccount"));
        withdrawInfo.setDealStartDateTime(jsonObject.optString("dealStartDateTime"));
        withdrawInfo.setDealAmount(jsonObject.optDouble("dealAmount"));
        withdrawInfo.setSign(jsonObject.optString("sign"));
        withdrawInfo.setSid(jsonObject.optString("sid"));
        withdrawInfo.setBusId(jsonObject.optString("busId"));
        withdrawInfo.setBusName(jsonObject.optString("busName"));
        withdrawInfo.setTradeType(jsonObject.optString("tradeType"));
        return withdrawInfo;
    }
}
