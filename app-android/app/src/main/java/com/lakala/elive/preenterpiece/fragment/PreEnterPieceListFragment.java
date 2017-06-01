package com.lakala.elive.preenterpiece.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.lakala.elive.R;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.common.widget.LazyFragment;
import com.lakala.elive.merapply.interfaces.MyAdapterItemClickListener;
import com.lakala.elive.preenterpiece.PreEntContactInfoActivity;
import com.lakala.elive.preenterpiece.PreEnterMerchanDetailsActivity;
import com.lakala.elive.preenterpiece.PreEnterPieceListActivity;
import com.lakala.elive.preenterpiece.request.PreEnPieceDetailRequ;
import com.lakala.elive.preenterpiece.request.PreEnPieceListRequ;
import com.lakala.elive.preenterpiece.response.PreEnPieceListResponse;
import com.lakala.elive.preenterpiece.swapmenurecyleview.view.DividerItemDecoration;
import com.lakala.elive.preenterpiece.swapmenurecyleview.view.SwapRecyclerView;
import com.lakala.elive.preenterpiece.swapmenurecyleview.bean.SwipeMenu;
import com.lakala.elive.preenterpiece.adapter.PreEnListSwapAdapter;
import com.lakala.elive.preenterpiece.swapmenurecyleview.adapter.SwapRefreshRecyclerView;
import com.lakala.elive.preenterpiece.swapmenurecyleview.view.SwipeMenuView;

import java.util.ArrayList;
import java.util.List;

/**
 * 合作商预进件的Fragent
 */
public class PreEnterPieceListFragment extends LazyFragment {

    private Button btnNext;
    private SwapRecyclerView recyclerView;
    public SwapRefreshRecyclerView refresh_recycler_view;
    private PreEnListSwapAdapter adapter;

    private TextView tvEmptyTitle;//数据为空的时候显示的界面

    private int mPageNo = 1; //分页查询当前页码
    private int process = 1;//进件状态
    private int pageSize = 8;
    private int totalPage;
    public static final String PROCESS = "process";

    private String statue;//0：待录入 1：已提交、2：处理中、3：处理成功、4：处理失败
    private PreEnPieceListRequ preEnPieceListRequ;

    //预进件删除
    private PreEnPieceDetailRequ preEnPieceDelectRequ;

    private boolean isLoad;

    List<PreEnPieceListResponse.ContentBean.PartnerApplyInfo> mData;

    public static PreEnterPieceListFragment newInstance(int process) {
        PreEnterPieceListFragment fragment = new PreEnterPieceListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PROCESS, process);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int setContentViewId() {
        return R.layout.fragment_cooperpreenter;
    }

