package com.lakala.elive.common.net.resp;

import com.lakala.elive.beans.FunctionMenuInfo;
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
public class FunMenuRespInfo extends ResponseInfo {

    protected List<FunctionMenuInfo> content;

    public List<FunctionMenuInfo> getContent() {
        return content;
    }

    public void setContent(List<FunctionMenuInfo> content) {
        this.content = content;
    }


}
