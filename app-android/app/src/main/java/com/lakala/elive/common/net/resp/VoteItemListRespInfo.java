package com.lakala.elive.common.net.resp;

import com.lakala.elive.beans.VoteItemInfo;
import com.lakala.elive.common.net.resp.base.ResponseInfo;

import java.util.List;

/**
 * @author hongzhiliang
 * @version $Rev$
 * @time 2016/9/26 12:08
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class VoteItemListRespInfo extends ResponseInfo {

    protected List<VoteItemInfo> content;

    public List<VoteItemInfo> getContent() {
        return content;
    }

    public void setContent(List<VoteItemInfo> content) {
        this.content = content;
    }
}
