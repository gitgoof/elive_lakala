package com.lakala.elive.common.net.req.base;

import java.io.Serializable;

public class RequestInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	protected Long timestamp;
    protected String authToken;// 动态授权令牌(包含用户加密信息)
    private String devCode;  //设备终端唯一标识
    protected String platformType; //系统类型

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getDevCode() {
        return devCode;
    }

    public void setDevCode(String devCode) {
        this.devCode = devCode;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public String toString() {
        return "RequestInfo{" +
                "timestamp=" + timestamp +
                ", authToken='" + authToken + '\'' +
                ", devCode='" + devCode + '\'' +
                ", platformType='" + platformType + '\'' +
                '}';
    }
}
