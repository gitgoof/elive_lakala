package com.lakala.shoudan.activity.shoudan.loan.datafine;

import java.util.ArrayList;
import java.util.List;

public class RegionInfo {
	private String pId;
	private String pNm;
	private String aId;
	private String aNm;
	private String cId;
	private String cNm;
	
	private List<RegionInfo> children = new ArrayList<RegionInfo>();
	
	public List<RegionInfo> getChildren() {
		return children;
	}
	public void setChildren(List<RegionInfo> children) {
		this.children = children;
	}
	public String getpId() {
		return pId;
	}
	public void setpId(String pId) {
		this.pId = pId;
	}
	public String getpNm() {
		return pNm;
	}
	public void setpNm(String pNm) {
		this.pNm = pNm;
	}
	public String getaId() {
		return aId;
	}
	public void setaId(String aId) {
		this.aId = aId;
	}
	public String getaNm() {
		return aNm;
	}
	public void setaNm(String aNm) {
		this.aNm = aNm;
	}
	public String getcId() {
		return cId;
	}
	public void setcId(String cId) {
		this.cId = cId;
	}
	public String getcNm() {
		return cNm;
	}
	public void setcNm(String cNm) {
		this.cNm = cNm;
	}
}
