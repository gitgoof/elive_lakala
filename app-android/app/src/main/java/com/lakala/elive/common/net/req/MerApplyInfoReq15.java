package com.lakala.elive.common.net.req;

import com.lakala.elive.beans.MerOpenInfo;

/**
 * 进件驳回后请求
 * Created by wenhaogu on 2017/3/8.
 */

public class MerApplyInfoReq15 {
    public String authToken;
    private MerOpenInfo merOpenInfo;

    public MerOpenInfo getMerOpenInfo() {
        return merOpenInfo;
    }

    public void setMerOpenInfo(MerOpenInfo merOpenInfo) {
        this.merOpenInfo = merOpenInfo;
    }
}
