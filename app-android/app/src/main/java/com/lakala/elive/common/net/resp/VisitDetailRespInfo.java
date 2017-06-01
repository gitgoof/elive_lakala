package com.lakala.elive.common.net.resp;

import com.lakala.elive.beans.ShopVisitInfo;
import com.lakala.elive.common.net.resp.base.ResponseInfo;


/**
 * 拜访详情接口
 */
public class VisitDetailRespInfo extends ResponseInfo {

    protected ShopVisitInfo content;


    public ShopVisitInfo getContent() {
        return content;
    }

    public void setContent(ShopVisitInfo content) {
        this.content = content;
    }
}
