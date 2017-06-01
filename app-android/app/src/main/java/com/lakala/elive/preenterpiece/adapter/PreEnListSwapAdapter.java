package com.lakala.elive.preenterpiece.adapter;

import android.content.Context;
import android.view.View;

import com.lakala.elive.R;
import com.lakala.elive.preenterpiece.response.PreEnPieceListResponse;
import com.lakala.elive.preenterpiece.swapmenurecyleview.adapter.SwapBaseViewHolder;
import com.lakala.elive.preenterpiece.swapmenurecyleview.adapter.SwapMyBaseAdapter;

/**
 * Created by ousachisan on 2017/3/21.(swapmenu)
 */
public class PreEnListSwapAdapter extends SwapMyBaseAdapter<PreEnPieceListResponse.ContentBean.PartnerApplyInfo> {

    public PreEnListSwapAdapter(int layoutResId, Context context) {
        super(layoutResId, context);
    }

    @Override
    protected void convert(SwapBaseViewHolder viewHolder, PreEnPieceListResponse.ContentBean.PartnerApplyInfo data) {
        viewHolder.setText(R.id.tv_applyid, data.getApplyId());
        viewHolder.setText(R.id.tv_applystyle, data.getApplyType());
        viewHolder.setText(R.id.tv_time, data.getCreateTimeStr());
        viewHolder.getView(R.id.tv_mechantname).setVisibility(View.GONE);

        String statue = data.getStatus();
        if ("0".equals(statue)) {
            viewHolder.setText(R.id.tv_mechantststue, "待录入");//0：待录入 1：已提交、2：处理中、3：处理成功、4：处理失败
        } else if ("1".equals(statue)) {
            viewHolder.setText(R.id.tv_mechantststue, "已提交");
        } else if ("2".equals(statue)) {
            viewHolder.setText(R.id.tv_mechantststue, "处理中");
        } else if ("3".equals(statue)) {
            viewHolder.setText(R.id.tv_mechantststue, "处理成功");
        } else if ("4".equals(statue)) {
            viewHolder.setText(R.id.tv_mechantststue, "处理失败");
        }

        viewHolder.setText(R.id.tv_mechantaddress, data.getAddress());
        viewHolder.setText(R.id.tv_mechantphone, data.getMobile());
    }

}

