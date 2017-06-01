package com.lakala.elive.common.net.resp;

import com.lakala.elive.beans.MerShopInfo;
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
public class MerShopPageRespInfo extends ResponseInfo {

    protected PageModel<MerShopInfo> content;

    public PageModel<MerShopInfo> getContent() {
        return content;
    }

    public void setContent(PageModel<MerShopInfo> content) {
        this.content = content;
    }
}
