package com.lakala.elive.market.activity;


import android.os.AsyncTask;
import android.text.TextUtils;
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
import com.lakala.elive.beans.MessageEvent;
import com.lakala.elive.beans.PageModel;
import com.lakala.elive.beans.TaskInfo;
import com.lakala.elive.common.dropdownmenu.GirdDropDownAdapter;
import com.lakala.elive.common.dropdownmenu.ListDropDownAdapter;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.PageReqInfo;
import com.lakala.elive.common.utils.DictionaryUtil;
import com.lakala.elive.common.utils.UiUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.common.widget.SearchView;
import com.lakala.elive.market.adapter.DealListViewAdpter;
import com.lakala.elive.user.base.BaseActivity;
import com.yyydjk.library.DropDownMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 
 * 用户工单处理模块
 * 
 * @author hongzhiliang
 *
 */
public class DealListActivity extends BaseActivity  implements SearchView.SearchViewListener {

    //下拉刷新页面
    private PullToRefreshListView mPullToRefreshListView;
    private DealListViewAdpter mDealListViewAdpter;
    private ListView mTaskListView;

    //工单列表
    private List<TaskInfo> mTaskInfoList = new ArrayList<TaskInfo>();
    PageReqInfo pageReqInfo = new PageReqInfo();

    private int mPageNo = 1; //分页查询当前页码
    private int mPageCnt = 0; //分页页数

    private RelativeLayout rlSearchTitle;
    private TextView tvSearchRet;
    private TextView tvTitleName;
    private ImageButton ivRemoveBtn;
    private String headers[] = {"工单类型","工单状态","剩余天数","处理天数"};
    private GirdDropDownAdapter taskTypeView;

    /**
     * 搜索view
     */
    private SearchView searchView;

