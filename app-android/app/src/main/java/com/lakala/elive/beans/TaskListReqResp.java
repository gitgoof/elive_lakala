package com.lakala.elive.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhouzx on 2017/3/10.
 */
public class TaskListReqResp implements Serializable{


    /**
     * content : [{"orderType":1,"merchantCode":"822100054990418","outTaskId":"","shopAddr":"北京市海淀区圆明园西路18号中发百旺商城1808","shopName":"北京市海淀区马连洼街道张录俊干果商店","userId":"200518","shopNo":"105289","taskId":"2494546967023901105152","createDate":1489075200000},{"orderType":1,"merchantCode":"822100054990407","outTaskId":"","shopAddr":"北京市石景山区古城大街西侧古城饭庄","shopName":"北京京西朱恩英食品店","userId":"200518","shopNo":"105278","taskId":"2498571022613701833285","createDate":1489075200000},{"orderType":1,"merchantCode":"822100054990418","outTaskId":"","shopAddr":"北京市海淀区圆明园西路18号中发百旺商城1808","shopName":"北京市海淀区马连洼街道张录俊干果商店","userId":"200518","shopNo":"105289","taskId":"2498571024628301751192","createDate":1489075200000}]
     * message : 执行成功!
     * resultCode : SUCCESS
     * resultDataType : -1
     */
    /**
     * 请求ID唯一号
     */
    private String commandId;
    /**
     * 返回消息
     */
    private String message;
    /**
     * 00：成功  99：未知错误
     */
    private String resultCode;
    /**
     * 返回内容
     */
    private int resultDataType;
    private List<ContentBean> content;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public int getResultDataType() {
        return resultDataType;
    }

    public void setResultDataType(int resultDataType) {
        this.resultDataType = resultDataType;
    }

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public static class ContentBean implements Serializable{
        /**
         * orderType : 1
         * merchantCode : 822100054990418
         * outTaskId :
         * shopAddr : 北京市海淀区圆明园西路18号中发百旺商城1808
         * shopName : 北京市海淀区马连洼街道张录俊干果商店
         * userId : 200518
         * shopNo : 105289
         * taskId : 2494546967023901105152
         * createDate : 1489075200000
         */

//        {"beginTime":1366300800000,
// "beginTimeStr":"2013-04-19",
// "createDate":1495468800000,
// "custName":"测试","isEnd":0,
// "isStart":0,"merchantCode":"822100054990418",
// "orderType":1,"outTaskId":"","shopAddr":"北京市海淀区圆明园西路18号中发百旺商城1808",
// "shopCnt":1,"shopName":"北京市海淀区马连洼街道张录俊干果商店",
// "shopNo":"105289","status":0,"statusName":"0",
// "taskId":"3217906483549040175220","taskType":10,
// "taskTypeName":"日常维护","termCnt":1,"userId":"200518"},

        private long beginTime;
        private String beginTimeStr;
        private String custName;
        private int delayDays;
        private int isEnd;
        private int isStart;
        private int shopCnt;
        private int status;
        private String statusName;
        private String taskBizId;
        private int taskType;
        private String taskTypeName;
        private int termCnt;

        public int planOrder = -1;
        /**
         * 任务类型   1：日常维护  2：工单
         */
        private int orderType;
        /**
         * 商户编号
         */
        private String merchantCode;
        /**
         *处理工单编号
         */
        private String outTaskId;
        /**
         *商户地址
         */
        private String shopAddr;
        /**
         *网点名称
         */
        private String shopName;
        /**
         *用户编号
         */
        private String userId;
        /**
         *网点编号
         */
        private String shopNo;
        /**
         *任务编号
         */
        private String taskId;
        /**
         *创建时间
         */
        private long createDate;

        private String finishTimeStr;
        private String finishLimitDays;

        public String getTelNo() {
            return telNo;
        }

        public void setTelNo(String telNo) {
            this.telNo = telNo;
        }
        /**
         *联系号码
         */
        private String telNo;

        public int getOrderType() {
            return orderType;
        }

        public void setOrderType(int orderType) {
            this.orderType = orderType;
        }

        public String getMerchantCode() {
            return merchantCode;
        }

        public void setMerchantCode(String merchantCode) {
            this.merchantCode = merchantCode;
        }

        public String getOutTaskId() {
            return outTaskId;
        }

        public void setOutTaskId(String outTaskId) {
            this.outTaskId = outTaskId;
        }

        public String getShopAddr() {
            return shopAddr;
        }

        public void setShopAddr(String shopAddr) {
            this.shopAddr = shopAddr;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getShopNo() {
            return shopNo;
        }

        public void setShopNo(String shopNo) {
            this.shopNo = shopNo;
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        public long getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(long beginTime) {
            this.beginTime = beginTime;
        }

        public String getBeginTimeStr() {
            return beginTimeStr;
        }

        public void setBeginTimeStr(String beginTimeStr) {
            this.beginTimeStr = beginTimeStr;
        }

        public String getCustName() {
            return custName;
        }

        public void setCustName(String custName) {
            this.custName = custName;
        }

        public int getIsEnd() {
            return isEnd;
        }

        public void setIsEnd(int isEnd) {
            this.isEnd = isEnd;
        }

        public int getIsStart() {
            return isStart;
        }

        public void setIsStart(int isStart) {
            this.isStart = isStart;
        }

        public int getShopCnt() {
            return shopCnt;
        }

        public void setShopCnt(int shopCnt) {
            this.shopCnt = shopCnt;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getStatusName() {
            return statusName;
        }

        public void setStatusName(String statusName) {
            this.statusName = statusName;
        }

        public int getTaskType() {
            return taskType;
        }

        public void setTaskType(int taskType) {
            this.taskType = taskType;
        }

        public String getTaskTypeName() {
            return taskTypeName;
        }

        public void setTaskTypeName(String taskTypeName) {
            this.taskTypeName = taskTypeName;
        }

        public int getTermCnt() {
            return termCnt;
        }

        public void setTermCnt(int termCnt) {
            this.termCnt = termCnt;
        }

        public String getFinishTimeStr() {
            return finishTimeStr;
        }

        public void setFinishTimeStr(String finishTimeStr) {
            this.finishTimeStr = finishTimeStr;
        }

        public String getTaskBizId() {
            return taskBizId;
        }

        public void setTaskBizId(String taskBizId) {
            this.taskBizId = taskBizId;
        }

        public int getDelayDays() {
            return delayDays;
        }

        public void setDelayDays(int delayDays) {
            this.delayDays = delayDays;
        }

        public String getFinishLimitDays() {
            return finishLimitDays;
        }

        public void setFinishLimitDays(String finishLimitDays) {
            this.finishLimitDays = finishLimitDays;
        }
    }
}
