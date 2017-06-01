package com.lakala.elive.task.activity;

import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.lakala.elive.beans.PageModel;
import com.lakala.elive.beans.TaskInfo;
import com.lakala.elive.common.dropdownmenu.GirdDropDownAdapter;
import com.lakala.elive.common.dropdownmenu.ListDropDownAdapter;
import com.lakala.elive.common.net.DotAndorderReq;
import com.lakala.elive.common.net.DotAndorderResp;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.PageReqInfo;
import com.lakala.elive.common.utils.UiUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.common.widget.SearchView;
import com.lakala.elive.market.activity.TaskDetailActivity;
import com.lakala.elive.task.adapter.SelectTaskListAdapter;
import com.lakala.elive.task.common.TaskBaseActivity;
import com.yyydjk.library.DropDownMenu;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gaofeng on 2017/3/24.
 * 工单选择界面
 */

public class SelectTasksActivity extends TaskBaseActivity implements SearchView.SearchViewListener {

    //下拉刷新页面
    private PullToRefreshListView mPullToRefreshListView;
    private SelectTaskListAdapter mTaskListViewAdpter;
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
    private Button commit;
//    private int entrance;

    private DropDownMenu mDropDownMenu;
    private int constellationPosition = 0;
    private List<View> popupViews = new ArrayList<>();
    private ListDropDownAdapter taskTypeAdapter;
    private ListDropDownAdapter taskStatusAdapter;
    private ListDropDownAdapter taskDelayAdapter;
    private ListDropDownAdapter taskFinishAdapter;

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_select_tasks_layout);
    }

    @Override
    protected void bindView() {
        //初始化查询控件
        searchView = (SearchView) findViewById(R.id.main_search_layout);
        commit = (Button) findViewById(R.id.commit);
        //查询结果title
        rlSearchTitle = (RelativeLayout) findViewById(R.id.rl_search_title);
        tvSearchRet = (TextView) findViewById(R.id.tv_search_ret);
        ivRemoveBtn = (ImageButton) findViewById(R.id.iv_remove_btn);

        tvTitleName = (TextView) findViewById(R.id.tv_title_name);

        ivRemoveBtn.setOnClickListener(this);
        rlSearchTitle.setVisibility(View.GONE);

        //综合过滤条件
        mDropDownMenu = (DropDownMenu)findViewById(R.id.dropDownMenu);

        tvTitleName.setText("外勤工单列表");
//        entrance = getIntent().getIntExtra("entrance", -1);
        initDealTaskList();
    }

    @Override
    protected void bindEvent() {
        //设置监听
        searchView.setSearchViewListener(this);
        commit.setOnClickListener(this);
        initSearchFilterView();
    }

    @Override
    protected void bindData() {
        queryTaskDealList(1, Constants.DEFAULT_PAGE_SIZE);
        commit.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onResume() {
        super.onResume();
//        queryTaskDealList(1, Constants.DEFAULT_PAGE_SIZE);
    }

    /**
     * 工单列表分页查询
     *
     * @param pageNo
     * @param pageSize
     */
    private void queryTaskDealList(int pageNo, int pageSize) {
        if (pageNo == 1) {
            showProgressDialog("工单列表加载中.....");
            if (mTaskInfoList != null && mTaskInfoList.size() > 0) {
                mTaskInfoList.clear();
            }
        }
        if (!searchView.getSearchMode()) {
            pageReqInfo.setSearchCode(""); //不是查询模式综合查询条件消除
        }

        pageReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        pageReqInfo.setPageNo(pageNo);
        pageReqInfo.setPageSize(pageSize);
        NetAPI.queryTaskDealList(this, this, pageReqInfo);
    }

    private void initDealTaskList() {
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_task_list);
        mPullToRefreshListView.setEmptyView(findViewById(R.id.lv_task_empty));
        /*
         * 设置PullToRefresh刷新模式
         * BOTH:上拉刷新和下拉刷新都支持
         * DISABLED：禁用上拉下拉刷新
         * PULL_FROM_START:仅支持下拉刷新（默认）
         * PULL_FROM_END：仅支持上拉刷新
         * MANUAL_REFRESH_ONLY：只允许手动触发
        * */
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        ILoadingLayout endLabelsr = mPullToRefreshListView.getLoadingLayoutProxy(false, true);
        endLabelsr.setLastUpdatedLabel("正在加载");// 刷新时
        endLabelsr.setReleaseLabel("松开后加载更多工单任务信息");// 下来达到一定距离时，显示的提示

        mPullToRefreshListView.setOnRefreshListener(
                new PullToRefreshBase.OnRefreshListener<ListView>() {

                    @Override
                    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                        String label = DateUtils.formatDateTime(
                                SelectTasksActivity.this,
                                System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME
                                        | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL);
                        if (mPageNo + 1 <= mPageCnt) {
                            queryTaskDealList(mPageNo + 1, Constants.DEFAULT_PAGE_SIZE);
                        } else {
                            Utils.showToast(SelectTasksActivity.this, "没有数据了!");
                            new SelectTasksActivity.FinishRefresh().execute();
                        }
                    }
                }
        );

        mTaskListView = mPullToRefreshListView.getRefreshableView();

        //设置点击事件响应处理
        mTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(SelectTasksActivity.this,"单项选择被点击:" + position,Toast.LENGTH_SHORT).show();
                TaskInfo taskInfo = mTaskInfoList.get(position - 1);
                UiUtils.startActivityWithExObj(SelectTasksActivity.this, TaskDetailActivity.class, Constants.EXTRAS_TASK_DEAL_INFO, taskInfo);
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
            case NetAPI.ACTION_ADD_DOT_AND_ORDER:
                DotAndorderResp dotAndorderResp = (DotAndorderResp) obj;
                Toast.makeText(this, dotAndorderResp.getMessage(), Toast.LENGTH_SHORT).show();
                setResult(100);
                finish();
                EventBus.getDefault().post("refresh");
                break;
            default:
                break;
        }
    }

    private void handlerPageShopInfo(PageModel<TaskInfo> page) {
        if(page == null){
            page = new PageModel<TaskInfo>();
        }
        if(page.getRows() == null){
            page.setRows(new ArrayList<TaskInfo>());
        }
        try {
            mTaskInfoList.addAll(page.getRows());
            mPageNo = page.getPageNo();
            mPageCnt = page.getTotalPage();

            if (mTaskListViewAdpter == null) {
                mTaskListViewAdpter = new SelectTaskListAdapter(SelectTasksActivity.this, mTaskInfoList, 0);
                mTaskListView.setAdapter(mTaskListViewAdpter);
            } else {
                mTaskListViewAdpter.setTaskInfoList(mTaskInfoList);
                mTaskListViewAdpter.notifyDataSetChanged();
            }

            new SelectTasksActivity.FinishRefresh().execute();
            mPullToRefreshListView.getRefreshableView().setSelection(mPullToRefreshListView.getRefreshableView().getCount() - 2 - (10 * (mTaskInfoList.size() - 1)));

            rlSearchTitle.setVisibility(View.VISIBLE);
            tvSearchRet.setText("查询出：" + page.getRecordCount() + " 个工单！已加载：" + mTaskInfoList.size() + " 个工单！");

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
                Utils.showToast(this, "获取失败：" + statusCode + "!");
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
                rlSearchTitle.setVisibility(View.GONE);
                break;
            case R.id.commit:
                commitPoint();
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
        queryTaskDealList(mPageNo, Constants.DEFAULT_PAGE_SIZE);
    }

    @Override
    public void cancelSearch() {
        mPageNo = 1;
        queryTaskDealList(mPageNo, Constants.DEFAULT_PAGE_SIZE);
    }


    private void commitPoint() {

        DotAndorderReq dotAndorderReq = new DotAndorderReq();
        List<DotAndorderReq.UserOrderTask> userOrderTaskList = new ArrayList<>();
        dotAndorderReq.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        dotAndorderReq.setUserId(mSession.getUserLoginInfo().getUserId());
        dotAndorderReq.setOrderType("2");
        dotAndorderReq.setTaskDateStr("");

        for (int i = 0; i < mTaskInfoList.size(); i++) {
            if (mTaskInfoList.get(i).isChecked()) {
                DotAndorderReq.UserOrderTask userOrderTask = dotAndorderReq.new UserOrderTask();
                userOrderTask.setShopNo("");
                userOrderTask.setOutTaskId(mTaskInfoList.get(i).getTaskId());
                userOrderTaskList.add(userOrderTask);
            }
        }
        dotAndorderReq.setUserOrderTaskList(userOrderTaskList);
        showProgressDialog("添加中");
        NetAPI.addDotAndorder(this, this, dotAndorderReq);
    }


    private void initSearchFilterView() {
        //init task Type menu
        final ListView taskTypeView = new ListView(this);
        final List<DictDetailInfo> taskTypeDictList = mSession.getSeltMapByKey(Constants.TASK_TYPE);
        taskTypeAdapter = new ListDropDownAdapter(this,taskTypeDictList);
        taskTypeView.setDividerHeight(0);
        taskTypeView.setAdapter(taskTypeAdapter);

        //init task status menu
        final ListView taskStatusView = new ListView(this);
        final List<DictDetailInfo> taskStatusDictList = mSession.getSeltMapByKey(Constants.TASK_STATUS);
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
}
