package com.lakala.elive.market.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.beans.MessageEvent;
import com.lakala.elive.beans.TaskInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.TaskReqInfo;
import com.lakala.elive.common.utils.DialogUtil;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.user.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;


/**
 *
 *
 * 工单拒绝
 *
 * @author hongzhiliang
 */
public class TaskRejectActivity extends BaseActivity {

    //拜访备注
    private EditText etVisitComment;

    private TaskInfo taskInfo;
    private TextView tvVisitType;
    private Button btnSubmitVisit;

    /**
     * 输入法管理器
     */
    private InputMethodManager mInputMethodManager;


    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_task_reject);
    }

    @Override
    protected void bindView() {
        etVisitComment = (EditText) findViewById(R.id.et_visit_comment);
        tvVisitType = (TextView) findViewById(R.id.tv_visit_type);
        btnSubmitVisit = (Button) findViewById(R.id.btn_submit_visit);//提交按钮

        //返回按钮处理
        iBtnBack.setVisibility(View.VISIBLE);
        iBtnBack.setOnClickListener(this);
    }

    @Override
    protected void bindEvent() {
        //初始化输入法
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        etVisitComment.setOnClickListener(this);

        btnSubmitVisit.setOnClickListener(this);
    }

    @Override
    protected void bindData() {
        //获取页面传递的对象
        taskInfo = (TaskInfo) getIntent().getExtras().get(Constants.EXTRAS_TASK_DEAL_INFO);
        tvTitleName.setText("工单撤回处理");
        //拜访类型下拉
        tvVisitType.setText(mSession.getSysDictMap().get(Constants.TASK_TYPE).get(taskInfo.getTaskType()));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit_visit:
                submitVisit();
                break;
            case R.id.et_visit_comment:
//              etVisitComment = (EditText) findViewById(R.id.et_visit_comment);
                etVisitComment.setFocusable(true);//设置输入框可聚集
                etVisitComment.setFocusableInTouchMode(true);//设置触摸聚焦
                etVisitComment.requestFocus();//请求焦点
                etVisitComment.findFocus();//获取焦点
                mInputMethodManager.showSoftInput(etVisitComment, InputMethodManager.SHOW_FORCED);// 显示输入法
                break;
            case R.id.btn_iv_back:
                finish();
                break;
            case R.id.btn_action:
                doCancel();
                break;
            default:
        }
    }



    private void doCancel() {
        DialogUtil.createAlertDialog(
                this,
                "用户确认提示！",
                "取消工单处理？",
                "取消",
                "确定",
                mCancleListener
        ).show();
    }

    private TaskReqInfo taskReqInfo = new TaskReqInfo();

    public void submitVisit(){
        try {

            String visitComment = etVisitComment.getText().toString();
            if(!TextUtils.isEmpty(visitComment)){
                if(visitComment.length() > Constants.VISIT_COMMENT_LEN){
                    //说明过长
                    Utils.showToast(this,"签到内容输入过长！");
                    return;
                }else{
                    taskReqInfo.setExecuteComments(visitComment);
                }

            }else{//未添加说明
                Utils.showToast(this,"签到说明不能为空！");
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



        //信息编辑验证 这里没有必选条件 提示用户是否确认信息提交
        DialogUtil.createAlertDialog(
                this,
                "用户确认提示！",
                "撤回工单信息？",
                "取消",
                "确定",
                mListener
        ).show();

    }

    @Override
    public void onSuccess(int method, Object obj) {
        switch (method) {
            case NetAPI.ACTION_TASK_STATUS_UPDATE:
                closeProgressDialog();
                Utils.showToast(this, "工单撤回成功");
                EventBus.getDefault().post(new MessageEvent(Constants.MessageType.TASK_DEAL_REJUCT,null));
                finish();
                break;
        }
    }

    @Override
    public void onError(int method, String message) {
        switch (method) {
            case NetAPI.ACTION_TASK_STATUS_UPDATE:
                closeProgressDialog();
                Utils.showToast(this, "工单撤回失败:" + message + "!" );
                break;
        }
    }


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

    /**监听对话框里面的button点击事件*/
    DialogInterface.OnClickListener mListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which){
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    dialog.dismiss();
                    taskReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
                    taskReqInfo.setTaskId(taskInfo.getTaskId());
                    taskReqInfo.setStatus("4");
                    NetAPI.taskStatusUpdate(TaskRejectActivity.this, TaskRejectActivity.this, taskReqInfo);
                    NetAPI.addTaskVisit(TaskRejectActivity.this, TaskRejectActivity.this, taskReqInfo);
                    showProgressDialog("工单撤回提交中......");
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

}
