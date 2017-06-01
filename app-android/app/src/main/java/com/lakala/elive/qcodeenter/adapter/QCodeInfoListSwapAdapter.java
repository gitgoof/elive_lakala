package com.lakala.elive.qcodeenter.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.lakala.elive.R;
import com.lakala.elive.common.utils.ImageTools;
import com.lakala.elive.preenterpiece.swapmenurecyleview.adapter.SwapBaseViewHolder;
import com.lakala.elive.preenterpiece.swapmenurecyleview.adapter.SwapMyBaseAdapter;
import com.lakala.elive.qcodeenter.response.QCodeListResponse;
import com.mining.app.zxing.encoding.EncodingHandler;

/**
 * Created by ousachisan on 2017/3/21.(swapmenu)
 */
public class QCodeInfoListSwapAdapter extends SwapMyBaseAdapter<QCodeListResponse.ContentBean.Qcodes> {

    public QCodeInfoListSwapAdapter(int layoutResId, Context context) {
        super(layoutResId, context);
    }

    @Override
    protected void convert(SwapBaseViewHolder viewHolder, QCodeListResponse.ContentBean.Qcodes data) {
        if(data.getQCode()!=null&&!"null".equals(data.getQCode())){
            viewHolder.setText(R.id.item_qcodeinfo_number,"NO." +data.getQCode());
        }else{
            viewHolder.setText(R.id.item_qcodeinfo_number,"");
        }
        if ("BINDING".equals(data.getStatus())) {//绑定中
            viewHolder.setText(R.id.item_qcodeinfo_statue, "绑定中");
        } else if ("BIND_SUCCESS".equals(data.getStatus())) {//绑定成功
            viewHolder.setText(R.id.item_qcodeinfo_statue, "绑定成功");
        } else if ("BIND_FAIL".equals(data.getStatus())) {//绑定失败
            viewHolder.setText(R.id.item_qcodeinfo_statue, "绑定失败");
        } else if ("UNBINDING".equals(data.getStatus())) {//已解绑
            viewHolder.setText(R.id.item_qcodeinfo_statue, "已解绑");
        }
        viewHolder.setText(R.id.item_qcodeinfo_time, data.getCreateTime());
        ((ImageView) (viewHolder.getView(R.id.item_qcodeinfo_img))).setImageBitmap(EncodingHandler.createQRCode(data.getQcodeUrl(), 90));
    }
}

