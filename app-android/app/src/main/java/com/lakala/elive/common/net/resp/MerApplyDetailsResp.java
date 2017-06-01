package com.lakala.elive.common.net.resp;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wenhaogu on 2017/1/22.
 */

public class MerApplyDetailsResp implements Serializable {


    private String commandId;
    private ContentBean content;
    private String resultCode;
    private int resultDataType;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public int getResultDataType() {
        return resultDataType;
    }

    public void setResultDataType(int resultDataType) {
        this.resultDataType = resultDataType;
    }

    public static class ContentBean implements Serializable {

        private MerApplyInfoBean merApplyInfo;
        private MerOpenInfoBean merOpenInfo;
        private List<MerAttachFileListBean> merAttachFileList;
        private TerminalInfo terminalInfo;
        private List<CardAppRateInfoListBean> cardAppRateInfoList;

        public List<CardAppRateInfoListBean> getCardAppRateInfoList() {
            return cardAppRateInfoList;
        }

        public void setCardAppRateInfoList(List<CardAppRateInfoListBean> cardAppRateInfoList) {
            this.cardAppRateInfoList = cardAppRateInfoList;
        }

        public MerApplyInfoBean getMerApplyInfo() {
            return merApplyInfo;
        }

        public void setMerApplyInfo(MerApplyInfoBean merApplyInfo) {
            this.merApplyInfo = merApplyInfo;
        }

        public MerOpenInfoBean getMerOpenInfo() {
            return merOpenInfo;
        }

        public void setMerOpenInfo(MerOpenInfoBean merOpenInfo) {
            this.merOpenInfo = merOpenInfo;
        }

        public List<MerAttachFileListBean> getMerAttachFileList() {
            return merAttachFileList;
        }

        public void setMerAttachFileList(List<MerAttachFileListBean> merAttachFileList) {
            this.merAttachFileList = merAttachFileList;
        }

        public TerminalInfo getTerminalInfo() {
            return terminalInfo;
        }

        public void setTerminalInfo(TerminalInfo terminalInfoList) {
            this.terminalInfo = terminalInfoList;
        }

        public static class MerApplyInfoBean implements Serializable {


            private String applyChannel;
            private String applyId;
            private long applyTime;
            private String applyTimeStr;
            private String applyType;
            private String createBy;
            private long createTime;
            private String createTimeStr;
            private String deviceDrawMethod;
            private String process;
            private String resultId;
            private String status;
            private String updateBy;
            private long updateTime;
            private String shopId;
            private String statusName;
            private String processName;

            public String getStatusName() {
                return statusName;
            }

            public void setStatusName(String statusName) {
                this.statusName = statusName;
            }

            public String getProcessName() {
                return processName;
            }

            public void setProcessName(String processName) {
                this.processName = processName;
            }

            public String getResultDesc() {
                return resultDesc;
            }

            public void setResultDesc(String resultDesc) {
                this.resultDesc = resultDesc;
            }

            private String resultDesc;

            public String getShopId() {
                return shopId;
            }

            public void setShopId(String shopId) {
                this.shopId = shopId;
            }

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

            public long getApplyTime() {
                return applyTime;
            }

            public void setApplyTime(long applyTime) {
                this.applyTime = applyTime;
            }

            public String getApplyTimeStr() {
                return applyTimeStr;
            }

            public void setApplyTimeStr(String applyTimeStr) {
                this.applyTimeStr = applyTimeStr;
            }

            public String getApplyType() {
                return applyType;
            }

            public void setApplyType(String applyType) {
                this.applyType = applyType;
            }

            public String getCreateBy() {
                return createBy;
            }

