package com.lakala.elive.common.net.resp;

import com.lakala.elive.beans.NoticeInfo;
import com.lakala.elive.beans.PageModel;
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
public class NoticePageRespInfo extends ResponseInfo {

    protected PageModel<NoticeInfo> content;

    public PageModel<NoticeInfo> getContent() {
        return content;
    }

    public void setContent(PageModel<NoticeInfo> content) {
        this.content = content;
    }
}
