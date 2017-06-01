package com.lakala.elive.preenterpiece.request;

import java.io.Serializable;

/**
 * Created by ousachisan on 2017/3/23.
 * //合作方预进件的详情查询接口的Request(ELIVE_PARTNER_APPLY_002)
 */
public class PreEnPieceDetailRequ implements Serializable {

    public String authToken;//授权令牌
    public String applyId;//  申请ID;

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
