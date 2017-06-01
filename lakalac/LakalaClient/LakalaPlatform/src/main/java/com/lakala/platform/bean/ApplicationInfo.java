package com.lakala.platform.bean;

/**
 * Created by LMQ on 2015/3/30.
 */
public class ApplicationInfo {

    /**
     * msg : null
     * applicationType : APPLY_BILL
     * amount : null
     * shopName : null
     * applicantName : null
     * applicantMobile : null
     * applicantAddr : null
     * reviewRemark : null
     * optCode : null
     * applicationId : null
     * applyTime : 20150325011459
     * shopNo : 822290063000099
     * reviewTime : 20150325011459
     * status : FROZEN
     */
    private String msg;//用户留言
    private String applicationType;//订单类型
    private String amount;//金额
    private String shopName;
    private String applicantName;
    private String applicantMobile;//申请人\联系人手机
    private String applicantAddr;//申请人\联系人地址
    private String reviewRemark;//审核注释
    private String optCode;//业务类型
    private String applicationId;//订单号
    private String applyTime;//申请时间
    private String shopNo;//商户号
    private String reviewTime;//审核时间
    private String status;//状态

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public void setApplicantMobile(String applicantMobile) {
        this.applicantMobile = applicantMobile;
    }

    public void setApplicantAddr(String applicantAddr) {
        this.applicantAddr = applicantAddr;
    }

    public void setReviewRemark(String reviewRemark) {
        this.reviewRemark = reviewRemark;
    }

    public void setOptCode(String optCode) {
        this.optCode = optCode;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    public void setReviewTime(String reviewTime) {
        this.reviewTime = reviewTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public String getAmount() {
        return amount;
    }

    public String getShopName() {
        return shopName;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public String getApplicantMobile() {
        return applicantMobile;
    }

    public String getApplicantAddr() {
        return applicantAddr;
    }

    public String getReviewRemark() {
        return reviewRemark;
    }

    public String getOptCode() {
        return optCode;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public String getShopNo() {
        return shopNo;
    }

    public String getReviewTime() {
        return reviewTime;
    }

    public String getStatus() {
        return status;
    }
}
