package com.lakala.elive.common.net.resp;


import com.lakala.elive.beans.ReportType;
import com.lakala.elive.common.net.resp.base.ResponseInfo;

import java.util.ArrayList;
import java.util.List;

public class ReportTypeRespInfo extends ResponseInfo {
	
	private static final long serialVersionUID = 1L;
	
	List<ReportType> content = new ArrayList<ReportType>();

	public List<ReportType> getContent() {
		return content;
	}

	public void setContent(List<ReportType> content) {
		this.content = content;
	}
	
	@Override
	public String toString() {
		return " [resultCode=" + resultCode + ", message=" + message
				+ ", commandNo=" + commandNo + ", resultDataType="
				+ resultDataType + ", content=" + content + "]";
	}
}
