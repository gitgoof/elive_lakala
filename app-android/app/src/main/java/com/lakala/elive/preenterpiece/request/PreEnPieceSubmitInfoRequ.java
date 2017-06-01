package com.lakala.elive.preenterpiece.request;

import java.io.Serializable;

/**
 * Created by ousachisan on 2017/3/23.
 * //合作方预进件的提交商户信息查询接口的Request(ELIVE_PARTNER_APPLY_001)
 */
public class PreEnPieceSubmitInfoRequ implements Serializable {

    public String authToken;    //授权令牌
    public PartnerApplyInfo merApplyInfo;//预进件申请信息
    public MerOpenInfo merOpenInfo;//商户基本信息

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public PartnerApplyInfo getMerApplyInfo() {
        return merApplyInfo;
    }

    public void setMerApplyInfo(PartnerApplyInfo merApplyInfo) {
        this.merApplyInfo = merApplyInfo;
    }

    public MerOpenInfo getMerOpenInfo() {
        return merOpenInfo;
    }

    public void setMerOpenInfo(MerOpenInfo merOpenInfo) {
        this.merOpenInfo = merOpenInfo;
    }


    public static class PartnerApplyInfo implements Serializable {

        public String applyId; //申请ID
        public String applyType;//申请类型
        public String status;// 申请状态
        public String process;// 当前步骤

        public String applyChannel;

        public String getApplyChannel() {
            return applyChannel;
        }

        public void setApplyChannel(String applyChannel) {
            this.applyChannel = applyChannel;
        }

        public String getApplyId() {
            return applyId;
        }

        public void setApplyId(String applyId) {
            this.applyId = applyId;
        }

        public String getApplyType() {
            return applyType;
        }

        public void setApplyType(String applyType) {
            this.applyType = applyType;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getProcess() {
            return process;
        }

        public void setProcess(String process) {
            this.process = process;
        }
    }


    public static class MerOpenInfo implements Serializable {

        //申请ID
        public String applyId;
        //商户名称
        public String merchantName;

        // 商户地址
        public String merAddr;
        //经度
        public String longitude;
        // 纬度
        public String latitude;
        // 归属省代码
        public String provinceCode;
        //归属市代码
        public String cityCode;
        // 归属区县代码
        public String districtCode;
        // 商户mcc类别
        public String mccCode;
        //营业用地面积
        public String businessArea;
        // 营业时间
        public String businessTime;
        //经营内容
        public String businessContent;
        // 联系人
        public String contact;
        // 联系邮箱
        public String emailAddr;
        // 联系固定电话
        public String teleNo;
        // 联系手机
        public String mobileNo;
        // 申请人身份证名称
        public String idName;
        // 申请人身份证号
        public String idCard;
        //申请人身份证有效期
        public String idCardExpire;
        // 结算银行账户性质
        public String accountKind;
        // 结算银行账户名称
        public String accountName;
        // 结算银行卡号
        public String accountNo;
        // 结算开户行
        public String openningBank;
        // 结算开户行名称
        public String openningBankName;
        //  结算银行预留手机号码
        public String bankRegMobileNo;
        // 结算周期
        public String settlePeriod;
        // 统一社会信用代码
        public String socialCreditCode;
        // 营业执照号
        public String merLicenceNo;
        // 组织机构号
        public String merOrgLicenceNo;
        // 税务登记号
        public String merTaxId;
        // 注册名称
        public String registName;
        // 注册年限
        public String registAgeLimit;
        //  注册地址
        public String registAddress;
        //法人代表
        public String corporate_representative;
        // dd法人证件类型
        public String credentialType;
        //  法人性别
        public String corporateSex;
        // 法人证件号
        public String credentialIdNo;

        public String getApplyId() {
            return applyId;
        }

        public void setApplyId(String applyId) {
            this.applyId = applyId;
        }

        public String getMerAddr() {
            return merAddr;
        }

