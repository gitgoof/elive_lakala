package com.lakala.elive.common.net.req;

import com.lakala.elive.beans.MerAttachFile;

import java.util.List;

/**
 * 进件接口  附件上传
 * Created by wenhaogu on 2017/1/13.
 */

public class MerApplyInfoReq8 {
    private String authToken;

    private List<MerAttachFile> attachments;

    private long id;

    private String applyId;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public List<MerAttachFile> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<MerAttachFile> attachments) {
        this.attachments = attachments;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }
}
