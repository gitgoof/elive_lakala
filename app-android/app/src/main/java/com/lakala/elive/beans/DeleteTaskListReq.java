package com.lakala.elive.beans;

/**
 * Created by zhouzx on 2017/3/13.
 */
public class DeleteTaskListReq {
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

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    private String taskId;
}
