package com.lakala.shoudan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.wallet.bean.RedPackageInfo;

import java.util.List;

/**
 * Created by LMQ on 2015/12/31.
 */
public class RedPackageDialogListAdapter extends BaseAdapter {
    private List<RedPackageInfo.GiftListEntity> mData;
    private int selectedPosition = -1;

    public RedPackageDialogListAdapter(List<RedPackageInfo.GiftListEntity> mData) {
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData == null?0:mData.size();
    }

    @Override
    public RedPackageInfo.GiftListEntity getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public RedPackageDialogListAdapter setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        return this;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.redpkg_list_item,null);
            holder.icon = (ImageView)convertView.findViewById(R.id.icon);
            holder.text = (TextView)convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        RedPackageInfo.GiftListEntity gift = getItem(position);
        if(gift == null){
            holder.text.setText("不使用红包");
        }else{
            holder.text.setText(gift.getShowSpanned());
        }
        if(position == selectedPosition){
            holder.icon.setBackgroundResource(com.lakala.ui.R.drawable.home_icon_xzan_sel);
        }else{
            holder.icon.setBackgroundResource(com.lakala.ui.R.drawable.home_icon_xzan_nol);
        }
        return convertView;
    }
    private class ViewHolder{
        ImageView icon;
        TextView text;
    }
}
