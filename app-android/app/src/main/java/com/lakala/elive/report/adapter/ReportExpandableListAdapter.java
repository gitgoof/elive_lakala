package com.lakala.elive.report.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.beans.ReportInfo;
import com.lakala.elive.beans.ReportType;
import com.lakala.elive.common.net.ApiRequestListener;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.UserReqInfo;
import com.lakala.elive.common.utils.GsonJsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportExpandableListAdapter extends BaseExpandableListAdapter implements OnClickListener, ApiRequestListener {
	
	private Context mContext;
	
	private Map< String, List<ReportInfo> > childMap = new HashMap<String,List<ReportInfo>>();
	private List<ReportType> groupList = new ArrayList<ReportType>();
	
	public ReportExpandableListAdapter(Context context, List<ReportType> groupList,
                                       Map<String,List<ReportInfo>> childMap) {
		this.mContext = context;
		this.groupList = groupList;
		this.childMap = childMap;
	}
	/*
	 *  Gets the data associated with the given child within the given group
	 */
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		//我们这里返回一下每个item的名称，以便单击item时显示
		return childMap.get(groupList.get(groupPosition).getTypeId()).get(childPosition);
	}
	/*  
	 * 取得给定分组中给定子视图的ID. 该组ID必须在组中是唯一的.必须不同于其他所有ID（分组及子项目的ID）
	 */
	@Override
	public long getChildId(int groupPosition, int childPosition) {		
		return childPosition;
	}
	/* 
	 *  Gets a View that displays the data for the given child within the given group
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
		ChildHolder childHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.report_child_item, null);
			childHolder = new ChildHolder();
			childHolder.childName = (TextView) convertView.findViewById(R.id.tv_report_name);
			childHolder.ivBtnAttention = (ImageButton) convertView.findViewById(R.id.iv_btn_attention);
			convertView.setTag(childHolder);
		}else {
			childHolder = (ChildHolder) convertView.getTag();
		}
		final ReportInfo reportInfo = childMap.get(groupList.get(groupPosition).getTypeId()).get(childPosition);
//		childHolder.childImg.setBackgroundResource(childMap.get(groupPosition).get(childPosition).getMarkerImgId());
		childHolder.childName.setText(reportInfo.getReportName());
		
		if(reportInfo.getAttentionFlag() != null && !"0".equals(reportInfo.getAttentionFlag())){
			childHolder.ivBtnAttention.setImageResource(R.drawable.attention_cancel);
			childHolder.ivBtnAttention.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
                    reportInfo.setAttentionFlag("");
                    UserReqInfo reqInfo = new UserReqInfo();
                    reqInfo.setAuthToken(Session.get(mContext).getUserLoginInfo().getAuthToken());
                    reqInfo.setReportId(reportInfo.getReportId());
                    try {
                        Log.e("UserReqInfo", "removeAttention   " + GsonJsonUtils.parseObj2Json(reqInfo));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    NetAPI.removeAttention(mContext,ReportExpandableListAdapter.this,reqInfo);
					notifyDataSetChanged();
				}
			});
		}else{
			childHolder.ivBtnAttention.setImageResource(R.drawable.attention_add);
			childHolder.ivBtnAttention.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
                    reportInfo.setAttentionFlag(reportInfo.getReportId());
                    UserReqInfo reqInfo = new UserReqInfo();
                    reqInfo.setAuthToken(Session.get(mContext).getUserLoginInfo().getAuthToken());
                    reqInfo.setReportId(reportInfo.getReportId());
					NetAPI.addAttention(mContext,ReportExpandableListAdapter.this,reqInfo);
					notifyDataSetChanged();
				}
			});
		}
		return convertView;
	}

	/* 
	 * 取得指定分组的子元素数
	 */
	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return childMap.get(groupList.get(groupPosition).getTypeId()).size();
	}

	/**
	 * 取得与给定分组关联的数据
	 */
	@Override
	public Object getGroup(int groupPosition) {
		return groupList.get(groupPosition);
	}
		
	/**
	 * 取得分组数
	 */
	@Override
	public int getGroupCount() {
		return groupList.size();
	}

	/**
	 * 取得指定分组的ID.该组ID必须在组中是唯一的.必须不同于其他所有ID（分组及子项目的ID）
	 */
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}
	/* 
	 *Gets a View that displays the given group
	 *return: the View corresponding to the group at the specified position 
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		GroupHolder groupHolder = null;
		
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.report_group_item, null);
			groupHolder = new GroupHolder();
			groupHolder.groupImgLogo = (ImageView) convertView.findViewById(R.id.img_indicator);
			groupHolder.groupName = (TextView) convertView.findViewById(R.id.tv_group_name);
			groupHolder.exListDevider = (View) convertView.findViewById(R.id.ex_list_devider);
			convertView.setTag(groupHolder);
		}else {
			groupHolder = (GroupHolder) convertView.getTag();
		}
		
		if (isExpanded) {
			groupHolder.groupImgLogo.setBackgroundResource(R.drawable.ex_up);
			groupHolder.exListDevider.setVisibility(View.GONE);
		}else {
			groupHolder.groupImgLogo.setBackgroundResource(R.drawable.ex_down);
			groupHolder.exListDevider.setVisibility(View.VISIBLE);
		}
		groupHolder.groupName.setText(groupList.get(groupPosition).getTypeName());
		
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// Indicates whether the child and group IDs are stable across changes to the underlying data
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// Whether the child at the specified position is selectable
		return true;
	}
	/**
	 * show the text on the child and group item
	 */	
	private class GroupHolder{
		ImageView groupImgLogo;
		TextView groupName;
		View exListDevider;
	}
	
	private class ChildHolder{
		ImageButton ivBtnAttention;
		TextView childName;
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
		
	}
	
	public Map<String, List<ReportInfo>> getChildMap() {
		return childMap;
	}
	
	public void setChildMap(Map<String, List<ReportInfo>> childMap) {
		this.childMap = childMap;
	}
	
	public List<ReportType> getGroupList() {
		return groupList;
	}
	
	public void setGroupList(List<ReportType> groupList) {
		this.groupList = groupList;
	}
	
	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
			case NetAPI.ACTION_ADD_ATTENTION: //添加关注
//				Utils.makeEventToast(mContext, "关注成功", true);
				//刷新数据
				Session.get(mContext).updateReportAttention();
				break;
			case NetAPI.ACTION_REMOVE_ATTENTION: //取消关注
//				Utils.makeEventToast(mContext, "取消关注成功", true);
				//刷新数据
				Session.get(mContext).updateReportAttention();
				break;
		}
	}

	@Override
	public void onError(int method, String  statusCode) {
		switch (method) {
			case NetAPI.ACTION_ADD_ATTENTION: //添加关注
//				Utils.makeEventToast(mContext, "关注失败", true);
				//刷新数据
				Session.get(mContext).updateReportAttention();
				break;
			case NetAPI.ACTION_REMOVE_ATTENTION: //取消关注
//				Utils.makeEventToast(mContext, "取消关注失败", true);
				//刷新数据
				Session.get(mContext).updateReportAttention();
				break;
		}
	}
	
}
