package com.lakala.elive.market.activity;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.beans.MerShopInfo;
import com.lakala.elive.beans.MessageEvent;
import com.lakala.elive.beans.TermInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.MerShopReqInfo;
import com.lakala.elive.common.net.req.VisitOrEditReqInfo;
import com.lakala.elive.common.utils.DisplayUtils;
import com.lakala.elive.common.utils.UiUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.common.widget.LazyFragment;
import com.lakala.elive.market.adapter.ShopFragPagerAdapter;
import com.lakala.elive.market.base.BaseFragment;
import com.lakala.elive.market.base.BaseFragmentActivity;
import com.lakala.elive.market.fragment.MerBaseInfoFragment;
import com.lakala.elive.market.fragment.MerMarkInfoFragment;
import com.lakala.elive.market.fragment.MerQCodeInfoFragment;
import com.lakala.elive.market.fragment.MerTermListFragment;
import com.lakala.elive.market.fragment.ShopReportFragment;
import com.lakala.elive.market.fragment.VisitHisInfoFragment;
import com.lakala.elive.market.merqcodebind.MerQCodeLazyFragment;
import com.lakala.elive.preenterpiece.swapmenurecyleview.MyViewPager;
import com.lakala.elive.preenterpiece.swapmenurecyleview.SwipeMenuBuilder;
import com.lakala.elive.preenterpiece.swapmenurecyleview.bean.SwipeMenu;
import com.lakala.elive.preenterpiece.swapmenurecyleview.bean.SwipeMenuItem;
import com.lakala.elive.preenterpiece.swapmenurecyleview.view.SwipeMenuView;
import com.lakala.elive.qcodeenter.fragment.QCodeInfoFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 商户网点详情
 加载内容
 *  1.商户信息（基本信息，标签信息）
 *  2.终端列表
 *  3.签到信息
 * 
 * @author hongzhiliang
 *
 */
public class MerDetailActivity extends BaseFragmentActivity implements SwipeMenuBuilder {
    public static final String CONTENT_BEAN = "contentBean";
    private Session mSession;

    private MerShopReqInfo merShopReqInfo = new MerShopReqInfo(); //请求查询接口

    private MerShopInfo merShopInfo = null; //商户详情信息
    List<TermInfo> termList = null;

    private TextView tvTitleName;
    private ImageView btnIvBack;

