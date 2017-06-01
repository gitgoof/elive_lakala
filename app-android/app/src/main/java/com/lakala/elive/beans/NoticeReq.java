package com.lakala.elive.beans;

/**
 * Created by zhouzx on 2017/2/21.
 */
public class NoticeReq {
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    private String userId;
    private String organId;
}
