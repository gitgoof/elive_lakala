package com.lakala.shoudan.activity.shoudan.finance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lakala.shoudan.R;

import java.util.List;

/**
 * Created by fengx on 2015/10/20.
 */
public class BankChooseAdapter extends BaseAdapter{

    private Context context;
    private List<String> data;
    private float lvHeight,tvHeight;

    public BankChooseAdapter(Context context, List<String> data,float lvHeight) {
        this.context = context;
        this.data = data;
        this.lvHeight = lvHeight;
        tvHeight = lvHeight/26;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_charlist,null);
            holder.tvChar = (TextView) convertView.findViewById(R.id.tv_char);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvChar.setText(data.get(position));
        holder.tvChar.setHeight((int)tvHeight);
        return convertView;
    }

    class ViewHolder{
        TextView tvChar;
    }
}
