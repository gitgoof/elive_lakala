package com.lakala.elive.common.net.resp;

import com.lakala.elive.beans.NoticeInfo;
import com.lakala.elive.common.net.resp.base.ResponseInfo;

import java.util.ArrayList;
import java.util.List;

public class NoticeRespInfo extends ResponseInfo {
	
	private static final long serialVersionUID = 1L;
	
	List<NoticeInfo> content = new ArrayList<NoticeInfo>();

	public List<NoticeInfo> getContent() {
		return content;
	}

	public void setContent(List<NoticeInfo> content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return " [resultCode=" + resultCode + ", message=" + message
				+ ", commandNo=" + commandNo + ", resultDataType="
				+ resultDataType + ", content=" + content + "]";
	}
}
