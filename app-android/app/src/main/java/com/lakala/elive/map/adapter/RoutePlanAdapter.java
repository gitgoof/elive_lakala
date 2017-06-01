package com.lakala.elive.map.adapter;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.beans.MerShopInfo;
import com.lakala.elive.beans.TaskInfo;
import com.lakala.elive.beans.TaskListReqResp;
import com.lakala.elive.common.utils.UiUtils;
import com.lakala.elive.market.activity.MerDetailActivity;
import com.lakala.elive.market.activity.TaskDetailActivity;

import java.util.List;

/**
 * Created by xiaogu on 2017/3/14.
 */
public class RoutePlanAdapter extends BaseQuickAdapter<TaskListReqResp.ContentBean> {
    public RoutePlanAdapter(int layoutid, List<TaskListReqResp.ContentBean> data) {
        super(layoutid,data);
    }
    private int mCurrentIndex = 0;

    public void setmCurrentIndex(int mCurrentIndex) {
        this.mCurrentIndex = mCurrentIndex;
    }

    @Override
    protected void convert(final BaseViewHolder baseViewHolder, final TaskListReqResp.ContentBean myMarker) {

//        baseViewHolder.setText(R.id.tv_path_project_storename, "上海食品中江路店");
        baseViewHolder.setText(R.id.tv_path_project_storename, myMarker.getShopName());
//        baseViewHolder.setText(R.id.tv_path_project_storecore, "(23409123)");
        String shopNo = myMarker.getShopNo();
        if(TextUtils.isEmpty(shopNo)){
            shopNo = "";
        } else {
            shopNo = "(" + shopNo + ")";
        }
        baseViewHolder.setText(R.id.tv_path_project_storecore, shopNo);
//        baseViewHolder.setText(R.id.tv_path_project_storeaddress, "地址:上海市洪山区中江路32号");
        baseViewHolder.setText(R.id.tv_path_project_storeaddress, myMarker.getShopAddr());
        int currentIndex = baseViewHolder.getLayoutPosition();
        if (currentIndex == 0) {//第一个
            baseViewHolder.setText(R.id.tv_order, "起");
            int hasOver = mCurrentIndex;
            if(currentIndex == hasOver){
                baseViewHolder.setBackgroundRes(R.id.tv_order, R.drawable.item_path_project_white_bg);
                baseViewHolder.setBackgroundRes(R.id.img_item_path_project_start,android.R.color.transparent);
                baseViewHolder.setBackgroundRes(R.id.img_item_path_project_end,R.drawable.img_plan_arrows_gray_end);
            } else {
                baseViewHolder.setBackgroundRes(R.id.tv_order, R.drawable.item_path_project_green_bg);
                baseViewHolder.setBackgroundRes(R.id.img_item_path_project_start,android.R.color.transparent);
            }
        } else if (currentIndex == (getItemCount() - 1)) {//最后一个
            baseViewHolder.setText(R.id.tv_order, "终");
            int hasOver = mCurrentIndex;
            if(currentIndex == hasOver){
                baseViewHolder.setBackgroundRes(R.id.tv_order, R.drawable.item_path_project_green_bg);
                baseViewHolder.setBackgroundRes(R.id.img_item_path_project_start,R.drawable.img_plan_arrows_green_start);
                baseViewHolder.setBackgroundRes(R.id.img_item_path_project_end,android.R.color.transparent);
            } else {
                baseViewHolder.setBackgroundRes(R.id.tv_order, R.drawable.item_path_project_red_bg);
                baseViewHolder.setBackgroundRes(R.id.img_item_path_project_start,R.drawable.img_plan_arrows_gray_start);
                baseViewHolder.setBackgroundRes(R.id.img_item_path_project_end,android.R.color.transparent);
            }
        } else {
            final int position = baseViewHolder.getLayoutPosition();
            baseViewHolder.setText(R.id.tv_order, (position+1) + "");
            /*
            if(position == 1){
                baseViewHolder.setBackgroundRes(R.id.img_item_path_project_end,R.drawable.img_plan_arrows_gray_end);
            } else {
                baseViewHolder.setBackgroundRes(R.id.img_item_path_project_start,R.drawable.img_plan_arrows_gray_start);
                baseViewHolder.setBackgroundRes(R.id.img_item_path_project_end,R.drawable.img_plan_arrows_gray_end);
            }
            */
            // TODO 确定哪个点为现在的点
            int hasOver = mCurrentIndex;
            if(position<hasOver){
                baseViewHolder.setBackgroundRes(R.id.tv_order, R.drawable.item_path_project_green_bg);
                baseViewHolder.setBackgroundRes(R.id.img_item_path_project_start,R.drawable.img_plan_arrows_green_start);
                baseViewHolder.setBackgroundRes(R.id.img_item_path_project_end,R.drawable.img_plan_arrows_green_end);
            } else if(position == hasOver){
                baseViewHolder.setBackgroundRes(R.id.tv_order, R.drawable.item_path_project_white_bg);
                baseViewHolder.setBackgroundRes(R.id.img_item_path_project_start,R.drawable.img_plan_arrows_green_start);
                baseViewHolder.setBackgroundRes(R.id.img_item_path_project_end,R.drawable.img_plan_arrows_gray_end);
            } else {
                baseViewHolder.setBackgroundRes(R.id.tv_order, R.drawable.item_path_project_white_bg);
                baseViewHolder.setBackgroundRes(R.id.img_item_path_project_start,R.drawable.img_plan_arrows_gray_start);
                baseViewHolder.setBackgroundRes(R.id.img_item_path_project_end,R.drawable.img_plan_arrows_gray_end);
            }

        }

        baseViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mPlanItemOnClickListener != null){
                    mPlanItemOnClickListener.onClick(myMarker,baseViewHolder.getLayoutPosition());
                }
            }
        });
    }
    private PlanItemOnClickListener mPlanItemOnClickListener;

    public interface PlanItemOnClickListener{
        public void onClick(TaskListReqResp.ContentBean contentBean,int position);
    }

    public void setmPlanItemOnClickListener(PlanItemOnClickListener mPlanItemOnClickListener) {
        this.mPlanItemOnClickListener = mPlanItemOnClickListener;
    }
}

