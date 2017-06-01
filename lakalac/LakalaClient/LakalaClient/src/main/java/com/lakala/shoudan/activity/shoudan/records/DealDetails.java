package com.lakala.shoudan.activity.shoudan.records;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by fengxuan on 2015/12/28.
 */
public class DealDetails implements Serializable {
    private String queryBusId;
    private String dealTypeCode;
    private String dealTypeName;
    private String payTypeName;
    private String payTypeCode;
    private TreasureType treasureType;
    private String status;
    private String merchantName;
    private String merchantCode;
    private String shopNo;
    private String shopName;
    private String pasm;
    private String fee;
    private String tips;
    private String collectionAccount;
    private String posOgName;
    private String sysSeq;

    public String getQueryBusId() {
        return queryBusId;
    }

    public void setQueryBusId(String queryBusId) {
        this.queryBusId = queryBusId;
    }

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
    private String signurl;
    private int isWithdrawInfo;
    private String series;
    private WithdrawInfo withdrawInfo;

    public String getSignurl() {
        return signurl;
    }

    public void setSignurl(String signurl) {
        this.signurl = signurl;
    }

    public String getShopName() {
        return shopName;
    }

    public DealDetails setShopName(String shopName) {
        this.shopName = shopName;
        return this;
    }

    public String getShopNo() {
        return shopNo;
    }

    public DealDetails setShopNo(String shopNo) {
        this.shopNo = shopNo;
        return this;
    }

    public TreasureType getTreasureType() {
        return treasureType;
    }

    public void setTreasureType(TreasureType treasureType) {
        this.treasureType = treasureType;
    }

    public String getPayTypeCode() {
        return payTypeCode;
    }

    public void setPayTypeCode(String payTypeCode) {
        this.payTypeCode = payTypeCode;
    }

    public String getPayTypeName() {
        return payTypeName;
    }

    public void setPayTypeName(String payTypeName) {
        this.payTypeName = payTypeName;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getCollectionAccount() {
        return collectionAccount;
    }

    public void setCollectionAccount(String collectionAccount) {
        this.collectionAccount = collectionAccount;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

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

    public int getIsWithdrawInfo() {
        return isWithdrawInfo;
    }

    public void setIsWithdrawInfo(int isWithdrawInfo) {
        this.isWithdrawInfo = isWithdrawInfo;
    }

    public WithdrawInfo getWithdrawInfo() {
        return withdrawInfo;
    }

    public void setWithdrawInfo(WithdrawInfo withdrawInfo) {
        this.withdrawInfo = withdrawInfo;
    }

    public DealDetails parseObject(JSONObject jsonObject) throws JSONException {

        DealDetails dealDetails = new DealDetails();
        dealDetails.setDealTypeCode(jsonObject.optString("dealTypeCode"));
        dealDetails.setDealTypeName(jsonObject.optString("dealTypeName"));
        dealDetails.setPayTypeName(jsonObject.optString("payTypeName"));
        String payTypeCode = jsonObject.optString("payTypeCode");
        dealDetails.setPayTypeCode(payTypeCode);
        if (payTypeCode.equals("ZX")) {
            treasureType = TreasureType.WALLET_PAY;
        } else if (payTypeCode.equals("ZJ")) {
            treasureType = TreasureType.JIFEN;
        } else if (payTypeCode.equals("ZH")) {
            treasureType = TreasureType.RED_PACKAGE;
        } else if (payTypeCode.equals("XJ")) {
            treasureType = TreasureType.WALLET_AND_JIFEN;
        } else if (payTypeCode.equals("XH")) {
            treasureType = TreasureType.WALLET_AND_RED;
        } else if (payTypeCode.equals("PR")) {
            treasureType = TreasureType.SWIPPER;
        } else if (payTypeCode.equals("WK")) {
            treasureType = TreasureType.KUAIJIE;
        } else if (payTypeCode.equals("DS")) {
            treasureType = TreasureType.BANKCARD;
        }
        dealDetails.setTreasureType(treasureType);
        dealDetails.setQueryBusId(jsonObject.optString("queryBusId", ""));
        dealDetails.setStatus(jsonObject.optString("status"));
        dealDetails.setMerchantName(jsonObject.optString("merchantName"));
        dealDetails.setMerchantCode(jsonObject.optString("merchantCode"));
        dealDetails.setShopNo(jsonObject.optString("shopNo"));
        dealDetails.setShopName(jsonObject.optString("shopName"));
        dealDetails.setPasm(jsonObject.optString("pasm"));
        dealDetails.setFee(jsonObject.optString("fee"));
        dealDetails.setTips(jsonObject.optString("tips"));
        dealDetails.setSeries(jsonObject.optString("series"));
        dealDetails.setCollectionAccount(jsonObject.optString("collectionAccount"));
        dealDetails.setPosOgName(jsonObject.optString("posOgName"));
        dealDetails.setSysSeq(jsonObject.optString("sysSeq"));
        dealDetails.setAuthCode(jsonObject.optString("authCode"));
        dealDetails.setBatchNo(jsonObject.optString("batchNo"));
        dealDetails.setVoucherCode(jsonObject.optString("voucherCode"));
        dealDetails.setPaymentAccount(jsonObject.optString("paymentAccount"));
        dealDetails.setDealStartDateTime(jsonObject.optString("dealStartDateTime"));
        dealDetails.setDealAmount(jsonObject.optDouble("dealAmount"));
        dealDetails.setSign(jsonObject.optString("sign"));
        dealDetails.setSid(jsonObject.optString("sid"));
        dealDetails.setBusId(jsonObject.optString("busId"));
        dealDetails.setBusName(jsonObject.optString("busName"));
        dealDetails.setTradeType(jsonObject.optString("tradeType"));
        dealDetails.setIsWithdrawInfo(jsonObject.optInt("isWithdrawInfo"));
        dealDetails.setSignurl(jsonObject.optString("signurl"));
        JSONObject json = jsonObject.getJSONObject("withdrawInfo");
        WithdrawInfo withdrawInfo = new WithdrawInfo();
        withdrawInfo = withdrawInfo.parseObject(json);
        dealDetails.setWithdrawInfo(withdrawInfo);

        return dealDetails;
    }

    //一元夺宝支付方式
    enum TreasureType {

        WALLET_PAY,       //零钱支付
        JIFEN,            //积分
        RED_PACKAGE,      //红包支付
        WALLET_AND_JIFEN, //零钱加积分
        WALLET_AND_RED,   //零钱加红包
        SWIPPER,          //刷卡支付
        KUAIJIE,          //快捷支付
        BANKCARD;          //银行卡支付

    }
}
