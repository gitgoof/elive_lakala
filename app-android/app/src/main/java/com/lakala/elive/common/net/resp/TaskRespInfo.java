package com.lakala.elive.common.net.resp;

import com.lakala.elive.beans.ReportInfo;
import com.lakala.elive.beans.ShopVisitInfo;
import com.lakala.elive.beans.TaskInfo;
import com.lakala.elive.common.net.resp.base.ResponseInfo;

import java.util.ArrayList;
import java.util.List;

public class TaskRespInfo extends ResponseInfo {
	
	private static final long serialVersionUID = 1L;

	protected TaskInfo content;

	public TaskInfo getContent() {
		return content;
	}

	public void setContent(TaskInfo content) {
		this.content = content;
	}
}
