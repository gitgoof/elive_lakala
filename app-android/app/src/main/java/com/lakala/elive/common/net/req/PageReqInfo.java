package com.lakala.elive.common.net.req;

import com.lakala.elive.common.net.req.base.RequestInfo;

public class PageReqInfo extends RequestInfo {

	private static final long serialVersionUID = 1L;

    private String searchCode; //综合查询条件

    private int pageNo;      //请求页数

    private int pageSize;   //分页大小

    private String shopNo;      //网点号
    private String taskId;      //工单任务编号

    private String taskType;      //工单任务类型
    private String taskStatus;      //工单任务状态

    private String visitTimeType; //最后签到拜访时间
    private String createTimeType; //网点创建时间

    private String taskLeaveType; //任务剩余时间
    private String taskFinishType; //任务完成时间

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskLeaveType() {
        return taskLeaveType;
    }

    public void setTaskLeaveType(String taskLeaveType) {
        this.taskLeaveType = taskLeaveType;
    }

    public String getTaskFinishType() {
        return taskFinishType;
    }

    public void setTaskFinishType(String taskFinishType) {
        this.taskFinishType = taskFinishType;
    }

    public String getVisitTimeType() {
        return visitTimeType;
    }

    public void setVisitTimeType(String visitTimeType) {
        this.visitTimeType = visitTimeType;
    }

    public String getCreateTimeType() {
        return createTimeType;
    }

    public void setCreateTimeType(String createTimeType) {
        this.createTimeType = createTimeType;
    }

    public String getShopNo() {
        return shopNo;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    public String getSearchCode() {
        return searchCode;
    }

    public void setSearchCode(String searchCode) {
        this.searchCode = searchCode;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
