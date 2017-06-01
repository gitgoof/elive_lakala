package com.lakala.elive.task.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.beans.MerShopInfo;
import com.lakala.elive.common.widget.Tag;
import com.lakala.elive.common.widget.TagListView;
import com.lakala.elive.market.adapter.MerListViewAdpter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaofeng on 2017/4/6.
 */

public class SelectStoreListAdapter extends BaseAdapter {
    private final int isVisibility;
    private List<MerShopInfo> merInfoList;
    private LayoutInflater inflater;
    private Context mContext;

    public SelectStoreListAdapter(Context context, List<MerShopInfo> merInfoList, int isVisibility) {
        this.mContext = context;
        if (merInfoList != null) {
            this.merInfoList = merInfoList;
        }
        inflater = LayoutInflater.from(context);
        this.isVisibility = isVisibility;
    }

    public List<MerShopInfo> getMerInfoList() {
        return merInfoList;
    }

    public void setMerInfoList(List<MerShopInfo> merInfoList) {
        this.merInfoList = merInfoList;
    }

    @Override
    public int getCount() {
        return merInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return merInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Holder holder = null;
        if (view == null) {
            holder = new Holder();
            view = inflater.inflate(R.layout.item_select_store_list_layout, null);
            holder.tvMerchantCode = (TextView) view.findViewById(R.id.tv_merchant_code);
            holder.tvShopName = (TextView) view.findViewById(R.id.tv_shop_name);
            holder.tvShopAddr = (TextView) view.findViewById(R.id.tv_shop_addr);
            holder.tvCreateTime = (TextView) view.findViewById(R.id.tv_create_time);
            holder.tvVisitTime = (TextView) view.findViewById(R.id.tv_visit_time);
            holder.ivVisitStatus = (ImageView) view.findViewById(R.id.iv_visit_status);
            holder.mTagListView = (TagListView) view.findViewById(R.id.tagview);
            holder.checkbox = (CheckBox) view.findViewById(R.id.checkbox);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        //初始化条目信息
        final MerShopInfo merInfo = merInfoList.get(position);
        holder.tvMerchantCode.setText(merInfo.getMerchantCode());
        holder.tvShopName.setText(merInfo.getShopName() + "(" + merInfo.getShopNo() + ")");
        holder.tvShopAddr.setText(merInfo.getShopAddress());
        holder.tvCreateTime.setText(merInfo.getCreateTimeStr());
        holder.tvVisitTime.setText(merInfo.getLastVisitTimeStr());

        // TODO 根据todayTaskFlag来确认checkbox是否显示
        final String todayTaskFlag = merInfo.getTodayTaskFlag();
        if(!TextUtils.isEmpty(todayTaskFlag)&&todayTaskFlag.equals("0")){
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    merInfo.setChecked(isChecked);
                }
            });
        } else {
            holder.checkbox.setVisibility(View.INVISIBLE);
        }
        if (merInfo.getIsVisitToday() == 1) {
            holder.ivVisitStatus.setBackgroundResource(R.drawable.visit_yes_icon);
        } else {
            holder.ivVisitStatus.setBackgroundResource(R.drawable.visit_no_icon);
        }

        //打标签
        List<Tag> mTags = new ArrayList<Tag>();

        if (merInfo.getShopCnt() > 1) {
            Tag tag = new Tag();
            tag.setId(0);
            tag.setChecked(true);
            tag.setTitle("多网点商户");
            mTags.add(tag);
        } else {
            Tag tag = new Tag();
            tag.setId(0);
            tag.setChecked(true);
            tag.setTitle("单网点商户");
            mTags.add(tag);
        }

        if (merInfo.getTermCnt() > 1) {
            Tag tag = new Tag();
            tag.setId(1);
            tag.setChecked(true);
            tag.setTitle("多终端网点");
            mTags.add(tag);
        } else if (merInfo.getTermCnt() == 1){
            Tag tag = new Tag();
            tag.setId(1);
            tag.setChecked(true);
            tag.setTitle("单终端网点");
            mTags.add(tag);
        }else{
            Tag tag = new Tag();
            tag.setId(1);
            tag.setChecked(true);
            tag.setTitle("无终端网点");
            mTags.add(tag);
        }


        holder.mTagListView.setTags(mTags);

        return view;
    }


    public static class Holder {
        TextView tvMerchantName;
        TextView tvMerchantCode;
        TextView tvShopName;
        TextView tvShopAddr;
        TextView tvCreateTime; //最后拜访时间
        TextView tvVisitTime; //最后拜访时间
        ImageView ivVisitStatus; // 拜访状态
        TagListView mTagListView; //标签标记
        CheckBox checkbox;
    }
}
