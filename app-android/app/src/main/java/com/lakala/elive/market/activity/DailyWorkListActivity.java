package com.lakala.elive.market.activity;


import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.beans.DictDetailInfo;
import com.lakala.elive.beans.MerShopInfo;
import com.lakala.elive.beans.MessageEvent;
import com.lakala.elive.beans.PageModel;
import com.lakala.elive.common.dropdownmenu.ListDropDownAdapter;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.PageReqInfo;
import com.lakala.elive.common.utils.UiUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.common.widget.SearchView;
import com.lakala.elive.market.adapter.MerListViewAdpter;
import com.lakala.elive.user.base.BaseActivity;
import com.yyydjk.library.DropDownMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 用户日常维护工作
 *
 * @author hongzhiliang
 */
public class DailyWorkListActivity extends BaseActivity implements SearchView.SearchViewListener {
    //下拉刷新页面
    private PullToRefreshListView mPullToRefreshListView;
    private MerListViewAdpter mMerListViewAdpter;
    private ListView mMerListView;

    private RelativeLayout rlSearchTitle;
    private TextView tvSearchRet;
    private ImageButton ivRemoveBtn;

    //商户列表
    private List<MerShopInfo> merInfoList = new ArrayList<MerShopInfo>();//初始化ListView
    PageReqInfo pageReqInfo = new PageReqInfo();

    private int mPageNo = 1; //分页查询当前页码
    private int mPageCnt = 0; //分页页数

    /**
     * 搜索view
     */
    private SearchView searchView;


    private DropDownMenu mDropDownMenu;
    private List<View> popupViews = new ArrayList<>();

    private ListDropDownAdapter shopCreateTimeAdapter;
    private ListDropDownAdapter shopVisitTimeAdapter;
    private String headers[] = {"进件时间","拜访时间"};


    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_daily_work_list);
    }

    @Override
    protected void bindView() {
        //初始化查询控件
        searchView = (SearchView) findViewById(R.id.main_search_layout);

        //查询结果title
        rlSearchTitle = (RelativeLayout) findViewById(R.id.rl_search_title);
        tvSearchRet = (TextView) findViewById(R.id.tv_search_ret);
        ivRemoveBtn = (ImageButton) findViewById(R.id.iv_remove_btn);

        ivRemoveBtn.setOnClickListener(this);
        rlSearchTitle.setVisibility(View.GONE);

        //综合过滤条件
        mDropDownMenu = (DropDownMenu)findViewById(R.id.dropDownMenu);


        //初始化外勤人员维护的商户列表
        initMerList();
    }

    @Override
    protected void bindEvent() {
        //设置监听
        searchView.setSearchViewListener(this);

        initSearchFilterView();

        // 1 注册广播
        EventBus.getDefault().register(DailyWorkListActivity.this);


    }

    @Override
    protected void bindData() {
        queryMerShopList(mPageNo, Constants.DEFAULT_PAGE_SIZE);
    }


    /**
     * 商户网点分页查询
     *
     * @param pageNo
     * @param pageSize
     */
    private void queryMerShopList(int pageNo, int pageSize) {
        if (pageNo == 1) {
            showProgressDialog("网点列表加载中.....");
            if (merInfoList != null && merInfoList.size() > 0) {
                merInfoList.clear();
            }
        }
        if (!searchView.getSearchMode()) {
            pageReqInfo.setSearchCode(""); //不是查询模式综合查询条件消除
        }
        if(mSession.getUserLoginInfo() == null){
            closeProgressDialog();
            Toast.makeText(DailyWorkListActivity.this,"登录信息为空!",Toast.LENGTH_SHORT).show();
            return;
        }
        pageReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        pageReqInfo.setPageNo(pageNo);
        pageReqInfo.setPageSize(pageSize);
        NetAPI.queryMerShopList(this, this, pageReqInfo);
    }

    private void initMerList() {
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_mer_list);
        mPullToRefreshListView.setEmptyView(findViewById(R.id.lv_mer_empty));
        /*
         * 设置PullToRefresh刷新模式
         * BOTH:上拉刷新和下拉刷新都支持
         * DISABLED：禁用上拉下拉刷新
         * PULL_FROM_START:仅支持下拉刷新（默认）
         * PULL_FROM_END：仅支持上拉刷新
         * MANUAL_REFRESH_ONLY：只允许手动触发
        * */
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

        ILoadingLayout endLabelsr = mPullToRefreshListView.getLoadingLayoutProxy(false, true);
