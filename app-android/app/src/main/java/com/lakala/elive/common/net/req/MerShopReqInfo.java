package com.lakala.elive.common.net.req;

import com.lakala.elive.common.net.req.base.RequestInfo;

/**
 * @author hongzhiliang
 * @version $Rev$
 * @time 2016/9/26 10:44
 * @des
 *
 * 商户详情相关的请求信息
 *
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class MerShopReqInfo extends RequestInfo{
    //商户号
    private String merchantCode;

    //终端号
    private String termNo;

    //网点编号
    private String shopNo;

    //终端号
    private String terminalCode;

    //拜访编号
    private String visitNo;

    //任务编号
    private String taskId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getShopNo() {
        return shopNo;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    public String getTermNo() {
        return termNo;
    }

    public void setTermNo(String termNo) {
        this.termNo = termNo;
    }

    public String getVisitNo() {
        return visitNo;
    }

    public void setVisitNo(String visitNo) {
        this.visitNo = visitNo;
    }

    @Override
    public String toString() {
        return "MerShopReqInfo{" +
                "merchantCode='" + merchantCode + '\'' +
                ", termNo='" + termNo + '\'' +
                ", shopNo='" + shopNo + '\'' +
                ", terminalCode='" + terminalCode + '\'' +
                ", visitNo='" + visitNo + '\'' +
                '}';
    }
}
