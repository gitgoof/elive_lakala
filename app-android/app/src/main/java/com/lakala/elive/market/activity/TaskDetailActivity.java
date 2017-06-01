package com.lakala.elive.market.activity;



import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.beans.MerShopInfo;
import com.lakala.elive.beans.MessageEvent;
import com.lakala.elive.beans.TaskInfo;
import com.lakala.elive.beans.TermInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.MerShopReqInfo;
import com.lakala.elive.common.net.req.TaskReqInfo;
import com.lakala.elive.common.utils.DisplayUtils;
import com.lakala.elive.common.utils.StringUtil;
import com.lakala.elive.common.utils.UiUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.market.adapter.ShopFragPagerAdapter;
import com.lakala.elive.market.base.BaseFragmentActivity;
import com.lakala.elive.market.fragment.MerBaseInfoFragment;
import com.lakala.elive.market.fragment.MerMarkInfoFragment;
import com.lakala.elive.market.fragment.MerTermListFragment;
import com.lakala.elive.market.fragment.TaskInfoFragment;
import com.lakala.elive.market.fragment.VisitHisInfoFragment;
import com.lakala.elive.market.base.BaseFragment;
import com.lakala.elive.merapply.activity.EnterPieceActivity;
import com.lakala.elive.merapply.activity.MerApplyDetailsActivity;
import com.lakala.elive.merapply.activity.MyMerchantsActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 任务网点详情
 * 加载内容
 *
 *  1.工单详情
 *  2.商户信息（基本信息，标签信息）
 *  3.终端列表
 *  4.签到信息
 * 
 * @author hongzhiliang
 *
 */
public class TaskDetailActivity extends BaseFragmentActivity {
    private Session mSession;
    private TaskInfo taskInfo;

    private TextView tvTitleName;
    private ImageView btnIvBack;

    private LinearLayout llTaskOperator ;
    private Button btnAddTaskVisit;
    private Button btnRejuctTask;
    private Button btnMerApplyTask;
    private MerShopReqInfo merShopReqInfo = new MerShopReqInfo(); //请求查询接口
    private TabLayout tabLayout;
    private ViewPager viewPager;

