package com.lakala.core.cordova.pluginfilter;

/**
 * Cordova插件过滤文件数据单元格式
 * <p/>
 * Created by andy_lv on 2014/7/16.
 */
public class FilterEntry {

    /**
     * 使用插件网页host地址，格式如“['www.baidu.com','*.163.com']
     */
    private String hosts;

    /**
     * 允许使用的插件名称，格式如下
     * {
     * Httprequest:'*'
     * httprequest:[send,cancel]
     * }
     */
    private String allow;

    /**
     * 不允许使用的插件名称，格式如下
     * {
     * Httprequest:'*'
     * httprequest:[send,cancel]
     * }
     */
    private String deind;

    public String getDeind() {
        return deind;
    }

    public void setDeind(String deind) {
        this.deind = deind;
    }

    public String getAllow() {
        return allow;
    }

    public void setAllow(String allow) {
        this.allow = allow;
    }

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }
}
