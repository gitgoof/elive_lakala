package com.lakala.elive.beans;


import java.io.Serializable;

/**
 * 商户号 ，
 * 商户名称，
 * 网点编号
 * 网点名称，
 * 网点地址，
 * 网点类别，
 * 网点状态，
 * 网点联系人，
 * 网点联系人手机
 */
public class MerShopInfo implements Serializable {

    private String   shopContact;

    public String getShopMobileNo() {
        return shopMobileNo;
    }

    public void setShopMobileNo(String shopMobileNo) {
        this.shopMobileNo = shopMobileNo;
    }

    private String  shopMobileNo;

    private String  shopContactTel;

    public String getShopContactTel() {
        return shopContactTel;
    }

    public void setShopContactTel(String shopContactTel) {
        this.shopContactTel = shopContactTel;
    }

    public String getShopContact() {
        return shopContact;
    }

    public void setShopContact(String shopContact) {
        this.shopContact = shopContact;
    }

    //商户号
    private String merchantCode;

    //商户名称
    private String merchantName;

    //网点编号
    private String shopNo;

    //网点名称
    private String shopName;

    //网点地址
    private String shopAddress;

    //网点联系人
    private String contactor;

    //网点联系电话
    private String telNo;
    private String telNo1;

    private String email;
    private String shopProv;
    private String shopCity;
    private String shopDist;

    private String merchantStatus;

    //定位的经纬度
    private Double latitude;

    private Double longitude;
    private String taskId;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    private boolean isChecked;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getShopProv() {
        return shopProv;
    }

    public void setShopProv(String shopProv) {
        this.shopProv = shopProv;
    }

    public String getShopCity() {
        return shopCity;
    }

    public void setShopCity(String shopCity) {
        this.shopCity = shopCity;
    }

    public String getShopDist() {
        return shopDist;
    }

    public void setShopDist(String shopDist) {
        this.shopDist = shopDist;
    }

    //  商户真实性核查
    private String merInfoTruth;

    //  商户成熟度
    private String merMature;

    //  商户等级
    private String merLevel;

    //  商户主营
    private String merMainSale;

    //  商户分类
    private String merClass;

    //  商铺位置
    private String merShopArea;

    //  产品类型
    private String productClass;

    //  无交易原因
    private String noTranReason;

    //    商户已有产品
    private String cardAppType;

    //    其他pos优势
    private String otherPosAdvance;

    //    无交易梳理结果
    private String noTranAnalyseResult;

    //    非接改造结果
    private String nonConnChangeResult;

    //    强挥升级结果
    private String swingUpgradeResult;

    //    电子平台绑定结果
    private String bindElePlatformResult;

    //    电子平方台未绑定原因
    private String noBindElePlatformReason;

    //    是否换签
    private String isExchangeSign;

    //    是否换签
    private Integer isVisitToday;

    private String createTime;

    private String lastVisitTime;

    private Integer shopCnt;

    private Integer termCnt;

    private String createTimeStr;

    private String lastVisitTimeStr;
    /**
     * 当天拜访任务标识 1：已添加   0：未添加
     */
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

    /**
     * @return 当天拜访任务标识 1：已添加   0：未添加
     */
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

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getLastVisitTimeStr() {
        return lastVisitTimeStr;
    }

    public void setLastVisitTimeStr(String lastVisitTimeStr) {
        this.lastVisitTimeStr = lastVisitTimeStr;
    }

    public Integer getIsVisitToday() {
        return isVisitToday;
    }

    public void setIsVisitToday(Integer isVisitToday) {
        this.isVisitToday = isVisitToday;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastVisitTime() {
        return lastVisitTime;
    }

    public void setLastVisitTime(String lastVisitTime) {
        this.lastVisitTime = lastVisitTime;
    }

    public Integer getShopCnt() {
        return shopCnt;
    }

    public void setShopCnt(Integer shopCnt) {
        this.shopCnt = shopCnt;
    }

    public Integer getTermCnt() {
        return termCnt;
    }

    public void setTermCnt(Integer termCnt) {
        this.termCnt = termCnt;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }


    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }


    public String getContactor() {
        return contactor;
    }

    public void setContactor(String contactor) {
        this.contactor = contactor;
    }


    public String getShopNo() {
        return shopNo;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }


    public String getMerInfoTruth() {
        return merInfoTruth;
    }

    public void setMerInfoTruth(String merInfoTruth) {
        this.merInfoTruth = merInfoTruth;
    }

    public String getMerMature() {
        return merMature;
    }

    public void setMerMature(String merMature) {
        this.merMature = merMature;
    }

    public String getMerLevel() {
        return merLevel;
    }

    public void setMerLevel(String merLevel) {
        this.merLevel = merLevel;
    }

