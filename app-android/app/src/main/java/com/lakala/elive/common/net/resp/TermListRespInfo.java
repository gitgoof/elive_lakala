package com.lakala.elive.common.net.resp;

import com.lakala.elive.beans.TermInfo;
import com.lakala.elive.common.net.resp.base.ResponseInfo;

import java.util.List;

/**
 *
 * 网点终端列表
 *
 */
public class TermListRespInfo extends ResponseInfo {

    protected List<TermInfo> content;

    public List<TermInfo> getContent() {
        return content;
    }

    public void setContent(List<TermInfo> content) {
        this.content = content;
    }
}
