package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by LMQ on 2015/9/14.
 * 理财业务开通-请求参数
 */
public class FundSignUpRequest extends BaseRequest {
    private String BusId = "1G0";
    private String MobileInBank;//银行预留手机号
    private String State="0";//0:未开户，1:已激活，2:未激活
    private String IdentifierType = "01";//证件类型，身份证:00,其他不填
    private String Identifier;//证件号码
    private String CustomerName = ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName();//客户名
    private String BankName = ApplicationEx.getInstance().getUser().getMerchantInfo().getBankName();//银行名称
    private String BankCode = ApplicationEx.getInstance().getUser().getMerchantInfo().getBankNo();//银行Code
    private String AccountNo = ApplicationEx.getInstance().getUser().getMerchantInfo().getAccountNo();//签约卡卡号
    private String TrsPassword;//支付密码

    public String getBusId() {
        return BusId;
    }

    public FundSignUpRequest setBusId(String busId) {
        BusId = busId;
        return this;
    }

    public String getBankName() {
        return BankName;
    }

    public FundSignUpRequest setBankName(String bankName) {
        BankName = bankName;
        return this;
    }

    public String getBankCode() {
        return BankCode;
    }

    public FundSignUpRequest setBankCode(String bankCode) {
        BankCode = bankCode;
        return this;
    }

    public String getAccountNo() {
        return AccountNo;
    }

    public FundSignUpRequest setAccountNo(String accountNo) {
        AccountNo = accountNo;
        return this;
    }

    public String getTrsPassword() {
        return TrsPassword;
    }

    public FundSignUpRequest setTrsPassword(String trsPassword) {
        TrsPassword = trsPassword;
        return this;
    }

    public String getMobileInBank() {
        return MobileInBank;
    }

    public FundSignUpRequest setMobileInBank(String mobileInBank) {
        MobileInBank = mobileInBank;
        return this;
    }

    public String getState() {
        return State;
    }

    public FundSignUpRequest setState(String state) {
        State = state;
        return this;
    }

    public String getIdentifierType() {
        return IdentifierType;
    }

    public FundSignUpRequest setIdentifierType(String identifierType) {
        IdentifierType = identifierType;
        return this;
    }

    public String getIdentifier() {
        return Identifier;
    }

    public FundSignUpRequest setIdentifier(String identifier) {
        Identifier = identifier;
        return this;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public FundSignUpRequest setCustomerName(String customerName) {
        CustomerName = customerName;
        return this;
    }
}
