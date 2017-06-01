package com.lakala.elive.common.net.req;

import com.lakala.elive.beans.MerAttachFile;

import java.util.List;

/**
 * 进件接口 营业执照信息
 * Created by wenhaogu on 2017/1/12.
 */

public class MerApplyInfoReq2 {
    private String authToken;
    private String licenceNo;
    private long id;
    private String applyId;

    public MerApplyInfoReq2() {
    }

    public MerApplyInfoReq2(String authToken, String licenceNo, long id, String applyId) {
        this.authToken = authToken;
        this.licenceNo = licenceNo;
        this.id = id;
        this.applyId = applyId;
    }

    private List<MerAttachFile> attachments;

    public List<MerAttachFile> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<MerAttachFile> attachments) {
        this.attachments = attachments;
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLicenceNo() {
        return licenceNo;
    }

    public void setLicenceNo(String licenceNo) {
        this.licenceNo = licenceNo;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
