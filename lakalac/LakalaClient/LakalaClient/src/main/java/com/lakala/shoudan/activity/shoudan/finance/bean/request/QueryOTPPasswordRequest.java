package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by LMQ on 2015/9/14.
 * 理财开通页-短信验证（文档接口3.25.27）
 */
public class QueryOTPPasswordRequest extends BaseRequest {
    private String BusId = "19J";
    private String CustomerName;//签约人名称
    private String IdentifierType = "01";//00:身份证
    private String Identifier;//证件号
    private String BankName;//银行名称
    private String BankCode;//银行行号
    private String MobileInBank;//银行预留手机号
    private String SupportSMS = "1";//是否支持短信发送，1：支持，0：不支持
    private String IsShortcutSign = "1";//是否签约快捷，1：是，0：否
    private String AuthFlag = "1";//实名认证标识，0：已认证，1：未认证
    private String AccountNo;//签约卡卡号
    private String AccountType = "1";//签约卡类型，1：借记卡，2：信用卡
    private String CVN2;//信用卡
    private String CardExp;//信用卡有效期
    private String BusinessType = "0";//业务类型
    private String BusinessCode = "228102";//业务编码
    private String signType = "1";//签约类型

    public String getBusId() {
        return BusId;
    }

    public QueryOTPPasswordRequest setBusId(String busId) {
        BusId = busId;
        return this;
    }

    public String getBusinessCode() {
        return BusinessCode;
    }

    public QueryOTPPasswordRequest setBusinessCode(String businessCode) {
        BusinessCode = businessCode;
        return this;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public QueryOTPPasswordRequest setCustomerName(String customerName) {
        CustomerName = customerName;
        return this;
    }

    public String getIdentifierType() {
        return IdentifierType;
    }

    public QueryOTPPasswordRequest setIdentifierType(String identifierType) {
        IdentifierType = identifierType;
        return this;
    }

    public String getIdentifier() {
        return Identifier;
    }

    public QueryOTPPasswordRequest setIdentifier(String identifier) {
        Identifier = identifier;
        return this;
    }

    public String getBankName() {
        return BankName;
    }

    public QueryOTPPasswordRequest setBankName(String bankName) {
        BankName = bankName;
        return this;
    }

    public String getBankCode() {
        return BankCode;
    }

    public QueryOTPPasswordRequest setBankCode(String bankCode) {
        BankCode = bankCode;
        return this;
    }

    public String getMobileInBank() {
        return MobileInBank;
    }

    public QueryOTPPasswordRequest setMobileInBank(String mobileInBank) {
        MobileInBank = mobileInBank;
        return this;
    }

    public String getSupportSMS() {
        return SupportSMS;
    }

    public QueryOTPPasswordRequest setSupportSMS(String supportSMS) {
        SupportSMS = supportSMS;
        return this;
    }

    public String getIsShortcutSign() {
        return IsShortcutSign;
    }

    public QueryOTPPasswordRequest setIsShortcutSign(String isShortcutSign) {
        IsShortcutSign = isShortcutSign;
        return this;
    }

    public String getAuthFlag() {
        return AuthFlag;
    }

    public QueryOTPPasswordRequest setAuthFlag(String authFlag) {
        AuthFlag = authFlag;
        return this;
    }

    public String getAccountNo() {
        return AccountNo;
    }

    public QueryOTPPasswordRequest setAccountNo(String accountNo) {
        AccountNo = accountNo;
        return this;
    }

    public String getAccountType() {
        return AccountType;
    }

    public QueryOTPPasswordRequest setAccountType(String accountType) {
        AccountType = accountType;
        return this;
    }

    public String getCVN2() {
        return CVN2;
    }

    public QueryOTPPasswordRequest setCVN2(String CVN2) {
        this.CVN2 = CVN2;
        return this;
    }

    public String getCardExp() {
        return CardExp;
    }

    public QueryOTPPasswordRequest setCardExp(String cardExp) {
        CardExp = cardExp;
        return this;
    }

    public String getBusinessType() {
        return BusinessType;
    }

    public QueryOTPPasswordRequest setBusinessType(String businessType) {
        BusinessType = businessType;
        return this;
    }

    public String getSignType() {
        return signType;
    }

    public QueryOTPPasswordRequest setSignType(String signType) {
        this.signType = signType;
        return this;
    }
}
