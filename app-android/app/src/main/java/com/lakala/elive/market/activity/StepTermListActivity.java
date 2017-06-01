package com.lakala.elive.market.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.lakala.elive.common.net.req.VisitOrEditReqInfo;
import com.lakala.elive.common.utils.DialogUtil;
import com.lakala.elive.common.utils.UiUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.market.adapter.TermListViewAdpter;
import com.lakala.elive.user.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;


/**
 *
 *
 * Step3 网点终端列表
 *
 * @author hongzhiliang
 */
public class StepTermListActivity extends BaseActivity {

    private MerShopReqInfo merShopReqInfo = new MerShopReqInfo(); //请求
    private List<TermInfo> termResInfoList; //平台网点终端列表

    private VisitOrEditReqInfo visitReqInfo = new VisitOrEditReqInfo();
    private MerShopInfo merShopInfo = null;
    private List<TermInfo> termReqInfoList = new ArrayList<TermInfo>(); //用户核实的网点终端列表

    private Button btnPreEdit; //上一步提交信息
    private Button btnSubmitEdit; //下一步提交信息

    //下拉刷新页面
    private PullToRefreshListView mPullToRefreshListView;
    private TermListViewAdpter mTermListViewAdpter;
    private ListView mTermListView;

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_step_term_list);
    }

    @Override
    protected void bindView() {
        btnSubmitEdit = (Button) findViewById(R.id.btn_submit_edit);
        btnPreEdit = (Button) findViewById(R.id.btn_pre_edit);
        initTermRefreshListView();
    }

    private void initTermRefreshListView() {
        mPullToRefreshListView = (PullToRefreshListView)findViewById(R.id.pull_to_refresh_term_list);
        mPullToRefreshListView.setEmptyView(findViewById(R.id.lv_term_empty));
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
                                StepTermListActivity.this,
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
                TermInfo termInfo = termResInfoList.get(position -1);

                Intent intent = new Intent(StepTermListActivity.this,StepEditTermInfoActivity.class);//终端详情页面
                Bundle mBundle=new Bundle();
                mBundle.putSerializable(Constants.EXTRAS_TERM_INFO, termInfo);
                intent.putExtras(mBundle);


                startActivityForResult(intent,Constants.TAKE_EIDIT_TERM);
            }
        });

    }

    @Override
    protected void bindEvent() {
        btnSubmitEdit.setOnClickListener(this);
        btnPreEdit.setOnClickListener(this);

    }

    @Override
    protected void bindData() {
        tvTitleName.setText("网点终端列表核实");

        visitReqInfo = (VisitOrEditReqInfo) getIntent().getExtras().get(Constants.EXTRAS_MER_VISIT_INFO); //获取页面传递的对象
        merShopInfo = visitReqInfo.getShopChangeRecordVO();

        if(visitReqInfo.getTerminalList() != null && visitReqInfo.getTerminalList().size() > 0){
            termResInfoList = visitReqInfo.getTerminalList();
            handleTermListInfo(termResInfoList);
        }else{
            queryTermList();
        }

        if("EDIT".equals(visitReqInfo.getVisitOrEdit())){
            btnSubmitEdit.setText("提交编辑");
        }


        iBtnBack = (ImageView) findViewById(R.id.btn_iv_back);
        iBtnBack.setVisibility(View.VISIBLE);
        iBtnBack.setOnClickListener(this);

        btnCancel.setVisibility(View.VISIBLE);
        btnCancel.setOnClickListener(this);

    }

    private void queryTermList() {
        showProgressDialog("正在加载网点终端列表...");
        merShopReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        merShopReqInfo.setShopNo(merShopInfo.getShopNo());
        NetAPI.queryShopTermList(this, this, merShopReqInfo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit_edit:
                doNext();
                break;
            case R.id.btn_pre_edit:
                doPrevious(); //返回上一步
                break;
            case R.id.btn_iv_back:
                doPrevious(); //返回上一步
                break;
            case R.id.btn_action:
                doCancel();
                break;
            default:
                break;
        }
    }


    private void doCancel() {
        DialogUtil.createAlertDialog(
                this,
                "用户确认提示！",
                "取消编辑和签到？",
                "取消",
                "确定",
                mCancleListener
        ).show();
    }



    @Override
    public void onSuccess(int method, Object obj) {
        switch (method) {
            case NetAPI.ACTION_SHOP_TERM_LIST:
                closeProgressDialog();
                handleTermListInfo((List<TermInfo>) obj);
                break;
            case NetAPI.ACTION_ADD_VISIT_LOG:
                closeProgressDialog();
                Utils.showToast(this, "修改成功");
                finish();
                break;
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        switch (method) {
            case NetAPI.ACTION_SHOP_TERM_LIST:
                closeProgressDialog();
                Utils.showToast(this, "加载失败:" + statusCode + "!");
                break;
            case NetAPI.ACTION_ADD_VISIT_LOG:
                closeProgressDialog();
                Utils.showToast(this, "修改失败:" + statusCode + "!" );
                break;
        }
    }


    private void handleTermListInfo(List<TermInfo> termList) {
        try {
            termResInfoList = termList;
            for(TermInfo checkItem:termResInfoList){
                    checkItem.setCheckStatus(false);
            }
            if(mTermListViewAdpter == null){
                mTermListViewAdpter = new TermListViewAdpter(StepTermListActivity.this,termResInfoList);
                mTermListView.setAdapter(mTermListViewAdpter);
            }else{
                mTermListViewAdpter.setTermInfoList(termList);
                mTermListViewAdpter.notifyDataSetChanged();
            }
            new FinishRefresh().execute();
            mPullToRefreshListView.getRefreshableView().setSelection( mPullToRefreshListView.getRefreshableView().getCount() - 2- (10 * (termList.size() - 1)));
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.TAKE_EIDIT_TERM:
                if (resultCode == RESULT_OK) {
                    TermInfo tmpTermInfo = (TermInfo) data.getExtras().get(Constants.EXTRAS_TERM_INFO);//得到新Activity 关闭后返回的数据

                    if(termReqInfoList!=null){
                        termReqInfoList.remove(tmpTermInfo);
                        termReqInfoList.add(tmpTermInfo);
                    }
                    //更新检查状态
                    for(TermInfo checkItem:termResInfoList){
                        if(checkItem.getTerminalCode().equals(tmpTermInfo.getTerminalCode())){
                            checkItem.setCheckStatus(true);
                            mTermListViewAdpter.setTermInfoList(termResInfoList);
                            mTermListViewAdpter.notifyDataSetChanged();
                        }
                    }

                }
                break;
        }
    }


    /**
     * 下一步
     */
    private void doNext(){
        //信息编辑验证 这里没有必选条件 提示用户是否确认信息提交
        if(termReqInfoList.size() != termResInfoList.size() ){
            Utils.showToast(this,"请核查列表中的所有的终端！");
            return;
        }

        visitReqInfo.setTerminalList(termReqInfoList);

        if(Constants.MER_EDIT.equals(visitReqInfo.getVisitOrEdit())){
            DialogUtil.createAlertDialog(
                    this,
                    "用户确认提示！",
                    "已核实完终端信息？",
                    "取消",
                    "确定",
                    mListener
            ).show();
        }else{
            DialogUtil.createAlertDialog(
                    this,
                    "用户确认提示！",
                    "已核实完终端信息？",
                    "取消",
                    "确定",
                    mListener
            ).show();
        }
    }

    /**
     * 上一步
     *
     */
    private void doPrevious(){
        DialogUtil.createAlertDialog(
                this,
                "用户确认提示！",
                "返回上一步？",
                "取消",
                "确定",
                mPreListener
        ).show();
    }

    /**监听对话框里面的button点击事件*/
    DialogInterface.OnClickListener mListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which){
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    dialog.dismiss();
                    if(Constants.MER_EDIT.equals(visitReqInfo.getVisitOrEdit())){
                        visitReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
                        NetAPI.addMerVisitLog(StepTermListActivity.this, StepTermListActivity.this, visitReqInfo);
                        showProgressDialog("修改提交中......");
                    }else{
                        UiUtils.startActivityWithExObj(StepTermListActivity.this,StepAddVisitActivity.class,
                                Constants.EXTRAS_MER_VISIT_INFO,visitReqInfo);
                    }
                    finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };


    /**监听对话框里面的button点击事件*/
    DialogInterface.OnClickListener mPreListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which){
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    dialog.dismiss();
                    UiUtils.startActivityWithExObj(StepTermListActivity.this,StepMarkShopInfoActivity.class,
                            Constants.EXTRAS_MER_VISIT_INFO,visitReqInfo);
                    finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    /**监听对话框里面的button点击事件*/
    DialogInterface.OnClickListener mCancleListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which){
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    dialog.dismiss();
                    finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK ){
            doPrevious();
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeProgressDialog();
    }
}
