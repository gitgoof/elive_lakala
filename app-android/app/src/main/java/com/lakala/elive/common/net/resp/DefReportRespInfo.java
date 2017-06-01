package com.lakala.elive.common.net.resp;


import com.lakala.elive.beans.ReportInfo;
import com.lakala.elive.common.net.resp.base.ResponseInfo;

public class DefReportRespInfo extends ResponseInfo {
	
	private static final long serialVersionUID = 1L;
	
	ReportInfo content = new ReportInfo();

	public ReportInfo getContent() {
		return content;
	}

	public void setContent(ReportInfo content) {
		this.content = content;
	}
	
	@Override
	public String toString() {
		return " [resultCode=" + resultCode + ", message=" + message
				+ ", commandNo=" + commandNo + ", resultDataType="
				+ resultDataType + ", content=" + content + "]";
	}
}
