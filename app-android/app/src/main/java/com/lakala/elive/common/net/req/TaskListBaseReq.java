package com.lakala.elive.common.net.req;

import java.io.Serializable;

/**
 * Created by gaofeng on 2017/5/23.
 * 任务请求
 */
public class TaskListBaseReq implements Serializable {
    private String authToken;
    private String userId;
    private String taskDateStr;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTaskDateStr() {
        return taskDateStr;
    }

    public void setTaskDateStr(String taskDateStr) {
        this.taskDateStr = taskDateStr;
    }
}
