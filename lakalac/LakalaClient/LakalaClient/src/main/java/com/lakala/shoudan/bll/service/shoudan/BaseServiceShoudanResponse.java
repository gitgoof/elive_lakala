package com.lakala.shoudan.bll.service.shoudan;

import org.json.JSONObject;

/**
 * 联网返回数据解析后基础类
 * Created by More on 14-1-14.
 */
public class BaseServiceShoudanResponse {
    /**
     * 联网成功返回
     */
    private boolean pass;
    /**
     * 返回的错误信息
     */
    private String errMsg;
    /**
     * 是否超时
     */
    private boolean timeout = false;

    /**
     *
     * sid
     */
    private String sid;

    /**
     * 流水号
     */
    private String ref;

    /**
     * 交易时间
     */
    private String sytm;

//            "authCode";//授权码
//            // "posOgName";//收单机构
//            "batchNo";//批次号
//            SYS_SEQ = "sysSeq";//检索号
    /**
     * 授权码
     */
    private String authCode;

    /**
     * 检索号
     */
    private String sysReq;

    /**
     * 检索号
     */
    private String sysRef;

    public String getSysRef() {
        return sysRef;
    }

    public void setSysRef(String sysrRef) {
        this.sysRef = sysrRef;
    }

    /**
     * 收单机构
     */
    private String pos0gName;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 明文卡号
     */
    private String pan;

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getSysReq() {
        return sysReq;
    }

    public void setSysReq(String sysReq) {
        this.sysReq = sysReq;
    }

    public String getPos0gName() {
        return pos0gName;
    }

    public void setPos0gName(String pos0gName) {
        this.pos0gName = pos0gName;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getSytm() {
        return sytm;
    }

    public void setSytm(String sytm) {
        this.sytm = sytm;
    }

    private JSONObject retData;

    public JSONObject getRetData() {
        return retData;
    }

    public void setRetData(JSONObject retData) {
        this.retData = retData;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public boolean isPass() {
        return pass;
    }

    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }

    public boolean isTimeout() {
        return timeout;
    }
}
