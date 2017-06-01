package com.lakala.elive.common.net.req;

import com.lakala.elive.common.net.req.base.RequestInfo;

import java.util.Arrays;

/**
 * @author hongzhiliang
 * @version $Rev$
 * @time 2016/9/26 10:44
 * @des
 *
 * 用户登录请求体
 *
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class UserReqInfo extends RequestInfo{

    private String loginName; //用户登录名

    public String getPasswordMd5() {
        return passwordMd5;
    }

    public void setPasswordMd5(String passwordMd5) {
        this.passwordMd5 = passwordMd5;
    }

    private String passwordMd5; //MD5登录密码

    private String password;  //登录密码

    protected String creditCode;//验证码

    private String codeType;//动态码获取类型 （1、授信码 2、动态密码）

    private String[]  dictTypeCode;  //数据字典类型编码 列表

    private String noticeNo;

    private String voteId;

    private String reportId;

    private String reportName;

    private String  typeCode;  //数据字典类型编码 列表
    private String  bigType;  //数据字典类型编码 列表

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getBigType() {
        return bigType;
    }

    public void setBigType(String bigType) {
        this.bigType = bigType;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getVoteId() {
        return voteId;
    }

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }

    public String[] getDictTypeCode() {
        return dictTypeCode;
    }

    public void setDictTypeCode(String[] dictTypeCode) {
        this.dictTypeCode = dictTypeCode;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }


    public String getNoticeNo() {
        return noticeNo;
    }

    public void setNoticeNo(String noticeNo) {
        this.noticeNo = noticeNo;
    }

    @Override
    public String toString() {
        return "UserReqInfo{" +
                "loginName='" + loginName + '\'' +
                ", password='" + password + '\'' +
                ", creditCode='" + creditCode + '\'' +
                ", codeType='" + codeType + '\'' +
                ", dictTypeCode=" + Arrays.toString(dictTypeCode) +
                ", noticeNo='" + noticeNo + '\'' +
                ", voteId='" + voteId + '\'' +
                '}';
    }
}
