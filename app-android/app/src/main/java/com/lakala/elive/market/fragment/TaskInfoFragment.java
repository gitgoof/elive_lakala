package com.lakala.elive.market.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.beans.MessageEvent;
import com.lakala.elive.beans.TaskInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.TaskReqInfo;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.market.base.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

/**
 *
 * 工单详情信息
 *
 * @author hongzhiliang
 *
 */
public class TaskInfoFragment extends BaseFragment{

	private final String TAG = TaskInfoFragment.class.getSimpleName();

    /**
     * 终端号
     */
    private TextView tvTerminalId;
    //工单
    private TextView tvTaskBizId;
    private TextView tvTaskType;
    private TextView tvTaskChannel;
    private TextView tvTaskSubject;
    private TextView tvTaskDesc;
    private TextView tvTaskLevel;
    private TextView tvCreateTime;
    private TextView tvFinishDays;
    private TextView tvContactor;
    private TextView tvTelNo;
    private TextView tvTelPhoneNo;
    private TextView tvShopAddr;
    private TextView tvTaskStatus;
    private TextView tvOperatorName;
    private TextView tvFinishTime;
    private TextView tvDealResult;
    private TextView tvDealDesc;

    private TaskInfo taskInfo = null; //任务信息
    private TaskReqInfo taskReqInfo = new TaskReqInfo();

    public TaskInfoFragment() {

    }

    @SuppressLint("ValidFragment")
    public TaskInfoFragment(TaskInfo taskInfo) {
    	this.taskInfo = taskInfo;
    }

    @Override
    protected void setViewLayoutId() {
        viewLayoutId = R.layout.fragment_task_base_info;
    }

    @Override
    protected void bindViewIds() {
        tvTerminalId = (TextView) mView.findViewById(R.id.tv_task_terminal_id);
        tvTaskBizId = (TextView) mView.findViewById(R.id.tv_task_biz_id);
        tvTaskType = (TextView) mView.findViewById(R.id.tv_task_type);
        tvTaskChannel = (TextView) mView.findViewById(R.id.tv_task_channel);
        tvTaskSubject = (TextView) mView.findViewById(R.id.tv_task_subject);
        tvTaskDesc = (TextView) mView.findViewById(R.id.tv_task_desc);
        tvTaskLevel = (TextView) mView.findViewById(R.id.tv_task_level);
        tvCreateTime = (TextView) mView.findViewById(R.id.tv_create_time);
        tvContactor = (TextView) mView.findViewById(R.id.tv_contactor);
        tvTelNo = (TextView) mView.findViewById(R.id.tv_tel_no);
        tvTelPhoneNo = (TextView) mView.findViewById(R.id.tv_tel_phone_no);
        tvShopAddr = (TextView) mView.findViewById(R.id.tv_shop_addr);
        tvTaskStatus = (TextView) mView.findViewById(R.id.tv_task_status);
        tvOperatorName = (TextView) mView.findViewById(R.id.tv_operator_name);
        tvFinishTime = (TextView) mView.findViewById(R.id.tv_actual_deal_time);
        tvFinishDays = (TextView) mView.findViewById(R.id.tv_finish_limit_days);
        tvDealResult = (TextView) mView.findViewById(R.id.tv_deal_result);
        tvDealDesc = (TextView) mView.findViewById(R.id.tv_deal_desc);
        initListener();
    }

