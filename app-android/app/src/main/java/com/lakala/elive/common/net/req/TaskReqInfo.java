package com.lakala.elive.common.net.req;

import com.lakala.elive.common.net.req.base.RequestInfo;

/**
 * @author hongzhiliang
 * @version $Rev$
 * @time 2016/9/26 10:44
 * @des
 *
 * 工单相关信息
 *
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class TaskReqInfo extends RequestInfo{


   private String taskId; //工单id

   private String  status; //工单状态

   private String  executeComments; //执行备注

    private String  executeResult; //执行结果


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExecuteComments() {
        return executeComments;
    }

    public void setExecuteComments(String executeComments) {
        this.executeComments = executeComments;
    }

    public String getExecuteResult() {
        return executeResult;
    }

    public void setExecuteResult(String executeResult) {
        this.executeResult = executeResult;
    }
}
