package com.lakala.elive.merapply.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.lakala.elive.R;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.MerApplyListReq;
import com.lakala.elive.common.net.resp.MyMerchantsListResp;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.common.widget.RefreshRecyclerView;
import com.lakala.elive.merapply.adapter.MyMerchantsAdapter;
import com.lakala.elive.merapply.interfaces.MyAdapterItemClickListener;
import com.lakala.elive.user.base.BaseActivity;

/**
 * 待录入列表
 * Created by gaofeng on 2017/4/12.
 */
public class WaitInputActivity extends BaseActivity {
    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_wait_input_layout);
    }
    private ImageView mImgBack;
    private RefreshRecyclerView mRecyclerView;
    private TextView mTvEmptyView;
    private MyMerchantsAdapter mMyMerchantsAdapter;
    @Override
    protected void bindView() {
        mImgBack = findView(R.id.btn_iv_back);
        TextView tvTitleName = findView(R.id.tv_title_name);

        tvTitleName.setText("待录入");
        mImgBack.setVisibility(View.VISIBLE);

        mRecyclerView = findView(R.id.recy_wait_input_listcontent);
        mTvEmptyView = findView(R.id.tv_wait_input_listcontent_empty);
        RecyclerView recyclerView = mRecyclerView.getRefreshableView();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMyMerchantsAdapter = new MyMerchantsAdapter(R.layout.item_my_merchants);
        recyclerView.setAdapter(mMyMerchantsAdapter);
        needRefresh = false;
    }
    private int totalPage;
    private boolean isLoad;
    @Override
    protected void bindEvent() {
        mImgBack.setOnClickListener(this);
        mRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                mRecyclerView.setMode(PullToRefreshBase.Mode.BOTH);
                mPageNo = 1;
                merApplyListReq.setPageNo(mPageNo);
                isLoad = false;
                NetAPI.merApplyListReq(WaitInputActivity.this, WaitInputActivity.this, merApplyListReq);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                isLoad = true;
                merApplyListReq.setPageNo(mPageNo);
                NetAPI.merApplyListReq(WaitInputActivity.this, WaitInputActivity.this, merApplyListReq);
            }
        });

        mMyMerchantsAdapter.setOnItemClickListener(new MyAdapterItemClickListener() {
            @Override
            public void onAdapterItemClick(View view, int position) {
                if(mMyMerchantsAdapter.getItemCount() <= 0){
                    return;
                }
                Intent intent = new Intent(WaitInputActivity.this, MerApplyDetailsActivity.class)
                        .putExtra(MyMerchantsActivity.APPLYID_ID, mMyMerchantsAdapter.getItemData(position).getApplyId())
                        .putExtra("process", "ENTER")
                        .putExtra("APPLYCHANNEL",mMyMerchantsAdapter.getItemData(position).getApplyChannel());
                startActivityForResult(intent,EDIT_CODE);
            }
        });
    }
    private final int EDIT_CODE = 0x22;
    private MerApplyListReq merApplyListReq;
    private int pageSize = 10;

    private int mPageNo = 1; //分页查询当前页码
    @Override
    protected void bindData() {
        Intent  intent=getIntent();
        String applychianl= intent.getStringExtra("APPLYCHNAL");
        String accountKind= intent.getStringExtra("ACCOUNTKIND");
        // 对私 58   对公 57
        if(!TextUtils.isEmpty(accountKind)&&!TextUtils.isEmpty(applychianl)){
            if(applychianl.equals("01")){
                if(accountKind.equals("57")){
                    tvTitleName.setText("对公账户待录入");
                } else if (accountKind.equals("58")){
                    tvTitleName.setText("对私账户待录入");
                }
            } else {
                if(accountKind.equals("57")){
                    tvTitleName.setText("对公Q码待录入");
                } else if (accountKind.equals("58")){
                    tvTitleName.setText("对私Q码待录入");
                }
            }
        }
        showProgressDialog("获取中...");
        mPageNo = 1;
        isLoad = false;
        merApplyListReq = new MerApplyListReq();
        merApplyListReq.setAccountKind(accountKind);
        merApplyListReq.setApplyChannel(applychianl);
        merApplyListReq.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        merApplyListReq.setPageSize(pageSize);
        merApplyListReq.setPageNo(mPageNo);
        merApplyListReq.setProcess("ENTER");
        NetAPI.merApplyListReq(this, this, merApplyListReq);
    }
    public static boolean needRefresh = false;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case EDIT_CODE:
                if(needRefresh){
                    bindData();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_iv_back:
                finish();
                break;
            default:
                break;
        }
    }


    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        mRecyclerView.onRefreshComplete();
        if (method == NetAPI.ACTION_MER_APPLY_LIST) {
            MyMerchantsListResp myMerchantsListResp = (MyMerchantsListResp) obj;
            totalPage = myMerchantsListResp.getContent().getTotalPage();
            if (mPageNo == totalPage) {
                mRecyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
            if (mPageNo < totalPage) {
                mPageNo++;
            }
            if (isLoad) {
//                if (mPageNo == totalPage || myMerchantsListResp.getContent().getRows().size() == 0) {
//                    Utils.showToast(_activity, "没有更多数据了");
//                    return;
//                }
                mMyMerchantsAdapter.addData(myMerchantsListResp.getContent().getRows());
            } else {
                mMyMerchantsAdapter.setNewData(myMerchantsListResp.getContent().getRows());
            }
            //判空
            if (null != myMerchantsListResp.getContent() && myMerchantsListResp.getContent().getRows().size() < 1 && mPageNo == 1) {
                mTvEmptyView.setVisibility(View.VISIBLE);
            } else {
                mTvEmptyView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        mRecyclerView.onRefreshComplete();
        if (method == NetAPI.ACTION_MER_APPLY_LIST) {
            Utils.showToast(this, statusCode);
        }
    }

}
