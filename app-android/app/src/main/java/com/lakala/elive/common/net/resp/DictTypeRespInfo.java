package com.lakala.elive.common.net.resp;

import com.lakala.elive.beans.DictDetailInfo;
import com.lakala.elive.common.net.resp.base.ResponseInfo;

import java.util.List;
import java.util.Map;

/**
 * @author hongzhiliang
 * @version $Rev$
 * @time 2016/9/26 12:08
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class DictTypeRespInfo extends ResponseInfo {

    List<DictDetailInfo> content;

    public List<DictDetailInfo> getContent() {
        return content;
    }

    public void setContent(List<DictDetailInfo> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "DictDetailRespInfo{" +
                "content=" + content +
                '}';
    }


}
