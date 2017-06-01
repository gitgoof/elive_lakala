package com.lakala.elive.task.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.beans.DeleteTaskListReq;
import com.lakala.elive.beans.DeleteTaskListResp;
import com.lakala.elive.beans.MerShopInfo;
import com.lakala.elive.beans.TaskInfo;
import com.lakala.elive.beans.TaskListReq;
import com.lakala.elive.beans.TaskListReqResp;
import com.lakala.elive.common.net.ApiRequestListener;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.TaskListBaseReq;
import com.lakala.elive.common.utils.UiUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.map.activity.MapStoreShowAct;
import com.lakala.elive.map.adapter.TaskListAdapter;
import com.lakala.elive.market.activity.MerDetailActivity;
import com.lakala.elive.market.activity.TaskDetailActivity;
import com.lakala.elive.task.activity.SelectStoresActivity;
import com.lakala.elive.task.activity.SelectTasksActivity;
import com.lakala.elive.task.activity.TaskListActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by gaofeng on 2017/3/20.
 * 任务列表界面
 */

public class TaskListFragment extends Fragment implements View.OnClickListener,ApiRequestListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_list_show, null);
    }

    private RecyclerView horizontalRecyclerView;
    private TextView mTvEmptyView;

    private TextView tvPoint;// 已添加
    private RecyclerView pointInfoRecycleview;// 详情列表

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        horizontalRecyclerView = (RecyclerView) this.getView().findViewById(R.id.hrecycler_view);
        mTvEmptyView = (TextView) getView().findViewById(R.id.tv_task_list_show_recyclerview_emptyview);

        tvPoint = (TextView) getView().findViewById(R.id.tv_point);
        pointInfoRecycleview = (RecyclerView) getView().findViewById(R.id.point_info_recycleview);

        // TODO 对显示进行初始化
        initShowNum(0);
        setAdapter();
    }


    private void setAdapter() {
        final ArrayList<String> objects = new ArrayList<>();
        objects.add("日常维护");
        objects.add("工单");
        objects.add("自动载入工单");
        objects.add("地图添加");

        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        horizontalRecyclerView.setAdapter(new RecyclerView.Adapter<HoriItemBean>() {
            @Override
            public HoriItemBean onCreateViewHolder(ViewGroup parent, int viewType) {
                return new HoriItemBean(LayoutInflater.from(getActivity()).inflate(R.layout.item_hrecycle_view,parent,false));
            }
            @Override
            public void onBindViewHolder(HoriItemBean holder, int position) {
                holder.textView.setTag(position);
                holder.textView.setText(objects.get(position));
            }
            @Override
            public int getItemCount() {
                return objects.size();
            }
        });

        pointInfoRecycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPointAdater = new TaskListAdapter();
        pointInfoRecycleview.setAdapter(mPointAdater);
        initListener();
    }
    private TaskListAdapter mPointAdater;
    public int getBeginIndex(){
        if(mPointAdater!=null){
            return mPointAdater.getmIntBeginIndex();
        }
        return -1;
    }
    public void setBeginIndex(int index){
        if(mPointAdater!=null){
            mPointAdater.setmIntBeginIndex(index);
        }
    }
    public void notifyDataChanged(){
        mPointAdater.notifyDataSetChanged();
    }
    private class HoriItemBean extends RecyclerView.ViewHolder{

        TextView textView;
        public HoriItemBean(View itemView) {
            super(itemView);
            if(itemView instanceof TextView) {
                textView = (TextView) itemView;
            }
            textView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    switch (Integer.valueOf(v.getTag().toString())){
                    case 0:// 日常维护
                        startActivityForResult(new Intent(getActivity(), SelectStoresActivity.class),ACT_REQUEST_STORE);
                        break;
                    case 1:// 工单
                        startActivityForResult(new Intent(getActivity(), SelectTasksActivity.class),ACT_REQUEST_TASK);
                        break;
                    case 2:// 自动载入工单
//                        Toast.makeText(getActivity(),"还未实现该功能！",Toast.LENGTH_SHORT).show();
                        loadWfTaskList();
                        break;
                    case 3:// 地图添加
                        getActivity().startActivity(new Intent(getActivity(), MapStoreShowAct.class).putExtra("entrance", 0));
                        break;
                    }
                }
            });
        }
    }

    /**
     * 列表界面更新返回标识
     */
    private final int ACT_REQUEST_TASK = 0x1000;
    /**
     *  网点界面更新返回的标识
     */
    private final int ACT_REQUEST_STORE = 0x1001;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ACT_REQUEST_TASK:
                if(resultCode == 100){
                    // TODO
                    bindData();
                }
                break;
            case ACT_REQUEST_STORE:
                if(resultCode == 100){
                    // TODO
                    bindData();
                }
                break;
        }
    }

    private void initListener(){
        /* 该部分为下拉刷新显示
        final TextView textView = new TextView(getActivity());
        textView.setText("测试头文件");
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setPadding(10,30,10,30);
        textView.setGravity(Gravity.CENTER);
        textView.setHeight(0);
        mPointAdater.setHeadView(textView);
        */

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) pointInfoRecycleview.getLayoutManager();
        pointInfoRecycleview.setOnTouchListener(new View.OnTouchListener() {
            float y = -1;
            boolean scroll =false;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!mPointAdater.canLoadMore())return false;
                final int position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                final int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_MOVE:
                        if(position == 1){
                            if(y<0){
                                y = event.getY();
                                scroll = true;
                            }
                        }
                        if(scroll&&y>=0){
                            float hei = event.getY()-y;
                            if(hei<0){
                                mPointAdater.setHeadViewHeight(0);
                            } else {
                                mPointAdater.setHeadViewHeight((int) hei);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if(scroll){
                            scroll = false;
                            y=-1;
                            mPointAdater.animationHeadView();
                        }
                        break;
                }
                return false;
            }
        });

        mPointAdater.setmOnItemClickListener(new TaskListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object object) {}
            @Override
            public void onItemDelClick(int position, Object object) {
                // TODO 移除
                final TaskListReqResp.ContentBean bean = (TaskListReqResp.ContentBean) object;
              deleteItem(position,bean.getTaskId());
//                Toast.makeText(getActivity(), "移除点击" + position, Toast.LENGTH_SHORT).show();
//                mPointAdater.removeItemByPos(position);
            }
            @Override
            public void onItemBegin(int position, Object object) {
                // TODO 设为起点
                Toast.makeText(getActivity(), "还未实现--设为起点" + position, Toast.LENGTH_SHORT).show();
                // 需要得到工单的定位信息.当已经是选择状态，就返回不处理
                final TaskListReqResp.ContentBean bean = (TaskListReqResp.ContentBean) object;
                bean.planOrder = 0;
            }
            @Override
            public void onItemMsg(int position, Object object) {
                // TODO 查看详情
                final TaskListReqResp.ContentBean bean = (TaskListReqResp.ContentBean) object;
                if(bean.getOrderType() == 1){
                    // TODO 网点详情
                    MerShopInfo merchantInfo = new MerShopInfo();
                    merchantInfo.setShopNo(bean.getShopNo());
//              MerShopInfo merchantInfo = merInfoList.get(position - 1);
                    UiUtils.startActivityWithExObj(getActivity(), MerDetailActivity.class, Constants.EXTRAS_MER_INFO, merchantInfo);
                } else if (bean.getOrderType() == 2){
                    // TODO 工单详情
                    TaskInfo taskInfo = new TaskInfo();
                    taskInfo.setShopNo(bean.getShopNo());
                    // 缺少工单状态
                    taskInfo.setStatus(bean.getStatus()+"");
                    taskInfo.setTaskId(bean.getOutTaskId());
                    taskInfo.setTaskBizId(bean.getTaskBizId());
                    taskInfo.setTaskType(bean.getTaskType()+"");
//                    TaskInfo taskInfo = mTaskInfoList.get(position -1);
                    // TaskInfo 信息不完整，无法跳转到详情界面
                    UiUtils.startActivityWithExObj(getActivity(),TaskDetailActivity.class,Constants.EXTRAS_TASK_DEAL_INFO,taskInfo);
                }

            }
        });
        bindData();
    }

    private void showProgressDialog(String string){
        Activity activity = getActivity();
        if(activity == null)return;
        if(activity instanceof TaskListActivity)
        ((TaskListActivity)getActivity()).showProgressDialog(string);
    }
    private int mDelPosition = -1;
    private void deleteItem(int i,String taskId) {
        mDelPosition=i;
        showProgressDialog("删除中");
        DeleteTaskListReq deleteTaskListReq = new DeleteTaskListReq();
//        deleteTaskListReq.setTaskDateStr("");
        deleteTaskListReq.setAuthToken(Session.get(getActivity()).getUserLoginInfo().getAuthToken());
//        deleteTaskListReq.setTaskId(pointInfoRecycleAdapter.getData().get(i).getTaskId());
        deleteTaskListReq.setTaskId(taskId);
        NetAPI.deleteTaskList(getActivity(), this, deleteTaskListReq);
    }

    /**
     * 请求列表数据
     */
    public void bindData() {

        // 设置请求参数
        TaskListReq taskListReq = new TaskListReq();
        taskListReq.setAuthToken(Session.get(getActivity()).getUserLoginInfo().getAuthToken());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        taskListReq.setTaskDateStr("" + simpleDateFormat.format(new Date()));// 不填默认今天，选择日期
        String userId = Session.get(getActivity()).getUserLoginInfo().getUserId();
        if(TextUtils.isEmpty(userId)){
            Utils.showToast(getActivity(), "请重新登录!");
            return;
        }
        showProgressDialog("获取数据中");
        taskListReq.setUserId(userId);
        NetAPI.getTaskList(getActivity(), this, taskListReq);
    }
    @Override
    public void onClick(View v) {

    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.ACTION_GET_TASK_LIST:
                Utils.showToast(getActivity(), "获取失败：" + statusCode + "!");
                break;
            case NetAPI.ACTION_DELETE_TASK_LIST:
                Utils.showToast(getActivity(), "删除失败：" + statusCode + "!");
                break;
            case NetAPI.loadWfTaskList:
                Utils.showToast(getActivity(), "载入失败：" + statusCode + "!");
            default:
                break;
        }
    }

    private void closeProgressDialog() {
        Activity activity = getActivity();
        if(activity == null)return;
        if(activity instanceof TaskListActivity)
            ((TaskListActivity)getActivity()).closeProgressDialog();
    }


    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.ACTION_GET_TASK_LIST:
                setTaskList((TaskListReqResp) obj);
                break;
            case NetAPI.ACTION_DELETE_TASK_LIST:
                DeleteTaskListResp deleteTaskListResp = (DeleteTaskListResp) obj;
                Toast.makeText(getActivity(), deleteTaskListResp.getMessage(), Toast.LENGTH_SHORT).show();
                mPointAdater.removeItemByPos(mDelPosition);
                initShowNum(mPointAdater.getDataSize());
                break;
            case NetAPI.loadWfTaskList:
                // TODO 载入工单。如果成功，就得重新请求
                bindData();
                break;
            default:
                break;
        }
    }

    private final ArrayList<TaskListReqResp.ContentBean> mAryPoints = new ArrayList<TaskListReqResp.ContentBean>();
    public TaskListReqResp getPointsList(){
        TaskListReqResp taskListReqResp = new TaskListReqResp();
        taskListReqResp.setContent(mAryPoints);
        return taskListReqResp;
    }
    /**
     * 初始化显示列表
     * @param taskListReqResp
     */
    private void setTaskList(TaskListReqResp taskListReqResp) {
        mAryPoints.clear();
        if(taskListReqResp == null || taskListReqResp.getContent() == null){
            Toast.makeText(getActivity(), "返回值为空!", Toast.LENGTH_SHORT).show();
        } else {
            mAryPoints.addAll(taskListReqResp.getContent());
        }
        mPointAdater.setShowDataList(mAryPoints);

        final int count = taskListReqResp.getContent().size();
        if(count <=0){
            mTvEmptyView.setVisibility(View.VISIBLE);
        } else {
            mTvEmptyView.setVisibility(View.GONE);
        }
        initShowNum(count);
    }

    private void initShowNum(int count){
        tvPoint.setText("已添加 : "+count+"个网点");
    }

    private void loadWfTaskList(){
        TaskListBaseReq req = new TaskListBaseReq();
        req.setAuthToken(Session.get(getActivity()).getUserLoginInfo().getAuthToken());
        String userId = Session.get(getActivity()).getUserLoginInfo().getUserId();
        if(TextUtils.isEmpty(userId)){
            Utils.showToast(getActivity(), "请重新登录!");
            return;
        }
        showProgressDialog("载入中...");
        req.setUserId(userId);
        NetAPI.loadWfTaskList(getActivity(),this,req);
    }
}
