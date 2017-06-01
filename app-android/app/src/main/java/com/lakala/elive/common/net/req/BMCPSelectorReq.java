package com.lakala.elive.common.net.req;

/**
 * bmcp字典查询
 * Created by wenhaogu on 2017/1/10.
 */

public class BMCPSelectorReq {
    private String version;
    private String type;
    private String code;

    public BMCPSelectorReq() {
    }

    public BMCPSelectorReq(String version, String type) {
        this.version = version;
        this.type = type;
    }

    public BMCPSelectorReq(String version, String type, String code) {
        this.version = version;
        this.type = type;
        this.code = code;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
