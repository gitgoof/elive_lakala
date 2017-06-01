package com.lakala.platform.swiper.devicemanager.controller;

/**
 * Created by More on 15/12/20.
 */
public class TransFactor {

    private TransactionType transactionType;

    /**
     * 交易金额
     */
    private String amount;


    /**
     * 服务端随机因子
     */
    private String serviceCode;

    /**
     * 附加信息(入卡号,手机号等)
     */
    private String additionalMsg;

    private boolean downGrade = false;

    public boolean isDownGrade() {
        return downGrade;
    }

    public void setDownGrade(boolean downGrade) {
        this.downGrade = downGrade;
    }

    public TransFactor(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public TransFactor() {
    }

    public TransFactor(TransactionType transactionType, String amount) {
        this.transactionType = transactionType;
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getAdditionalMsg() {
        return additionalMsg;
    }

    public void setAdditionalMsg(String additionalMsg) {
        this.additionalMsg = additionalMsg;
    }

    public boolean isIcSupported() {
        return transactionType.isSupportIc();
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
}
