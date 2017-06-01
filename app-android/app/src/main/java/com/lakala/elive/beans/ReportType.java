package com.lakala.elive.beans;

/**
 * 
 * @author hongzhiliang
 *
 */
public class ReportType {
	
	private String typeId;
	
	private String typeName; //类型名称
	
	private String iconUrl;

	private String simpleDesc; //简要说明
	
	private String detailDesc; //详细说明

	private String typePid; //类型名称
	
	private int childCnt; //类型名称
	
	private boolean isParent; //
	
	private int nodeLevel;//层次
	
	public int getNodeLevel() {
		return nodeLevel;
	}

	public void setNodeLevel(int nodeLevel) {
		this.nodeLevel = nodeLevel;
	}

	public String getTypePid() {
		return typePid;
	}

	public void setTypePid(String typePid) {
		this.typePid = typePid;
	}

	public int getChildCnt() {
		return childCnt;
	}

	public void setChildCnt(int childCnt) {
		this.childCnt = childCnt;
	}

	public boolean isParent() {
		return isParent;
	}

	public void setParent(boolean isParent) {
		this.isParent = isParent;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getSimpleDesc() {
		return simpleDesc;
	}

	public void setSimpleDesc(String simpleDesc) {
		this.simpleDesc = simpleDesc;
	}

	public String getDetailDesc() {
		return detailDesc;
	}

	public void setDetailDesc(String detailDesc) {
		this.detailDesc = detailDesc;
	}
	
	@Override
	public String toString() {
		return " [typeId=" + typeId + ", typePid=" + typePid + ", childCnt=" + childCnt
				+ ", typeName=" + typeName + ", nodeLevel:" + nodeLevel + "]";
	}
}
