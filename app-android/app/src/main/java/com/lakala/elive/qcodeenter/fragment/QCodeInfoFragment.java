package com.lakala.elive.qcodeenter.fragment;

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
import com.lakala.elive.common.net.resp.MerApplyDetailsResp;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.common.widget.LazyFragment;
import com.lakala.elive.merapply.activity.MerApplyDetailsActivity;
import com.lakala.elive.merapply.interfaces.MyAdapterItemClickListener;
import com.lakala.elive.preenterpiece.response.PreEnPieceListResponse;
import com.lakala.elive.preenterpiece.swapmenurecyleview.adapter.SwapRefreshRecyclerView;
import com.lakala.elive.preenterpiece.swapmenurecyleview.bean.SwipeMenu;
import com.lakala.elive.preenterpiece.swapmenurecyleview.view.DividerItemDecoration;
import com.lakala.elive.preenterpiece.swapmenurecyleview.view.SwapRecyclerView;
import com.lakala.elive.preenterpiece.swapmenurecyleview.view.SwipeMenuView;
import com.lakala.elive.qcodeenter.QCodeBindActivity;
import com.lakala.elive.qcodeenter.QCodeDetailActivity;
import com.lakala.elive.qcodeenter.adapter.QCodeInfoListSwapAdapter;
import com.lakala.elive.qcodeenter.request.QCodeBindRequ;
import com.lakala.elive.qcodeenter.request.QCodeListRequ;
import com.lakala.elive.qcodeenter.response.QCodeListResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Q码信息的Fragent
 */
public class QCodeInfoFragment extends LazyFragment {
    private String TAG = getClass().getSimpleName();

    private Button btnNext;
    private SwapRecyclerView recyclerView;
    private SwapRefreshRecyclerView refresh_recycler_view;
    private QCodeInfoListSwapAdapter adapter;

    private TextView tvEmptyTitle;//数据为空的时候显示的界面

    private static final String PROCESS = "process";

    private int process;
    private String mechanNo;
    private QCodeListRequ qCodeListRequ;
    private QCodeListResponse qCodeListResponse;

    private boolean isLoad;

    private List<PreEnPieceListResponse.ContentBean.PartnerApplyInfo> mData;

    private MerApplyDetailsResp.ContentBean contentBean;

    public static SwipeMenuView.OnMenuItemClickListener mOnSwipeItemClickListener;

    public static QCodeInfoFragment newInstance(MerApplyDetailsResp.ContentBean contentBean) {
        QCodeInfoFragment fragment = new QCodeInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MerApplyDetailsActivity.CONTENT_BEAN, contentBean);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int setContentViewId() {
        return R.layout.fragment_qcodeinfo;
    }

