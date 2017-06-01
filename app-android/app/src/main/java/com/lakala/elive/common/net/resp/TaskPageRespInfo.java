package com.lakala.elive.common.net.resp;

import com.lakala.elive.beans.PageModel;
import com.lakala.elive.beans.TaskInfo;
import com.lakala.elive.common.net.resp.base.ResponseInfo;


public class TaskPageRespInfo extends ResponseInfo {

    protected PageModel<TaskInfo> content;

    public PageModel<TaskInfo> getContent() {
        return content;
    }

    public void setContent(PageModel<TaskInfo> content) {
        this.content = content;
    }
}
