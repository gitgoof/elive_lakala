package com.lakala.platform.bean;

import org.json.JSONObject;

/**
 * Created by WangCheng on 2016/8/25.
 */
public class StlmRem2 {

    private String remAmt;
    private String remFee;
    private String merName;
    private String remTime;
    private String remStatus;
    private String remAccNo;
    private String shopNo;
    private String remStatusName;
    private String json;

    public StlmRem2(JSONObject jsonObject){
        remAmt=jsonObject.optString("remAmt");
        remFee=jsonObject.optString("remFee");
        merName=jsonObject.optString("merName");
        remTime=jsonObject.optString("remTime");
        remStatus=jsonObject.optString("remStatus");
        remAccNo=jsonObject.optString("remAccNo");
        shopNo=jsonObject.optString("shopNo");
        remStatusName=jsonObject.optString("remStatusName");
        json=jsonObject.toString();
    }

    public String getRemAmt() {
        return remAmt;
    }

    public void setRemAmt(String remAmt) {
        this.remAmt = remAmt;
    }

    public String getRemFee() {
        return remFee;
    }

    public void setRemFee(String remFee) {
        this.remFee = remFee;
    }

    public String getMerName() {
        return merName;
    }

    public void setMerName(String merName) {
        this.merName = merName;
    }

    public String getRemTime() {
        return remTime;
    }

    public void setRemTime(String remTime) {
        this.remTime = remTime;
    }

    public String getRemStatus() {
        return remStatus;
    }

    public void setRemStatus(String remStatus) {
        this.remStatus = remStatus;
    }

    public String getRemAccNo() {
        return remAccNo;
    }

    public void setRemAccNo(String remAccNo) {
        this.remAccNo = remAccNo;
    }

    public String getShopNo() {
        return shopNo;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    public String getRemStatusName() {
        return remStatusName;
    }

    public void setRemStatusName(String remStatusName) {
        this.remStatusName = remStatusName;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
