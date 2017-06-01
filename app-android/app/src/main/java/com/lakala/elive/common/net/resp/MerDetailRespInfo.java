package com.lakala.elive.common.net.resp;

import com.lakala.elive.beans.MerShopInfo;
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
public class MerDetailRespInfo extends ResponseInfo {

    protected MerShopInfo content;

    public MerShopInfo getContent() {
        return content;
    }

    public void setContent(MerShopInfo content) {
        this.content = content;
    }

}