    public String getMerMainSale() {
        return merMainSale;
    }

    public void setMerMainSale(String merMainSale) {
        this.merMainSale = merMainSale;
    }

    public String getMerClass() {
        return merClass;
    }

    public void setMerClass(String merClass) {
        this.merClass = merClass;
    }

    public String getMerShopArea() {
        return merShopArea;
    }

    public void setMerShopArea(String merShopArea) {
        this.merShopArea = merShopArea;
    }

    public String getProductClass() {
        return productClass;
    }

    public void setProductClass(String productClass) {
        this.productClass = productClass;
    }

    public String getNoTranReason() {
        return noTranReason;
    }

    public void setNoTranReason(String noTranReason) {
        this.noTranReason = noTranReason;
    }

    public String getCardAppType() {
        return cardAppType;
    }

    public void setCardAppType(String cardAppType) {
        this.cardAppType = cardAppType;
    }

    public String getOtherPosAdvance() {
        return otherPosAdvance;
    }

    public void setOtherPosAdvance(String otherPosAdvance) {
        this.otherPosAdvance = otherPosAdvance;
    }

    public String getNoTranAnalyseResult() {
        return noTranAnalyseResult;
    }

    public void setNoTranAnalyseResult(String noTranAnalyseResult) {
        this.noTranAnalyseResult = noTranAnalyseResult;
    }

    public String getNonConnChangeResult() {
        return nonConnChangeResult;
    }

    public void setNonConnChangeResult(String nonConnChangeResult) {
        this.nonConnChangeResult = nonConnChangeResult;
    }

    public String getSwingUpgradeResult() {
        return swingUpgradeResult;
    }

    public void setSwingUpgradeResult(String swingUpgradeResult) {
        this.swingUpgradeResult = swingUpgradeResult;
    }

    public String getBindElePlatformResult() {
        return bindElePlatformResult;
    }

    public void setBindElePlatformResult(String bindElePlatformResult) {
        this.bindElePlatformResult = bindElePlatformResult;
    }

    public String getNoBindElePlatformReason() {
        return noBindElePlatformReason;
    }

    public void setNoBindElePlatformReason(String noBindElePlatformReason) {
        this.noBindElePlatformReason = noBindElePlatformReason;
    }

    public String getIsExchangeSign() {
        return isExchangeSign;
    }

    public void setIsExchangeSign(String isExchangeSign) {
        this.isExchangeSign = isExchangeSign;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public String getTelNo1() {
        return telNo1;
    }

    public void setTelNo1(String telNo1) {
        this.telNo1 = telNo1;
    }

    public String getMerchantStatus() {
        return merchantStatus;
    }

    public void setMerchantStatus(String merchantStatus) {
        this.merchantStatus = merchantStatus;
    }

    @Override
    public String toString() {
        return "MerShopInfo{" +
                "merchantCode='" + merchantCode + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", shopNo='" + shopNo + '\'' +
                ", shopName='" + shopName + '\'' +
                ", shopAddress='" + shopAddress + '\'' +
                ", contactor='" + contactor + '\'' +
                ", telNo='" + telNo + '\'' +
                ", telNo1='" + telNo1 + '\'' +
                ", merchantStatus='" + merchantStatus + '\'' +
                ", merInfoTruth='" + merInfoTruth + '\'' +
                ", merMature='" + merMature + '\'' +
                ", merLevel='" + merLevel + '\'' +
                ", merMainSale='" + merMainSale + '\'' +
                ", merClass='" + merClass + '\'' +
                ", merShopArea='" + merShopArea + '\'' +
                ", productClass='" + productClass + '\'' +
                ", noTranReason='" + noTranReason + '\'' +
                ", cardAppType='" + cardAppType + '\'' +
                ", otherPosAdvance='" + otherPosAdvance + '\'' +
                ", noTranAnalyseResult='" + noTranAnalyseResult + '\'' +
                ", nonConnChangeResult='" + nonConnChangeResult + '\'' +
                ", swingUpgradeResult='" + swingUpgradeResult + '\'' +
                ", bindElePlatformResult='" + bindElePlatformResult + '\'' +
                ", noBindElePlatformReason='" + noBindElePlatformReason + '\'' +
                ", isExchangeSign='" + isExchangeSign + '\'' +
                ", isVisitToday=" + isVisitToday +
                ", createTime='" + createTime + '\'' +
                ", lastVisitTime='" + lastVisitTime + '\'' +
                ", shopCnt=" + shopCnt +
                ", termCnt=" + termCnt +
                ", createTimeStr='" + createTimeStr + '\'' +
                ", lastVisitTimeStr='" + lastVisitTimeStr + '\'' +
                '}';
    }
}
