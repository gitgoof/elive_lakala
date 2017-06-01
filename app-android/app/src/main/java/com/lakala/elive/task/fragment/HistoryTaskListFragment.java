package com.lakala.elive.task.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.beans.MerShopInfo;
import com.lakala.elive.beans.TaskInfo;
import com.lakala.elive.beans.TaskListReq;
import com.lakala.elive.beans.TaskListReqResp;
import com.lakala.elive.common.net.ApiRequestListener;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.utils.UiUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.market.activity.MerDetailActivity;
import com.lakala.elive.market.activity.TaskDetailActivity;
import com.lakala.elive.task.activity.TaskHistoryListAct;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by gaofeng on 2017/3/29.
 * 历史任务列表
 */

public class HistoryTaskListFragment extends Fragment implements ApiRequestListener{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_task_list_layout,null);
    }

    private RecyclerView mRecyclerView;
    private TextView mTvEmptyShow;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_fragment_history_task_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        mTvEmptyShow = (TextView) getView().findViewById(R.id.tv_frag_history_task_empty_show);
        initRecyclerView();
    }

    private void initRecyclerView(){
        mRecyclerView.setAdapter(new RecyclerView.Adapter<MyViewHodler>() {
            @Override
            public MyViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
                if(viewType == 1){
                    TextView textView = new TextView(getActivity());
                    textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                    textView.setGravity(Gravity.CENTER);
                    textView.setPadding(0,25,0,15);
                    textView.setTextColor(ContextCompat.getColor(getActivity(),R.color.gray_3));
                    return new MyViewHodler(textView);
                }
                return new MyViewHodler(LayoutInflater.from(getActivity()).inflate(R.layout.item_task_history_show_layout,parent,false));
            }

            @Override
            public void onBindViewHolder(MyViewHodler holder, int position) {
                if(getItemViewType(position) == 1){
                    if(mTaskListReqResp == null){
                        holder.tvBottom.setText("完成：0个网点  未完成：0个网点");
                    } else {
                        if(mAryPoints.size()==0){
                            holder.tvBottom.setText("完成：0个网点  未完成：0个网点");
                        } else {
                            int over = 0;
                            int noEnd =0 ;
                            for(TaskListReqResp.ContentBean contentBean:mAryPoints){
                                int status = contentBean.getStatus();
                                // 日常维护 0 未拜访，1已拜访
                                if(status==0){
                                    noEnd++;
                                } else if (status == 1||status==5){
                                    over++;
                                }
                            }
                            holder.tvBottom.setText("完成：" + over + "个网点  未完成："+noEnd+"个网点");
                        }
                    }
                    return;
                }
                // TODO 根据Holder设置界面显示
                final TaskListReqResp.ContentBean contentBean = mAryPoints.get(position);
                int orderType = contentBean.getOrderType();// 1 为 网点。
                holder.linearItemBottom.setVisibility(orderType==1?View.VISIBLE:View.GONE);
                holder.tvPhone.setVisibility(orderType==1?View.GONE:View.VISIBLE);
                holder.tvTaskCode.setText(contentBean.getOrderType()==1?checkString(contentBean.getMerchantCode()):checkString(contentBean.getTaskBizId()));
                // TODO 设置该单的类型
                holder.tvTaskType.setText(checkString(contentBean.getTaskTypeName()));
                // TODO 设置创建时间
                holder.tvCreateTime.setText(checkString( contentBean.getBeginTimeStr()));
                // TODO 设置订单状态
                holder.tvTaskStatus.setText(checkString(contentBean.getStatusName()));
                // TODO 设置结束时间
                holder.tvTime.setText(checkString(contentBean.getFinishTimeStr()));

                String shopNo = contentBean.getShopNo();
                if(TextUtils.isEmpty(shopNo)){
                    shopNo = "";
                } else {
                    shopNo = "  (" + shopNo + ")";
                }

                holder.tvStoreName.setText(checkString(contentBean.getShopName())+shopNo);
                holder.tvStoreAddress.setText(contentBean.getShopAddr());
                if(orderType==2){
                    holder.tvPhone.setText(contentBean.getMerchantCode());
                }
                holder.itemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(contentBean.getOrderType() == 1){
                            // TODO 网点详情
                            MerShopInfo merchantInfo = new MerShopInfo();
                            merchantInfo.setShopNo(contentBean.getShopNo());
                            UiUtils.startActivityWithExObj(getActivity(), MerDetailActivity.class, Constants.EXTRAS_MER_INFO, merchantInfo);
                        } else if (contentBean.getOrderType() == 2){
                            // TODO 工单详情
                            TaskInfo taskInfo = new TaskInfo();
                            taskInfo.setShopNo(contentBean.getShopNo());
                            taskInfo.setStatus(contentBean.getStatus()+"");
                            taskInfo.setTaskId(contentBean.getOutTaskId());
                            taskInfo.setTaskBizId("");
//                    TaskInfo taskInfo = mTaskInfoList.get(position -1);
                            // TaskInfo 信息不完整，无法跳转到详情界面
                            UiUtils.startActivityWithExObj(getActivity(),TaskDetailActivity.class,Constants.EXTRAS_TASK_DEAL_INFO,taskInfo);
                        }
                    }
                });
            }
            @Override
            public int getItemCount() {
                if(mAryPoints.size() == 0)return 0;
                return mAryPoints.size()+1;
            }
            @Override
            public int getItemViewType(int position) {
                if(position == getItemCount()-1){
                    return 1;
                }
                return super.getItemViewType(position);
            }
        });
        /*
        mRecyclerView.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                // TODO 用于监听数据变化
            }
        });
        */
        // TODO 假数据用来显示
