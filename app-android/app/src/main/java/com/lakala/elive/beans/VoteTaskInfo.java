package com.lakala.elive.beans;

import java.io.Serializable;

/**
 * 
 * 投票信息
 * 
 * @author hongzhiliang
 *
 */
public class VoteTaskInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;


    /**
     * 任务编号
     */
    protected String voteId;




    /**
     * 主题
     */
    protected String name;

    /**
     * 内容
     */
    protected String content;

    /**
     * 创建人
     */
    protected String createByName;

    /**
     * 创建时间
     */
    protected String createTimeStr;

    /**
     * 发起人
     */
    protected String createBy;

    /**
     * 组织机构
     */
    protected String sponsorOrg;

    /**
     * 开始日期
     */
    protected String beginTimeStr;

    /**
     * 截止日期
     */
    protected String endTimeStr;

    /**
     * 是否多选
     */
    protected Integer isMulti;

    /**
     * 0:作废,1:编辑,2:发布
     */
    protected Integer status;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getVoteId() {
        return voteId;
    }

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getSponsorOrg() {
        return sponsorOrg;
    }

    public void setSponsorOrg(String sponsorOrg) {
        this.sponsorOrg = sponsorOrg;
    }


    public Integer getIsMulti() {
        return isMulti;
    }

    public void setIsMulti(Integer isMulti) {
        this.isMulti = isMulti;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getBeginTimeStr() {
        return beginTimeStr;
    }

    public void setBeginTimeStr(String beginTimeStr) {
        this.beginTimeStr = beginTimeStr;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

    public void setEndTimeStr(String endTimeStr) {
        this.endTimeStr = endTimeStr;
    }


    @Override
    public String toString() {
        return "VoteTaskInfo{" +
                "voteId='" + voteId + '\'' +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", createByName='" + createByName + '\'' +
                ", createTimeStr='" + createTimeStr + '\'' +
                ", createBy='" + createBy + '\'' +
                ", sponsorOrg='" + sponsorOrg + '\'' +
                ", beginTimeStr='" + beginTimeStr + '\'' +
                ", endTimeStr='" + endTimeStr + '\'' +
                ", isMulti=" + isMulti +
                ", status=" + status +
                '}';
    }
}
