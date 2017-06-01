package com.lakala.core.http;

import java.io.File;

/**
 * Created by ZhangMY on 2015/2/4.
 * 请求类型
 */
public enum ERequestDataType {
    JSON,
    FILE,
    BINARY,
    TEXT;

    private File file;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
