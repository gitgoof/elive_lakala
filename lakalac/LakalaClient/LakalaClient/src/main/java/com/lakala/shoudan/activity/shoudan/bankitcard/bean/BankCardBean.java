package com.lakala.shoudan.activity.shoudan.bankitcard.bean;

import java.io.Serializable;

/**
 * Created by HJP on 2015/11/24.
 */
public class BankCardBean implements Serializable{
    /**
     * 卡片序号
     */
    private String cardId;
    /**
     * 银行行号
     */
    private String bankCode;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 卡号
     */
    private String accountNo;
    /**
     * 卡类型 1：借记卡；2：信用卡
     */
    private String accountType;
    /**
     * 快捷签约标识 0：未签约；1：已签约；2：已解约
     */
    private String signFlag;
    /**
     * 持卡人姓名
     */
    private String customerName;
    /**
     * logo名字
     */
    private String logoName;
    /**
     * LOGO地址
     */
    private String logoUrl;
    /**
     * 内部银行卡编号
     */
    private String innerBankCode;
    /**
     * 用户在银行的手机号码
     */
    private String bkMobile;

    /**
     * 删除列表位置
     */
    private int position;

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getSignFlag() {
        return signFlag;
    }

    public void setSignFlag(String signFlag) {
        this.signFlag = signFlag;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getLogoName() {
        return logoName;
    }

    public void setLogoName(String logoName) {
        this.logoName = logoName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getInnerBankCode() {
        return innerBankCode;
    }

    public void setInnerBankCode(String innerBankCode) {
        this.innerBankCode = innerBankCode;
    }

    public String getBkMobile() {
        return bkMobile;
    }

    public void setBkMobile(String bkMobile) {
        this.bkMobile = bkMobile;
    }
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void initData(BankCardBean bankCardBean){
        cardId=bankCardBean.getCardId();
        bankCode=bankCardBean.getBankCode();
        bankName=bankCardBean.getBankName();
    }
}