            public void setCreateBy(String createBy) {
                this.createBy = createBy;
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

            public String getDeviceDrawMethod() {
                return deviceDrawMethod;
            }

            public void setDeviceDrawMethod(String deviceDrawMethod) {
                this.deviceDrawMethod = deviceDrawMethod;
            }

            public String getProcess() {
                return process;
            }

            public void setProcess(String process) {
                this.process = process;
            }

            public String getResultId() {
                return resultId;
            }

            public void setResultId(String resultId) {
                this.resultId = resultId;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getUpdateBy() {
                return updateBy;
            }

            public void setUpdateBy(String updateBy) {
                this.updateBy = updateBy;
            }

            public long getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(long updateTime) {
                this.updateTime = updateTime;
            }
        }

        public static class MerOpenInfoBean implements Serializable {


            private String accountKind;
            private String accountName;
            private String accountNo;
            private String applyId;
            private String shopId;
            private String businessArea;
            private String businessAreaStr;
            private String businessContent;
            private String bussinessContentStr;

            private String businessTime;

            private String province;
            private String provinceCode;
            private String city;
            private String cityCode;
            private String district;
            private String districtCode;

            private String contact;
            private String createBy;
            private long createTime;
            private String emailAddr;
            private String idCard;
            private String idName;
            private String idCardExpire;
            private String latitude;
            private String longitude;

            private String mccBigType;
            private String mccBigTypeStr;
            private String mccCode;
            private String mccCodeStr;
            private String mccSmallType;
            private String mccSmallTypeStr;

            private String merAddr;
            private String merchantId;
            private String merchantName;
            private String mobileNo;
            private String openningBank;
            private String openningBankName;
            private String corporateRepresentative;
            private String settlePeriod;
            private String settlePeriodStr;
            private String updateBy;
            private long updateTime;
            private String registAddress;
            private String registAgeLimit;
            private String merLicenceNo;
            private String registName;

            public String getRegistName() {
                return registName;
            }

            public void setRegistName(String registName) {
                this.registName = registName;
            }

            public String getIdName() {
                return idName;
            }

            public void setIdName(String idName) {
                this.idName = idName;
            }

            public String getShopId() {
                return shopId;
            }

            public void setShopId(String shopId) {
                this.shopId = shopId;
            }

            public String getBusinessAreaStr() {
                return businessAreaStr;
            }

            public void setBusinessAreaStr(String businessAreaStr) {
                this.businessAreaStr = businessAreaStr;
            }

            public String getBussinessContentStr() {
                return bussinessContentStr;
            }

            public void setBussinessContentStr(String bussinessContentStr) {
                this.bussinessContentStr = bussinessContentStr;
            }

            public String getMccBigType() {
                return mccBigType;
            }

            public void setMccBigType(String mccBigType) {
                this.mccBigType = mccBigType;
            }

            public String getMccBigTypeStr() {
                return mccBigTypeStr;
            }

            public void setMccBigTypeStr(String mccBigTypeStr) {
                this.mccBigTypeStr = mccBigTypeStr;
            }

            public String getMccCodeStr() {
                return mccCodeStr;
            }

            public void setMccCodeStr(String mccCodeStr) {
                this.mccCodeStr = mccCodeStr;
            }

            public String getMccSmallType() {
                return mccSmallType;
            }

            public void setMccSmallType(String mccSmallType) {
                this.mccSmallType = mccSmallType;
            }

            public String getMccSmallTypeStr() {
                return mccSmallTypeStr;
            }

            public void setMccSmallTypeStr(String mccSmallTypeStr) {
                this.mccSmallTypeStr = mccSmallTypeStr;
            }

            public String getSettlePeriodStr() {
                return settlePeriodStr;
            }

            public void setSettlePeriodStr(String settlePeriodStr) {
                this.settlePeriodStr = settlePeriodStr;
            }

            public String getMerLicenceNo() {
                return merLicenceNo;
            }

            public void setMerLicenceNo(String merLicenceNo) {
                this.merLicenceNo = merLicenceNo;
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

            public String getCorporateRepresentative() {
                return corporateRepresentative;
            }

            public void setCorporateRepresentative(String corporateRepresentative) {
                this.corporateRepresentative = corporateRepresentative;
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

            public String getApplyId() {
                return applyId;
            }

            public void setApplyId(String applyId) {
                this.applyId = applyId;
            }

            public String getBusinessArea() {
                return businessArea;
            }

            public void setBusinessArea(String businessArea) {
                this.businessArea = businessArea;
            }

            public String getBusinessContent() {
                return businessContent;
            }

            public void setBusinessContent(String businessContent) {
                this.businessContent = businessContent;
            }

            public String getBusinessTime() {
                return businessTime;
            }

            public void setBusinessTime(String businessTime) {
                this.businessTime = businessTime;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getCityCode() {
                return cityCode;
            }

            public void setCityCode(String cityCode) {
                this.cityCode = cityCode;
            }

            public String getContact() {
                return contact;
            }

            public void setContact(String contact) {
                this.contact = contact;
            }

            public String getCreateBy() {
                return createBy;
            }

            public void setCreateBy(String createBy) {
                this.createBy = createBy;
            }

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }

            public String getDistrict() {
                return district;
            }

            public void setDistrict(String district) {
                this.district = district;
            }

            public String getDistrictCode() {
                return districtCode;
            }

            public void setDistrictCode(String districtCode) {
                this.districtCode = districtCode;
            }

            public String getEmailAddr() {
                return emailAddr;
            }

            public void setEmailAddr(String emailAddr) {
                this.emailAddr = emailAddr;
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

            public String getMccCode() {
                return mccCode;
            }

            public void setMccCode(String mccCode) {
                this.mccCode = mccCode;
            }

            public String getMerAddr() {
                return merAddr;
            }

            public void setMerAddr(String merAddr) {
                this.merAddr = merAddr;
            }

            public String getMerchantId() {
                return merchantId;
            }

            public void setMerchantId(String merchantId) {
                this.merchantId = merchantId;
            }

            public String getMerchantName() {
                return merchantName;
            }

            public void setMerchantName(String merchantName) {
                this.merchantName = merchantName;
            }

            public String getMobileNo() {
                return mobileNo;
            }

            public void setMobileNo(String mobileNo) {
                this.mobileNo = mobileNo;
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

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getProvinceCode() {
                return provinceCode;
            }

            public void setProvinceCode(String provinceCode) {
                this.provinceCode = provinceCode;
            }

            public String getSettlePeriod() {
                return settlePeriod;
            }

            public void setSettlePeriod(String settlePeriod) {
                this.settlePeriod = settlePeriod;
            }

            public String getUpdateBy() {
                return updateBy;
            }

            public void setUpdateBy(String updateBy) {
                this.updateBy = updateBy;
            }

            public long getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(long updateTime) {
                this.updateTime = updateTime;
            }
        }

        public static class MerAttachFileListBean implements Serializable {


            private int enabled;
            private String fileId;
            private String fileName;
            private String fileType;
            private String locate;
            private long operateDate;
            private String operator;
            private String refObject;
            private String saveName;
            private String segment;
            private String comments;

            public String getComments() {
                return comments;
            }

            public void setComments(String comments) {
                this.comments = comments;
            }

            public int getEnabled() {
                return enabled;
            }

            public void setEnabled(int enabled) {
                this.enabled = enabled;
            }

            public String getFileId() {
                return fileId;
            }

            public void setFileId(String fileId) {
                this.fileId = fileId;
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

            public String getLocate() {
                return locate;
            }

            public void setLocate(String locate) {
                this.locate = locate;
            }

            public long getOperateDate() {
                return operateDate;
            }

            public void setOperateDate(long operateDate) {
                this.operateDate = operateDate;
            }

            public String getOperator() {
                return operator;
            }

            public void setOperator(String operator) {
                this.operator = operator;
            }

            public String getRefObject() {
                return refObject;
            }

            public void setRefObject(String refObject) {
                this.refObject = refObject;
            }

            public String getSaveName() {
                return saveName;
            }

            public void setSaveName(String saveName) {
                this.saveName = saveName;
            }

            public String getSegment() {
                return segment;
            }

            public void setSegment(String segment) {
                this.segment = segment;
            }
        }

        public static class TerminalInfo implements Serializable {


            private String applyId;
            private String deviceDrawMethod;
            private double deviceSaleAmount;
            private String deviceType;
            private double deviceRent;
            private double deviceDeposit;
            private String deviceCnt;
            private double commFee;

            public double getCommFee() {
                return commFee;
            }

            public void setCommFee(double commFee) {
                this.commFee = commFee;
            }

            public String getDeviceCnt() {
                return deviceCnt;
            }

            public void setDeviceCnt(String deviceCnt) {
                this.deviceCnt = deviceCnt;
            }

            public double getDeviceRent() {
                return deviceRent;
            }

            public void setDeviceRent(int deviceRent) {
                this.deviceRent = deviceRent;
            }

            public double getDeviceDeposit() {
                return deviceDeposit;
            }

            public void setDeviceDeposit(int deviceDeposit) {
                this.deviceDeposit = deviceDeposit;
            }


            public String getApplyId() {
                return applyId;
            }

            public void setApplyId(String applyId) {
                this.applyId = applyId;
            }

            public String getDeviceDrawMethod() {
                return deviceDrawMethod;
            }

            public void setDeviceDrawMethod(String deviceDrawMethod) {
                this.deviceDrawMethod = deviceDrawMethod;
            }

            public double getDeviceSaleAmount() {
                return deviceSaleAmount;
            }

            public void setDeviceSaleAmount(double deviceSaleAmount) {
                this.deviceSaleAmount = deviceSaleAmount;
            }

            public String getDeviceType() {
                return deviceType;
            }

            public void setDeviceType(String deviceType) {
                this.deviceType = deviceType;
            }


        }

        public static class CardAppRateInfoListBean implements Serializable {


            private String applyId;
            private double baseFeeMax;
            private double baseFeeRate;
            private String cardType;

            public String getApplyId() {
                return applyId;
            }

            public void setApplyId(String applyId) {
                this.applyId = applyId;
            }

            public double getBaseFeeMax() {
                return baseFeeMax;
            }

            public void setBaseFeeMax(double baseFeeMax) {
                this.baseFeeMax = baseFeeMax;
            }

            public double getBaseFeeRate() {
                return baseFeeRate;
            }

            public void setBaseFeeRate(double baseFeeRate) {
                this.baseFeeRate = baseFeeRate;
            }

            public String getCardType() {
                return cardType;
            }

            public void setCardType(String cardType) {
                this.cardType = cardType;
            }
        }
    }
}