    private void initListener(){
        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tv_tel_no:
                        if(taskInfo==null)break;
                        String tel = taskInfo.getTelNo();
                        if(TextUtils.isEmpty(tel))break;

                        if(mPopupWindow != null && mPopupWindow.isShowing()){
                            break;
                        }

                        popWindowForCall(tel);
                        break;
                    case R.id.tv_tel_phone_no:
                        if(taskInfo==null)break;
                        String phone = taskInfo.getMobileNo();
                        if(TextUtils.isEmpty(phone))break;
                        if(mPopupWindow != null && mPopupWindow.isShowing()){
                            break;
                        }
                        popWindowForCall(phone);
                        break;
                }
            }
        };
        tvTelNo.setOnClickListener(listener);
        tvTelPhoneNo.setOnClickListener(listener);
    }

    @Override
    protected void initData() {
        updateTaskDataView();
        if(taskInfo==null)return;
        if("2".equals(taskInfo.getStatus())){ //工单已分配
            updateTaskStatus();
        }
        refershUi();
    }



    protected void updateTaskDataView() {
        if(taskInfo == null)return;
        tvTerminalId.setText(taskInfo.getTermNo());
        tvTaskBizId.setText(taskInfo.getTaskBizId());
        tvTaskType.setText(mSession.getSysDictMap().get(Constants.TASK_TYPE).get(taskInfo.getTaskType()));
        tvTaskSubject.setText(taskInfo.getTaskSubject());
        tvTaskChannel.setText(mSession.getSysDictMap().get(Constants.TASK_CHANNEL).get(taskInfo.getTaskChannel()));
        String comments = taskInfo.getComments();
        if(TextUtils.isEmpty(comments)){
            tvTaskDesc.setText("");
        } else {
            tvTaskDesc.setText(Html.fromHtml(comments));
        }

        tvTaskLevel.setText(mSession.getSysDictMap().get(Constants.TASK_LEVEL).get(taskInfo.getTaskLevel()));
        tvTelNo.setText(taskInfo.getTelNo());
        tvTelPhoneNo.setText(taskInfo.getMobileNo());
        tvContactor.setText(taskInfo.getCustName());
        tvCreateTime.setText(taskInfo.getCreateTimeStr());
        tvShopAddr.setText(taskInfo.getCustAddr());
        tvFinishTime.setText(taskInfo.getFinishTimeStr());

        tvTaskStatus.setText(mSession.getSysDictMap().get(Constants.TASK_STATUS).get(taskInfo.getStatus()));
        tvFinishDays.setText(" T + " + taskInfo.getFinishLimitDays());
        tvOperatorName.setText(taskInfo.getOperatorName());

        if(taskInfo.getExecuteResult() != null){
            tvDealResult.setText(mSession.getSysDictMap().get(Constants.TASK_RESULT).get(taskInfo.getExecuteResult()));
        }

        if(taskInfo.getExecuteComments() != null){
            tvDealDesc.setText(Html.fromHtml(taskInfo.getExecuteComments()));
        }

    }

    private void updateTaskStatus() {
        taskReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        taskReqInfo.setTaskId(taskInfo.getTaskId());
        taskReqInfo.setStatus("3");//工单受理
        NetAPI.taskStatusUpdate(getActivity(), this, taskReqInfo);
    }

    @Override
    public void onSuccess(int method, Object obj) {
        switch (method) {
            case NetAPI.ACTION_TASK_STATUS_UPDATE:
                Utils.showToast(getActivity(), "工单已受理!");
                tvTaskStatus.setText("已受理");
                EventBus.getDefault().post(new MessageEvent(Constants.MessageType.TASK_DEAL_STAUTS_UPDATE,null));
                break;
            case NetAPI.ACTION_TASK_DETAIL:
                if(obj == null){
                    Utils.showToast(getActivity(), "返回为空!");
                    break;
                }
                taskInfo = (TaskInfo) obj;
                updateTaskDataView();
                break;
        }
        super.onSuccess(method, obj);
    }


    @Override
    public void onError(int method, String statusCode) {
        switch (method) {
            case NetAPI.ACTION_TASK_STATUS_UPDATE:
                Utils.showToast(getActivity(), "工单状态更新失败:" + statusCode + "!");
                break;
            case NetAPI.ACTION_TASK_DETAIL:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void refershUi() {
        taskReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        taskReqInfo.setTaskId(taskInfo.getTaskId());
        NetAPI.getTaskDetail(getActivity(), this, taskReqInfo);
    }
    private PopupWindow mPopupWindow;
    private void popWindowForCall(final String phoneNum){
        if(mPopupWindow == null){
            mPopupWindow = new PopupWindow(getActivity());
            mPopupWindow.setContentView(LayoutInflater.from(getActivity()).inflate(R.layout.pop_task_call_bottom_layout,null));
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        View tvPh = mPopupWindow.getContentView().findViewById(R.id.tv_pop_task_call_bottom_phonenum);
        if(tvPh != null){
            if(tvPh instanceof TextView){
                ((TextView)tvPh).setText("" + phoneNum);
            }
            tvPh.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String result = startCall(phoneNum);
                    if(!TextUtils.isEmpty(result)){
                        Toast.makeText(getActivity(),result,Toast.LENGTH_SHORT).show();
                    }
                    if(mPopupWindow.isShowing()){
                        mPopupWindow.dismiss();
                    }
                }
            });
        }
        View cancelCall = mPopupWindow.getContentView().findViewById(R.id.btn_pop_task_call_bottom_cancel);
        if(cancelCall != null){
            cancelCall.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(mPopupWindow.isShowing()){
                        mPopupWindow.dismiss();
                    }
                }
            });
        }

        if(mPopupWindow.isShowing()){
           return;
        }

        mPopupWindow.showAtLocation(getView(), Gravity.BOTTOM,0,0);
    }
    private String startCall(String phoneNum){
        if(TextUtils.isEmpty(phoneNum))return "电话号码不能为空!";
        phoneNum = phoneNum.replace(" ","");
        if(phoneNum.length()<3)return "号码不存在!";
        if(!phoneNum.matches("^\\+?\\d+$"))return "号码不正确!";
        Intent tell = new Intent(Intent.ACTION_DIAL);
        tell.setData(Uri.parse("tel://" + phoneNum));
        startActivity(tell);
        return null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mPopupWindow!= null){
            if(mPopupWindow.isShowing()){
                mPopupWindow.dismiss();
            }
            mPopupWindow = null;
        }
    }

}
