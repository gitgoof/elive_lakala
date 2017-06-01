package com.lakala.platform.bean;

/**
 * Created by LMQ on 2015/3/13.
 */
public class UserMobileChargeHis {

    /**
     * createTime : 1399535633000
     * mobileId : 270
     * updateTime : 1426214505000
     * mobileNo : 13400531556
     * userid : 10002474
     * mobileMark : 林风水
     * status : 1
     */
    private String createTime;
    private int mobileId;
    private String updateTime;
    private String mobileNo;
    private String userid;
    private String mobileMark;
    private String status;

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setMobileId(int mobileId) {
        this.mobileId = mobileId;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setMobileMark(String mobileMark) {
        this.mobileMark = mobileMark;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public int getMobileId() {
        return mobileId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getUserid() {
        return userid;
    }

    public String getMobileMark() {
        return mobileMark;
    }

    public String getStatus() {
        return status;
    }
}
