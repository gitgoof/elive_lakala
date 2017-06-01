package com.lakala.platform.bean;

import org.json.JSONObject;

/**
 * Created by linmq on 2016/3/17.
 */
public class MerLevelInfo {
//   debitPerLimit	借记卡单笔限额
//    debitDayLimit	借记卡单日限额
//    debitMonthLimit	借记卡单月限额
//    creditPerLimit	贷记卡单笔限额
//    creditDayLimit	贷记卡单日限额
//    creditMonthLimit	贷记卡单月限额
//    merLevel	商户等级
//    wechatPerLimit	扫码单笔限额。
//    wechatDayLimit	扫码单日限额。
//    wechatMonthLimit	扫码单月限额。
//    bigDebitPerLimit	MPOS大额收款借记卡单笔限额。
//    bigDebitDayLimit	MPOS大额收款借记卡单日限额。
//    bigDebitMonthLimit	MPOS大额收款借记卡单月限额。
//    bigCreditPerLimit	  MPOS大额收款贷记卡单笔限额。
//    bigCreditDayLimit	  MPOS大额收款贷记卡单日限额。
//    bigCreditMonthLimit	  MPOS大额收款贷记卡单月限额。
//    bigCreditPerLimitMin 	  MPOS大额收款贷记卡单笔最小限额。
//    bigDebitPerLimitMin	  MPOS大额收款贷记卡单笔最小限额。
//    dPerMinLimit	  D0单笔最小提款金额。
//    dPerMaxLimit	  D0单笔最大提款金额。
//    dDayMaxLimit	  D0单日最大提款金额。
//    dDayMaxCount	  D0单日最大提款笔数。


    private String debitPerLimit;
    private String debitDayLimit;
    private String debitMonthLimit;
    private String creditPerLimit;
    private String creditDayLimit;
    private String creditMonthLimit;
    private String merLevel;
    private String wechatPerLimit;
    private String wechatDayLimit;
    private String wechatMonthLimit;
    private String bigDebitPerLimit;
    private String bigDebitDayLimit;
    private String bigDebitMonthLimit;
    private String bigCreditPerLimit;
    private String bigCreditDayLimit;
    private String bigCreditMonthLimit;
    private String bigCreditPerLimitMin;
    private String bigDebitPerLimitMin;
    private String dPerMinLimit;
    private String dPerMaxLimit;
    private String dDayMaxLimit;
    private String dDayMaxCount;

    public static MerLevelInfo newInstance(JSONObject jsonObject){
        if(jsonObject == null){
            return null;
        }
        MerLevelInfo merLevelInfo = new MerLevelInfo();
        merLevelInfo.setDebitPerLimit(jsonObject.optString("debitPerLimit","0"));
        merLevelInfo.setDebitDayLimit(jsonObject.optString("debitDayLimit","0"));
        merLevelInfo.setDebitMonthLimit(jsonObject.optString("debitMonthLimit","0"));
        merLevelInfo.setCreditPerLimit(jsonObject.optString("creditPerLimit","0"));
        merLevelInfo.setCreditDayLimit(jsonObject.optString("creditDayLimit","0"));
        merLevelInfo.setCreditMonthLimit(jsonObject.optString("creditMonthLimit","0"));
        merLevelInfo.setWechatPerLimit(jsonObject.optString("wechatPerLimit","0"));
        merLevelInfo.setWechatDayLimit(jsonObject.optString("wechatDayLimit","0"));
        merLevelInfo.setWechatMonthLimit(jsonObject.optString("wechatMonthLimit","0"));
        merLevelInfo.setBigDebitPerLimit(jsonObject.optString("bigDebitPerLimit","0"));
        merLevelInfo.setBigDebitDayLimit(jsonObject.optString("bigDebitDayLimit","0"));
        merLevelInfo.setBigDebitMonthLimit(jsonObject.optString("bigDebitMonthLimit","0"));
        merLevelInfo.setBigCreditPerLimit(jsonObject.optString("bigCreditPerLimit","0"));
        merLevelInfo.setBigCreditDayLimit(jsonObject.optString("bigCreditDayLimit","0"));
        merLevelInfo.setBigCreditMonthLimit(jsonObject.optString("bigCreditMonthLimit","0"));
        merLevelInfo.setBigCreditPerLimitMin(jsonObject.optString("bigCreditPerLimitMin","0"));
        merLevelInfo.setBigDebitPerLimitMin(jsonObject.optString("bigDebitPerLimitMin","0"));
        merLevelInfo.setdPerMinLimit(jsonObject.optString("dPerMinLimit","0"));
        merLevelInfo.setdPerMaxLimit(jsonObject.optString("dPerMaxLimit","0"));
        merLevelInfo.setdDayMaxCount(jsonObject.optString("dDayMaxCount","0"));
        merLevelInfo.setdDayMaxLimit(jsonObject.optString("dDayMaxLimit","0"));
        merLevelInfo.setMerLevel(jsonObject.optString("merLevel",""));
        return merLevelInfo;
    }

