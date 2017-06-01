package com.lakala.elive.beans;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * 进件--商户开通上传图片列表
 * Created by wenhaogu on 2017/1/9.
 */

public class MerAttachFile implements Serializable {
    private String applyId;
    private String merchantId;
    private String fileName;
    private String fileType;
    private String fileContent;
    private String isOcr;
    private String ocrType;
    private String segment;

    @Expose(serialize = false, deserialize = false)//忽略
    public int photoUploadType;//区分图片上传请求

    private String comments;

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getIsOcr() {
        return isOcr;
    }

    public void setIsOcr(String isOcr) {
        this.isOcr = isOcr;
    }

    public String getOcrType() {
        return ocrType;
    }

    public void setOcrType(String ocrType) {
        this.ocrType = ocrType;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }
}
