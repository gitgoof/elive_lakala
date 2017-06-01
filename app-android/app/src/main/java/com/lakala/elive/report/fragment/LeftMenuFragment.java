package com.lakala.elive.report.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.beans.ReportInfo;
import com.lakala.elive.beans.ReportType;
import com.lakala.elive.common.net.ApiRequestListener;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.UserReqInfo;
import com.lakala.elive.report.ReportFragListener;
import com.lakala.elive.report.adapter.ReportExpandableListAdapter;
import com.lakala.elive.report.adapter.ReportListViewAdpter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * 
 * 左侧滑动Fragment
 * 
 * @author hongzhiliang
 *
 */
public class LeftMenuFragment extends Fragment implements OnClickListener,ApiRequestListener,Observer {
	
	private View view;
	private TextView attentionBtn;
	private TextView allBtn;
	
	//报表分类
	private List<ReportType> reportTypeList = new ArrayList<ReportType>();
	//报表信息
	private List<ReportInfo> reportInfoList = new ArrayList<ReportInfo>();
	
	//关注下拉刷新页面
	private LinearLayout llAllReportList;
	private PullToRefreshListView attentionRefreshListview;
	private ReportListViewAdpter attentionListViewAdapter;
	private ListView attentionListview;
	//关注报表列表
	private List<ReportInfo> attentionReportInfoList = new ArrayList<ReportInfo>();
	
	//报表分类数据刷新页面
	private LinearLayout llAttetionList;
	private PullToRefreshExpandableListView reportRefreshExListView;
	private ReportExpandableListAdapter exListViewAdapter;
	private ExpandableListView allExListview;
	//报表分类数据结构
	private Map<String,List<ReportInfo>> exReportInfoList = new HashMap<String,List<ReportInfo>>();
	private List<ReportType> exReportTypeList = new ArrayList<ReportType>();
	
