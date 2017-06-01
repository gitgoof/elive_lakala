package com.lakala.shoudan.activity.shoudan.finance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.shoudan.finance.bean.TransDetailProInfo;

import java.util.List;

/**
 * Created by Administrator on 2015/10/14.
 */
public class AllProdAdapter extends BaseAdapter{

    private Context context;
    private List<TransDetailProInfo> data;

    public AllProdAdapter(Context context, List<TransDetailProInfo> data) {
        this.context = context;
        this.data = data;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.pop_product_list_item,null);
            holder.name = (TextView) convertView.findViewById(R.id.tv_prod_name);
            holder.tick = (ImageView) convertView.findViewById(R.id.iv_tick);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(data.get(position).getProName());
        if (data.get(position).isTick()){
            holder.tick.setVisibility(View.VISIBLE);
        }else {
            holder.tick.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder{
        TextView name;
        ImageView tick;
    }
}
