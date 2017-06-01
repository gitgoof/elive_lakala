package com.lakala.elive.report.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.beans.ReportInfo;

import java.util.ArrayList;
import java.util.List;

public class ReporMenuGridViewAdapter   extends BaseAdapter {
	
	Context parentContext;
	
	private LayoutInflater inflater;
	
	public ReporMenuGridViewAdapter(Context context){
		parentContext = context;
	}
	
	@Override
	public int getCount() {
		return reportInfoList.size();
	}
	
	@Override
	public Object getItem(int position) {
		return  reportInfoList.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ItemView itemView = null;// 声明组件封装对象 初始为null
		if (convertView == null) {
			convertView = View.inflate(parentContext, R.layout.report_info_gridview_item, null);
			itemView = new ItemView();
			itemView.ivReportLogo = (ImageView) convertView.findViewById(R.id.iv_item_menu_icon);
			itemView.tvReportName = (TextView) convertView.findViewById(R.id.tv_item_menu_name);
			convertView.setTag(itemView);
		} else {// 存在缓存
			itemView = (ItemView) convertView.getTag();
		}
		// 获取当前位置的数据
		final ReportInfo bean = reportInfoList.get(position);
		itemView.tvReportName.setText(bean.getReportName());// 显示黑名单号码
		
		return convertView;
	}
	
	List<ReportInfo> reportInfoList = new ArrayList<ReportInfo>();
	
	public List<ReportInfo> getReportInfoList() {
		return reportInfoList;
	}

	public void setReportInfoList(List<ReportInfo> reportInfoList) {
		this.reportInfoList = reportInfoList;
	}
	
	private class ItemView {
		TextView tvReportName; //报表名称
		ImageView ivReportLogo; //报表Logo
	}
	
}
