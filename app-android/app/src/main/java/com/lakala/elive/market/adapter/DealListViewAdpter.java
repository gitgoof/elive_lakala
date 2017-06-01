package com.lakala.elive.market.adapter;

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
import android.widget.Toast;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.beans.TaskInfo;
import com.lakala.elive.common.utils.StringUtil;

import java.util.List;
import java.util.Map;


/**
 * 工单处理
 *
 * @author hongzhiliang
 */
public class DealListViewAdpter extends BaseAdapter {

    private final int isVisibility;
    private List<TaskInfo> taskInfoList;
    private LayoutInflater inflater;

    private Context mContext;
    protected Session mSession;


    public DealListViewAdpter(Context context, List<TaskInfo> taskInfoList, int isVisibility) {
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
            view = inflater.inflate(R.layout.list_item_task_deal, null);
            holder.llTaskInfo = (LinearLayout) view.findViewById(R.id.ll_task_info);
            holder.tvTaskBizId = (TextView) view.findViewById(R.id.tv_task_biz_id);
            holder.tvTaskType = (TextView) view.findViewById(R.id.tv_task_type);
            holder.tvBeginTime = (TextView) view.findViewById(R.id.tv_begin_time);
            holder.tvFinishTime = (TextView) view.findViewById(R.id.tv_finish_time);
            holder.tvTaskSubject = (TextView) view.findViewById(R.id.tv_task_subject); //工单标题
            holder.tvShopName = (TextView) view.findViewById(R.id.tv_shop_name);
            holder.tvShopAddr = (TextView) view.findViewById(R.id.tv_shop_addr);
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
        TaskInfo taskInfo = taskInfoList.get(position);
        if(taskInfo == null){
            return view;
        }
        holder.tvTaskBizId.setText(taskInfo.getTaskBizId());
//        if(mSession!=null && taskInfo!=null && !TextUtils.isEmpty(taskInfo.getTaskType())){
//            Map<String, Map<String, String>> dictMap = mSession.getSysDictMap();
//            if(dictMap !=null && dictMap.size() > 0){
//                holder.tvTaskType.setText(dictMap.get(Constants.TASK_TYPE).get(taskInfo.getTaskType()));
//            }else{
//
//            }
//        }
        if(StringUtil.isNotNullAndBlank(taskInfo.getTaskTypeName())){
            holder.tvTaskType.setText(taskInfo.getTaskTypeName());
        }

        holder.tvBeginTime.setText(taskInfo.getBeginTimeStr());
        holder.tvTaskSubject.setText(taskInfo.getTaskSubject());


        if (isVisibility == 0) {
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    taskInfoList.get(position).setChecked(isChecked);
                }
            });
        }


        if (StringUtil.isNullOrBlank(taskInfo.getShopNo())) {
            holder.tvShopName.setVisibility(View.GONE);
        } else {
            if(StringUtil.isNullOrBlank(taskInfo.getShopName())){
                holder.tvShopName.setVisibility(View.VISIBLE);
                holder.tvShopName.setText( taskInfo.getShopNo());
            }else{
                holder.tvShopName.setText( taskInfo.getShopName()+ "("+  taskInfo.getShopNo() +")");
            }

        }
        if (taskInfo.getCustAddr() == null || "".equals(taskInfo.getCustAddr())) {
            holder.tvShopAddr.setVisibility(View.GONE);
        } else {
            holder.tvShopAddr.setVisibility(View.VISIBLE);
            holder.tvShopAddr.setText(taskInfo.getCustAddr());
        }
        if (StringUtil.isNullOrBlank(taskInfo.getMerchantCode()) ) {
            holder.tvTelno.setVisibility(View.GONE);
        } else {
            holder.tvTelno.setVisibility(View.VISIBLE);
            holder.tvTelno.setText(taskInfo.getMerchantCode());
        }
        /*
        if (StringUtil.isNullOrBlank(taskInfo.getTelNo()) && StringUtil.isNullOrBlank(taskInfo.getCustName()) ) {
                holder.tvTelno.setVisibility(View.GONE);
        } else {
            holder.tvTelno.setVisibility(View.VISIBLE);
            if(StringUtil.isNotNullAndBlank(taskInfo.getTelNo())){
                if(StringUtil.isNotNullAndBlank(taskInfo.getCustName() + " " + taskInfo.getCustName())){
                    holder.tvTelno.setText(taskInfo.getTelNo());
                }else{
                    holder.tvTelno.setText(taskInfo.getCustName());
                }
            }else{
                if(StringUtil.isNotNullAndBlank(taskInfo.getCustName())){
                    holder.tvTelno.setText(taskInfo.getCustName());
                }
            }
        }
        */
        // TODO 显示终端号

        if ("5".equals(taskInfo.getStatus())) { //已经完成
            holder.tvFinishDays.setText("T + " + taskInfo.getFinishLimitDays());
            holder.tvFinishDays.setTextColor(Color.parseColor("#ff798089"));
        } else {
            String dayResult;
            if (taskInfo.getDelayDays() > 0) {
                dayResult = "已经超时" + taskInfo.getDelayDays() + "天请尽快处理";
                holder.tvFinishDays.setTextColor(Color.parseColor("#eb4f38"));
            } else if (taskInfo.getDelayDays() < 0) {
                dayResult = "还有" + taskInfo.getDelayDays()*(-1) + "天到截止时间";
                holder.tvFinishDays.setTextColor(Color.parseColor("#ff798089"));
            } else {
                dayResult = "今天到截止时间";
                holder.tvFinishDays.setTextColor(Color.parseColor("#ff33b5e5"));
            }
            holder.tvFinishDays.setText("T + " + taskInfo.getFinishLimitDays() + " ( " + dayResult + " ) ");
        }
        if(StringUtil.isNotNullAndBlank(taskInfo.getTaskLevelName())){
            holder.tvTaskLevel.setText(taskInfo.getTaskLevelName());
        }
        if(StringUtil.isNotNullAndBlank(taskInfo.getStatusName())){
            holder.tvStatus.setText(taskInfo.getStatusName());
        }
        if (taskInfo.getFinishTimeStr() != null) {
            holder.tvFinishTime.setText(taskInfo.getFinishTimeStr());
        }else{
            holder.tvFinishTime.setText("");
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