    MerShopInfo shopInfo = new MerShopInfo();
    ArrayList<BaseFragment> fragments = new ArrayList<>();
    private BaseFragment taskInfoFragment;
    private BaseFragment merBaseInfoFragment;
    private BaseFragment merMarkInfoFragment;
    private BaseFragment termListInfoFragment;
    private BaseFragment visitHisInfoFragment;
    private ShopFragPagerAdapter mShopPagerAdapter;
    private RelativeLayout rlMerApply;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        taskInfo = (TaskInfo) getIntent().getExtras().get(Constants.EXTRAS_TASK_DEAL_INFO);
        mSession = Session.get(this);
        initView();
        initTabLayout();
        loadData();
        // 1 注册广播
        EventBus.getDefault().register(TaskDetailActivity.this);
    }


    private void initTabLayout() {
        tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        viewPager = (ViewPager)findViewById(R.id.view_pager);

        //tabLayout添加分割线
        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerPadding(DisplayUtils.dp2px(15));
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,R.drawable.layout_divider_vertical));
    }


    private void initView() {
        tvTitleName = (TextView) findViewById(R.id.tv_title_name);
        tvTitleName.setText(R.string.title_task_detail);

        //进件按钮
        rlMerApply = (RelativeLayout) findViewById(R.id.rl_task_mer_apply);

        btnIvBack = (ImageView) findViewById(R.id.btn_iv_back);
        btnIvBack.setVisibility(View.VISIBLE);
        btnIvBack.setOnClickListener(this);

        btnAddTaskVisit = (Button) findViewById(R.id.btn_add_task_visit);//结单
        btnAddTaskVisit.setOnClickListener(this);
        btnRejuctTask = (Button) findViewById(R.id.btn_rejuct_task);//拒单
        btnRejuctTask.setOnClickListener(this);
        btnMerApplyTask = (Button) findViewById(R.id.btn_task_mer_apply);//进件
        btnMerApplyTask.setOnClickListener(this);
        llTaskOperator = (LinearLayout) findViewById(R.id.ll_task_operator);

        if("5".equals(taskInfo.getStatus())){ //已经处理
            llTaskOperator.setVisibility(View.GONE);
        }else{
            llTaskOperator.setVisibility(View.VISIBLE);
        }
        if("12".equals(taskInfo.getTaskType())){ //工单类型预进件
            rlMerApply.setVisibility(View.VISIBLE);
        }else{
            rlMerApply.setVisibility(View.GONE);
        }
    }

    private void loadData() {
        shopInfo = new MerShopInfo();

        //工单任务标签
        taskInfoFragment = new TaskInfoFragment(taskInfo);
        taskInfoFragment.setTitle("工单详情");
        fragments.add(taskInfoFragment);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        if(StringUtil.isNotNullAndBlank(taskInfo.getShopNo())){ //有商户网点加载商户网点的信息'
            shopInfo.setShopNo(taskInfo.getShopNo());

            merBaseInfoFragment = new MerBaseInfoFragment(shopInfo);
            merBaseInfoFragment.setTitle("网点详情");
            fragments.add(merBaseInfoFragment);

            merMarkInfoFragment = new MerMarkInfoFragment(shopInfo);
            merMarkInfoFragment.setTitle("网点标签");
            fragments.add(merMarkInfoFragment);

            List<TermInfo> merTermListInfo = new ArrayList<TermInfo>();
            termListInfoFragment = new MerTermListFragment(shopInfo,merTermListInfo);
            termListInfoFragment.setTitle("终端列表");
            fragments.add(termListInfoFragment);

            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }

        //签到记录
        shopInfo.setTaskId(taskInfo.getTaskId());
        visitHisInfoFragment = new VisitHisInfoFragment(shopInfo);
        visitHisInfoFragment.setTitle("签到记录");
        fragments.add(visitHisInfoFragment);

        mShopPagerAdapter = new ShopFragPagerAdapter(getSupportFragmentManager(),fragments);

        viewPager.setAdapter(mShopPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        loadTaskDetail();
    }

    private TaskReqInfo taskReqInfo = new TaskReqInfo();

    public void loadTaskDetail() {
        showProgressDialog("正在加载工单详情...");
        taskReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        taskReqInfo.setTaskId(taskInfo.getTaskId());
        NetAPI.getTaskDetail(this, this, taskReqInfo);
    }

    private void queryMerShopInfoDetail() {
        showProgressDialog("正在加载网点详情...");
        merShopReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        merShopReqInfo.setShopNo(taskInfo.getShopNo());
        NetAPI.queryEliveMerShopInfoDetail(this, this, merShopReqInfo);
    }

    private void queryMerTermList() {
        showProgressDialog("正在加载终端列表...");
        merShopReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        merShopReqInfo.setShopNo(taskInfo.getShopNo());
        NetAPI.queryShopTermList(this, this, merShopReqInfo);
    }

    private int merShopInfoCnt = 0;
    private int merTermListCnt = 0;

    @Override
    public void onSuccess(int method, Object obj) {
        switch (method) {
            case NetAPI.ACTION_GET_ELIVE_SHOP_INFO:
                closeProgressDialog();
                merShopInfoCnt = 1;
                handleRespose();
                break;
            case NetAPI.ACTION_SHOP_TERM_LIST:
                closeProgressDialog();
                merTermListCnt = 1;
                handleRespose();
                break;
            case NetAPI.ACTION_TASK_DETAIL:
                closeProgressDialog();
                if(obj == null){
                    break;
                }
                taskInfo = (TaskInfo) obj;
        }
        super.onSuccess(method, obj);
    }



    @Override
    public void onError(int method, String statusCode) {
        switch (method) {
            case NetAPI.ACTION_GET_ELIVE_SHOP_INFO:
                closeProgressDialog();
                merShopInfoCnt = 1;
                handleRespose();
                break;
            case NetAPI.ACTION_SHOP_TERM_LIST:
                closeProgressDialog();
                merTermListCnt = 1;
                handleRespose();
                break;
            case NetAPI.ACTION_TASK_DETAIL:
                closeProgressDialog();
                break;
        }
    }

    /**
     * 结果处理
     */
    private void handleRespose() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_iv_back:
                finish();
                break;
            case R.id.btn_add_task_visit: //增加拜访记录
               UiUtils.startActivityWithExObj(TaskDetailActivity.this,TaskVisitActivity.class, Constants.EXTRAS_TASK_DEAL_INFO,taskInfo);
                break;
            case R.id.btn_rejuct_task: //拒绝任务
                UiUtils.startActivityWithExObj(TaskDetailActivity.this,TaskRejectActivity.class, Constants.EXTRAS_TASK_DEAL_INFO,taskInfo);
                break;
            case R.id.btn_task_mer_apply: //进件
                if(mSession != null && !TextUtils.isEmpty(mSession.getUserLoginInfo().getUserSource())
                        && mSession.getUserLoginInfo().getUserSource().equals("2")){//BCMP账号
                    startActivity(new Intent(TaskDetailActivity.this, MerApplyDetailsActivity.class).putExtra(MyMerchantsActivity.APPLYID_ID, taskInfo.getBizSheetId()).putExtra("process", "ENTER"));
                }else{
                    Utils.showToast(TaskDetailActivity.this, "非BMCP账号登录不能进件,请联系负责人或拒单！");
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MesssageEventBus(MessageEvent event){
        if(Constants.MessageType.TASK_DEAL.equals(event.type)){
            llTaskOperator.setVisibility(View.GONE);
            taskInfoFragment.refershUi();
            visitHisInfoFragment.refershUi();
        }
        if(Constants.MessageType.TASK_DEAL_REJUCT.equals(event.type)){
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(TaskDetailActivity.this);// 2 解注册
    }
}
