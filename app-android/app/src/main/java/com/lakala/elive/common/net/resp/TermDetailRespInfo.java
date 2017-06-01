package com.lakala.elive.common.net.resp;

import com.lakala.elive.beans.TermInfo;
import com.lakala.elive.common.net.resp.base.ResponseInfo;

/**
 *  终端详情
 */
public class TermDetailRespInfo extends ResponseInfo {

    protected TermInfo content;

    public TermInfo getContent() {
        return content;
    }

    public void setContent(TermInfo content) {
        this.content = content;
    }

}
