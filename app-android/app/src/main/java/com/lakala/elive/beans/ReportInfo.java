package com.lakala.elive.beans;


import com.lakala.elive.Constants;
import com.lakala.elive.common.net.NetAPI;

import java.io.Serializable;


/**
 * 
 * @author hongzhiliang
 *
 */
public class ReportInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String typeId;
	
	private String typeName; //类型名称
	
	private String reportId; //报表编号
	
	private String reportName; //类型名称
	
	private String iconUrl;   //logo URL
	
	private String simpleDesc; //简要说明
	
	private String detailDesc; //详细说明
	
	private String queryUrl;  //查询URL
	
	private String resultUrl; //默认查询结果URL
	
	private String attentionFlag; //是否用户关注报表

    private String shortResutlUrl;  //查询URL

	private String sysType;

	private int nodeLevel;

	public int getNodeLevel() {
		return nodeLevel;
	}

	public void setNodeLevel(int nodeLevel) {
		this.nodeLevel = nodeLevel;
	}

	public String getAttentionFlag() {
		return attentionFlag;
	}

	public void setAttentionFlag(String attentionFlag) {
		this.attentionFlag = attentionFlag;
	}

	//查询条件
	public String getQueryUrl() {
		if("2".equals(sysType)){
			return Constants.ELIVE_DATA_QUERY_API + "?reportId=" + reportId;
		}else{
			return Constants.REPORT_DATA_QUERY_API + "?reportId=" + reportId;
		}
	}

    //查询条件
    public String getShortResutlUrl() {
        if("2".equals(sysType)){
            return Constants.ELIVE_DATA_RESULT_API ;
        }else{
            return Constants.REPORT_DATA_RESULT_API;
        }
    }


	//查询结果
	public String getResultUrl() {
		if("2".equals(sysType)){
			return Constants.ELIVE_DATA_RESULT_API + "?reportId=" + reportId;
		}else{
			return Constants.REPORT_DATA_RESULT_API + "?reportId=" + reportId;
		}
	}
	
	public void setQueryUrl(String queryUrl) {
		this.queryUrl = queryUrl;
	}
	
	public void setResultUrl(String resultUrl) {
		this.resultUrl = resultUrl;
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

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getSysType() {
		return sysType;
	}

	public void setSysType(String sysType) {
		this.sysType = sysType;
	}

	@Override
	public String toString() {
		return " [reportName=" + reportName + ", reportId=" + reportId + ", queryUrl=" + queryUrl
				+ ", typeId=" + typeId  + ", typeName=" + typeName  + ", attentionFlag=" + attentionFlag
				+ ", resultUrl=" + resultUrl + ", sysType=" + sysType + "]";
	}
}
