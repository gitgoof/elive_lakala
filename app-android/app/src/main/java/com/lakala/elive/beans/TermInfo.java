package com.lakala.elive.beans;


import java.io.Serializable;
import java.util.List;

/**
 *
 * 网点终端信息表
 *
 */
public class TermInfo implements Serializable {

    //  终端编号
    public String terminalCode;

    //  网点号
    public String shopNo;

    //  终端SN
    public String deviceSn;

    //  终端产品分类
    public String productClass;

    //  机具领用类型
    public String deviceDrawMethod;

    //  无交易原因
    private String noTranReason;



    //    无交易梳理结果
    private String noTranAnalyseResult;

    //    非接改造结果
    private String nonConnChangeResult;

    //    强挥升级结果
    private String swingUpgradeResult;


    //  终端类型(纯无线/固定/固定无线)
    private String terminalType;

    //    创建时间
    private String createTimeStr;

    //    最后修改时间
    private String updateTime;

    //    签约机构
    private String signOrg;

    //    对应分公司
    private String branchOrganId;

    private String branchOrgName;
    private String signName;

    //终端状态
    public String termStatus;

    //  其它无交易原因描述
    private String noTranDesc;

    private String organTypeId; //机构小类

    private String organTypeTid;//机构大类

    private String  deviceCheckStatus;

    private List<CardAppInfo>  cardAppVOs;

    private Boolean checkStatus;

    public String getDeviceCheckStatus() {
        return deviceCheckStatus;
    }

    public void setDeviceCheckStatus(String deviceCheckStatus) {
        this.deviceCheckStatus = deviceCheckStatus;
    }

    public Boolean getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(Boolean checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getNoTranDesc() {
        return noTranDesc;
    }

    public void setNoTranDesc(String noTranDesc) {
        this.noTranDesc = noTranDesc;
    }

    public String getOrganTypeId() {
        return organTypeId;
    }

    public void setOrganTypeId(String organTypeId) {
        this.organTypeId = organTypeId;
    }

    public String getOrganTypeTid() {
        return organTypeTid;
    }

    public void setOrganTypeTid(String organTypeTid) {
        this.organTypeTid = organTypeTid;
    }

    public String getTermStatus() {
        return termStatus;
    }

    public void setTermStatus(String termStatus) {
        this.termStatus = termStatus;
    }

    public String getBranchOrgName() {
        return branchOrgName;
    }

    public void setBranchOrgName(String branchOrgName) {
        this.branchOrgName = branchOrgName;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public List<CardAppInfo> getCardAppVOs() {
        return cardAppVOs;
    }

    public void setCardAppVOs(List<CardAppInfo> cardAppVOs) {
        this.cardAppVOs = cardAppVOs;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getShopNo() {
        return shopNo;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public String getProductClass() {
        return productClass;
    }

    public void setProductClass(String productClass) {
        this.productClass = productClass;
    }



    public String getDeviceDrawMethod() {
        return deviceDrawMethod;
    }

    public void setDeviceDrawMethod(String deviceDrawMethod) {
        this.deviceDrawMethod = deviceDrawMethod;
    }


    public String getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getSignOrg() {
        return signOrg;
    }

    public void setSignOrg(String signOrg) {
        this.signOrg = signOrg;
    }

    public String getBranchOrganId() {
        return branchOrganId;
    }

    public void setBranchOrganId(String branchOrganId) {
        this.branchOrganId = branchOrganId;
    }

    public String getNoTranReason() {
        return noTranReason;
    }

    public void setNoTranReason(String noTranReason) {
        this.noTranReason = noTranReason;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TermInfo termInfo = (TermInfo) o;

        return terminalCode.equals(termInfo.terminalCode);

    }

    @Override
    public int hashCode() {
        return terminalCode.hashCode();
    }
}
