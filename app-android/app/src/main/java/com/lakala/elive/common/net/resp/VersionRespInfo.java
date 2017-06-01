package com.lakala.elive.common.net.resp;


import com.lakala.elive.beans.VersionInfo;
import com.lakala.elive.common.net.resp.base.ResponseInfo;

public class VersionRespInfo extends ResponseInfo {
	
	private static final long serialVersionUID = 1L;
	
	VersionInfo content = new VersionInfo();

	public VersionInfo getContent() {
		return content;
	}

	public void setContent(VersionInfo content) {
		this.content = content;
	}
	
	@Override
	public String toString() {
		return " [resultCode=" + resultCode + ", message=" + message
				+ ", commandNo=" + commandNo + ", resultDataType="
				+ resultDataType + ", content=" + content + "]";
	}
}
