package com.lakala.elive.preenterpiece.request;

import java.io.Serializable;

/**
 * Created by ousachisan on 2017/3/23.
 * //合作方预进件的列表也查询接口的Request(ELIVE_PARTNER_APPLY_001)
 */
public class PreEnPieceListRequ implements Serializable {

    public String searchCode; //综合查询
    public String status;//申请状态
    public int pageNo;//当前页
    public int pageSize;//一页显示多少条
    public String authToken;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getSearchCode() {
        return searchCode;
    }

    public void setSearchCode(String searchCode) {
        this.searchCode = searchCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
