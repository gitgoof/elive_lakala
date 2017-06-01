package com.lakala.elive.merapply.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lakala.elive.R;
import com.lakala.elive.beans.TaskListReqResp;

import java.util.List;

/**
 * Created by zhouzx on 2017/3/10.
 */
public class PointInfoRecycleAdapter extends BaseQuickAdapter<TaskListReqResp.ContentBean> {

    public PointInfoRecycleAdapter(int layoutResId, List<TaskListReqResp.ContentBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, TaskListReqResp.ContentBean s) {
        baseViewHolder.setVisible(R.id.ll_dot_merchant, s.getOrderType() == 1 ? true : false)
                .setVisible(R.id.rl_task_info_d, s.getOrderType() == 1 ? false : true)
                .setText(R.id.tv_shop_name,s.getShopName()+"  "+"("+s.getShopNo()+")")
                .setText(R.id.tv_shop_addr,s.getShopAddr())
                .setText(R.id.tv_shop_telno,s.getTelNo())
                .addOnClickListener(R.id.delete)
                .addOnClickListener(R.id.tv_item_point_info_msg)
                .addOnClickListener(R.id.tv_item_point_info_begin);


    }
}
