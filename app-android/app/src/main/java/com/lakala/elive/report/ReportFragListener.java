package com.lakala.elive.report;


import com.lakala.elive.beans.ReportInfo;

/**
 * 
 * 报表查询接口的联动事件定义
 * 
 * @author hongzhiliang
 *
 */
public interface ReportFragListener {

	/**
	 * 更新内容页面
	 * @param reportInfo
	 */
	void updateContentUI(ReportInfo reportInfo);
	
	/**
	 * 更新内容页面
	 * @param url
	 */
	void updateContentUI(String url);

	/**
	 * 更新查询页面
	 * @param reportInfo
	 */
	void updateQueryUI(ReportInfo reportInfo);
	
	
	/**
	 * 获取当前内容请求URL
	 * @return
	 */
	String getCurContentUrl();

	/**
	 * 获取当前查询条件URL
	 * @return
	 */
	String getCurQueryUrl();

	/**
	 * 获取当前查询条件URL
	 * @return
	 */
	String getShortResutlUrl();


	/**
	 * 关闭撤滑
	 * @param type
	 */
	void closeDrawer(int type);
	
	
}
