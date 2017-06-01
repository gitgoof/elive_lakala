package com.lakala.elive.task.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.beans.TaskInfo;
import com.lakala.elive.common.utils.StringUtil;

import java.util.List;

/**
 * Created by gaofeng on 2017/3/28. copy from TaskListAdapter
 */

public class SelectTaskListAdapter extends BaseAdapter {

    private final int isVisibility;
    private List<TaskInfo> taskInfoList;
    private LayoutInflater inflater;

    private Context mContext;
    protected Session mSession;


    public SelectTaskListAdapter(Context context, List<TaskInfo> taskInfoList, int isVisibility) {
        this.mContext = context;
        this.taskInfoList = taskInfoList;
        inflater = LayoutInflater.from(context);
        mSession = Session.get(mContext);
        this.isVisibility = isVisibility;
    }

    @Override
    public int getCount() {
        return taskInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Holder holder = null;
        if (view == null) {
            holder = new Holder();
            view = inflater.inflate(R.layout.item_select_task_list_layout, null);
            holder.llTaskInfo = (LinearLayout) view.findViewById(R.id.ll_task_info);
            holder.tvTaskBizId = (TextView) view.findViewById(R.id.tv_task_biz_id);
            holder.tvTaskType = (TextView) view.findViewById(R.id.tv_task_type);
            holder.tvBeginTime = (TextView) view.findViewById(R.id.tv_begin_time);
            holder.tvFinishTime = (TextView) view.findViewById(R.id.tv_finish_time);
            holder.tvTaskSubject = (TextView) view.findViewById(R.id.tv_task_subject); //工单标题
            holder.tvShopName = (TextView) view.findViewById(R.id.tv_shop_name);
            holder.tvShopAddr = (TextView) view.findViewById(R.id.tv_shop_addr);
//            holder.tvShopContact = (TextView) view.findViewById(R.id.tv_shop_contact);
            holder.tvTelno = (TextView) view.findViewById(R.id.tv_shop_telno);
            holder.tvFinishDays = (TextView) view.findViewById(R.id.tv_finish_days); //处理期限
            holder.tvStatus = (TextView) view.findViewById(R.id.tv_status);
            holder.tvTaskLevel = (TextView) view.findViewById(R.id.tv_task_level);
            holder.checkbox = (CheckBox) view.findViewById(R.id.checkbox);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        //初始化条目信息
        final TaskInfo taskInfo = taskInfoList.get(position);
        holder.tvTaskBizId.setText(taskInfo.getTaskBizId());
//        if(mSession.getSysDictMap() != null)
//        holder.tvTaskType.setText(mSession.getSysDictMap().get(Constants.TASK_TYPE).get(taskInfo.getTaskType()));
        if(StringUtil.isNotNullAndBlank(taskInfo.getTaskTypeName())){
            holder.tvTaskType.setText(taskInfo.getTaskTypeName());
        }

        holder.tvBeginTime.setText(taskInfo.getBeginTimeStr());
        holder.tvTaskSubject.setText(taskInfo.getTaskSubject());

        final String todayTaskFlag = taskInfo.getTodayTaskFlag();
        final String status = taskInfo.getStatus();
        final int statusInt = Integer.valueOf(status);
        if(statusInt != 5 && !TextUtils.isEmpty(todayTaskFlag) && todayTaskFlag.equals("0")){
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    taskInfo.setChecked(isChecked);
                }
            });
        } else {
            holder.checkbox.setVisibility(View.INVISIBLE);
        }

        if (taskInfo.getShopNo() == null || "".equals(taskInfo.getShopNo())) {
            holder.tvShopName.setVisibility(View.GONE);
        } else {
            holder.tvShopName.setVisibility(View.VISIBLE);
            String shopName = taskInfo.getShopName();
            if(TextUtils.isEmpty(shopName))shopName = "";
            holder.tvShopName.setText(shopName + "(" + taskInfo.getShopNo() + ")");
        }
        if (taskInfo.getCustAddr() == null || "".equals(taskInfo.getCustAddr())) {
            holder.tvShopAddr.setVisibility(View.GONE);
        } else {
            holder.tvShopAddr.setVisibility(View.VISIBLE);
            holder.tvShopAddr.setText(taskInfo.getCustAddr());
        }

        if (taskInfo.getTelNo() == null || "".equals(taskInfo.getTelNo())) {
            holder.tvTelno.setVisibility(View.GONE);
        } else {
            holder.tvTelno.setVisibility(View.VISIBLE);
            holder.tvTelno.setText(taskInfo.getTelNo());
        }
/*

        if (taskInfo.getCustName() == null || "".equals(taskInfo.getCustName())) {
            holder.tvShopContact.setVisibility(View.GONE);
        } else {
            holder.tvShopContact.setVisibility(View.VISIBLE);
            holder.tvShopContact.setText(taskInfo.getCustName());
        }
*/

        if ("5".equals(taskInfo.getStatus())) { //未完成
            holder.tvFinishDays.setText("T + " + taskInfo.getFinishLimitDays());
        } else {
            String dayResult;
            if (taskInfo.getDelayDays() > 0) {
                dayResult = "已经超时" + taskInfo.getDelayDays() + "天请尽快处理";
                holder.tvFinishDays.setTextColor(Color.parseColor("#eb4f38"));
            } else if (taskInfo.getDelayDays() < 0) {
                dayResult = "还有" + taskInfo.getDelayDays() * (-1) + "天到截止时间";
            } else {
                dayResult = "今天到截止时间";
                holder.tvFinishDays.setTextColor(Color.parseColor("#ff33b5e5"));
            }
            holder.tvFinishDays.setText("T + " + taskInfo.getFinishLimitDays() + " ( " + dayResult + " ) ");
        }
        holder.tvTaskLevel.setText(mSession.getSysDictMap().get(Constants.TASK_LEVEL).get(taskInfo.getTaskLevel()));
        holder.tvStatus.setText(mSession.getSysDictMap().get(Constants.TASK_STATUS).get(taskInfo.getStatus()));

        if (taskInfo.getFinishTimeStr() != null) {
            holder.tvFinishTime.setText(taskInfo.getFinishTimeStr());
        }
        return view;
    }


    public static class Holder {
        LinearLayout llTaskInfo;
        TextView tvTaskBizId;
        TextView tvTaskType;
        TextView tvTaskSubject;
        TextView tvBeginTime;
        TextView tvShopName;
        TextView tvShopAddr;
        TextView tvTelno;
//        TextView tvShopContact; //网点联系人
        TextView tvFinishDays;
        TextView tvStatus; //工单状态
        TextView tvTaskLevel; //工单等级
        TextView tvFinishTime; //工单完成时间
        CheckBox checkbox;
    }

    public List<TaskInfo> getTaskInfoList() {
        return taskInfoList;
    }

    public void setTaskInfoList(List<TaskInfo> taskInfoList) {
        this.taskInfoList = taskInfoList;
    }
}