    private Button btnEditMer;
    private Button btnAddVisit;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mer_detail);
        //获取页面传递的对象
        merShopInfo = (MerShopInfo)getIntent().getExtras().get(Constants.EXTRAS_MER_INFO);
        mSession = Session.get(this);
        initView();
        initTabLayout();
         loadData();

        // 1 注册广播
        EventBus.getDefault().register(MerDetailActivity.this);
    }

    private void loadData() {
        queryMerShopInfoDetail(); //查询网点
        queryMerTermList();
    }

    private void queryMerShopInfoDetail() {
        if(merShopInfo == null)return;
        showProgressDialog("正在加载网点详情...");
        merShopReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        merShopReqInfo.setShopNo(merShopInfo.getShopNo());
        NetAPI.queryEliveMerShopInfoDetail(this, this, merShopReqInfo);
    }

    private void queryMerTermList() {
        if(merShopInfo == null)return;
        showProgressDialog("正在加载终端列表...");
        merShopReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        merShopReqInfo.setShopNo(merShopInfo.getShopNo());
        NetAPI.queryShopTermList(this, this, merShopReqInfo);
    }

    private void initView() {
        tvTitleName = (TextView) findViewById(R.id.tv_title_name);
        tvTitleName.setText(R.string.title_merchant_detail);
        btnIvBack = (ImageView) findViewById(R.id.btn_iv_back);
        btnIvBack.setVisibility(View.VISIBLE);
        btnIvBack.setOnClickListener(this);

        //新增拜访记录
        btnAddVisit = (Button)  findViewById(R.id.btn_add_visit);
        btnAddVisit.setOnClickListener(this);

        //网点编辑
        btnEditMer = (Button)  findViewById(R.id.btn_edit_mer);
        btnEditMer.setOnClickListener(this);

    }

    private VisitOrEditReqInfo visitReqInfo = new VisitOrEditReqInfo(); //提交签到请求信息体
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.btn_add_visit:
                //进入拜访新增页面
                if(visitReqInfo.getShopChangeRecordVO() !=null ){
                    visitReqInfo.setVisitOrEdit("VISIT");
                    UiUtils.startActivityWithExObj(MerDetailActivity.this,StepEditShopInfoActivity.class, Constants.EXTRAS_MER_VISIT_INFO,visitReqInfo);
                }
                break;
            case  R.id.btn_edit_mer:
                //进入信息编辑页面
                if(visitReqInfo.getShopChangeRecordVO() !=null ){
                    visitReqInfo.setVisitOrEdit("EDIT");
                    UiUtils.startActivityWithExObj(MerDetailActivity.this,StepEditShopInfoActivity.class, Constants.EXTRAS_MER_VISIT_INFO,visitReqInfo);
                }
                break;
            case R.id.btn_iv_back:
                finish();
                break;
            default:
                break;
        }
    }
    ArrayList<BaseFragment> fragments = new ArrayList<>();
    private BaseFragment merBaseInfoFragment;
    private BaseFragment merMarkInfoFragment;
    private BaseFragment termListInfoFragment;
    private BaseFragment visitHisInfoFragment;
    private BaseFragment merQCodeInfoFragment;
    private BaseFragment shopReportFragment;
    private ShopFragPagerAdapter mShopPagerAdapter;
    private TabLayout tabLayout;
    private MyViewPager viewPager;

    private void initTabLayout() {
        tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        viewPager = (MyViewPager)findViewById(R.id.view_pager);
        //tabLayout添加分割线
        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerPadding(DisplayUtils.dp2px(15));
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,R.drawable.layout_divider_vertical));

        merBaseInfoFragment = new MerBaseInfoFragment(merShopInfo);
        merBaseInfoFragment.setTitle("网点详情");
        fragments.add(merBaseInfoFragment);

        merMarkInfoFragment = new MerMarkInfoFragment(merShopInfo);
        merMarkInfoFragment.setTitle("网点标签");
        fragments.add(merMarkInfoFragment);

        termListInfoFragment = new MerTermListFragment(merShopInfo,termList);
        termListInfoFragment.setTitle("终端列表");
        fragments.add(termListInfoFragment);

        visitHisInfoFragment = new VisitHisInfoFragment(merShopInfo);
        visitHisInfoFragment.setTitle("签到记录");
        fragments.add(visitHisInfoFragment);

        merQCodeInfoFragment = new MerQCodeInfoFragment(merShopInfo);
        merQCodeInfoFragment.setTitle("Q码信息");
        fragments.add(merQCodeInfoFragment);

        shopReportFragment = new ShopReportFragment(merShopInfo);
        shopReportFragment.setTitle("交易统计");
        fragments.add(shopReportFragment);

        mShopPagerAdapter = new ShopFragPagerAdapter(getSupportFragmentManager(),fragments);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        viewPager.setNoScroll(true);//设置viewPage不能滑动
        viewPager.setAdapter(mShopPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private int merShopInfoCnt = 0;
    private int merTermListCnt = 0;

    @Override
    public void onSuccess(int method, Object obj) {
        switch (method) {
            case NetAPI.ACTION_GET_ELIVE_SHOP_INFO:
                merShopInfo = (MerShopInfo) obj;
                visitReqInfo.setShopChangeRecordVO((MerShopInfo) obj);
                merShopInfoCnt = 1;
                handleRespose();
                break;
            case NetAPI.ACTION_SHOP_TERM_LIST:
                termList = (List<TermInfo>)obj;
                visitReqInfo.setTerminalList((List<TermInfo>)obj);
                merTermListCnt = 1;
                handleRespose();
                break;
        }
        super.onSuccess(method, obj);
    }

    @Override
    public void onError(int method, String statusCode) {
        switch (method) {
            case NetAPI.ACTION_GET_ELIVE_SHOP_INFO:
                Utils.showToast(this, "商户详情加载失败:" + statusCode + "!");
                handleRespose();
                merShopInfoCnt = 1;
                break;
            case NetAPI.ACTION_SHOP_TERM_LIST:
                Utils.showToast(this, "终端列表加载失败:" + statusCode + "!");
                merTermListCnt = 1;
                handleRespose();
                break;
        }
    }


    /**
     * 结果处理
     */
    private void handleRespose() {
        if(merShopInfoCnt == 1 && merTermListCnt == 1 ){
                closeProgressDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MesssageEventBus(MessageEvent event){
        if(Constants.MessageType.SHOP_VISIT.equals(event.type)){
            queryMerShopInfoDetail();
            merBaseInfoFragment.refershUi();
            merMarkInfoFragment.refershUi();
            termListInfoFragment.refershUi();
            visitHisInfoFragment.refershUi();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(MerDetailActivity.this);// 2 解注册
    }

    @Override
    public SwipeMenuView create() {
        SwipeMenu menu = new SwipeMenu(this);
        SwipeMenuItem item = new SwipeMenuItem(this);
        item.setTitle("删除")
                .setTitleColor(Color.WHITE)
                .setBackground(new ColorDrawable(Color.RED));
        menu.addMenuItem(item);
        SwipeMenuView menuView = new SwipeMenuView(menu);
        menuView.setOnMenuItemClickListener(MerQCodeInfoFragment.mOnSwipeItemClickListener);
        return menuView;
    }
}
