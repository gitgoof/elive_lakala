package com.lakala.elive.merapply.adapter;

import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.*;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lakala.elive.R;

import java.util.List;

/**
 * Created by zhouzx on 2017/3/10.
 */
public class HorizontalRecyclerAdapter extends BaseQuickAdapter<String> {


    private List<String> data;

    public HorizontalRecyclerAdapter(int layoutResId, List<String> data) {
        super(layoutResId, data);
        this.data = data;

    }


    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv, item);
        if(item.equals("自动载入工单"))
        helper.setBackgroundRes(R.id.tv,R.drawable.item_hrecycleview_shape_ora);
    }
}
