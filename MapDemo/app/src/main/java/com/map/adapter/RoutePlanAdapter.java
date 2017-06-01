package com.map.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.map.R;
import com.map.bean.MyMarker;

/**
 * Created by xiaogu on 2017/3/14.
 */

public class RoutePlanAdapter extends BaseQuickAdapter<MyMarker, BaseViewHolder> {
    public RoutePlanAdapter() {
        super(R.layout.item_routeplan);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, MyMarker myMarker) {
        baseViewHolder.setText(R.id.tv_title, myMarker.getTitle());
        if (baseViewHolder.getLayoutPosition() == 0) {//第一个
            baseViewHolder.setText(R.id.tv_order, "起点");
            baseViewHolder.setBackgroundRes(R.id.tv_order, R.drawable.bg_green);
        } else if (baseViewHolder.getLayoutPosition() == (getItemCount() - 1)) {//最后一个
            baseViewHolder.setText(R.id.tv_order, "终点");
            baseViewHolder.setBackgroundRes(R.id.tv_order, R.drawable.bg_red);
        } else {
            baseViewHolder.setText(R.id.tv_order, baseViewHolder.getLayoutPosition() + "");
            baseViewHolder.setBackgroundRes(R.id.tv_order, R.drawable.bg_white);
        }
    }
}

