package com.lakala.elive.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 通知信息
 * 
 * @author hongzhiliang
 *
 */
public class NoticeInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String noticeNo;//通知编码
	
	private String noticeType;//通知类型
	
	private String noticeTitle;//通知标题

    private String noticeSubject;
    private String content;
    private String readConfirm; //是否要确认


	private String noticeContent;//通知内容
	
	private Date publishTime; //发布时间

	private String publishTimeStr; //发布时间
	
	private String isRead; //是否已读
	
	private String userId; //阅读USER_ID


    private String beginDateStr;

    private String endDateStr;

    private String operateDateStr;


    public String getBeginDateStr() {
        return beginDateStr;
    }

    public void setBeginDateStr(String beginDateStr) {
        this.beginDateStr = beginDateStr;
    }

    public String getEndDateStr() {
        return endDateStr;
    }

    public void setEndDateStr(String endDateStr) {
        this.endDateStr = endDateStr;
    }

    public String getOperateDateStr() {
        return operateDateStr;
    }

    public void setOperateDateStr(String operateDateStr) {
        this.operateDateStr = operateDateStr;
    }

    public String getReadConfirm() {
        return readConfirm;
    }

    public void setReadConfirm(String readConfirm) {
        this.readConfirm = readConfirm;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNoticeSubject() {
        return noticeSubject;
    }

    public void setNoticeSubject(String noticeSubject) {
        this.noticeSubject = noticeSubject;
    }

    public String getNoticeNo() {
		return noticeNo;
	}

	public void setNoticeNo(String noticeNo) {
		this.noticeNo = noticeNo;
	}

	public String getIsRead() {
		return isRead;
	}

	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPublishTimeStr() {
		return publishTimeStr;
	}

	public void setPublishTimeStr(String publishTimeStr) {
		this.publishTimeStr = publishTimeStr;
	}

	public String getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(String noticeType) {
		this.noticeType = noticeType;
	}

	public String getNoticeTitle() {
		return noticeTitle;
	}

	public void setNoticeTitle(String noticeTitle) {
		this.noticeTitle = noticeTitle;
	}

	public String getNoticeContent() {
		return noticeContent;
	}

	public void setNoticeContent(String noticeContent) {
		this.noticeContent = noticeContent;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}
	@Override
	public String toString() {
		return "NoticeInfo  [noticeNo=" + noticeNo + ", noticeTitle=" + noticeTitle + ", isRead=" + isRead
				+ ", userId=" + userId  + "]";
	}
}
