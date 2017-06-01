package com.lakala.elive.common.net.resp.base;

import java.io.Serializable;

public class ResponseInfo implements Serializable {
	
	protected String resultCode;
	
	protected String message;
	
	protected String commandNo;
	
	protected String resultDataType;

	private String commandId;

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCommandNo() {
		return commandNo;
	}

	public void setCommandNo(String commandNo) {
		this.commandNo = commandNo;
	}

	public String getResultDataType() {
		return resultDataType;
	}

	public void setResultDataType(String resultDataType) {
		this.resultDataType = resultDataType;
	}

	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	@Override
    public String toString() {
        return "ResponseInfo{" +
                "resultCode='" + resultCode + '\'' +
                ", message='" + message + '\'' +
                ", commandNo='" + commandNo + '\'' +
                ", resultDataType='" + resultDataType + '\'' +
                '}';
    }
}
