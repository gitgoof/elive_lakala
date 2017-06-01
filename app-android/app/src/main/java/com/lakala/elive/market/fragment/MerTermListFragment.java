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
import com.lakala.elive.beans.TermInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.MerShopReqInfo;
import com.lakala.elive.common.utils.UiUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.market.activity.TermDetailActivity;
import com.lakala.elive.market.adapter.TermListViewAdpter;
import com.lakala.elive.market.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 商户网点基础信息
 * @author hongzhiliang
 *
 */
public class MerTermListFragment extends BaseFragment{

    private MerShopReqInfo merShopReqInfo = new MerShopReqInfo(); //请求查询接口

	private final String TAG = MerTermListFragment.class.getSimpleName();

    List<TermInfo> merTermListInfo = new ArrayList<TermInfo>();
    MerShopInfo merchantInfo = null; //商户详情信息
    //下拉刷新页面
    private PullToRefreshListView mPullToRefreshListView;
    private TermListViewAdpter mTermListViewAdpter;
    private ListView mTermListView;

    public MerTermListFragment() {

    }

    @SuppressLint("ValidFragment")
    public MerTermListFragment(MerShopInfo merchantInfo,List<TermInfo> termList) {
        this.merchantInfo = merchantInfo;
    	this.merTermListInfo = termList;
    }

    @Override
    protected void setViewLayoutId() {
        viewLayoutId = R.layout.fragment_mer_term_list;
    }

    @Override
    protected void bindViewIds() {
        initTermRefreshListView();
    }

    @Override
    protected void initData() {
        handleTermListInfo();
        refershUi();
    }

    private void initTermRefreshListView() {
        mPullToRefreshListView = (PullToRefreshListView) mView.findViewById(R.id.pull_to_refresh_term_list);
        mPullToRefreshListView.setEmptyView(mView.findViewById(R.id.lv_term_empty));
        /*
         * 设置PullToRefresh刷新模式
         * BOTH:上拉刷新和下拉刷新都支持
         * DISABLED：禁用上拉下拉刷新
         * PULL_FROM_START:仅支持下拉刷新（默认）
         * PULL_FROM_END：仅支持上拉刷新
         * MANUAL_REFRESH_ONLY：只允许手动触发
        * */
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        ILoadingLayout endLabelsr = mPullToRefreshListView.getLoadingLayoutProxy(false, true);
//       endLabelsr.setPullLabel("上拉可以刷新");// 刚下拉时，显示的提示
        endLabelsr.setLastUpdatedLabel("正在加载");// 刷新时
        endLabelsr.setReleaseLabel("松开后加载更多网点信息");// 下来达到一定距离时，显示的提示

        mPullToRefreshListView.setOnRefreshListener(
                new PullToRefreshBase.OnRefreshListener<ListView>() {
                    @Override
                    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                        String label = DateUtils.formatDateTime(
                                getActivity(),
                                System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME
                                        | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL);
                        new FinishRefresh().execute();
                    }
                }
        );

        //--------------------------------------------------------//
        mTermListView = mPullToRefreshListView.getRefreshableView();

        //设置点击事件响应处理
        mTermListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                UiUtils.startActivityWithExObj(getActivity(),TermDetailActivity.class,
                        Constants.EXTRAS_TERM_INFO,merTermListInfo.get(position -1));

            }
        });

    }

    private void handleTermListInfo() {
        try {
            if(merTermListInfo!=null){
                mTermListViewAdpter = new TermListViewAdpter(getActivity(),merTermListInfo);
                mTermListView.setAdapter(mTermListViewAdpter);
                new FinishRefresh().execute();
            } else {
//                Utils.showToast(getActivity(), "加载失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    public void onSuccess(int method, Object obj) {
        switch (method) {
            case NetAPI.ACTION_SHOP_TERM_LIST:
                merTermListInfo = (List<TermInfo>)obj;
                handleTermListInfo();
                break;
        }
        super.onSuccess(method, obj);
    }

    @Override
    public void onError(int method, String statusCode) {
        switch (method) {
            case NetAPI.ACTION_SHOP_TERM_LIST:
                break;
        }
    }

    @Override
    public void refershUi() {
        merShopReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        merShopReqInfo.setShopNo(merchantInfo.getShopNo());
        NetAPI.queryShopTermList(getActivity(), this, merShopReqInfo);
    }

}
