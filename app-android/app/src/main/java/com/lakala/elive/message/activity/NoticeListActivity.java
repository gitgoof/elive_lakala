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
import com.lakala.elive.beans.NoticeInfo;
import com.lakala.elive.beans.PageModel;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.PageReqInfo;
import com.lakala.elive.common.utils.UiUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.message.adapter.NoticeListViewAdpter;
import com.lakala.elive.user.base.BaseActivity;

import java.util.ArrayList;

/**
 * 
 * 公共列表页面
 * @author hongzhiliang
 *
 */
public class NoticeListActivity extends BaseActivity implements OnClickListener {
	
	private ImageButton backBtn;
	
	private PullToRefreshListView noticeRefreshListview;
	private NoticeListViewAdpter noticeListViewAdapter;

	private ListView noticeListview;
	private ArrayList<NoticeInfo> noticeInfoList = new ArrayList<NoticeInfo>();

    PageReqInfo pageReqInfo = new PageReqInfo();
    private int mPageNo = 1; //分页查询当前页码
    private int mPageCnt = 0; //分页页数

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_notice_list);
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
        tvTitleName.setText("通知列表");
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
            showProgressDialog("通知列表加载中.....");
            if(noticeInfoList!=null && noticeInfoList.size()>0){
                noticeInfoList.clear();
            }
        }
        pageReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        pageReqInfo.setPageNo(pageNo);
        pageReqInfo.setPageSize(pageSize);
        NetAPI.listNoticeInfo(this,this,pageReqInfo);
    }


	private void initNoticeRefreshListview() {
		noticeRefreshListview = (PullToRefreshListView) findViewById(R.id.notice_pull_to_refresh_list);
		noticeRefreshListview.setEmptyView(findViewById(R.id.lv_notice_empty));
        /*
         * 设置PullToRefresh刷新模式
         * BOTH:上拉刷新和下拉刷新都支持
         * DISABLED：禁用上拉下拉刷新
         * PULL_FROM_START:仅支持下拉刷新（默认）
         * PULL_FROM_END：仅支持上拉刷新
         * MANUAL_REFRESH_ONLY：只允许手动触发
        * */
        noticeRefreshListview.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        ILoadingLayout endLabelsr = noticeRefreshListview.getLoadingLayoutProxy(false, true);
        endLabelsr.setLastUpdatedLabel("正在加载");// 刷新时
        endLabelsr.setReleaseLabel("松开后加载更多通知信息");// 下来达到一定距离时，显示的提示

        noticeRefreshListview.setOnRefreshListener(
                new PullToRefreshBase.OnRefreshListener<ListView>() {
                    @Override
                    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                        String label = DateUtils.formatDateTime(
                                NoticeListActivity.this,
                                System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME
                                        | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL);
                        if(mPageNo +1 <= mPageCnt){
//                          refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                            queryNoticeList(mPageNo + 1, Constants.DEFAULT_PAGE_SIZE);
                        }else{
                            Utils.showToast(NoticeListActivity.this, "没有数据了!" );
                            new FinishRefresh().execute();
                        }
                    }
                }
        );

        //--------------------------------------------------------//
        noticeListview = noticeRefreshListview.getRefreshableView();

        //设置点击事件响应处理
        noticeListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NoticeInfo noticeInfo = noticeInfoList.get(position -1);
                //进入通知详情
                UiUtils.startActivityWithExObj(NoticeListActivity.this,NoticeDetailActivity.class,Constants.EXTRAS_NOTICE_INFO,noticeInfo);
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
            case NetAPI.ACTION_LIST_MSG:
                closeProgressDialog();
                handlerPageNoticeInfo((PageModel<NoticeInfo>) obj);
                break;
            default:
                break;
        }
    }



    @Override
    public void onError(int method, String statusCode) {
        switch (method) {
            case NetAPI.ACTION_LIST_MSG:
                Utils.showToast(this, "获取失败："+ statusCode +"!" );
                break;
            default:
                break;
        }
    }


    private void handlerPageNoticeInfo(PageModel<NoticeInfo> page) {
        try {
            noticeInfoList.addAll(page.getRows());
            mPageNo = page.getPageNo();
            mPageCnt = page.getTotalPage();

            //刷新列表
            if(noticeListViewAdapter == null){
                noticeListViewAdapter = new NoticeListViewAdpter(NoticeListActivity.this,noticeInfoList);
                noticeListview.setAdapter(noticeListViewAdapter);
            }else{
                noticeListViewAdapter.setNoticeInfoList(noticeInfoList);
                noticeListViewAdapter.notifyDataSetChanged();
            }

            new FinishRefresh().execute();
            noticeRefreshListview.getRefreshableView().setSelection(noticeRefreshListview.getRefreshableView().getCount() - 2- (10 * (noticeInfoList.size() - 1)));

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
            noticeRefreshListview.onRefreshComplete();
        }
    }

}
