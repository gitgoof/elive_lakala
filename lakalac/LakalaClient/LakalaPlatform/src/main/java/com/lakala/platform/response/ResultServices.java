package com.lakala.platform.response;

import android.text.TextUtils;

import com.lakala.platform.http.BusinessRequest;

import java.io.Serializable;

/**
 * Created by ZhangMY on 2015/2/4.
 */
public class ResultServices implements Serializable {

    public String retCode = "";
    public String retMsg = "";
    public String retData = "";


    @Override
    public String toString() {
        return "ResultServices{" +
                "retCode='" + retCode + '\'' +
                ", retMsg='" + retMsg + '\'' +
                ", retData='" + retData + '\'' +
                '}';
    }

    public boolean isRetCodeSuccess() {
        return BusinessRequest.SUCCESS_CODE.equals(retCode);
    }


    public boolean isTimeoutCode() {
        if ("011030".equals(retCode)) {
            return true;
        }

        if ("0100PX".equals(retCode)) {
            return true;
        }
        return false;
    }

    public boolean isDealDetailLimit() {
        if (TextUtils.equals(retCode, "000500")) {
            return true;
        }
        return false;
    }

}
