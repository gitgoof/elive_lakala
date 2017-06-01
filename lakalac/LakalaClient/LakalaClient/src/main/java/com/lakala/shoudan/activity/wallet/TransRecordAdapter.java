package com.lakala.shoudan.activity.wallet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.wallet.bean.TransDetail;
import com.lakala.shoudan.common.util.StringUtil;

import java.util.ArrayList;


/**
 * Created by Administrator on 2015/10/12.
 */
public class TransRecordAdapter extends BaseAdapter{

    private ArrayList<TransDetail> data;
    private Context context;

    public TransRecordAdapter(ArrayList<TransDetail> data, Context context) {
        this.data = data;
        this.context = context;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_change_product_detail,null);
            holder.date = (TextView) convertView.findViewById(R.id.change_detail_date);
            holder.typeName = (TextView) convertView.findViewById(R.id.change_detail_type);
            holder.amount = (TextView) convertView.findViewById(R.id.change_detail_amount);
            holder.seperator = convertView.findViewById(R.id.seperator);
            holder.seperator1 = convertView.findViewById(R.id.seperator1);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        TransDetail transDetail = data.get(position);

        holder.date.setText(transDetail.getTransDate() + " " + transDetail.getTransTime());
        holder.typeName.setText(transDetail.getTransName());
        holder.amount.setText(transDetail.getTranAmount());
        if (position == (data.size() - 1)){
            holder.seperator.setVisibility(View.GONE);
            holder.seperator1.setVisibility(View.VISIBLE);
        }else {
            holder.seperator.setVisibility(View.VISIBLE);
            holder.seperator1.setVisibility(View.GONE);
        }
        return convertView;
    }

    public String format(double amount){

        return StringUtil.formatTwo(amount);
    }
    class ViewHolder{
        TextView date;
        TextView typeName;
        TextView amount;
        View seperator;
        View seperator1;
    }
}
