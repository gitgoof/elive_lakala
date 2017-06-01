package com.lakala.elive.map.adapter;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.beans.TaskListReqResp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by gaofeng on 2017/3/22.
 * 工单列表的适配器
 */
public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.PointItemBean> {

    private final ArrayList<TaskListReqResp.ContentBean> mList = new ArrayList<TaskListReqResp.ContentBean>();
    private OnItemClickListener mOnItemClickListener;
    public void destory(){
        mList.clear();
        mOnItemClickListener = null;
    }
    public void setmOnItemClickListener(OnItemClickListener listener){
        mOnItemClickListener = listener;
    }
    public void setShowDataList(ArrayList<TaskListReqResp.ContentBean> list){
        mList.clear();
        mList.addAll(list);
        this.notifyDataSetChanged();
    }

    @Override
    public PointItemBean onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 1&&mHeadView!=null)return new PointItemBean(mHeadView,1);
        return new PointItemBean(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_point_info,parent,false));
    }

    public void removeItemByPos(int position){
        final int listPosition = position;
        if(mHeadView!= null){
            position++;
        }
        if(getItemCount()<=position)return;
        mList.remove(listPosition);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,getItemCount());

    }

    public int getmIntBeginIndex() {
        return mIntBeginIndex;
    }
    public void setmIntBeginIndex(int mIntBeginIndex) {
        this.mIntBeginIndex = mIntBeginIndex;
        setBeginPostion(mIntBeginIndex);
    }
    private int mIntBeginIndex = -1;
    private void setBeginPostion(int postion){
        mIntBeginIndex = postion;
        for(int i = 0;i < mList.size();i++){
            final TaskListReqResp.ContentBean bean = mList.get(i);
            if(i == postion){
                if(bean.planOrder != 0)
                    bean.planOrder = 0;
            } else {
                if(bean.planOrder == 0){
                    bean.planOrder = -1;
                }
            }
        }
        this.notifyDataSetChanged();
    }
    @Override
    public int getItemViewType(int position) {
        if(position == 0&&mHeadView!=null)return 1;
        return super.getItemViewType(position);
    }

    private TextView mHeadView ;
    public void setHeadView(TextView view){
        mHeadView = view;
    }
