package com.lakala.shoudan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.datadefine.PayType;

import java.util.List;

/**
 * Created by LMQ on 2015/10/20.
 */
public class PayTypeItemAdapter extends BaseAdapter {
    private List<PayType> data;


    public PayTypeItemAdapter(List<PayType> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public PayType getItem(int position) {
        if(position >=0 && position < data.size()){
            return data.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tvTypeText;
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.dialog_paytype_item,null);
            tvTypeText = (TextView)convertView.findViewById(R.id.tv_typetext);
            convertView.setTag(tvTypeText);
        }else{
            tvTypeText = (TextView)convertView.getTag();
        }
        PayType item = getItem(position);
        tvTypeText.setText(item.getShowText());
        return convertView;
    }
}