    public String getDebitPerLimit() {
        return debitPerLimit;
    }

    public MerLevelInfo setDebitPerLimit(String debitPerLimit) {
        this.debitPerLimit = debitPerLimit;
        return this;
    }

    public String getDebitDayLimit() {
        return debitDayLimit;
    }

    public MerLevelInfo setDebitDayLimit(String debitDayLimit) {
        this.debitDayLimit = debitDayLimit;
        return this;
    }

    public String getBigCreditPerLimitMin() {
        return bigCreditPerLimitMin;
    }

    public void setBigCreditPerLimitMin(String bigCreditPerLimitMin) {
        this.bigCreditPerLimitMin = bigCreditPerLimitMin;
    }

    public String getBigDebitPerLimitMin() {
        return bigDebitPerLimitMin;
    }

    public void setBigDebitPerLimitMin(String bigDebitPerLimitMin) {
        this.bigDebitPerLimitMin = bigDebitPerLimitMin;
    }

    public String getDebitMonthLimit() {
        return debitMonthLimit;
    }

    public MerLevelInfo setDebitMonthLimit(String debitMonthLimit) {
        this.debitMonthLimit = debitMonthLimit;
        return this;
    }

    public String getCreditPerLimit() {
        return creditPerLimit;
    }

    public MerLevelInfo setCreditPerLimit(String creditPerLimit) {
        this.creditPerLimit = creditPerLimit;
        return this;
    }

    public String getCreditDayLimit() {
        return creditDayLimit;
    }

    public MerLevelInfo setCreditDayLimit(String creditDayLimit) {
        this.creditDayLimit = creditDayLimit;
        return this;
    }

    public String getCreditMonthLimit() {
        return creditMonthLimit;
    }

    public MerLevelInfo setCreditMonthLimit(String creditMonthLimit) {
        this.creditMonthLimit = creditMonthLimit;
        return this;
    }

    public String getMerLevel() {
        return merLevel;
    }

    public MerLevelInfo setMerLevel(String merLevel) {
        this.merLevel = merLevel;
        return this;
    }

    public String getWechatPerLimit() {
        return wechatPerLimit;
    }

    public void setWechatPerLimit(String wechatPerLimit) {
        this.wechatPerLimit = wechatPerLimit;
    }

    public String getWechatDayLimit() {
        return wechatDayLimit;
    }

    public void setWechatDayLimit(String wechatDayLimit) {
        this.wechatDayLimit = wechatDayLimit;
    }

    public String getWechatMonthLimit() {
        return wechatMonthLimit;
    }

    public void setWechatMonthLimit(String wechatMonthLimit) {
        this.wechatMonthLimit = wechatMonthLimit;
    }

    public String getBigDebitPerLimit() {
        return bigDebitPerLimit;
    }

    public void setBigDebitPerLimit(String bigDebitPerLimit) {
        this.bigDebitPerLimit = bigDebitPerLimit;
    }

    public String getBigDebitDayLimit() {
        return bigDebitDayLimit;
    }

    public void setBigDebitDayLimit(String bigDebitDayLimit) {
        this.bigDebitDayLimit = bigDebitDayLimit;
    }

    public String getBigDebitMonthLimit() {
        return bigDebitMonthLimit;
    }

    public void setBigDebitMonthLimit(String bigDebitMonthLimit) {
        this.bigDebitMonthLimit = bigDebitMonthLimit;
    }

    public String getBigCreditPerLimit() {
        return bigCreditPerLimit;
    }

    public void setBigCreditPerLimit(String bigCreditPerLimit) {
        this.bigCreditPerLimit = bigCreditPerLimit;
    }

    public String getBigCreditDayLimit() {
        return bigCreditDayLimit;
    }

    public void setBigCreditDayLimit(String bigCreditDayLimit) {
        this.bigCreditDayLimit = bigCreditDayLimit;
    }

    public String getBigCreditMonthLimit() {
        return bigCreditMonthLimit;
    }

    public void setBigCreditMonthLimit(String bigCreditMonthLimit) {
        this.bigCreditMonthLimit = bigCreditMonthLimit;
    }

    public String getdPerMinLimit() {
        return dPerMinLimit;
    }

    public void setdPerMinLimit(String dPerMinLimit) {
        this.dPerMinLimit = dPerMinLimit;
    }

    public String getdPerMaxLimit() {
        return dPerMaxLimit;
    }

    public void setdPerMaxLimit(String dPerMaxLimit) {
        this.dPerMaxLimit = dPerMaxLimit;
    }

    public String getdDayMaxLimit() {
        return dDayMaxLimit;
    }

    public void setdDayMaxLimit(String dDayMaxLimit) {
        this.dDayMaxLimit = dDayMaxLimit;
    }

    public String getdDayMaxCount() {
        return dDayMaxCount;
    }

    public void setdDayMaxCount(String dDayMaxCount) {
        this.dDayMaxCount = dDayMaxCount;
    }
}
