/**
 *@Generated by QuickVO Tools 1.6
 */
package com.lakala.elive.beans;

import java.io.Serializable;
import java.util.Date;


public class OperateLogInfo implements Serializable {

    private static final long serialVersionUID = 7504203250416830621L;

    /**
     * 应用系统编码
     */
    protected String sysCode;

    /**
     * 一般为:登陆、采购、出库等业务分类
     */
    protected String operateType;

    /**
     * 业务对象,一般记录相关的业务单据号
     */
    protected String refObject;

    /**
     * 业务说明
     */
    protected String comments;

    /**
     * 业务日期
     */
    protected Date bizDate;

    /**
     * 操作人
     */
    protected String operator;

    /**
     * 操作时间
     */
    protected Date operateTime;

    /**
     * 终端IP
     */
    protected String terminalIp;

    /**
     * 操作结果
     */
    protected Integer resultFlag;

    /**
     * 一般应用分：
     终端访问发起和后台定时任务发起，
     终端发起要求记录IP
     */
    protected Integer isClient;







}
