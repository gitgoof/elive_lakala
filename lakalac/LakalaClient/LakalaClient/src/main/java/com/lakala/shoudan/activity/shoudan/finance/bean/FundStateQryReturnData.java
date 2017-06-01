package com.lakala.shoudan.activity.shoudan.finance.bean;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by LMQ on 2015/10/9.
 */
public class FundStateQryReturnData {

    /**
     * IsExist : 1
     * AuthFlag : 1
     * SignFlag : 0
     * CustomerId : 200502000004862
     * TrsPasswordFlag : 0
     * IdentifierType : 00
     * sid : LAKALAYD000201510090040000004403
     * QuestionFlag : 0
     * Identifier : 430423198404081090
     * _Guid : 6933f5bc38b24890c6d22a48e2116a94
     * AutoTransFlag :
     * CoreUserId : 4400006983
     * UserId : 200502000004862
     * State : 0
     * MinAmount :
     * TerminalId : CBC3A14400006983
     * CustomerName : 扫码
     * LineNo : 100290010114559
     */
    private String IsExist;
    private AuthFlagEnum AuthFlag;
    private String SignFlag;
    private String CustomerId;
    private TrsPasswordFlagEnum TrsPasswordFlag;
    private String IdentifierType;
    private String sid;
    private QuestionFlagEnum QuestionFlag;
    private String Identifier;
    private String _Guid;
    private String AutoTransFlag;
    private String CoreUserId;
    private String UserId;
    private StateEnum State;
    private String MinAmount;
    private String TerminalId;
    private String CustomerName;
    private String LineNo;
    private List<Product> ProductList;
    private List<BankCard> List;
    public FundStateQryReturnData setState(String state) {
        int index = Integer.parseInt(state);
        this.State = State.未开户;
        StateEnum[] values = State.values();
        if(index >= 0 && index < values.length){
            this.State = values[index];
        }
        return this;
    }

    public java.util.List<Product> getProductList() {
        return ProductList;
    }

    public FundStateQryReturnData setProductList(java.util.List<Product> productList) {
        ProductList = productList;
        return this;
    }

    public java.util.List<BankCard> getList() {
        return List;
    }

    public FundStateQryReturnData setList(java.util.List<BankCard> list) {
        List = list;
        return this;
    }

    public void setIsExist(String IsExist) {
        this.IsExist = IsExist;
    }

    public void setAuthFlag(String authFlag) {
        int index = Integer.parseInt(authFlag);
        this.AuthFlag = AuthFlagEnum.未实名认证;
        AuthFlagEnum[] values = AuthFlagEnum.values();
        if(index >=0 && index < values.length){
            this.AuthFlag = values[index];
        }
    }

    public void setSignFlag(String SignFlag) {
        this.SignFlag = SignFlag;
    }

    public void setCustomerId(String CustomerId) {
        this.CustomerId = CustomerId;
    }

    public void setTrsPasswordFlag(String trsPasswordFlag) {
        int index = TextUtils.isEmpty(trsPasswordFlag) ? TrsPasswordFlagEnum.未设置.ordinal() : Integer
                .parseInt(
                        trsPasswordFlag
                );
        this.TrsPasswordFlag = TrsPasswordFlagEnum.未设置;
        TrsPasswordFlagEnum[] values = TrsPasswordFlagEnum.values();
        if(index >=0 && index < values.length){
            this.TrsPasswordFlag = values[index];
        }
    }

    public void setIdentifierType(String IdentifierType) {
        this.IdentifierType = IdentifierType;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setQuestionFlag(int questionFlag) {
        QuestionFlag = QuestionFlagEnum.未设置;
        QuestionFlagEnum[] values = QuestionFlagEnum.values();
        if(questionFlag >=0 && questionFlag < values.length){
            this.QuestionFlag = values[questionFlag];
        }
    }

    public void setIdentifier(String Identifier) {
        this.Identifier = Identifier;
    }

    public void set_Guid(String _Guid) {
        this._Guid = _Guid;
    }

    public void setAutoTransFlag(String AutoTransFlag) {
        this.AutoTransFlag = AutoTransFlag;
    }

    public void setCoreUserId(String CoreUserId) {
        this.CoreUserId = CoreUserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public void setState(int state) {
        this.State = StateEnum.已激活;
        StateEnum[] values = State.values();
        if (state >= 0 && state < values.length) {
            this.State = values[state];
        }
    }

    public void setMinAmount(String MinAmount) {
        this.MinAmount = MinAmount;
    }

    public void setTerminalId(String TerminalId) {
        this.TerminalId = TerminalId;
    }

    public void setCustomerName(String CustomerName) {
        this.CustomerName = CustomerName;
    }

    public void setLineNo(String LineNo) {
        this.LineNo = LineNo;
    }

    public String getIsExist() {
        return IsExist;
    }

    public AuthFlagEnum getAuthFlag() {
        return AuthFlag;
    }

    public String getSignFlag() {
        return SignFlag;
    }

    public String getCustomerId() {
        return CustomerId;
    }

    public TrsPasswordFlagEnum getTrsPasswordFlag() {
        return TrsPasswordFlag;
    }

    public String getIdentifierType() {
        return IdentifierType;
    }

    public String getSid() {
        return sid;
    }

    public QuestionFlagEnum getQuestionFlag() {
        return QuestionFlag;
    }

    public String getIdentifier() {
        return Identifier;
    }

    public String get_Guid() {
        return _Guid;
    }

    public String getAutoTransFlag() {
        return AutoTransFlag;
    }

    public String getCoreUserId() {
        return CoreUserId;
    }

    public String getUserId() {
        return UserId;
    }

    public StateEnum getState() {
        return State;
    }

    public String getMinAmount() {
        return MinAmount;
    }

    public String getTerminalId() {
        return TerminalId;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public String getLineNo() {
        return LineNo;
    }

    public enum StateEnum {
        未开户,已激活,未激活,开户失败
    }
    public enum AuthFlagEnum{
        未实名认证,实名认证
    }

    public enum TrsPasswordFlagEnum {
        未设置,已设置
    }
    public enum QuestionFlagEnum{
        未设置,已设置
    }

    public static FundStateQryReturnData parse(String json){
        return JSON.parseObject(json,FundStateQryReturnData.class);
    }
    public static FundStateQryReturnData parse(JSONObject jsonObject){
        return parse(jsonObject.toString());
    }
}
