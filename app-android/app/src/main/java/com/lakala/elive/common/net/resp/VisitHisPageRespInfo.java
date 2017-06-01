package com.lakala.elive.common.net.resp;

import com.lakala.elive.beans.ShopVisitInfo;
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
public class VisitHisPageRespInfo extends ResponseInfo {

    protected PageModel<ShopVisitInfo> content;

    public PageModel<ShopVisitInfo> getContent() {
        return content;
    }

    public void setContent(PageModel<ShopVisitInfo> content) {
        this.content = content;
    }
}