    @Override
    protected void bindView() {
        btnNext = findView(R.id.btn_next);
        tvEmptyTitle = findView(R.id.tv_empty_title);
        refresh_recycler_view = findView(R.id.refresh_recycler_qcode_view);
        refresh_recycler_view.setScrollingWhileRefreshingEnabled(true);
        recyclerView = refresh_recycler_view.getRefreshableView();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(_activity, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setLayoutManager(new LinearLayoutManager(_activity));
        adapter = new QCodeInfoListSwapAdapter(R.layout.item_qcodeinfo_recycleview, getActivity());
        recyclerView.setAdapter(adapter);
    }

    private String mechanName;
    private String content;
    private String accountNo;

    @Override
    protected void initData() {
        mData = new ArrayList<>();
        showProgressDialog("获取中...");
        Bundle bundle = getArguments();
        if (null != bundle) {
            contentBean = (MerApplyDetailsResp.ContentBean) bundle.getSerializable(MerApplyDetailsActivity.CONTENT_BEAN);
            if (contentBean != null) {
                MerApplyDetailsResp.ContentBean.MerOpenInfoBean merOpenInfo = contentBean.getMerOpenInfo();
                if (merOpenInfo != null) {
                    mechanNo = getNoEmptyString(merOpenInfo.getMerchantId());
                    mechanName = getNoEmptyString(merOpenInfo.getMerchantName());
                    content = getNoEmptyString(merOpenInfo.getContact());
                    accountNo = getNoEmptyString(merOpenInfo.getAccountNo());
                } else {
                    Log.e(TAG, "merOpenInfo为空");
                }
            } else {
                Log.e(TAG, "contentBean为空");
            }
        }
        qCodeListRequ = new QCodeListRequ();
        requestData(mechanNo);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * 请求列表网络数据
     */
    private void requestData(String mechanNo) {
        qCodeListRequ.setMerchantNo(mechanNo);
        qCodeListRequ.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        NetAPI.qCodeListRequest(_activity, this, qCodeListRequ);
    }

    /**
     * Q码绑定删除
     */
    private void delect(String codeID) {
        QCodeBindRequ    requ=new QCodeBindRequ();
        requ.setCodeId(codeID);
        requ.setMerchantNo(mechanNo);
        requ.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        NetAPI.qCodeDelectRequest(_activity, this, requ);
    }

    @Override
    protected void bindEvent() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//
                Intent intent = new Intent(getActivity(), QCodeBindActivity.class);
                intent.putExtra("MERCHANNO", mechanNo);
                intent.putExtra("MERCHANNAME", mechanName);
                intent.putExtra("CONTENT", content);
                intent.putExtra("ACCONUTNO", accountNo);
                intent.putExtra("QCODE", accountNo);
                startActivity(intent);
            }
        });
        refresh_recycler_view.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<SwapRecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<SwapRecyclerView> refreshView) {
                refresh_recycler_view.setMode(PullToRefreshBase.Mode.BOTH);
                requestData(mechanNo);
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<SwapRecyclerView> refreshView) {
                requestData(mechanNo);
            }
        });
        adapter.setOnItemClickListener(new MyAdapterItemClickListener() {
            @Override
            public void onAdapterItemClick(View view, int position) {//Q码绑定的列表的自条目点击事件
                String  statue=adapter.getItemData(position).getStatus();
                if("BIND_SUCCESS".equals(statue)) {//绑定成功
                    Intent intent = new Intent(_activity, QCodeDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("QCODEBEAN", adapter.getItemData(position));
                    intent.putExtras(bundle);
                    intent.putExtra(PROCESS, process);
                    startActivity(intent);
                }else{
                    Utils.showToast(_activity, "未绑定成功");
                }
            }
        });
    }


    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        refresh_recycler_view.onRefreshComplete();
        if (method == NetAPI.qcodeApplyList) {
            qCodeListResponse = (QCodeListResponse) obj;
            refresh_recycler_view.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            if(qCodeListResponse!=null&&qCodeListResponse.getContent()!=null&&qCodeListResponse.getContent().getQcodes()!=null){
                adapter.setNewData(qCodeListResponse.getContent().getQcodes());
            }
            //判空
            if (null != qCodeListResponse.getContent() && qCodeListResponse.getContent().getQcodes().size() < 1) {
                tvEmptyTitle.setVisibility(View.VISIBLE);
            } else {
                tvEmptyTitle.setVisibility(View.GONE);
            }
        } else if (method == NetAPI.qCodeApplyUnBind) {//预进件删除成功
            Utils.showToast(_activity, "删除成功");
            //刷新数据
            requestData(mechanNo);
        }
        mOnSwipeItemClickListener = new SwipeMenuView.OnMenuItemClickListener() {//子条目的滑动删除，在activity调用
            @Override
            public void onMenuItemClick(int pos, SwipeMenu menu, int index) {
                String qcode = adapter.getItemData(pos).getQCode();
                String codeID = adapter.getItemData(pos).getCodeId();
                String  statue=adapter.getItemData(pos).getStatus();
                Log.e("qcodeqcode",  ""+qcode);
                Log.e("OnMenuItemClickListener", "第" + pos + "个" +"codeID"+ codeID);
                if("BIND_FAIL".equals(statue)){//绑定失败的
                    delect(codeID);//调用删除接口
                }else{
                    Utils.showToast(_activity, "只有绑定失败的才可以删除");
                }
            }
        };
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        refresh_recycler_view.onRefreshComplete();
        Utils.showToast(_activity, statusCode);
    }

}
