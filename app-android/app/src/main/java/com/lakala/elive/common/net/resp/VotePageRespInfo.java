package com.lakala.elive.common.net.resp;

import com.lakala.elive.beans.PageModel;
import com.lakala.elive.beans.VoteTaskInfo;
import com.lakala.elive.common.net.resp.base.ResponseInfo;

/**
 * @author hongzhiliang
 * @version $Rev$
 * @time 2016/9/26 12:08
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class VotePageRespInfo extends ResponseInfo {

    protected PageModel<VoteTaskInfo> content;

    public PageModel<VoteTaskInfo> getContent() {
        return content;
    }

    public void setContent(PageModel<VoteTaskInfo> content) {
        this.content = content;
    }
}
