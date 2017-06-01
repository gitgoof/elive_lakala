package com.lakala.elive.common.net;

import java.util.List;

/**
 * Created by zhouzx on 2017/3/9.
 */
public class DotAndorderReq {

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public List<UserOrderTask> getUserOrderTaskList() {
        return UserOrderTaskList;
    }

    public void setUserOrderTaskList(List<UserOrderTask> userOrderTaskList) {
        UserOrderTaskList = userOrderTaskList;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    private String authToken;
    private String userId;
    private String taskDateStr;
    private String orderType;

    private List<UserOrderTask> UserOrderTaskList;

    public class UserOrderTask {


        public String getShopNo() {
            return shopNo;
        }

        public void setShopNo(String shopNo) {
            this.shopNo = shopNo;
        }

        public String getOutTaskId() {
            return outTaskId;
        }

        public void setOutTaskId(String outTaskId) {
            this.outTaskId = outTaskId;
        }

        private String shopNo;
        private String outTaskId;
    }
}
