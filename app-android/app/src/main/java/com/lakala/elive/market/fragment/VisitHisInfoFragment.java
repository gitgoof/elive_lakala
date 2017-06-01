package com.lakala.elive.market.fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.beans.MerShopInfo;
import com.lakala.elive.beans.PageModel;
import com.lakala.elive.beans.ShopVisitInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.PageReqInfo;
import com.lakala.elive.common.utils.UiUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.market.activity.DailyWorkListActivity;
import com.lakala.elive.market.activity.TaskVisitDetailActivity;
import com.lakala.elive.market.activity.VisitDetailActivity;
import com.lakala.elive.market.adapter.MerVisitListViewAdpter;
import com.lakala.elive.market.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 商户网点基础信息
 * @author hongzhiliang
 *
 */
public class VisitHisInfoFragment extends BaseFragment {

	private final String TAG = VisitHisInfoFragment.class.getSimpleName();

    private MerShopInfo merchantInfo = null; //商户详情信息
    private int mPageNo = 1; //分页查询当前页码
    private int mPageCnt = 0; //分页页数

    //下拉刷新页面
    private PullToRefreshListView mPullToRefreshListView;

    private MerVisitListViewAdpter mVisitListViewAdpter;

    private ListView mVisitListView;

    //拜访列表
    private List<ShopVisitInfo> merVisitInfoList = new ArrayList<ShopVisitInfo>();//初始化ListView

    PageReqInfo pageReqInfo = new PageReqInfo();

    public VisitHisInfoFragment() {

    }

    @SuppressLint("ValidFragment")
    public VisitHisInfoFragment(MerShopInfo merchantInfo) {
    	this.merchantInfo = merchantInfo;
    }

    @Override
    protected void setViewLayoutId() {
        viewLayoutId = R.layout.fragment_mer_visit_list;
    }

    @Override
    protected void bindViewIds() {
        initMerVisitList();
    }

    @Override
    protected void initData() {
        //请求拜访记录历史
        queryMerVisitList(mPageNo, Constants.DEFAULT_PAGE_SIZE);
    }

    /**
     *
     * 商户签到记录分页查询
     *
     * @param pageNo
     * @param pageSize
     *
     */
    private void queryMerVisitList(int pageNo,int pageSize) {
        if (pageNo == 1) {
            if (merVisitInfoList != null && merVisitInfoList.size() > 0) {
                merVisitInfoList.clear();
            }
        }
        pageReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        if(merchantInfo.getShopNo()!=null){
            pageReqInfo.setShopNo(merchantInfo.getShopNo());
        }
        if(merchantInfo.getTaskId() != null && !"".equals(merchantInfo.getTaskId())){
            pageReqInfo.setTaskId(merchantInfo.getTaskId());
        }
        pageReqInfo.setPageNo(pageNo);
        pageReqInfo.setPageSize(pageSize);


        NetAPI.queryMerVisitList(getActivity(),this,pageReqInfo);
    }


    private void initMerVisitList() {
        mPullToRefreshListView = (PullToRefreshListView) mView.findViewById(R.id.pull_to_refresh_visit_list);
        mPullToRefreshListView.setEmptyView(mView.findViewById(R.id.lv_visit_empty));
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
//      endLabelsr.setPullLabel("上拉可以刷新");// 刚下拉时，显示的提示
        endLabelsr.setLastUpdatedLabel("正在加载");// 刷新时
        endLabelsr.setReleaseLabel("松开后加载更多签到记录");// 下来达到一定距离时，显示的提示

        mPullToRefreshListView.setOnRefreshListener(
                new PullToRefreshBase.OnRefreshListener2<ListView>() {
                    // 下拉Pulling Down
                    @Override
                    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                        // 下拉的时候数据重置
                        queryMerVisitList(1, Constants.DEFAULT_PAGE_SIZE);
                    }

                    // 上拉Pulling Up
                    @Override
                    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                        String label = DateUtils.formatDateTime(
                                getActivity(),
                                System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME
                                        | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL);
                        if(mPageNo +1 <= mPageCnt){
                            queryMerVisitList(mPageNo + 1, Constants.DEFAULT_PAGE_SIZE);
                        }else{
                            Utils.showToast(getActivity(), "没有数据了!" );
                            new FinishRefresh().execute();
                        }
                    }
                }
        );

        mVisitListView = mPullToRefreshListView.getRefreshableView();
        mVisitListViewAdpter = new MerVisitListViewAdpter(getActivity(),merVisitInfoList);
        mVisitListView.setAdapter(mVisitListViewAdpter);

        //设置点击事件响应处理
        mVisitListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShopVisitInfo  shopVisitInfo = merVisitInfoList.get(position -1);
                if(shopVisitInfo.getTaskId()==null || "".equals(shopVisitInfo.getTaskId())){
                    UiUtils.startActivityWithExObj(getActivity(), VisitDetailActivity.class,
                            Constants.EXTRAS_MER_VISIT_INFO, shopVisitInfo);
                }else{
                    //任务的签到详情页面
                    UiUtils.startActivityWithExObj(getActivity(), TaskVisitDetailActivity.class,
                            Constants.EXTRAS_MER_VISIT_INFO, shopVisitInfo);
                }
            }

        });


    }

    private void handlerPageVisitInfo(PageModel<ShopVisitInfo> page) {
        try {
            merVisitInfoList.addAll(page.getRows());
            mPageNo = page.getPageNo();
            mPageCnt = page.getTotalPage();
            if(mVisitListViewAdpter == null){
                mVisitListViewAdpter = new MerVisitListViewAdpter(getActivity(),merVisitInfoList);
                mVisitListView.setAdapter(mVisitListViewAdpter);
            }else{
                mVisitListViewAdpter.setVisitInfoList(merVisitInfoList);
                mVisitListViewAdpter.notifyDataSetChanged();
            }
            new FinishRefresh().execute();
            mPullToRefreshListView.getRefreshableView().setSelection( mPullToRefreshListView.getRefreshableView().getCount() - 2- (10 * (merVisitInfoList.size() - 1)));
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showToast(getActivity(), "拜访记录加载失败!");
        }
        new FinishRefresh().execute();
    }

    @Override
    public void onSuccess(int method, Object obj) {
        switch (method) {
            case NetAPI.ACTION_VISIT_PAGE_LIST:
                closeProgressDialog();
                handlerPageVisitInfo((PageModel<ShopVisitInfo>) obj);
                break;
            default:
                break;
        }
    }


    @Override
    public void onError(int method, String statusCode) {
        switch (method) {
            case NetAPI.ACTION_VISIT_PAGE_LIST:
                closeProgressDialog();
                new FinishRefresh().execute();
                Utils.showToast(getActivity(), "签到记录加载失败："+ statusCode +"!" );
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
    public void refershUi() {
        queryMerVisitList(1,Constants.DEFAULT_PAGE_SIZE);
    }
}
