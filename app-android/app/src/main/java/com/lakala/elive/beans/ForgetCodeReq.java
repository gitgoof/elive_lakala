package com.lakala.elive.beans;


/**
 * Created by zhouzx on 2017/2/13.
 */
public class ForgetCodeReq {
    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getDevCode() {
        return devCode;
    }

    public void setDevCode(String devCode) {
        this.devCode = devCode;
    }

    public String getCodeType() {
        return CodeType;
    }

    public void setCodeType(String codeType) {
        CodeType = codeType;
    }

    private String loginName;
    private String devCode;
    private String CodeType;

}
