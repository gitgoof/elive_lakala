package com.lakala.elive.common.net.req;

/**
 * Created by wenhaogu on 2017/1/12.
 */

public class BMCPMccReq {

    private String version;
    private String parentMcc;
    private String level;

    public BMCPMccReq(String version, String parentMcc, String level) {
        this.version = version;
        this.parentMcc = parentMcc;
        this.level = level;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getParentMcc() {
        return parentMcc;
    }

    public void setParentMcc(String parentMcc) {
        this.parentMcc = parentMcc;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
