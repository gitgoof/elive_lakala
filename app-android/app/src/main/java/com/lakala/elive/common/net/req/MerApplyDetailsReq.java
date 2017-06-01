package com.lakala.elive.common.net.req;

/**
 * 商户进件申请详情
 * Created by wenhaogu on 2017/1/22.
 */

public class MerApplyDetailsReq {
    private String authToken;
    private String applyId;

    public MerApplyDetailsReq(String authToken, String applyId) {
        this.authToken = authToken;
        this.applyId = applyId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }
}
