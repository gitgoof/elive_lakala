package com.lakala.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.lakala.ui.R;

import java.util.List;

/**
 * Created by linmq on 2016/6/24.
 */
public class BluetoothListAdapter extends ArrayAdapter<String> {
    private int selectedPosition = 0;
    public BluetoothListAdapter(Context context, List<String> data) {
        super(context, -1,data);
    }

    public BluetoothListAdapter setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        return this;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Context context = parent.getContext();
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_bluetooth_list, null);
            holder.icon = (ImageView)convertView.findViewById(R.id.icon);
            holder.tvName = (TextView)convertView.findViewById(R.id.tv_name);
            holder.tvStatus = (TextView)convertView.findViewById(R.id.tv_status);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        String data = getItem(position);
        holder.tvName.setText(data);
        boolean isSelected = position == selectedPosition;
        holder.icon.setSelected(isSelected);
        holder.tvName.setSelected(isSelected);
        holder.tvStatus.setSelected(isSelected);
        if(position == selectedPosition){
            holder.tvStatus.setText("已选择");
        }else {
            holder.tvStatus.setText("未选择");
        }
        return convertView;
    }
    class ViewHolder{
        ImageView icon;
        TextView tvName,tvStatus;
    }
}
