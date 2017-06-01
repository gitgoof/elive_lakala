package com.lakala.shoudan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.datadefine.CashRecordList;

import java.util.List;

/**
 * Created by linmq on 2016/6/14.
 */
public class CashRecordListAdapter extends ArrayAdapter<CashRecordList.Detail> {
    public CashRecordListAdapter(Context context, List<CashRecordList.Detail> data) {
        super(context, -1, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.adapter_cash_recordlist, null);
            holder = new ViewHolder();
            holder.tvTime = (TextView)convertView.findViewById(R.id.tv_time);
            holder.tvType = (TextView)convertView.findViewById(R.id.tv_type);
            holder.tvAmount = (TextView)convertView.findViewById(R.id.tv_amount);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        CashRecordList.Detail data = getItem(position);
        holder.tvTime.setText(data.getCreateTime());
        holder.tvType.setText(data.getTypeEnum().getDesc());
        holder.tvAmount.setText(data.getAmount());
        return convertView;
    }
    private class ViewHolder{
        private TextView tvTime,tvType,tvAmount;
    }
}
