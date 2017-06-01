package com.lakala.elive.common.net.resp;

import com.lakala.elive.common.net.resp.base.ResponseInfo;
import com.lakala.elive.beans.UserLoginInfo;

/**
 * @author hongzhiliang
 * @version $Rev$
 * @time 2016/9/26 12:08
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class UserLoginRespInfo extends ResponseInfo {

    protected UserLoginInfo content;


    public UserLoginInfo getContent() {
        return content;
    }

    public void setContent(UserLoginInfo content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "UserLoginRespInfo{" +
                "content=" + content +
                '}';
    }
}
