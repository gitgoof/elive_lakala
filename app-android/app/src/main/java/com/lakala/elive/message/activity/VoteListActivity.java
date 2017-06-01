package com.lakala.elive.message.activity;

import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.beans.PageModel;
import com.lakala.elive.beans.VoteTaskInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.PageReqInfo;
import com.lakala.elive.common.utils.UiUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.message.adapter.VoteListViewAdpter;
import com.lakala.elive.user.base.BaseActivity;

import java.util.ArrayList;

/**
 * 
 * 公共列表页面
 * @author hongzhiliang
 *
 */
public class VoteListActivity extends BaseActivity implements OnClickListener {
	
	private ImageButton backBtn;
	
	private PullToRefreshListView voteRefreshListview;
	private VoteListViewAdpter voteListViewAdapter;

	private ListView voteListview;
	private ArrayList<VoteTaskInfo> voteInfoList = new ArrayList<VoteTaskInfo>();

    PageReqInfo pageReqInfo = new PageReqInfo();
    private int mPageNo = 1; //分页查询当前页码
    private int mPageCnt = 0; //分页页数

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_vote_list);
    }

    @Override
    protected void bindView() {
        initNoticeRefreshListview();
    }

    @Override
    protected void bindEvent() {
        iBtnBack.setVisibility(View.VISIBLE);
        iBtnBack.setOnClickListener(this);
    }

    @Override
    protected void bindData() {
        tvTitleName.setText("投票列表");
        queryNoticeList(1, Constants.DEFAULT_PAGE_SIZE);
    }


    /**
     *
     * 商户网点分页查询
     *
     * @param pageNo
     * @param pageSize
     *
     */
    private void queryNoticeList(int pageNo,int pageSize) {
        if(pageNo == 1){
            showProgressDialog("投票列表加载中.....");
            if(voteInfoList !=null && voteInfoList.size()>0){
                voteInfoList.clear();
            }
        }
        pageReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        pageReqInfo.setPageNo(pageNo);
        pageReqInfo.setPageSize(pageSize);
        NetAPI.listVoteInfo(this,this,pageReqInfo);
    }


	private void initNoticeRefreshListview() {
		voteRefreshListview = (PullToRefreshListView) findViewById(R.id.vote_pull_to_refresh_list);
		voteRefreshListview.setEmptyView(findViewById(R.id.lv_vote_empty));
        /*
         * 设置PullToRefresh刷新模式
         * BOTH:上拉刷新和下拉刷新都支持
         * DISABLED：禁用上拉下拉刷新
         * PULL_FROM_START:仅支持下拉刷新（默认）
         * PULL_FROM_END：仅支持上拉刷新
         * MANUAL_REFRESH_ONLY：只允许手动触发
        * */
        voteRefreshListview.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        ILoadingLayout endLabelsr = voteRefreshListview.getLoadingLayoutProxy(false, true);
        endLabelsr.setLastUpdatedLabel("正在加载");// 刷新时
        endLabelsr.setReleaseLabel("松开后加载更多通知信息");// 下来达到一定距离时，显示的提示

        voteRefreshListview.setOnRefreshListener(
                new PullToRefreshBase.OnRefreshListener<ListView>() {
                    @Override
                    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                        String label = DateUtils.formatDateTime(
                                VoteListActivity.this,
                                System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME
                                        | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL);
                        if(mPageNo +1 <= mPageCnt){
//                          refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                            queryNoticeList(mPageNo + 1, Constants.DEFAULT_PAGE_SIZE);
                        }else{
                            Utils.showToast(VoteListActivity.this, "没有数据了!" );
                            new FinishRefresh().execute();
                        }
                    }
                }
        );

        //--------------------------------------------------------//

        voteListview = voteRefreshListview.getRefreshableView();

        //设置点击事件响应处理
        voteListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VoteTaskInfo  voteInfo = voteInfoList.get(position -1);
                UiUtils.startActivityWithExObj(VoteListActivity.this,VoteDetailActivity.class,Constants.EXTRAS_TASK_VOTE_INFO,voteInfo);
            }
        });

	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_iv_back:
				this.finish();
				break;
			default:
				break;
		}
	}

    @Override
    public void onSuccess(int method, Object obj) {
        switch (method) {
            case NetAPI.ACTION_LIST_VOTE:
                closeProgressDialog();
                handlerPageVoteInfo((PageModel<VoteTaskInfo>) obj);
                break;
            default:
                break;
        }
    }



    @Override
    public void onError(int method, String statusCode) {
        switch (method) {
            case NetAPI.ACTION_LIST_VOTE:
                Utils.showToast(this, "获取失败："+ statusCode +"!" );
                break;
            default:
                break;
        }
    }


    private void handlerPageVoteInfo(PageModel<VoteTaskInfo> page) {
        try {
            voteInfoList.addAll(page.getRows());
            mPageNo = page.getPageNo();
            mPageCnt = page.getTotalPage();

            //刷新列表
            if(voteListViewAdapter == null){
                voteListViewAdapter = new VoteListViewAdpter(VoteListActivity.this, voteInfoList);
                voteListview.setAdapter(voteListViewAdapter);
            }else{
                voteListViewAdapter.setVoteInfoList(voteInfoList);
                voteListViewAdapter.notifyDataSetChanged();
            }

            new FinishRefresh().execute();
            voteRefreshListview.getRefreshableView().setSelection(voteRefreshListview.getRefreshableView().getCount() - 2- (10 * (voteInfoList.size() - 1)));

        } catch (Exception e) {
            e.printStackTrace();
            Utils.showToast(this, "加载失败!");
        }
    }

    class FinishRefresh extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            voteRefreshListview.onRefreshComplete();
        }
    }

}
