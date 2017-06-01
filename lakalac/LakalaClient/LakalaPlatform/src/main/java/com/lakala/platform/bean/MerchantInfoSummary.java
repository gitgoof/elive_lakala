package com.lakala.platform.bean;

/**
 * Created by LMQ on 2015/3/26.
 */
public class MerchantInfoSummary {

    /**
     * optStatusName : 成功
     * optStatus : 00
     * typeName : 打款
     * optAmount : 100
     * remark : null
     * type : PAYMENT
     * optTime : null
     * optAmountB : 0
     * merchantName : 测试测试测试
     */
    private String optStatusName;//状态名称
    private String optStatus;//状态代码
    private String typeName;//摘要类型名称
    private Double optAmount;//操作金额
    private Double optAmountB;//操作金额2
    private String remark;//备注
    private String type;//摘要类型
    private String optTime;//操作时间
    private String merchantName;//商户名称

    public void setOptStatusName(String optStatusName) {
        this.optStatusName = optStatusName;
    }

    public void setOptStatus(String optStatus) {
        this.optStatus = optStatus;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setOptAmount(Double optAmount) {
        this.optAmount = optAmount;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOptTime(String optTime) {
        this.optTime = optTime;
    }

    public void setOptAmountB(Double optAmountB) {
        this.optAmountB = optAmountB;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getOptStatusName() {
        return optStatusName;
    }

    public String getOptStatus() {
        return optStatus;
    }

    public String getTypeName() {
        return typeName;
    }

    public Double getOptAmount() {
        return optAmount;
    }

    public String getRemark() {
        return remark;
    }

    public String getType() {
        return type;
    }

    public String getOptTime() {
        return optTime;
    }

    public Double getOptAmountB() {
        return optAmountB;
    }

    public String getMerchantName() {
        return merchantName;
    }
}
