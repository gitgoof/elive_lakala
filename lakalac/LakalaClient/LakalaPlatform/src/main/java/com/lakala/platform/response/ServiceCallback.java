package com.lakala.platform.response;

/**
 * Created by huangjp on 2016/6/21.
 */
abstract public class ServiceCallback implements ServiceResultCallback{
    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
