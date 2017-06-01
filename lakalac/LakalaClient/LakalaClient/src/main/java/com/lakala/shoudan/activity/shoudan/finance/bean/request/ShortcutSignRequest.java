package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by LMQ on 2015/10/9.
 */
public class ShortcutSignRequest extends BaseRequest {
    private String BusId = "1CI";
    private String CustomerName;//签约人名称
    private String IdentifierType = "01";//00:身份证
    private String Identifier;//证件号
    private String BankName;//银行名称
    private String BankCode;//银行行号
    private String MobileInBank;//银行预留手机号
    private String SupportSMS = "1";//是否支持短信发送，1：支持，0：不支持
    private String AccountNo;
    private String AccountType = "1";
    private String OTPPassword;
    private String BusinessType = "0";
    private String BusinessCode = "228102";
    private String signType = "1";
    private String SmsIsSend;
    private String Srcid;

    public String getSrcid() {
        return Srcid;
    }

    public ShortcutSignRequest setSrcid(String srcid) {
        Srcid = srcid;
        return this;
    }

    public String getBusId() {
        return BusId;
    }

    public ShortcutSignRequest setBusId(String busId) {
        BusId = busId;
        return this;
    }

    public ShortcutSignRequest setIdentifierType(String identifierType) {
        IdentifierType = identifierType;
        return this;
    }

    public ShortcutSignRequest setSupportSMS(String supportSMS) {
        SupportSMS = supportSMS;
        return this;
    }

    public ShortcutSignRequest setAccountType(String accountType) {
        AccountType = accountType;
        return this;
    }

    public ShortcutSignRequest setBusinessType(String businessType) {
        BusinessType = businessType;
        return this;
    }

    public ShortcutSignRequest setBusinessCode(String businessCode) {
        BusinessCode = businessCode;
        return this;
    }

    public ShortcutSignRequest setSignType(String signType) {
        this.signType = signType;
        return this;
    }

    public String getOTPPassword() {
        return OTPPassword;
    }

    public ShortcutSignRequest setOTPPassword(String OTPPassword) {
        this.OTPPassword = OTPPassword;
        return this;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public ShortcutSignRequest setCustomerName(String customerName) {
        CustomerName = customerName;
        return this;
    }

    public String getIdentifierType() {
        return IdentifierType;
    }
    public String getIdentifier() {
        return Identifier;
    }

    public ShortcutSignRequest setIdentifier(String identifier) {
        Identifier = identifier;
        return this;
    }

    public String getBankName() {
        return BankName;
    }

    public ShortcutSignRequest setBankName(String bankName) {
        BankName = bankName;
        return this;
    }

    public String getBankCode() {
        return BankCode;
    }

    public ShortcutSignRequest setBankCode(String bankCode) {
        BankCode = bankCode;
        return this;
    }

    public String getMobileInBank() {
        return MobileInBank;
    }

    public ShortcutSignRequest setMobileInBank(String mobileInBank) {
        MobileInBank = mobileInBank;
        return this;
    }

    public String getSupportSMS() {
        return SupportSMS;
    }

    public String getAccountNo() {
        return AccountNo;
    }

    public ShortcutSignRequest setAccountNo(String accountNo) {
        AccountNo = accountNo;
        return this;
    }

    public String getAccountType() {
        return AccountType;
    }

    public String getBusinessType() {
        return BusinessType;
    }

    public String getBusinessCode() {
        return BusinessCode;
    }

    public String getSignType() {
        return signType;
    }

    public String getSmsIsSend() {
        return SmsIsSend;
    }

    public ShortcutSignRequest setSmsIsSend(String smsIsSend) {
        SmsIsSend = smsIsSend;
        return this;
    }
}
