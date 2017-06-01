package com.lakala.elive.merapply.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.lakala.elive.R;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.MerApplyListReq;
import com.lakala.elive.common.net.resp.MyMerchantsListResp;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.common.widget.LazyFragment;
import com.lakala.elive.common.widget.RefreshRecyclerView;
import com.lakala.elive.merapply.activity.MerApplyDetailsActivity;
import com.lakala.elive.merapply.activity.MyMerchantsActivity;
import com.lakala.elive.merapply.adapter.MyMerchantsAdapter;
import com.lakala.elive.merapply.interfaces.MyAdapterItemClickListener;

/**
 * Created by wenhaogu on 2017/2/27.
 */

public class MyMerchantsFragment extends LazyFragment {

    private RefreshRecyclerView refresh_recycler_view;
    private RecyclerView recyclerView;
    private TextView tvEmptyTitle;
    private int pageSize = 10;

    private int mPageNo = 1; //分页查询当前页码
    private String process = "STORAGED";//进件状态
    public static final String PROCESS = "process";
    private MerApplyListReq merApplyListReq;
    private MyMerchantsAdapter adapter;
    private int totalPage;
    private boolean isLoad;


    public static MyMerchantsFragment newInstance(String process) {
        MyMerchantsFragment fragment = new MyMerchantsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PROCESS, process);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int setContentViewId() {
        return R.layout.fragment_my_merchants;
    }

    @Override
    protected void bindView() {
        refresh_recycler_view = findView(R.id.refresh_recycler_view);
        tvEmptyTitle = findView(R.id.tv_empty_title);
        recyclerView = refresh_recycler_view.getRefreshableView();
        recyclerView.setLayoutManager(new LinearLayoutManager(_activity));
        adapter = new MyMerchantsAdapter(R.layout.item_my_merchants);
        recyclerView.setAdapter(adapter);

        refresh_recycler_view.setScrollingWhileRefreshingEnabled(true);

    }

    @Override
    protected void bindEvent() {
        refresh_recycler_view.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                refresh_recycler_view.setMode(PullToRefreshBase.Mode.BOTH);
                mPageNo = 1;
                merApplyListReq.setPageNo(mPageNo);
                isLoad = false;
                NetAPI.merApplyListReq(_activity, MyMerchantsFragment.this, merApplyListReq);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                isLoad = true;
                merApplyListReq.setPageNo(mPageNo);
                NetAPI.merApplyListReq(_activity, MyMerchantsFragment.this, merApplyListReq);
            }
        });

        adapter.setOnItemClickListener(new MyAdapterItemClickListener() {
            @Override
            public void onAdapterItemClick(View view, int position) {
                startActivity(new Intent(_activity, MerApplyDetailsActivity.class)
                        .putExtra(MyMerchantsActivity.APPLYID_ID, adapter.getItemData(position).getApplyId())
                        .putExtra(PROCESS, process)
                        .putExtra("APPLYCHANNEL",adapter.getItemData(position).getApplyChannel()));
            }
        });


    }

    @Override
    protected void initData() {
        showProgressDialog("获取中...");
        Bundle bundle = getArguments();
        if (null != bundle) {
            process = bundle.getString(PROCESS);
        }
        merApplyListReq = new MerApplyListReq();
        merApplyListReq.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        merApplyListReq.setPageSize(pageSize);
        merApplyListReq.setPageNo(mPageNo);
        merApplyListReq.setProcess(process);
        NetAPI.merApplyListReq(_activity, this, merApplyListReq);
    }


    /**
     * 搜索
     *
     * @param search
     */
    public void setSearch(String search) {
        mPageNo = 1;
        isLoad = false;
        merApplyListReq.setSearchCode(search);
        merApplyListReq.setPageNo(mPageNo);
        refresh_recycler_view.setMode(PullToRefreshBase.Mode.BOTH);
        showProgressDialog("获取中...");
        NetAPI.merApplyListReq(_activity, this, merApplyListReq);
    }

    /**
     * 手动调用刷新
     */
    public void refresh() {
        showProgressDialog("获取中...");
        refresh_recycler_view.setMode(PullToRefreshBase.Mode.BOTH);
        mPageNo = 1;
        merApplyListReq.setPageNo(mPageNo);
        isLoad = false;
        NetAPI.merApplyListReq(_activity, this, merApplyListReq);
    }


    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        refresh_recycler_view.onRefreshComplete();
        if (method == NetAPI.ACTION_MER_APPLY_LIST) {
            MyMerchantsListResp myMerchantsListResp = (MyMerchantsListResp) obj;
            totalPage = myMerchantsListResp.getContent().getTotalPage();
            if (mPageNo == totalPage) {
                refresh_recycler_view.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
            if (mPageNo < totalPage) {
                mPageNo++;
            }

            if (isLoad) {
//                if (mPageNo == totalPage || myMerchantsListResp.getContent().getRows().size() == 0) {
//                    Utils.showToast(_activity, "没有更多数据了");
//                    return;
//                }
                adapter.addData(myMerchantsListResp.getContent().getRows());
            } else {
                adapter.setNewData(myMerchantsListResp.getContent().getRows());
            }
            //判空
            if (null != myMerchantsListResp.getContent() && myMerchantsListResp.getContent().getRows().size() < 1 && mPageNo == 1) {
                tvEmptyTitle.setVisibility(View.VISIBLE);
            } else {
                tvEmptyTitle.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        refresh_recycler_view.onRefreshComplete();
        if (method == NetAPI.ACTION_MER_APPLY_LIST) {
            Utils.showToast(_activity, statusCode);
        }
    }


}
