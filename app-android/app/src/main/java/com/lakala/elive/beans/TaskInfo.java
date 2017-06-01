package com.lakala.elive.beans;

import com.lakala.elive.common.net.req.base.RequestInfo;

import java.io.Serializable;

public class TaskInfo  implements Serializable {
    private long acceptTime;
    private String acceptTimeStr;
    private long beginTime;
    private String createBy;
    private String createByName;

    private long createTime;

/*
            acceptTime": 1490793897000,
            "acceptTimeStr": "2017-03-29",
            "beginTime": 1490011587000,
            "beginTimeStr": "2017-03-20",
            "bizSheetId": "5801209387458320078348",
            "comments": "测试工单2",
            "createBy": "1414097",
            "createByName": "李晓兰",
            "createTime": 1490011587000,
            "createTimeStr": "2017-03-20 20:06:27",
            "custAddr": "北京市丰台区科技园区产业基地东区15-D2号2号楼822号",
            "custName": "李四",
            "delayDays": 8,
            "executeComments": "黑呼呼p",
            "executeResult": 0,
            "finishLimitDays": "8",
            "finishLimitTime": 1490702787000,
            "finishLimitTimeStr": "2017-03-28",
            "finishTime": 1490762081000,
            "finishTimeFullStr": "2017-03-29 12:34:41",
            "finishTimeStr": "2017-03-29",
            "merchantCode": "822100059990199",
            "operator": "200518",
            "operatorName": "洪志亮",
            "organId": "2",
            "organName": "北京分公司(收单)",
            "shopName": "乐亿联（北京）网络科技有限公司",
            "shopNo": "108490",
            "status": 3,
            "taskBizId": "2017032020062666021",
            "taskChannel": "5",
            "taskId": "5801209387458320078348",
            "taskLevel": 1,
            "taskSubject": "测试工单2",
            "taskType": "3",
            "termNo": "10006135",
            "todayTaskFlag": "0"
    */

    private long finishLimitTime;
    private String finishLimitTimeStr;
    private long finishTime;
    private String finishTimeFullStr;
    private String operator;
    private String organId;
    private String organName;
    /**
     * 网点创建时间
     */
    private String termNo;
    /**
     * 当天拜访人物标识。1：已添加  0：未添加
     */
    private String todayTaskFlag;


    protected String taskId;
    protected String taskBizId; //工单号
    protected String bizSheetId; //外包工单号
    protected String  merchantCode;

    protected String shopNo;
    protected String shopName;
    protected String custName; //联系人
    protected String custAddr; //地址
    protected String telNo; //联系电话
    protected String mobileNo; //联系手机
    protected String status; //工单状态
    private String statusName;
    protected String taskType; //工单状态
    protected String taskTypeName;
    protected String finishLimitDays;
    protected String taskSubject; //工单标题
    protected String taskLevel;//工单登记
    protected String taskLevelName;
    protected String beginTimeStr; //开始时间
    protected String finishTimeStr; //完成时间

    protected String taskChannel;
    protected String taskChannelName;
    protected String comments;
    protected String createTimeStr;
    protected String operatorName;//处理人

    protected String executeResult; //执行结果
    protected String executeComments;//执行备注
    private boolean checked;

    private Integer delayDays;  //延时天数
    private Integer isDelayDays;  //延时工作天数列表Vo使用

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getBizSheetId() {
        return bizSheetId;
    }

    public void setBizSheetId(String bizSheetId) {
        this.bizSheetId = bizSheetId;
    }

    public Integer getDelayDays() {
        return delayDays;
    }

    public void setDelayDays(Integer delayDays) {
        this.delayDays = delayDays;
    }

    public Integer getIsDelayDays() {
        return isDelayDays;
    }

    public void setIsDelayDays(Integer isDelayDays) {
        this.isDelayDays = isDelayDays;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getExecuteResult() {
        return executeResult;
    }

    public void setExecuteResult(String executeResult) {
        this.executeResult = executeResult;
    }

    public String getExecuteComments() {
        return executeComments;
    }

    public void setExecuteComments(String executeComments) {
        this.executeComments = executeComments;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getTaskChannel() {
        return taskChannel;
    }

    public void setTaskChannel(String taskChannel) {
        this.taskChannel = taskChannel;
    }


    public String getFinishTimeStr() {
        return finishTimeStr;
    }

    public void setFinishTimeStr(String finishTimeStr) {
        this.finishTimeStr = finishTimeStr;
    }

    public String getTaskLevel() {
        return taskLevel;
    }

    public void setTaskLevel(String taskLevel) {
        this.taskLevel = taskLevel;
    }

    public String getFinishLimitDays() {
        return finishLimitDays;
    }

    public void setFinishLimitDays(String finishLimitDays) {
        this.finishLimitDays = finishLimitDays;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustAddr() {
        return custAddr;
    }

    public void setCustAddr(String custAddr) {
        this.custAddr = custAddr;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getBeginTimeStr() {
        return beginTimeStr;
    }

    public void setBeginTimeStr(String beginTimeStr) {
        this.beginTimeStr = beginTimeStr;
    }

    public String getShopNo() {
        return shopNo;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskBizId() {
        return taskBizId;
    }

    public void setTaskBizId(String taskBizId) {
        this.taskBizId = taskBizId;
    }

    public String getTaskSubject() {
        return taskSubject;
    }

    public void setTaskSubject(String taskSubject) {
        this.taskSubject = taskSubject;
    }


    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    public long getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(long acceptTime) {
        this.acceptTime = acceptTime;
    }

    public String getAcceptTimeStr() {
        return acceptTimeStr;
    }

    public void setAcceptTimeStr(String acceptTimeStr) {
        this.acceptTimeStr = acceptTimeStr;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getCreateByName() {
        return createByName;
    }

    public void setCreateByName(String createByName) {
        this.createByName = createByName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getFinishLimitTime() {
        return finishLimitTime;
    }

    public void setFinishLimitTime(long finishLimitTime) {
        this.finishLimitTime = finishLimitTime;
    }

    public String getFinishLimitTimeStr() {
        return finishLimitTimeStr;
    }

    public void setFinishLimitTimeStr(String finishLimitTimeStr) {
        this.finishLimitTimeStr = finishLimitTimeStr;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public String getFinishTimeFullStr() {
        return finishTimeFullStr;
    }

    public void setFinishTimeFullStr(String finishTimeFullStr) {
        this.finishTimeFullStr = finishTimeFullStr;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public String getTermNo() {
        return termNo;
    }

    public void setTermNo(String termNo) {
        this.termNo = termNo;
    }

    public String getTodayTaskFlag() {
        return todayTaskFlag;
    }

    public void setTodayTaskFlag(String todayTaskFlag) {
        this.todayTaskFlag = todayTaskFlag;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getTaskTypeName() {
        return taskTypeName;
    }

    public void setTaskTypeName(String taskTypeName) {
        this.taskTypeName = taskTypeName;
    }

    public String getTaskLevelName() {
        return taskLevelName;
    }

    public void setTaskLevelName(String taskLevelName) {
        this.taskLevelName = taskLevelName;
    }

    public String getTaskChannelName() {
        return taskChannelName;
    }

    public void setTaskChannelName(String taskChannelName) {
        this.taskChannelName = taskChannelName;
    }
}
