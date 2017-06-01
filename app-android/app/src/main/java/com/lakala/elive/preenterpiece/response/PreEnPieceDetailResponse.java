package com.lakala.elive.preenterpiece.response;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ousachisan on 2017/3/23.
 * <p>
 * 合作方预进件的详情(商户开通上传图片列表)查询接口的Response(ELIVE_PARTNER_APPLY_001)
 */

public class PreEnPieceDetailResponse implements Serializable {

    private String message;
    private String resultCode;
    private ContentBean content;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public static class ContentBean implements Serializable {

        public PartnerApplyInfo merApplyInfo;
        public MerOpenInfo merOpenInfo;
        public List<MerAttachFile> merAttachFileList;

        public PartnerApplyInfo getMerApplyInfo() {
            return merApplyInfo;
        }

        public void setMerApplyInfo(PartnerApplyInfo merApplyInfo) {
            this.merApplyInfo = merApplyInfo;
        }

        public List<MerAttachFile> getMerAttachFileList() {
            return merAttachFileList;
        }

        public void setMerAttachFileList(List<MerAttachFile> merAttachFileList) {
            this.merAttachFileList = merAttachFileList;
        }

        public MerOpenInfo getMerOpenInfo() {
            return merOpenInfo;
        }

        public void setMerOpenInfo(MerOpenInfo merOpenInfo) {
            this.merOpenInfo = merOpenInfo;
        }

        public static class PartnerApplyInfo implements Serializable {

            // 申请ID
            public String applyId;
            // 申请类型
            public String applyType;
            // 申请状态
            public String status;
            public String accountKind;
            public String applyTimeStr;

            public String getApplyTimeStr() {
                return applyTimeStr;
            }

            public void setApplyTimeStr(String applyTimeStr) {
                this.applyTimeStr = applyTimeStr;
            }


            public String getAccountKind() {
                return accountKind;
            }

            public void setAccountKind(String accountKind) {
                this.accountKind = accountKind;
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

        }


        public static class MerOpenInfo implements Serializable {
            //申请ID
            public String applyId;
            //
            public String applyTime;

            //商户地址
            public String merAddr;

            public String accountKind;

            public String province;
            public String city;
            public String district;

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getDistrict() {
                return district;
            }

            public void setDistrict(String district) {
                this.district = district;
            }

            public String getAccountKind() {
                return accountKind;
            }

            public void setAccountKind(String accountKind) {
                this.accountKind = accountKind;
            }

            public String getMerAddr() {
                return merAddr;
            }

            public void setMerAddr(String merAddr) {
                this.merAddr = merAddr;
            }

            public String corporateRepresentative;

            public String getApplyTime() {
                return applyTime;
            }

            public void setApplyTime(String applyTime) {
                this.applyTime = applyTime;
            }

            public String getMerchantId() {
                return merchantId;
            }

            public void setMerchantId(String merchantId) {
                this.merchantId = merchantId;
            }

            public String getCorporateRepresentative() {
                return corporateRepresentative;
            }

            public void setCorporateRepresentative(String corporateRepresentative) {
                this.corporateRepresentative = corporateRepresentative;
            }


            public String registName;

            public String getRegistName() {
                return registName;
            }

            public void setRegistName(String registName) {
                this.registName = registName;
            }

            // 商户编号
            public String merchantId;
            //  商户名称
            public String merchantName;
            // 商户地址
            public String address;
            //  经度
            public String longitude;
            // 纬度
            public String latitude;
            // 归属省代码
            public String provinceCode;
            // 归属市代码
            public String cityCode;
            // 归属区县代码
            public String districtCode;
            //商户mcc类别
            public String mccCode;
            // 联系人
            public String contact;
            // 联系邮箱
            public String email;
            //  联系固定电话
            public String teleNo;
            //联系手机
            public String mobileNo;
            // 申请人身份证名称
            public String idName;
            // 申请人身份证号
            public String idCard;
            // 申请人身份证有效期
            public String idCardExpire;
            // 结算银行账户名称
            public String accountName;
            // 结算银行卡号
            public String accountNo;
            //  结算开户行
            public String openningBank;
            //结算开户行名称
            public String openningBankName;
            // 结算银行预留手机号码
            public String bankRegMobileNo;
            // 统一社会信用代码
            public String socialCreditCode;
            //  营业执照号
            public String merLicenceNo;
            // 组织机构号
            public String merOrgLicenceNo;
            //税务登记号
            public String merTaxId;
            // 注册年限
            public String registAgeLimit;
            // 注册地址
            public String registAddress;
            //  法人代表
            public String corporate_representative;
            // dd法人证件类型
            public String credentialType;
            //  法人性别
            public String corporateSex;
            //  法人证件号
            public String credentialIdNo;

            public String getMerchantName() {
                return merchantName;
            }

            public void setMerchantName(String merchantName) {
                this.merchantName = merchantName;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
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

            public String getContact() {
                return contact;
            }

            public void setContact(String contact) {
                this.contact = contact;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
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

        public static class MerAttachFile implements Serializable {

            //申请ID
            public String applyId;

            public String fileId;

            public String getFileId() {
                return fileId;
            }

            public void setFileId(String fileId) {
                this.fileId = fileId;
            }

            // 文件分类
            public String fileType;
            // 文件内容
            public String fileContent;
            // 文件大类
            public String segment;
            //文件序号
            public String comments;
            //文件名称
            public String fileName;

            public String getApplyId() {
                return applyId;
            }

            public void setApplyId(String applyId) {
                this.applyId = applyId;
            }

            public String getFileName() {
                return fileName;
            }

            public void setFileName(String fileName) {
                this.fileName = fileName;
            }

            public String getFileType() {
                return fileType;
            }

            public void setFileType(String fileType) {
                this.fileType = fileType;
            }

            public String getFileContent() {
                return fileContent;
            }

            public void setFileContent(String fileContent) {
                this.fileContent = fileContent;
            }

            public String getSegment() {
                return segment;
            }

            public void setSegment(String segment) {
                this.segment = segment;
            }

            public String getComments() {
                return comments;
            }

            public void setComments(String comments) {
                this.comments = comments;
            }
        }
    }
}
