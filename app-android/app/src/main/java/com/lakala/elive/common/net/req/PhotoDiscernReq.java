package com.lakala.elive.common.net.req;

import com.lakala.elive.beans.MerApplyInfo;
import com.lakala.elive.beans.MerAttachFile;

import java.util.List;

/**
 * 图片识别请求bean
 * Created by wenhaogu on 2017/1/20.
 */

public class PhotoDiscernReq {
    private String authToken;


    private MerApplyInfo merApplyInfo;
    private List<MerAttachFile> merAttachFileList;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public MerApplyInfo getMerApplyInfo() {
        return merApplyInfo;
    }

    public void setMerApplyInfo(MerApplyInfo merApplyInfo) {
        this.merApplyInfo = merApplyInfo;
    }

    public List<MerAttachFile> getMerAttachFileList() {
        return merAttachFileList;
    }

    public void setMerAttachFileList(List<MerAttachFile> merAttachFileList) {
        this.merAttachFileList = merAttachFileList;
    }
}
