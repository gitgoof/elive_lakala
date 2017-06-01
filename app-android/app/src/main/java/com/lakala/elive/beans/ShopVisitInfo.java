package com.lakala.elive.beans;


import android.graphics.Bitmap;

import com.lakala.elive.common.net.req.base.RequestInfo;

import java.io.Serializable;

/**
 *
 * 商户拜访记录流水
 *
 */
public class ShopVisitInfo extends RequestInfo implements Serializable{

    //网点号
    private String shopNo;

    private String shopName;

    //商户号
    private String merchantCode;

    private String merchantName;

    //签到主键
    private String visitNo;

    //拜访日期
    private String visitData;

    //拜访日期
    private String visitDateStr;

    //拜访类型
    private String visitType;

    //拜访类型
    private String visitTypeName;

    //定位的经纬度
    private Double latitude;

    private Double longitude;

    //用户上传的图片信息
    private Bitmap[] bitmapList;

    //定位的地址信息
    private String address;

    //拜访备注
    private String comments;

    //2016-10-17 16:09:55
    private String endTimeStr;

    private String[] photos ;

    private String staffId;

    private String staffName;

    private String taskId; //任务编号

    private String executeResult; //任务执行结果

    public String getVisitTypeName() {
        return visitTypeName;
    }

    public void setVisitTypeName(String visitTypeName) {
        this.visitTypeName = visitTypeName;
    }

    public String getExecuteResult() {
        return executeResult;
    }

    public void setExecuteResult(String executeResult) {
        this.executeResult = executeResult;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String[] getPhotos() {
        return photos;
    }

    public void setPhotos(String[] photos) {
        this.photos = photos;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

    public void setEndTimeStr(String endTimeStr) {
        this.endTimeStr = endTimeStr;
    }

    public String getVisitDateStr() {
        return visitDateStr;
    }

    public void setVisitDateStr(String visitDateStr) {
        this.visitDateStr = visitDateStr;
    }


    public String getVisitData() {
        return visitData;
    }

    public void setVisitData(String visitData) {
        this.visitData = visitData;
    }

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Bitmap[] getBitmapList() {
        return bitmapList;
    }

    public void setBitmapList(Bitmap[] bitmapList) {
        this.bitmapList = bitmapList;
    }


    public String getVisitNo() {
        return visitNo;
    }

    public void setVisitNo(String visitNo) {
        this.visitNo = visitNo;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


    public String getShopNo() {
        return shopNo;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
}
