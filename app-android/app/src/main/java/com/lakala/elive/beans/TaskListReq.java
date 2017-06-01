package com.lakala.elive.beans;

/**
 * Created by zhouzx on 2017/3/10.
 */
public class TaskListReq {
    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getTaskDateStr() {
        return taskDateStr;
    }

    public void setTaskDateStr(String taskDateStr) {
        this.taskDateStr = taskDateStr;
    }

    private String authToken;
    private String taskDateStr;
    /**
     * 任务状态过滤条件
     1: 过滤已完成的任务
     */
    private String filterStatus;
    /**
     * 1: 限制未完成的调试
     路径规划限制20条
     */
    private String limitTaskCnt;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;

    public String getFilterStatus() {
        return filterStatus;
    }

    public void setFilterStatus(String filterStatus) {
        this.filterStatus = filterStatus;
    }

    public String getLimitTaskCnt() {
        return limitTaskCnt;
    }

    public void setLimitTaskCnt(String limitTaskCnt) {
        this.limitTaskCnt = limitTaskCnt;
    }
}