//       endLabelsr.setPullLabel("上拉可以刷新");// 刚下拉时，显示的提示
        endLabelsr.setLastUpdatedLabel("正在加载");// 刷新时
        endLabelsr.setReleaseLabel("松开后加载更多网点信息");// 下来达到一定距离时，显示的提示

        mPullToRefreshListView.setOnRefreshListener(
                new PullToRefreshBase.OnRefreshListener2<ListView>() {
                    // 下拉Pulling Down
                    @Override
                    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                        // 下拉的时候数据重置
                        queryMerShopList(1, Constants.DEFAULT_PAGE_SIZE);
                    }

                    // 上拉Pulling Up
                    @Override
                    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                        String label = DateUtils.formatDateTime(
                                DailyWorkListActivity.this,
                                System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME
                                        | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL);
                        if (mPageNo + 1 <= mPageCnt) {
//                          refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                            queryMerShopList(mPageNo + 1, Constants.DEFAULT_PAGE_SIZE);
                        } else {
                            Utils.showToast(DailyWorkListActivity.this, "没有数据了!");
                            new FinishRefresh().execute();
                        }
                    }
                }
        );

        //--------------------------------------------------------//
        mMerListView = mPullToRefreshListView.getRefreshableView();

        //设置点击事件响应处理
        mMerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MerShopInfo merchantInfo = merInfoList.get(position - 1);
                //进入商户详情
                UiUtils.startActivityWithExObj(DailyWorkListActivity.this, MerDetailActivity.class, Constants.EXTRAS_MER_INFO, merchantInfo);
            }
        });

    }


    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();

        switch (method) {
            case NetAPI.ACTION_MER_PAGE_LIST:
                handlerPageShopInfo((PageModel<MerShopInfo>) obj);
                break;
            default:
                break;
        }
    }

    private void handlerPageShopInfo(PageModel<MerShopInfo> page) {
        try {
            merInfoList.addAll(page.getRows());
            mPageNo = page.getPageNo();
            mPageCnt = page.getTotalPage();
            if (mMerListViewAdpter == null) {
                mMerListViewAdpter = new MerListViewAdpter(DailyWorkListActivity.this, merInfoList, -1);
                mMerListView.setAdapter(mMerListViewAdpter);
            } else {
                mMerListViewAdpter.setMerInfoList(merInfoList);
                mMerListViewAdpter.notifyDataSetChanged();
            }
            new FinishRefresh().execute();
            mPullToRefreshListView.getRefreshableView().setSelection(mPullToRefreshListView.getRefreshableView().getCount() - 2 - (10 * (merInfoList.size() - 1)));
            rlSearchTitle.setVisibility(View.VISIBLE);
            tvSearchRet.setText("查询出：" + page.getRecordCount() + " 个网点！已加载：" + merInfoList.size() + " 个网点！");
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showToast(this, "加载失败!");
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.ACTION_MER_PAGE_LIST:
                Utils.showToast(this, "获取失败：" + statusCode + "!");
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_remove_btn: //进入标签页面
                rlSearchTitle.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    class FinishRefresh extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mPullToRefreshListView.onRefreshComplete();
        }
    }

    @Override
    public void onRefreshAutoComplete(String text) {
        Toast.makeText(this, "开始搜索：" + text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSearch(String text) {
        Toast.makeText(this, "开始搜索：" + text, Toast.LENGTH_SHORT).show();
        pageReqInfo.setSearchCode(text);
        mPageNo = 1;
        queryMerShopList(mPageNo, Constants.DEFAULT_PAGE_SIZE);
    }

    @Override
    public void cancelSearch() {
        mPageNo = 1;
        queryMerShopList(mPageNo, Constants.DEFAULT_PAGE_SIZE);
    }

    private void initSearchFilterView() {
        //网点创建时间
        final ListView shopCreateTimeView = new ListView(this);
        final List<DictDetailInfo> createTimeDictList = Constants.shopCreateTimeDictDataList;
        shopCreateTimeAdapter = new ListDropDownAdapter(this,createTimeDictList);
        shopCreateTimeView.setDividerHeight(0);
        shopCreateTimeView.setAdapter(shopCreateTimeAdapter);

        //网点拜访时间
        final ListView shopVisitTimeView = new ListView(this);
        final List<DictDetailInfo> vistitTimeDictList = Constants.shopVistitTimeDictDataList;
        shopVisitTimeAdapter = new ListDropDownAdapter(this,vistitTimeDictList);
        shopVisitTimeView.setDividerHeight(0);
        shopVisitTimeView.setAdapter(shopVisitTimeAdapter);

        popupViews.add(shopCreateTimeView);
        popupViews.add(shopVisitTimeView);

        shopCreateTimeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                shopCreateTimeAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[0] : createTimeDictList.get(position).getDictName());
                mDropDownMenu.closeMenu();
                pageReqInfo.setCreateTimeType(createTimeDictList.get(position).getDictKey());
                queryMerShopList(1, Constants.DEFAULT_PAGE_SIZE);
            }
        });

        shopVisitTimeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                shopCreateTimeAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[1] : vistitTimeDictList.get(position).getDictName());
                mDropDownMenu.closeMenu();
                pageReqInfo.setVisitTimeType(vistitTimeDictList.get(position).getDictKey());
                queryMerShopList(1, Constants.DEFAULT_PAGE_SIZE);
            }
        });

        //init dropdownview
        mDropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews);
    }

    @Override
    public void onBackPressed() {
        //退出activity前关闭菜单
        if (mDropDownMenu.isShowing()) {
            mDropDownMenu.closeMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MesssageEventBus(MessageEvent event){
        if(Constants.MessageType.SHOP_VISIT.equals(event.type)){
            queryMerShopList(1, Constants.DEFAULT_PAGE_SIZE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeProgressDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(DailyWorkListActivity.this);// 2 解注册
    }

}