        public void setMerAddr(String merAddr) {
            this.merAddr = merAddr;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getProvinceCode() {
            return provinceCode;
        }

        public void setProvinceCode(String provinceCode) {
            this.provinceCode = provinceCode;
        }

        public String getCityCode() {
            return cityCode;
        }

        public void setCityCode(String cityCode) {
            this.cityCode = cityCode;
        }

        public String getDistrictCode() {
            return districtCode;
        }

        public void setDistrictCode(String districtCode) {
            this.districtCode = districtCode;
        }

        public String getMccCode() {
            return mccCode;
        }

        public void setMccCode(String mccCode) {
            this.mccCode = mccCode;
        }

        public String getBusinessArea() {
            return businessArea;
        }

        public void setBusinessArea(String businessArea) {
            this.businessArea = businessArea;
        }

        public String getBusinessTime() {
            return businessTime;
        }

        public void setBusinessTime(String businessTime) {
            this.businessTime = businessTime;
        }

        public String getBusinessContent() {
            return businessContent;
        }

        public void setBusinessContent(String businessContent) {
            this.businessContent = businessContent;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getEmailAddr() {
            return emailAddr;
        }

        public void setEmailAddr(String emailAddr) {
            this.emailAddr = emailAddr;
        }

        public String getTeleNo() {
            return teleNo;
        }

        public void setTeleNo(String teleNo) {
            this.teleNo = teleNo;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public String getIdName() {
            return idName;
        }

        public void setIdName(String idName) {
            this.idName = idName;
        }

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }

        public String getIdCardExpire() {
            return idCardExpire;
        }

        public void setIdCardExpire(String idCardExpire) {
            this.idCardExpire = idCardExpire;
        }

        public String getAccountKind() {
            return accountKind;
        }

        public void setAccountKind(String accountKind) {
            this.accountKind = accountKind;
        }

        public String getAccountName() {
            return accountName;
        }

        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }

        public String getAccountNo() {
            return accountNo;
        }

        public void setAccountNo(String accountNo) {
            this.accountNo = accountNo;
        }

        public String getOpenningBank() {
            return openningBank;
        }

        public void setOpenningBank(String openningBank) {
            this.openningBank = openningBank;
        }

        public String getOpenningBankName() {
            return openningBankName;
        }

        public void setOpenningBankName(String openningBankName) {
            this.openningBankName = openningBankName;
        }

        public String getBankRegMobileNo() {
            return bankRegMobileNo;
        }

        public void setBankRegMobileNo(String bankRegMobileNo) {
            this.bankRegMobileNo = bankRegMobileNo;
        }

        public String getSettlePeriod() {
            return settlePeriod;
        }

        public void setSettlePeriod(String settlePeriod) {
            this.settlePeriod = settlePeriod;
        }

        public String getSocialCreditCode() {
            return socialCreditCode;
        }

        public void setSocialCreditCode(String socialCreditCode) {
            this.socialCreditCode = socialCreditCode;
        }

        public String getMerLicenceNo() {
            return merLicenceNo;
        }

        public void setMerLicenceNo(String merLicenceNo) {
            this.merLicenceNo = merLicenceNo;
        }

        public String getMerOrgLicenceNo() {
            return merOrgLicenceNo;
        }

        public void setMerOrgLicenceNo(String merOrgLicenceNo) {
            this.merOrgLicenceNo = merOrgLicenceNo;
        }

        public String getMerTaxId() {
            return merTaxId;
        }

        public void setMerTaxId(String merTaxId) {
            this.merTaxId = merTaxId;
        }

        public String getRegistName() {
            return registName;
        }

        public void setRegistName(String registName) {
            this.registName = registName;
        }

        public String getRegistAgeLimit() {
            return registAgeLimit;
        }

        public void setRegistAgeLimit(String registAgeLimit) {
            this.registAgeLimit = registAgeLimit;
        }

        public String getRegistAddress() {
            return registAddress;
        }

        public void setRegistAddress(String registAddress) {
            this.registAddress = registAddress;
        }

        public String getCorporate_representative() {
            return corporate_representative;
        }

        public void setCorporate_representative(String corporate_representative) {
            this.corporate_representative = corporate_representative;
        }

        public String getCredentialType() {
            return credentialType;
        }

        public void setCredentialType(String credentialType) {
            this.credentialType = credentialType;
        }

        public String getCorporateSex() {
            return corporateSex;
        }

        public void setCorporateSex(String corporateSex) {
            this.corporateSex = corporateSex;
        }

        public String getCredentialIdNo() {
            return credentialIdNo;
        }

        public void setCredentialIdNo(String credentialIdNo) {
            this.credentialIdNo = credentialIdNo;
        }

    }


}