//    {"orderType":1,"merchantCode":"822100054990407",
// "outTaskId":"","shopAddr":"北京市石景山区古城大街西侧古城饭庄",
// "shopName":"北京京西朱恩英食品店","userId":"200518",
// "telNo":"","shopNo":"105278",
// "taskId":"5771567872792684679159","createDate":1489680000000},
    @Override
    public void onBindViewHolder(PointItemBean holder, int position) {
        if(getItemViewType(position) == 1)return;
        TaskListReqResp.ContentBean bean = null;
        if(mHeadView != null){
            bean = mList.get(position-1);
        } else {
            bean = mList.get(position);
        }
        // 根据类型显示
        int orderType = bean.getOrderType();// 1 为 网点。

        holder.viewMe.setVisibility(orderType==1?View.VISIBLE:View.GONE);

//        holder.tvFinishDay.setVisibility(orderType==1?View.GONE:View.VISIBLE);
        holder.tvTel.setVisibility(orderType==1?View.GONE:View.VISIBLE);
        // TODO 根据类型不同显示不同
        holder.tvTaskCode.setText(bean.getOrderType()==1?checkString(bean.getMerchantCode()):checkString(bean.getTaskBizId()));
        // TODO 设置该单的类型
        holder.tvTaskType.setText(checkString(bean.getTaskTypeName()));
        // TODO 设置创建时间
        holder.tvCreateTime.setText(checkString( bean.getBeginTimeStr()));
        // TODO 设置订单状态
        holder.tvTaskStatus.setText(checkString(bean.getStatusName()));
        // TODO 设置结束时间
        holder.tvEndTime.setText(checkString(bean.getFinishTimeStr()));

        if(orderType == 1){
            String shopNo = bean.getShopNo();
            if(TextUtils.isEmpty(shopNo)){
                shopNo = "";
            } else {
                shopNo = "  (" + shopNo + ")";
            }
            if(TextUtils.isEmpty(shopNo)){
                holder.tvName.setVisibility(View.GONE);
            } else {
                holder.tvName.setVisibility(View.VISIBLE);
                if(TextUtils.isEmpty(bean.getShopName())){
                    holder.tvName.setText(checkString(bean.getShopNo()));
                } else {
                    holder.tvName.setText(checkString(bean.getShopName())+shopNo);
                }
            }
        } else {
            // TODO 这里判断是否需要显示名称。这里我暂定显示为custName。
            String custName = bean.getCustName();
            if(TextUtils.isEmpty(custName)){
                holder.tvName.setVisibility(View.GONE);
            } else {
                holder.tvName.setVisibility(View.VISIBLE);
                holder.tvName.setText(checkString(custName));
            }
        }

        String shopAddr = bean.getShopAddr();
        if(TextUtils.isEmpty(shopAddr)){
            holder.tvAddress.setVisibility(View.GONE);
        } else {
            holder.tvAddress.setVisibility(View.VISIBLE);
            holder.tvAddress.setText(shopAddr);
        }
        if(orderType==2){
            String merchantCode = bean.getMerchantCode();
            if(TextUtils.isEmpty(merchantCode)){
                holder.tvTel.setVisibility(View.GONE);
            } else {
                holder.tvTel.setVisibility(View.VISIBLE);
                holder.tvTel.setText(merchantCode);
            }
        }
        String finishLimitDays = bean.getFinishLimitDays();
        if ("5".equals(bean.getStatus())) { //已经完成
            if(TextUtils.isEmpty(finishLimitDays)){
                holder.tvFinishDay.setText("");
                if(TextUtils.isEmpty(bean.getFinishTimeStr())){
                    holder.viewInf.setVisibility(View.GONE);
                }
            } else {
                holder.viewInf.setVisibility(View.VISIBLE);
                holder.tvFinishDay.setText("T + " + finishLimitDays);
                holder.tvFinishDay.setTextColor(Color.parseColor("#ff798089"));
            }
        } else {
            String dayResult;
            if (bean.getDelayDays() > 0) {
                dayResult = "已经超时" + bean.getDelayDays() + "天请尽快处理";
                holder.tvFinishDay.setTextColor(Color.parseColor("#eb4f38"));
            } else if (bean.getDelayDays() < 0) {
                dayResult = "还有" + bean.getDelayDays()*(-1) + "天到截止时间";
                holder.tvFinishDay.setTextColor(Color.parseColor("#ff798089"));
            } else {
                dayResult = "今天到截止时间";
                holder.tvFinishDay.setTextColor(Color.parseColor("#ff33b5e5"));
            }
            if(TextUtils.isEmpty(finishLimitDays)){
                holder.tvFinishDay.setText("");
                if(TextUtils.isEmpty(bean.getFinishTimeStr())){
                    holder.viewInf.setVisibility(View.GONE);
                }
            } else {
                holder.tvFinishDay.setText("T + " + finishLimitDays + " ( " + dayResult + " ) ");
            }
        }

        holder.viewDel.setTag(position);
        holder.viewMsg.setTag(position);
        holder.viewBegin.setTag(position);

        if(bean.planOrder==0){
            holder.viewBegin.setVisibility(View.INVISIBLE);
        } else {
            holder.viewBegin.setVisibility(View.VISIBLE);
        }

    }
    private String getDateString(long da){
        Date date = new Date(da);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }
    private String checkString(String string){
        if(TextUtils.isEmpty(string))return "";
        return string;
    }

    public boolean canLoadMore(){
        return mHeadView==null?false:true;
    }
    public void setHeadViewHeight(int height){
        if(mHeadView!=null){
            mHeadView.setHeight(height);
        }
    }
    public void animationHeadView(){
        if(mHeadView==null)return;
        ObjectAnimator animator = ObjectAnimator.ofInt(mHeadView,"height",mHeadView.getHeight(),0);
        animator.setDuration(300);
        animator.start();
    }

    public int getDataSize(){
        return mList.size();
    }
    @Override
    public int getItemCount() {
        if(mHeadView==null)return mList.size();
        return mList.size()+1;
    }

    public class PointItemBean extends RecyclerView.ViewHolder{

        View viewMe;
        View viewInf;

        TextView tvTaskCode;
        TextView tvTaskType;
        TextView tvCreateTime;
        TextView tvTaskStatus;

        TextView tvFinishDay;
        TextView tvEndTime;

        TextView tvName;
        TextView tvAddress;
        TextView tvTel;

        View viewDel;
        View viewMsg;
        View viewBegin;

        public PointItemBean(View headView,int type){
            super(headView);
        }
        public PointItemBean(View itemView) {
            super(itemView);

            viewMe = itemView.findViewById(R.id.ll_dot_merchant);
            viewInf = itemView.findViewById(R.id.rl_task_info_d);
            tvName = (TextView) itemView.findViewById(R.id.tv_shop_name);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_shop_addr);

            tvTaskCode = (TextView) itemView.findViewById(R.id.tv_task_biz_id);
            tvTaskType = (TextView) itemView.findViewById(R.id.tv_task_type);
            tvCreateTime = (TextView) itemView.findViewById(R.id.tv_begin_time);
            tvTaskStatus = (TextView) itemView.findViewById(R.id.tv_item_point_task_status);
            tvFinishDay = (TextView) itemView.findViewById(R.id.tv_finish_days);
            tvEndTime = (TextView) itemView.findViewById(R.id.tv_finish_time);

            tvTel = (TextView) itemView.findViewById(R.id.tv_shop_telno);

            View.OnClickListener listener = new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    final int postion = Integer.valueOf(v.getTag().toString());
                    switch (v.getId()){
                        case R.id.delete:
                            if(mOnItemClickListener != null){
                                mOnItemClickListener.onItemDelClick(postion,mList.get(postion));
                            }
                            break;
                        case R.id.tv_item_point_info_msg:
                            if(mOnItemClickListener != null){
                                mOnItemClickListener.onItemMsg(postion,mList.get(postion));
                            }
                            break;
                        case R.id.tv_item_point_info_begin:
                            // TODO 设置起点。
                            /*
                            if(mOnItemClickListener != null){
                                mOnItemClickListener.onItemBegin(postion,mList.get(postion));
                            }
                            */
                            setBeginPostion(postion);
                            break;
                    }
                }
            };
            viewDel = itemView.findViewById(R.id.delete);
            viewMsg = itemView.findViewById(R.id.tv_item_point_info_msg);
            viewBegin = itemView.findViewById(R.id.tv_item_point_info_begin);
            viewDel.setOnClickListener(listener);
            viewMsg.setOnClickListener(listener);
            viewBegin.setOnClickListener(listener);
        }

    }
    public static interface OnItemClickListener{
        void onItemClick(int position,Object object);
        void onItemDelClick(int position,Object object);
        void onItemBegin(int position,Object object);
        void onItemMsg(int position,Object object);
    }

}
