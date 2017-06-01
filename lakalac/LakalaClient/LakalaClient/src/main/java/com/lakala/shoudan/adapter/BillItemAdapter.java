package com.lakala.shoudan.adapter;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;

import java.util.List;

/**
 * Created by linmq on 2016/3/31.
 */
public class BillItemAdapter extends BaseAdapter {
    List<ItemData> mData;
    private View.OnClickListener onDetailClickListener;

    public View.OnClickListener getOnDetailClickListener() {
        return onDetailClickListener;
    }

    public BillItemAdapter setOnDetailClickListener(View.OnClickListener onDetailClickListener) {
        this.onDetailClickListener = onDetailClickListener;
        return this;
    }

    public BillItemAdapter(List<ItemData> mData) {
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public ItemData getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemData itemData = getItem(position);
        View child = null;
        if (itemData != null && itemData.isContent) {
            TransferInfoEntity data = itemData.getTransInfo();
            if(convertView == null){
                child = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bill_content, null);
            }else {
                child = convertView;
            }
            TextView tvLabel = (TextView) child.findViewById(R.id.tv_label);
            TextView tvValue = (TextView) child.findViewById(R.id.tv_value);
            ImageView ivLogo = (ImageView)child.findViewById(R.id.iv_logo);

            int labelSize = -1;
            int valueSize = -1;
            if(itemData.isDetail){
                child.setPadding(child.getPaddingLeft(),child.getPaddingTop(),child.getPaddingRight(),
                        parent.getResources().getDimensionPixelSize(R.dimen.dimen_16));
                labelSize = parent.getResources().getDimensionPixelSize(R.dimen.mer_limit_text_size);
                valueSize = parent.getResources().getDimensionPixelSize(R.dimen.mer_limit_text_size);
            }else {
                labelSize = parent.getResources().getDimensionPixelSize(R.dimen.medium_text_size);
                valueSize = parent.getResources().getDimensionPixelSize(R.dimen.medium_text_size);
                child.setPadding(child.getPaddingLeft(),child.getPaddingTop(),child.getPaddingRight(),
                        parent.getResources().getDimensionPixelSize(R.dimen.dimen_40));
            }
            tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX,labelSize);
            tvValue.setTextSize(TypedValue.COMPLEX_UNIT_PX,valueSize);
            tvLabel.setText(data.getName());

            tvValue.setText(data.getValue());
            tvValue.setHint(data.getValueHint());
            tvValue.setHintTextColor(data.getValueColor());
            if(itemData.isAmount){
                tvValue.setTextColor(parent.getResources().getColor(R.color.amount_color));
                tvValue.setTextSize(TypedValue.COMPLEX_UNIT_PX,parent.getResources().getDimensionPixelSize(R.dimen.big_text_size));
            }
            if(itemData.getLogoRes() != 0){
                ivLogo.setBackgroundResource(itemData.getLogoRes());
                ivLogo.setVisibility(View.VISIBLE);
            }else {
                ivLogo.setVisibility(View.GONE);
            }
        } else {
            if(convertView == null){
                child = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bill_center_divider, null);
            }else {
                child = convertView;
            }
            child.setOnClickListener(onDetailClickListener);
        }
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        child.setLayoutParams(params);
        return child;
    }

    public static class ItemData {
        private TransferInfoEntity transInfo;
        private boolean isContent;
        private boolean isDetail = false;
        private boolean isAmount = false;
        private int logoRes;

        public ItemData() {
        }

        public ItemData(String name, String value) {
            transInfo = new TransferInfoEntity(name,value);
        }

        public int getLogoRes() {
            return logoRes;
        }

        public ItemData setLogoRes(int logoRes) {
            this.logoRes = logoRes;
            return this;
        }

        public boolean isDetail() {
            return isDetail;
        }

        public ItemData setDetail(boolean detail) {
            isDetail = detail;
            return this;
        }

        public boolean isAmount() {
            return isAmount;
        }

        public ItemData setAmount(boolean amount) {
            isAmount = amount;
            return this;
        }

        public boolean isContent() {
            return isContent;
        }

        public ItemData setContent(boolean content) {
            isContent = content;
            return this;
        }

        public TransferInfoEntity getTransInfo() {
            return transInfo;
        }

        public ItemData setTransInfo(TransferInfoEntity transInfo) {
            this.transInfo = transInfo;
            return this;
        }
    }
}
