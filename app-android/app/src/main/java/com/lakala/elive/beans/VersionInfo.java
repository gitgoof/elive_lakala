package com.lakala.elive.beans;

import java.io.Serializable;

public class VersionInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// apk下载地址
	private String url;
	
	// 版本号
	private int versionCode;
	
	// 版本号
	private String versionName;
	
	//版本描述 COMMENTS
	private String comments;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	@Override
	public String toString() {
		return "VersionInfo [url=" + url 
				+ ", versionCode=" + versionCode 
				+  ", versionName=" + versionName
				+ ", comments=" + comments + "]";
	}

	/**
	 * @return the versionName
	 */
	public String getVersionName() {
		return versionName;
	}

	/**
	 * @param versionName the versionName to set
	 */
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	
}
