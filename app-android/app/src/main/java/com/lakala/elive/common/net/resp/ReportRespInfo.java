package com.lakala.elive.common.net.resp;

import com.lakala.elive.beans.ReportInfo;
import com.lakala.elive.common.net.resp.base.ResponseInfo;

import java.util.ArrayList;
import java.util.List;

public class ReportRespInfo extends ResponseInfo {
	
	private static final long serialVersionUID = 1L;
	
	List<ReportInfo> content = new ArrayList<ReportInfo>();

	public List<ReportInfo> getContent() {
		return content;
	}

	public void setContent(List<ReportInfo> content) {
		this.content = content;
	}
	
	@Override
	public String toString() {
		return " [resultCode=" + resultCode + ", message=" + message
				+ ", commandNo=" + commandNo + ", resultDataType="
				+ resultDataType + ", content=" + content + "]";
	}
}
