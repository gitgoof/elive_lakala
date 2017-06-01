package com.lakala.platform.bean;

/**
 * Created by wangchao on 14-4-16.
 * cordovaPlugin postMessage 传递数据实体
 */
public class PluginObj {

    private String id;
    private String content;
    private boolean isCancel;

    public boolean isCancel() {
        return isCancel;
    }

    public void setCancel(boolean isCancel) {
        this.isCancel = isCancel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
