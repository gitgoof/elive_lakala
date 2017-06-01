package com.lakala.elive.common.net.resp;

import com.lakala.elive.common.net.resp.base.ResponseInfo;

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
public class DictDetailRespInfo extends ResponseInfo {

    Map<String, Map<String, String>> content;

    public Map<String, Map<String, String>> getContent() {
        return content;
    }

    public void setContent(Map<String, Map<String, String>> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "DictDetailRespInfo{" +
                "content=" + content +
                '}';
    }


}