    @Override
    protected void bindView() {
        btnNext = findView(R.id.btn_next);
        tvEmptyTitle = findView(R.id.tv_empty_title);
        refresh_recycler_view = findView(R.id.refresh_recycler_view);
        refresh_recycler_view.setScrollingWhileRefreshingEnabled(true);
        recyclerView = refresh_recycler_view.getRefreshableView();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(_activity, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setLayoutManager(new LinearLayoutManager(_activity));

        adapter = new PreEnListSwapAdapter(R.layout.item_coorperpreenter_recycleview, getActivity());
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void initData() {
        mData = new ArrayList<>();
        showProgressDialog("获取中...");
        Bundle bundle = getArguments();
        if (null != bundle) {
            process = bundle.getInt(PROCESS);
        }
        statue = "0";
        mPageNo = 1;
        preEnPieceListRequ = new PreEnPieceListRequ();
        requestData(statue);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        statue = "0";
    }

    /**
     * 请求网络数据
     */
    private void requestData(String statueStr) {
        preEnPieceListRequ.setPageSize(pageSize);
        preEnPieceListRequ.setPageNo(mPageNo);
        String str = PreEnterPieceListActivity.getmStatues();
        if (str != null) {////设置切换的界面对应的参数
            statueStr = str;
        }
        preEnPieceListRequ.setSearchCode(((PreEnterPieceListActivity) getActivity()).getSearch());
        Log.e("%%%%%%%%%%%%", statueStr + "requestData");
        preEnPieceListRequ.setStatus(statueStr);
        preEnPieceListRequ.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        NetAPI.preEnterPieListRequest(_activity, this, preEnPieceListRequ);
    }

    /**
     * 预进件删除
     */
    private void delect(String appplyId) {
        preEnPieceDelectRequ = new PreEnPieceDetailRequ();
        preEnPieceDelectRequ.setApplyId(appplyId);
        preEnPieceDelectRequ.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        NetAPI.preEnterPieListDeleRequest(_activity, this, preEnPieceDelectRequ);
    }

    @Override
    protected void bindEvent() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//点击新建合作商预进件
                Intent intent = new Intent(getActivity(), PreEntContactInfoActivity.class);
                startActivity(intent);
            }
        });
        refresh_recycler_view.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<SwapRecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<SwapRecyclerView> refreshView) {
                refresh_recycler_view.setMode(PullToRefreshBase.Mode.BOTH);
                mPageNo = 1;
                isLoad = false;
                requestData(statue);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<SwapRecyclerView> refreshView) {
                isLoad = true;
                requestData(statue);
            }
        });
        adapter.setOnItemClickListener(new MyAdapterItemClickListener() {
            @Override
            public void onAdapterItemClick(View view, int position) {//合作方预进件的列表的自条目点击事件
                startActivity(new Intent(_activity, PreEnterMerchanDetailsActivity.class)
                        .putExtra("APPLYID", adapter.getItemData(position).getApplyId())
                        .putExtra(PROCESS, process));
            }
        });
    }


    public SwipeMenuView.OnMenuItemClickListener mOnSwipeItemClickListener = new SwipeMenuView.OnMenuItemClickListener() {//子条目的滑动
        @Override
        public void onMenuItemClick(int pos, SwipeMenu menu, int index) {
            String applyID = adapter.getItemData(pos).getApplyId();
            Log.e("OnMenuItemClickListener", "第" + pos + "个" + applyID);
            delect(applyID);//调用删除接口
        }
    };


    /**
     * 搜索
     *
     * @param search
     */
    public void setSearch(String search) {
        mPageNo = 1;
        isLoad = false;
        preEnPieceListRequ.setSearchCode(search);
        preEnPieceListRequ.setPageNo(mPageNo);
        refresh_recycler_view.setMode(PullToRefreshBase.Mode.BOTH);
        showProgressDialog("获取中...");
        requestData(statue);
    }

    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        refresh_recycler_view.onRefreshComplete();
        if (method == NetAPI.ELIVE_PARTNER_APPLY_001) {
            Log.e("PARTNER_APPLY_001的数据", obj + "");
            PreEnPieceListResponse preEnPieceListResponse = (PreEnPieceListResponse) obj;
            totalPage = preEnPieceListResponse.getContent().getTotalPage();
            if (mPageNo == totalPage) {
                refresh_recycler_view.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
            if (mPageNo < totalPage) {
                mPageNo++;
            }
            if (isLoad) {
                if (mPageNo == totalPage || preEnPieceListResponse.getContent().getRows().size() == 0) {
                    Utils.showToast(_activity, "没有更多数据了");
                    return;
                }

                adapter.addData(preEnPieceListResponse.getContent().getRows());
            } else {
                adapter.setNewData(preEnPieceListResponse.getContent().getRows());
            }
            //判空
            if (null != preEnPieceListResponse.getContent() && preEnPieceListResponse.getContent().getRows().size() < 1 && mPageNo == 1) {
                tvEmptyTitle.setVisibility(View.VISIBLE);
            } else {
                tvEmptyTitle.setVisibility(View.GONE);
            }
        } else if (method == NetAPI.ELIVE_PARTNER_APPLY_005) {//预进件删除成功
            Utils.showToast(_activity, "删除成功");
            //刷新数据,回到第一页
            mPageNo = 1;
            isLoad = false;
            requestData(statue);
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        refresh_recycler_view.onRefreshComplete();
        Utils.showToast(_activity, statusCode);
    }

}
