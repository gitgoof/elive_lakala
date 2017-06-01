package com.lakala.elive.report.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.beans.ReportInfo;
import com.lakala.elive.common.net.ApiRequestListener;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.UserReqInfo;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.report.ReportFragListener;
import com.lakala.elive.report.fragment.ContentFragment;
import com.lakala.elive.report.fragment.LeftMenuFragment;
import com.lakala.elive.report.fragment.RightQueryFragment;


/**
 * 
 * 报表显示主界面
 * 
 * 集成查询页面，菜单选择页面，查询结果展示页面
 * 
 */
public class ReportMainActivity extends FragmentActivity
					implements OnClickListener,ApiRequestListener,ReportFragListener {
	
	private final String TAG = ReportMainActivity.class.getSimpleName();
	
	private DrawerLayout drawerLayout;
	
    private FragmentManager frManager;
    
    private LeftMenuFragment leftMenuFragment; //列表页
    private RightQueryFragment rightQueryFragment;//查询页
    
	private String currentQueryUrl;
    private ContentFragment contentFragment;//内容页
    
	private String currentContentUrl;
	private TextView tvTitleName;//报表Title

    private UserReqInfo reqInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_main_activity);
		initViews();
		initData();
	}
	
	private void initViews() {
		tvTitleName = (TextView)findViewById(R.id.tv_title_name);
		findViewById(R.id.iv_left_menu).setOnClickListener(this);
		findViewById(R.id.iv_right_query).setOnClickListener(this);
		
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerLayout.setDrawerListener(new DrawerLayoutStateListener());
		
		frManager = getSupportFragmentManager();
		
		//初始化内容页面
		contentFragment = new ContentFragment();
		contentFragment.setReportListener(this);
		
		//初始化查询页面和菜单页面
		rightQueryFragment = (RightQueryFragment) frManager.findFragmentById(R.id.fg_right_query);
		rightQueryFragment.setReportListener(this);
		leftMenuFragment = (LeftMenuFragment)  frManager.findFragmentById(R.id.fg_left_menu);
		leftMenuFragment.setReportListener(this);
	
		frManager.beginTransaction().replace(R.id.fl_report_content,contentFragment).commit();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		//获取用户默认报表数据

        reqInfo = new UserReqInfo();
        reqInfo.setAuthToken(Session.get(this).getUserLoginInfo().getAuthToken());

		NetAPI.getDefaultReportInfo(this, this,reqInfo);
	}
	
	/**
	 * 处理点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_left_menu://左边点击事件
				drawerLayout.openDrawer(Gravity.LEFT);
				break;
			case R.id.iv_right_query://右边点击事件 
				drawerLayout.openDrawer(Gravity.RIGHT);
				break;
		}
	}
	


	ReportInfo reportInfo;
	/**
	 * 网络异步请求成功处理接口
	 */
	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
			case NetAPI.ACTION_USER_DEFAULT_REPORT://获取用户默认的展示报表
				updateContentUI((ReportInfo)obj);
				updateQueryUI((ReportInfo)obj);
				break;
		}
	}



    /**
	 * 网络异步请求失败处理接口
	 */
	@Override
	public void onError(int method, String statusCode) {
		switch (method) {
			case NetAPI.ACTION_USER_DEFAULT_REPORT://获取用户默认的展示报表
				Utils.makeEventToast(this, "获取数据失败", true);
				break;
		}
	}

	/**
	 * 更新内容页接口不一样
	 */
	@Override
	public void updateContentUI(ReportInfo reportInfo) {
		if(reportInfo.getReportName() != null && !"".equals(reportInfo.getReportName())){
			tvTitleName.setText(reportInfo.getReportName());
		}
		contentFragment.updateContent(reportInfo.getResultUrl());
		currentContentUrl = reportInfo.getResultUrl();
		this.reportInfo = reportInfo;
	}

	/**
	 * 更新内容页接口不一样
	 */
	@Override
	public void updateContentUI(String url) {
		contentFragment.updateContent(url);
		currentContentUrl = url;
	}
	
	/**
	 * 更新查询页面
	 */
	@Override
	public void updateQueryUI(ReportInfo reportInfo) {
		rightQueryFragment.updateContent(reportInfo.getQueryUrl());
		currentQueryUrl = reportInfo.getQueryUrl();
	}

	@Override
	public String getCurContentUrl() {
		return currentContentUrl;
	}

	@Override
	public String getCurQueryUrl() {
		return currentQueryUrl;
	}

	@Override
	public String getShortResutlUrl() {
		return reportInfo.getShortResutlUrl();
	}

	@Override
	public void closeDrawer(int type) {
		drawerLayout.closeDrawer(type);
	}
	
	/**
	 * DrawerLayout状态变化监听
	 */
	private class DrawerLayoutStateListener extends DrawerLayout.SimpleDrawerListener {
		
		/**
		 * 当导航菜单滑动的时候被执行
		 */
		@Override
		public void onDrawerSlide(View drawerView, float slideOffset) {
		}

		/**
		 * 当导航菜单打开时执行
		 */
		@Override
		public void onDrawerOpened(android.view.View drawerView) {

		}

		/**
		 * 当导航菜单关闭时执行
		 */
		@Override
		public void onDrawerClosed(android.view.View drawerView) {
			
		}
	}

}
