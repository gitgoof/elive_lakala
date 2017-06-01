package com.lakala.elive.map.bean;

import java.io.Serializable;

/**
 * Created by gaofeng on 2017/4/10.
 */
public class StoreInfo implements Serializable {
    /*
    "createTime": 1366363182000,
			"createTimeStr": "2013-04-19",
			"isVisitToday": 1,
			"lastVisitTime": 1490791935000,
			"lastVisitTimeStr": "2017-03-29",
			"latitude": "31.238078",
			"longitude": "121.396189",
			"merchantCode": "822100054990418",
			"merchantName": "华夏特产",
			"shopAddress": "北京市海淀区圆明园西路18号中发百旺商城1808",
			"shopCnt": 1,
			"shopName": "北京市海淀区马连洼街道张录俊干果商店",
			"shopNo": "105289",
			"termCnt": 1,
			"todayTaskFlag": "0",
			"visitAddr": "北京市海淀区圆明园西路18号中发百旺商城1808",
			"visitLatitude": "39.99857100",
			"visitLongitude": "116.41431700"
    */
    private long createTime;

    private String createTimeStr;

    private int isVisitToday;
    private long lastVisitTime;
    private String lastVisitTimeStr;
    private String latitude;
    private String longitude;
    private String merchantCode;
    private String merchantName;
    private String shopAddress;
    private int shopCnt;
    private String shopName;
    private String shopNo;
    private int termCnt;
    private String todayTaskFlag;
    private String visitAddr;
    private String visitLatitude;
    private String visitLongitude;

    public String getVisitLongitude() {
        return visitLongitude;
    }

    public void setVisitLongitude(String visitLongitude) {
        this.visitLongitude = visitLongitude;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public int getIsVisitToday() {
        return isVisitToday;
    }

    public void setIsVisitToday(int isVisitToday) {
        this.isVisitToday = isVisitToday;
    }

    public long getLastVisitTime() {
        return lastVisitTime;
    }

    public void setLastVisitTime(long lastVisitTime) {
        this.lastVisitTime = lastVisitTime;
    }

    public String getLastVisitTimeStr() {
        return lastVisitTimeStr;
    }

    public void setLastVisitTimeStr(String lastVisitTimeStr) {
        this.lastVisitTimeStr = lastVisitTimeStr;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public int getShopCnt() {
        return shopCnt;
    }

    public void setShopCnt(int shopCnt) {
        this.shopCnt = shopCnt;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopNo() {
        return shopNo;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    public int getTermCnt() {
        return termCnt;
    }

    public void setTermCnt(int termCnt) {
        this.termCnt = termCnt;
    }

    public String getTodayTaskFlag() {
        return todayTaskFlag;
    }

    public void setTodayTaskFlag(String todayTaskFlag) {
        this.todayTaskFlag = todayTaskFlag;
    }

    public String getVisitAddr() {
        return visitAddr;
    }

    public void setVisitAddr(String visitAddr) {
        this.visitAddr = visitAddr;
    }

    public String getVisitLatitude() {
        return visitLatitude;
    }

    public void setVisitLatitude(String visitLatitude) {
        this.visitLatitude = visitLatitude;
    }
}
