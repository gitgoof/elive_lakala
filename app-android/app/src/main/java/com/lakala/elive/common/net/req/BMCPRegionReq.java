package com.lakala.elive.common.net.req;

/**
 * bmcp 查询行政区
 * Created by wenhaogu on 2017/1/12.
 */

public class BMCPRegionReq {
    private String version;
    private String id;
    private String level;

    public BMCPRegionReq(String version, String id, String level) {
        this.version = version;
        this.id = id;
        this.level = level;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