    private DropDownMenu mDropDownMenu;
    private int constellationPosition = 0;
    private List<View> popupViews = new ArrayList<>();
    private ListDropDownAdapter taskTypeAdapter;
    private ListDropDownAdapter taskStatusAdapter;
    private ListDropDownAdapter taskDelayAdapter;
    private ListDropDownAdapter taskFinishAdapter;


    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_task_deal_list);
    }

    @Override
    protected void bindView() {
        tvTitleName = (TextView) findViewById(R.id.tv_title_name);
        tvTitleName.setText("工单列表");
        //初始化查询控件
        searchView = (SearchView) findViewById(R.id.main_search_layout);
        //综合过滤条件
        mDropDownMenu = (DropDownMenu)findViewById(R.id.dropDownMenu);
        //查询结果title
        rlSearchTitle = (RelativeLayout) findViewById(R.id.rl_search_title);
        tvSearchRet = (TextView) findViewById(R.id.tv_search_ret);
        ivRemoveBtn = (ImageButton) findViewById(R.id.iv_remove_btn);
        ivRemoveBtn.setOnClickListener(this);
        rlSearchTitle.setVisibility(View.GONE);
        //初始化外勤人员维护的商户列表
        initDealTaskList();
    }

    @Override
    protected void bindEvent() {
        searchView.setSearchViewListener(this);
        initSearchFilterView();

        // 1 注册广播
        EventBus.getDefault().register(DealListActivity.this);
    }

    @Override
    protected void bindData() {
        pageReqInfo.setTaskStatus("23");
        queryTaskDealList(mPageNo, Constants.DEFAULT_PAGE_SIZE);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     *
     * 工单列表分页查询
     *
     * @param pageNo
     * @param pageSize
     *
     */
    private void queryTaskDealList(int pageNo, int pageSize) {
        if(pageNo == 1){
            showProgressDialog("工单列表加载中.....");
            if(mTaskInfoList != null && mTaskInfoList.size()>0){
                mTaskInfoList.clear();
            }
        }
        if(!searchView.getSearchMode()){
            pageReqInfo.setSearchCode(""); //不是查询模式综合查询条件消除
        }
        pageReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        pageReqInfo.setPageNo(pageNo);
        pageReqInfo.setPageSize(pageSize);
        NetAPI.queryTaskDealList(this,this,pageReqInfo);
    }

    private void initDealTaskList() {
        mPullToRefreshListView = (PullToRefreshListView)findViewById(R.id.pull_to_refresh_task_list);
        mPullToRefreshListView.setEmptyView(findViewById(R.id.lv_task_empty));
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
        endLabelsr.setLastUpdatedLabel("正在加载");// 刷新时
        endLabelsr.setReleaseLabel("松开后加载更多工单任务信息");// 下来达到一定距离时，显示的提示

        mPullToRefreshListView.setOnRefreshListener(
                new PullToRefreshBase.OnRefreshListener2<ListView>() {
                    // 下拉Pulling Down
                    @Override
                    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                        // 下拉的时候数据重置
                        queryTaskDealList(1, Constants.DEFAULT_PAGE_SIZE);
                    }

                    // 上拉Pulling Up
                    @Override
                    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                        String label = DateUtils.formatDateTime(
                                DealListActivity.this,
                                System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME
                                        | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL);
                        if(mPageNo +1 <= mPageCnt){
                            queryTaskDealList(mPageNo + 1, Constants.DEFAULT_PAGE_SIZE);
                        }else{
                            Utils.showToast(DealListActivity.this, "没有数据了!" );
                            new FinishRefresh().execute();
                        }
                    }
                }
        );

        mTaskListView = mPullToRefreshListView.getRefreshableView();

        //设置点击事件响应处理
        mTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position>mTaskInfoList.size())return;
                TaskInfo taskInfo = mTaskInfoList.get(position -1);
                if(taskInfo == null)return;
                UiUtils.startActivityWithExObj(DealListActivity.this,TaskDetailActivity.class,Constants.EXTRAS_TASK_DEAL_INFO,taskInfo);
            }
        });

    }


    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.ACTION_TASK_DEAL_LIST:
                handlerPageShopInfo((PageModel<TaskInfo>) obj);
                break;
            default:
                break;
        }
    }

    private void handlerPageShopInfo(PageModel<TaskInfo> page) {
        try {
            if(page == null)return;
            if(page.getRows() == null)return;
            mTaskInfoList.addAll(page.getRows());
            mPageNo = page.getPageNo();
            mPageCnt = page.getTotalPage();

            if(mDealListViewAdpter == null){
                mDealListViewAdpter = new DealListViewAdpter(DealListActivity.this,mTaskInfoList,-1);
                mTaskListView.setAdapter(mDealListViewAdpter);
            }else{
                mDealListViewAdpter.setTaskInfoList(mTaskInfoList);
                mDealListViewAdpter.notifyDataSetChanged();
            }

            new FinishRefresh().execute();
            mPullToRefreshListView.getRefreshableView().setSelection(mPullToRefreshListView.getRefreshableView().getCount() - 2- (10 * (mTaskInfoList.size() - 1)));

            rlSearchTitle.setVisibility(View.VISIBLE);
            tvSearchRet.setText("查询出：" + page.getRecordCount() + " 个工单！已加载：" + mTaskInfoList.size()  + " 个工单！" );

        } catch (Exception e) {
            e.printStackTrace();
            Utils.showToast(this, "加载失败!");
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.ACTION_TASK_DEAL_LIST:
                Utils.showToast(this, "获取失败："+ statusCode +"!" );
                break;
            case NetAPI.ACTION_ADD_DOT_AND_ORDER:
                Utils.showToast(this, "添加失败：" + statusCode + "!");
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_remove_btn: //进入标签页面
                rlSearchTitle.setVisibility(View.GONE);;
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
        protected void onPostExecute(Void result){
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
        queryTaskDealList(mPageNo, Constants.DEFAULT_PAGE_SIZE);
    }

    @Override
    public void cancelSearch() {
        mPageNo = 1;
        queryTaskDealList(mPageNo, Constants.DEFAULT_PAGE_SIZE);
    }

    private void initSearchFilterView() {
        //init task Type menu
        final ListView taskTypeView = new ListView(this);

//        final List<DictDetailInfo> taskTypeDictList = mSession.getSeltMapByKey(Constants.TASK_TYPE);
        final List<DictDetailInfo> taskTypeDictList = getDictListByType(Constants.TASK_TYPE);
        taskTypeAdapter = new ListDropDownAdapter(this,taskTypeDictList);
        taskTypeView.setDividerHeight(0);
        taskTypeView.setAdapter(taskTypeAdapter);

        //init task status menu
        final ListView taskStatusView = new ListView(this);
//        final List<DictDetailInfo> taskStatusDictList = mSession.getSeltMapByKey(Constants.TASK_STATUS);
        final List<DictDetailInfo> taskStatusDictList = getDictListByType(Constants.TASK_STATUS);
        taskStatusAdapter = new ListDropDownAdapter(this,taskStatusDictList);
        taskStatusView.setDividerHeight(0);
        taskStatusView.setAdapter(taskStatusAdapter);

        //init task status menu
        final ListView taskDelayView = new ListView(this);
        final List<DictDetailInfo> taskDealyDictList = Constants.delayDictDataList;
        taskDelayAdapter = new ListDropDownAdapter(this,taskDealyDictList);
        taskDelayView.setDividerHeight(0);
        taskDelayView.setAdapter(taskDelayAdapter);

        //init task status menu
        final ListView taskFinishView = new ListView(this);
        final List<DictDetailInfo> taskFinishDictList = Constants.finshDictDataList;
        taskFinishAdapter = new ListDropDownAdapter(this,taskFinishDictList);
        taskFinishView.setDividerHeight(0);
        taskFinishView.setAdapter(taskFinishAdapter);


        //init popupViews
        popupViews.add(taskTypeView);
        popupViews.add(taskStatusView);
        popupViews.add(taskDelayView);
        popupViews.add(taskFinishView);


        //add item click event
        taskTypeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                taskTypeAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[0] : taskTypeDictList.get(position).getDictName());
                mDropDownMenu.closeMenu();
                pageReqInfo.setTaskType(taskTypeDictList.get(position).getDictKey());
                queryTaskDealList(1, Constants.DEFAULT_PAGE_SIZE);
            }
        });

        taskStatusView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                taskStatusAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[1] : taskStatusDictList.get(position).getDictName());
                mDropDownMenu.closeMenu();
                pageReqInfo.setTaskStatus(taskStatusDictList.get(position).getDictKey());
                queryTaskDealList(1, Constants.DEFAULT_PAGE_SIZE);
            }
        });

        taskDelayView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                taskDelayAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[2] : taskDealyDictList.get(position).getDictName());
                mDropDownMenu.closeMenu();
                pageReqInfo.setTaskLeaveType(taskDealyDictList.get(position).getDictKey());
                queryTaskDealList(1, Constants.DEFAULT_PAGE_SIZE);
            }
        });

        taskFinishView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                taskFinishAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[3] : taskFinishDictList.get(position).getDictName());
                mDropDownMenu.closeMenu();
                pageReqInfo.setTaskFinishType(taskFinishDictList.get(position).getDictKey());
                queryTaskDealList(1, Constants.DEFAULT_PAGE_SIZE);
            }
        });

        //init dropdownview
        mDropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews);

        for(int i = 0;i < taskStatusDictList.size();i++){
            DictDetailInfo detailInfo = taskStatusDictList.get(i);
            if(detailInfo == null){
                continue;
            }
            String dictName = detailInfo.getDictKey();
            if(!TextUtils.isEmpty(dictName)&&dictName.equals("23")){
                // 默认为已分配
                taskStatusAdapter.setCheckItem(i);
                mDropDownMenu.setTabText(detailInfo.getDictName(),2);
                break;
            }
        }
    }

    private List<DictDetailInfo> getDictListByType(String type){
        List<DictDetailInfo> tmpDictList = new ArrayList<DictDetailInfo>();
        tmpDictList.add(new DictDetailInfo("-1","不限"));
        Map<String,String> visitTypeDict = DictionaryUtil.getInstance().getOtherDictMap(type);
        if(visitTypeDict!=null && visitTypeDict.size() > 0){
            for(Map.Entry<String,String> entry:visitTypeDict.entrySet()){
                tmpDictList.add(new DictDetailInfo(entry.getKey(),entry.getValue()));
            }
        }
        return tmpDictList;
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MesssageEventBus(MessageEvent event){
        if(Constants.MessageType.TASK_DEAL.equals(event.type) || Constants.MessageType.TASK_DEAL_STAUTS_UPDATE.equals(event.type)
                || Constants.MessageType.TASK_DEAL_REJUCT.equals(event.type)){
            // 下拉的时候数据重置
            queryTaskDealList(1, Constants.DEFAULT_PAGE_SIZE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(DealListActivity.this);// 2 解注册
    }
}
