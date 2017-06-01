package com.lakala.elive.message.activity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.beans.VoteItemInfo;
import com.lakala.elive.beans.VoteTaskInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.UserReqInfo;
import com.lakala.elive.common.net.req.base.VoteReqInfo;
import com.lakala.elive.common.utils.DialogUtil;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.message.adapter.VoteItemListViewAdpter;
import com.lakala.elive.user.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * 投票详情界面
 * 
 */
public class VoteDetailActivity extends BaseActivity {
	
	private List<VoteItemInfo> listVoteItems = new ArrayList<VoteItemInfo>();

    private VoteTaskInfo voteTaskInfo;


	private TextView tvVoteTitle;
	private TextView tvVoteContent;


    //下拉刷新页面
    private PullToRefreshListView mPullToRefreshListView;
    private VoteItemListViewAdpter mVoteItemListViewAdpter;
    private ListView mVoteItemListView;

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_vote_detail);
    }

    @Override
    protected void bindView() {
        tvTitleName = (TextView)findViewById(R.id.tv_title_name);
        tvVoteTitle = (TextView)findViewById(R.id.tv_vote_title);
        tvVoteContent = (TextView)findViewById(R.id.tv_vote_content);
    }

    @Override
    protected void bindEvent() {
        tvTitleName.setText("投票详情");
        iBtnBack.setVisibility(View.VISIBLE);
        iBtnBack.setOnClickListener(this);

        btnCancel.setVisibility(View.VISIBLE);
        btnCancel.setOnClickListener(this);
        btnCancel.setText("投票");

        initVoteItemRefreshListView();
    }

    @Override
    protected void bindData() {
        //获取页面传递的对象
        voteTaskInfo = (VoteTaskInfo)getIntent().getExtras().get(Constants.EXTRAS_TASK_VOTE_INFO);

        tvVoteTitle.setText(voteTaskInfo.getName());
        tvVoteContent.setText(Html.fromHtml(voteTaskInfo.getContent()));

        getVoteDetailList();

    }


    public void getVoteDetailList(){
        UserReqInfo reqInfo = new UserReqInfo();
        reqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        reqInfo.setVoteId(voteTaskInfo.getVoteId());
        NetAPI.listVoteItemDetail(this, this,reqInfo);
    }

    private void initVoteItemRefreshListView() {
        mPullToRefreshListView = (PullToRefreshListView)findViewById(R.id.pull_to_refresh_vote_detail_list);
        mPullToRefreshListView.setEmptyView(findViewById(R.id.lv_vote_detail_empty));
        /*
         * 设置PullToRefresh刷新模式
         * BOTH:上拉刷新和下拉刷新都支持
         * DISABLED：禁用上拉下拉刷新
         * PULL_FROM_START:仅支持下拉刷新（默认）
         * PULL_FROM_END：仅支持上拉刷新
         * MANUAL_REFRESH_ONLY：只允许手动触发
        * */
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);

        ILoadingLayout endLabelsr = mPullToRefreshListView.getLoadingLayoutProxy(false, true);
//       endLabelsr.setPullLabel("上拉可以刷新");// 刚下拉时，显示的提示
        endLabelsr.setLastUpdatedLabel("正在加载");// 刷新时
        endLabelsr.setReleaseLabel("松开后加载更多网点信息");// 下来达到一定距离时，显示的提示

        mPullToRefreshListView.setOnRefreshListener(
                new PullToRefreshBase.OnRefreshListener<ListView>() {
                    @Override
                    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                        String label = DateUtils.formatDateTime(
                                VoteDetailActivity.this,
                                System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME
                                        | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL);
                        new FinishRefresh().execute();
                    }
                }
        );

        //--------------------------------------------------------//
        mVoteItemListView = mPullToRefreshListView.getRefreshableView();


    }

    @Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_iv_back:
				this.finish();
				break;
			case R.id.btn_action:

                DialogUtil.createAlertDialog(
                        this,
                        "用户确认提示！",
                        "提交投票选项？",
                        "取消",
                        "确定",
                        mListener
                ).show();

				break;
			default:
				break;
		}
	}

    /**监听对话框里面的button点击事件*/
    DialogInterface.OnClickListener mListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which){
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    dialog.dismiss();
                    showProgressDialog("投票提交中......");
                    submitVoteSel();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };


    public  void submitVoteSel(){
        VoteReqInfo reqInfo = new VoteReqInfo();

        reqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        reqInfo.setVoterVo(voteTaskInfo);
        List<VoteItemInfo> listVoteCheck = new ArrayList<VoteItemInfo>();
        for(VoteItemInfo tmp :mVoteItemListViewAdpter.getVoteItemList()){
            if(tmp.getIsVote()==1){
                listVoteCheck.add(tmp);
            }
        }
        if(listVoteCheck.size() >= 1){
            reqInfo.setItems(listVoteCheck);
        }else{
            Utils.showToast(getApplicationContext(),"请至少选择一项！");
            return;
        }
        NetAPI.addUserVote(this, this, reqInfo);
    }


	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
			case NetAPI.ACTION_VOTE_DETAIL_LIST:
                handleVoteItemListInfo((List<VoteItemInfo>) obj);
				break;
            case NetAPI.ACTION_ADD_USER_VOTE:
                Toast.makeText(this, "投票成功！", Toast.LENGTH_LONG).show();
                finish();
                break;
			default:
				break;
		}
		
	}

	@Override
	public void onError(int method, String statusCode) {
		switch (method) {
			case NetAPI.ACTION_VOTE_DETAIL_LIST:
				Toast.makeText(this, "投票选项加载失败:"+ statusCode +"！", Toast.LENGTH_LONG).show();
				break;
            case NetAPI.ACTION_ADD_USER_VOTE:
                Toast.makeText(this, "投票失败:"+ statusCode +"！", Toast.LENGTH_LONG).show();
                break;
			default:
				break;
		}
	}


    private void handleVoteItemListInfo(List<VoteItemInfo> voteItems) {
        try {
            this.listVoteItems = voteItems;
            if(mVoteItemListViewAdpter == null){
                mVoteItemListViewAdpter = new VoteItemListViewAdpter(VoteDetailActivity.this,listVoteItems, voteTaskInfo);
                mVoteItemListView.setAdapter(mVoteItemListViewAdpter);
            }else{
                mVoteItemListViewAdpter.setVoteItemList(listVoteItems);
                mVoteItemListViewAdpter.notifyDataSetChanged();
            }
            new FinishRefresh().execute();
            mPullToRefreshListView.getRefreshableView().setSelection( mPullToRefreshListView.getRefreshableView().getCount() - 2- (10 * (listVoteItems.size() - 1)));
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
            mPullToRefreshListView.onRefreshComplete();
        }
    }

}
