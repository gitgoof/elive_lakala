package com.lakala.shoudan.common.net.volley;

/**
 * Created by More on 15/9/2.
 */
public class ReturnHeader {

    private String retCode;

    private String errMsg;

    public ReturnHeader(String retCode, String errMsg) {
        this.retCode = retCode;
        this.errMsg = errMsg;
    }

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public boolean isSuccess(){
        return "TS0000".equals(retCode) ||"000000".equals(retCode);
    }

}