	private int refreshFlag = 0;
	private int reqNetCnt = 0;
    UserReqInfo reqInfo;
    Session mSession;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.report_left_menu_fragment, container, false);
        initView();
        initData();
        //添加数据观察者
        Session.get(getActivity()).addObserver(this);
        mSession =  Session.get(getActivity());
        return view;
    }

	@Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState); 
    } 
    
	
	private void initView() {
		initAttentionRefreshListview();
		initAllExRefreshListview(); 
		
		attentionBtn = (TextView) view.findViewById(R.id.tv_attention_report);
		allBtn = (TextView) view.findViewById(R.id.tv_all_report);
		
		RelativeLayout rlAttentionBtn = (RelativeLayout) view.findViewById(R.id.rl_attention_report);
		RelativeLayout rlTypeBtn = (RelativeLayout) view.findViewById(R.id.rl_type_report);
		rlAttentionBtn.setOnClickListener(this);
		rlTypeBtn.setOnClickListener(this);
		
		llAttetionList =  (LinearLayout) view.findViewById(R.id.ll_attetion_report_list);
		llAllReportList =  (LinearLayout) view.findViewById(R.id.ll_all_report_list);
		
		ivTypeBgLine = (ImageView) view.findViewById(R.id.iv_type_bg_line);
		ivAttentionBgLine = (ImageView) view.findViewById(R.id.iv_attention_bg_line);
		
	}
	
	
    private void initData() {
    	getAllReportData();
    	getAttentionData();		
	}

	
	private void initAllExRefreshListview() {
		reportRefreshExListView = (PullToRefreshExpandableListView) view.findViewById(R.id.report_pull_to_refresh_exlist);
		reportRefreshExListView.setEmptyView(view.findViewById(R.id.ex_report_empty));
		reportRefreshExListView.setOnRefreshListener(
			new OnRefreshListener<ExpandableListView>() {
	            @Override
	            public void onRefresh(PullToRefreshBase<ExpandableListView> refreshView ) {
	                String label = DateUtils.formatDateTime(
	                		getActivity(), 
	                		System.currentTimeMillis(),
	                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
	                // Update the LastUpdatedLabel
	                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
	                
	                getAllReportData();
	                
	                refreshFlag = 1;
	            }
			}
		);
		allExListview = reportRefreshExListView.getRefreshableView();
		allExListview.setGroupIndicator(null);  
		allExListview.setSelector(android.R.color.transparent);  
		
		allExListview.setOnChildClickListener(new OnChildClickListener() {
			
            @Override
            public boolean onChildClick(ExpandableListView parent, View view,
                                        int groupPosition, int childPosition, long id) {
                Log.i("allExListview","group:"+groupPosition+"   child" + childPosition);
                // 获取数据结构
                ReportInfo reportInfo = exReportInfoList.get(exReportTypeList.get(groupPosition)
                		.getTypeId()).get(childPosition);
                
                reportListener.updateContentUI(reportInfo);
                reportListener.updateQueryUI(reportInfo);
                reportListener.closeDrawer(GravityCompat.START);
                return false;  
            }  
            
        });  
	}


	private void initAttentionRefreshListview() {
		attentionRefreshListview = (PullToRefreshListView) view.findViewById(R.id.attention_pull_to_refresh_listview);
		attentionRefreshListview.setEmptyView(view.findViewById(R.id.lv_report_empty));
		attentionRefreshListview.setOnRefreshListener(
			new OnRefreshListener<ListView>() {
				@Override
				public void onRefresh(PullToRefreshBase<ListView> refreshView) {
	                String label = DateUtils.formatDateTime(
	                		getActivity(), 
	                		System.currentTimeMillis(),
	                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
	                
	                // Update the LastUpdatedLabel
	                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
	                getAttentionData();
	                
	                refreshFlag = 2;
				}
			}
		);  
		attentionListview = attentionRefreshListview.getRefreshableView();
		
		attentionListview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				Log.i("attentionListview","position:"+position);
				 // 获取数据结构
                ReportInfo reportInfo = attentionReportInfoList.get(position-1);
                reportListener.updateContentUI(reportInfo);
                reportListener.updateQueryUI(reportInfo);
                reportListener.closeDrawer(GravityCompat.START);
			}
		});
		
	}

	/**
	 * 获取所有的分类报表数据
	 */
	private void getAllReportData() {
		reqNetCnt = 0;

        reqInfo = new UserReqInfo();
        reqInfo.setAuthToken(Session.get(getActivity()).getUserLoginInfo().getAuthToken());

		NetAPI.getReportTypeList(getActivity(), this,reqInfo);//分类
		NetAPI.getReportInfoList(getActivity(), this,reqInfo);//报表数据

	}

	/**
	 * 获取所有的分类报表数据
	 */
	private void getAttentionData() {
        reqInfo = new UserReqInfo();
        reqInfo.setAuthToken(Session.get(getActivity()).getUserLoginInfo().getAuthToken());

		NetAPI.listAttention(getActivity(), this,reqInfo);
	}

	private void handleAllReportInfo() {
		if(reqNetCnt > 1){
			exReportTypeList.clear();
			exReportInfoList.clear();
			Log.i("handleAllReportInfo", "1:" + exReportTypeList.size());
			for (int i = 0; i < reportTypeList.size(); i++){
				List<ReportInfo> tempInfoList = new ArrayList<ReportInfo>();
				for(ReportInfo reportInfo :reportInfoList){
					if(reportInfo.getTypeId().equals(reportTypeList.get(i).getTypeId())){
						tempInfoList.add(reportInfo);
					}
				}
				if(tempInfoList.size() > 0){//按分类，去除没有分类的报表
					exReportTypeList.add(reportTypeList.get(i));
					exReportInfoList.put(reportTypeList.get(i).getTypeId(), tempInfoList);
				}
			}
			Log.i("handleAllReportInfo", "12:" + exReportTypeList.size());
			if(exListViewAdapter == null){
				exListViewAdapter = new ReportExpandableListAdapter(getActivity(), exReportTypeList, exReportInfoList);
				allExListview.setAdapter(exListViewAdapter);
				Log.i("handleAllReportInfo", "3:" + exReportTypeList.size());
			}else{
				exListViewAdapter.setChildMap(exReportInfoList);
				exListViewAdapter.setGroupList(exReportTypeList);
				exListViewAdapter.notifyDataSetChanged();
				Log.i("handleAllReportInfo", "4:" + exReportTypeList.size());
			}
		}
		reportRefreshExListView.onRefreshComplete();
	}
	
	private void  handleAttentionInfo(){
		//刷新列表
		if(attentionListViewAdapter == null){
			attentionListViewAdapter = new ReportListViewAdpter(getActivity(),attentionReportInfoList);
			attentionListview.setAdapter(attentionListViewAdapter);
		}else{
			attentionListViewAdapter.setReportInfoList(attentionReportInfoList);
			attentionListViewAdapter.notifyDataSetChanged();
		}
		attentionRefreshListview.onRefreshComplete();
		
		if(refreshFlag == 0){
			if(attentionReportInfoList.size() > 0){
				setAttention();
			}else{
				setType();
			}
		}
		
	}

	private void setType() {
		llAllReportList.setVisibility(View.VISIBLE);
		llAttetionList.setVisibility(View.GONE);
		ivTypeBgLine.setVisibility(View.VISIBLE);
		ivAttentionBgLine.setVisibility(View.GONE);
		allBtn.setTextColor(getResources().getColor(R.color.text_light_blue));
		attentionBtn.setTextColor(Color.BLACK);
	}

	private void setAttention() {
		llAllReportList.setVisibility(View.GONE);
		llAttetionList.setVisibility(View.VISIBLE);
		ivTypeBgLine.setVisibility(View.GONE);
		ivAttentionBgLine.setVisibility(View.VISIBLE);
		attentionBtn.setTextColor(getResources().getColor(R.color.text_light_blue));
		allBtn.setTextColor(Color.BLACK);
	}

	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
			case NetAPI.ACTION_REPORT_INFO_LIST:
				reportInfoList = (List<ReportInfo>) obj;
				reqNetCnt++;
				handleAllReportInfo();
				break;
			case NetAPI.ACTION_REPORT_TYPE_LIST:
				reportTypeList = (List<ReportType>)obj ;
				reqNetCnt++;
				handleAllReportInfo();
				break;
			case NetAPI.ACTION_LIST_ATTENTION:
				attentionReportInfoList = (List<ReportInfo>)obj ;
				handleAttentionInfo();
				break;
		}
	}
	
	@Override
	public void onError(int method, String statusCode) {
		switch (method) {
			case NetAPI.ACTION_REPORT_INFO_LIST:
				break;
			case NetAPI.ACTION_REPORT_TYPE_LIST:
				break;
			case NetAPI.ACTION_LIST_ATTENTION:
				break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.rl_attention_report:
				setAttention();
				break;
			case R.id.rl_type_report:
				setType();
				break;
			default:
				break;
		}

	}

    /**
     * 
     * 提供给MainActivity设置回调接口方法
     * @param reportListener
     * 
     */
	private ReportFragListener reportListener;
	private ImageView ivTypeBgLine;
	private ImageView ivAttentionBgLine;
	
    public void setReportListener(ReportFragListener reportListener){
        this.reportListener = reportListener;
    }


	@Override
	public void update(Observable observable, Object data) {
		Log.i("data.toString()","LeftMenuFragment update:" + data.toString());
        if (data.toString().equals(Constants.REPORT_ATTENTION_UPDATE)) {
        	getAllReportData();
        	getAttentionData();
        }		
	}
    
}
