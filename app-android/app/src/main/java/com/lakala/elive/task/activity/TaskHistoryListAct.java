package com.lakala.elive.task.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.beans.DeleteTaskListResp;
import com.lakala.elive.beans.TaskListReqResp;
import com.lakala.elive.common.net.ApiRequestListener;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.TaskListBaseReq;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.task.fragment.HistoryTaskListFragment;

import java.util.ArrayList;

/**
 * Created by gaofeng on 2017/3/20.
 * 任务历史记录
 */
public class TaskHistoryListAct extends FragmentActivity implements View.OnClickListener,ApiRequestListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_history_layout);
        initWidget();
    }
    private TextView mTvBack;

    private Button mBtnAdd;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private void initWidget() {
        mTvBack = (TextView) findViewById(R.id.tv_task_history_back);
        mTvBack.setOnClickListener(this);

        mBtnAdd = (Button) findViewById(R.id.btn_task_history_bottom_add);
        mBtnAdd.setOnClickListener(this);

        mTabLayout = (TabLayout) findViewById(R.id.tab_task_history_title);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_task_history_content);

        initTopView();
        initViewPager();
    }

    private void initViewPager() {
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mHistoryFragments.get(position);
            }
            @Override
            public int getCount() {
                return mHistoryFragments.size();
            }
        });
        mTabLayout.setupWithViewPager(mViewPager);
        final int count = mHistoryFragments.size();
        for(int i = 0 ;i < count ;i++){
            mHistoryFragments.get(i).getTabView(mTabLayout,i,count-i-1);
        }
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                mHistoryFragments.get(position).toResume();
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }
    private boolean isFirst = true;
    @Override
    protected void onResume() {
        super.onResume();
        if(isFirst){
            isFirst = false;
            showProgressDialog("获取数据中");
            mViewPager.setCurrentItem(mHistoryFragments.size()-1);
        }
    }

    private final ArrayList<HistoryTaskListFragment> mHistoryFragments = new ArrayList<HistoryTaskListFragment>();
    private void initTopView(){

        if(mTabLayout.getTabCount()!=0){
            mTabLayout.removeAllTabs();
        }
        if(mHistoryFragments.size() != 0){
            mHistoryFragments.clear();
        }
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

        for(int i = 0;i < 7;i++){
            HistoryTaskListFragment fragment = new HistoryTaskListFragment();
            mHistoryFragments.add(fragment);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_task_history_back:
                TaskHistoryListAct.this.finish();
                break;
            case R.id.btn_task_history_bottom_add:
                // TODO 将未完成加入任务
                // TODO 当前显示的Fragment 的 index
                int index = mViewPager.getCurrentItem();
                if(index>=mHistoryFragments.size())break;
                String requestDate = mHistoryFragments.get(index).getRequestDate();
                if(TextUtils.isEmpty(requestDate)){
                    Utils.showToast(TaskHistoryListAct.this, "暂无任务!" );
                    break;
                }
                loadHisOrderTaskList(requestDate);
                break;
        }
    }
    private ProgressDialog mProgressDialog;
    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        } else if(mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }

        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }
    public void closeProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mProgressDialog != null){
            if(mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }// 销毁该进度条
            mProgressDialog = null;
        }
    }

    private void loadHisOrderTaskList(String dateStr){
        if(TextUtils.isEmpty(dateStr))return;
        showProgressDialog("载入中...");
        TaskListBaseReq req = new TaskListBaseReq();
        req.setAuthToken(Session.get(TaskHistoryListAct.this).getUserLoginInfo().getAuthToken());
        req.setUserId(Session.get(TaskHistoryListAct.this).getUserLoginInfo().getUserId());
        req.setTaskDateStr(dateStr);
        NetAPI.loadHisOrderTaskList(TaskHistoryListAct.this,this,req);
    }
    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.loadHisOrderTaskList:
                Utils.showToast(TaskHistoryListAct.this, "加入失败：" + statusCode + "!");
            default:
                break;
        }
    }

    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.loadHisOrderTaskList:
                // TODO 加入未完成。
                Utils.showToast(TaskHistoryListAct.this, "加入成功!" );
                break;
            default:
                break;
        }
    }
}
