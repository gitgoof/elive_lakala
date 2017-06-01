package com.map.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.map.R;
import com.map.bean.MyMarker;

/**
 * Created by xiaogu on 2017/3/13.
 */
public class TaskAtapter extends BaseQuickAdapter<MyMarker, BaseViewHolder> {
    public TaskAtapter() {
        super(R.layout.item_task);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, MyMarker myMarker) {
        baseViewHolder.setText(R.id.tv_title, myMarker.getTitle())
                .addOnClickListener(R.id.btn_start)
                .addOnClickListener(R.id.btn_remove);
    }
}
