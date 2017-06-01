package com.lakala.elive.merapply.adapter;

import com.lakala.elive.R;
import com.lakala.elive.common.net.resp.MyMerchantsListResp;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wenhaogu on 2017/1/20.
 */

public class MyMerchantsAdapter extends MyBaseAdapter<MyMerchantsListResp.ContentBean.RowsBean> {
    public MyMerchantsAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, MyMerchantsListResp.ContentBean.RowsBean data) {
        viewHolder.setText(R.id.tv_date, data.getApplyTimeStr())
                .setText(R.id.tv_state, data.getAccountKind().equals("58") ? "对私" : "对公")
                .setText(R.id.tv_store_name, data.getMerchantName())
                .setText(R.id.tv_phone, data.getMobile())
                .setText(R.id.tv_address, data.getAddress())
                .setText(R.id.tv_applyid, data.getApplyId());

    }

    private String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d1 = new Date(time);
        return format.format(d1);
    }


}