//        for(int i = 0;i < 5;i++){
//            mRecyclerListData.add(new HistoryTaskListFragment.ActItemBean());
//        }
    }

    private String checkString(String string){
        if(TextUtils.isEmpty(string))return "";
        return string;
    }

    private void bindData() {
        if(TextUtils.isEmpty(mDateTime)){
            return;
        }
        showProgressDialog("获取数据中");
        // 设置请求参数
        TaskListReq taskListReq = new TaskListReq();
        taskListReq.setAuthToken(Session.get(getActivity()).getUserLoginInfo().getAuthToken());
        taskListReq.setTaskDateStr("" + mDateTime);
        taskListReq.setUserId(Session.get(getActivity()).getUserLoginInfo().getUserId());
        NetAPI.getTaskList(getActivity(), this, taskListReq);
    }

    class MyViewHodler extends RecyclerView.ViewHolder {

        TextView tvBottom;

        TextView tvTaskCode;
        TextView tvTaskType;
        TextView tvCreateTime;

        TextView tvTaskStatus;
        TextView tvStoreName;
        TextView tvStoreAddress;
        TextView tvPhone;
        TextView tvTime;

        LinearLayout linearItemBottom;
        public MyViewHodler(View itemView) {
            super(itemView);
            if(itemView instanceof TextView){
                tvBottom = (TextView) itemView;
                return;
            }
            tvTaskCode = (TextView)itemView.findViewById(R.id.tv_item_task_history_taskcode);
            tvTaskType = (TextView)itemView.findViewById(R.id.tv_item_task_history_tasktype);
            tvCreateTime = (TextView)itemView.findViewById(R.id.tv_item_task_history_createtime);

            tvTaskStatus = (TextView)itemView.findViewById(R.id.tv_item_task_history_taskstatus);
            tvStoreName = (TextView)itemView.findViewById(R.id.tv_item_task_history_storename);
            tvStoreAddress = (TextView)itemView.findViewById(R.id.tv_item_task_history_address);
            tvPhone = (TextView)itemView.findViewById(R.id.tv_item_task_history_phone);
            tvTime = (TextView)itemView.findViewById(R.id.tv_item_task_history_time);

            linearItemBottom = (LinearLayout) itemView.findViewById(R.id.linear_item_task_history_bottomshow);
        }
    }

    public void toResume(){
        // TODO 初始化该界面数据. 保存界面信息，更新界面显示
        if(mTaskListReqResp==null){
            bindData();
        } else {
            final int count = mAryPoints.size();
            if(count <=0){
                mTvEmptyShow.setVisibility(View.VISIBLE);
            } else {
                mTvEmptyShow.setVisibility(View.GONE);
            }
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }
    @Override
    public void onSuccess(int method, Object obj) {
        // TODO 请求成功调用
        closeProgressDialog();
        switch (method) {
            case NetAPI.ACTION_GET_TASK_LIST:
                setTaskList((TaskListReqResp) obj);
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        // TODO 请求失败调用
        closeProgressDialog();
        switch (method) {
            case NetAPI.ACTION_GET_TASK_LIST:
                Utils.showToast(getActivity(), "获取失败：" + statusCode + "!");
                mTaskListReqResp= null;
                mAryPoints.clear();
                mTvEmptyShow.setVisibility(View.VISIBLE);
                mRecyclerView.getAdapter().notifyDataSetChanged();
                break;
            default:
                break;
        }
    }
    private TaskListReqResp mTaskListReqResp;
    private final List<TaskListReqResp.ContentBean> mAryPoints = new ArrayList<>();
    private void setTaskList(TaskListReqResp taskListReqResp) {
        mTaskListReqResp = taskListReqResp;

        mAryPoints.clear();
        if(taskListReqResp == null || taskListReqResp.getContent() == null){
            Toast.makeText(getActivity(), "返回值为空!", Toast.LENGTH_SHORT).show();
        } else {
            mAryPoints.addAll(taskListReqResp.getContent());
        }

        final int count = mAryPoints.size();
        if(count <=0){
            mTvEmptyShow.setVisibility(View.VISIBLE);
        } else {
            mTvEmptyShow.setVisibility(View.GONE);
        }
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }
    private void closeProgressDialog() {
        Activity activity = getActivity();
        if(activity == null)return;
        if(activity instanceof TaskHistoryListAct)
            ((TaskHistoryListAct)getActivity()).closeProgressDialog();
    }

    private void showProgressDialog(String string){
        Context context = getContext();
        if(context == null)return;
        if(context instanceof TaskHistoryListAct){
            ((TaskHistoryListAct)getActivity()).showProgressDialog(string);
        }
    }
    public String getRequestDate(){
        return mDateTime;
    }
    /**
     * 请求的时间
     */
    private String mDateTime = "";
    public void getTabView(TabLayout tabLayout,int position,int time){
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        final SimpleDateFormat sdfShow = new SimpleDateFormat("MM-dd");
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE,-time);
        Date date = calendar.getTime();
        /*
        if(time == 0){
            mDateTime = "";
        } else {
        }
        */
        mDateTime = sdf.format(date);
        tabLayout.getTabAt(position).setText(sdfShow.format(date)).setTag(sdf.format(date));
    }
}
