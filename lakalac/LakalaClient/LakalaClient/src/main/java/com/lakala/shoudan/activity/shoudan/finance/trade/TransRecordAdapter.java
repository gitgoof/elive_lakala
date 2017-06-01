package com.lakala.shoudan.activity.shoudan.finance.trade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.activity.shoudan.finance.bean.TransDetail;

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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_financial_product_detail,null);
            holder.date = (TextView) convertView.findViewById(R.id.financial_detail_date);
            holder.name = (TextView) convertView.findViewById(R.id.financial_detail_product);
            holder.amount = (TextView) convertView.findViewById(R.id.financial_detail_amount);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        String[] time = data.get(position).getTradeTime().split(" ");
        holder.date.setText(time[0]);
        holder.name.setText(data.get(position).getTradeName() + " " + data.get(position).getProdName());
        String tradeType = data.get(position).getTradeType();
        //(1申购,2赎回,3本金返还,4收益返还,5转让,6退款)
        //2和3绿色，4红色
        if (tradeType.equalsIgnoreCase("2") || tradeType.equalsIgnoreCase("3")){
            double amount = data.get(position).getAmount();
            String text  = format(amount)+"元";
            holder.amount.setText(text);
            holder.amount.setTextColor(context.getResources().getColor(R.color.take_out_amount));
        }else if(tradeType.equalsIgnoreCase("4")){
            double amount = data.get(position).getAmount();
            String text  = format(amount)+"元";
            holder.amount.setText(text);
            holder.amount.setTextColor(context.getResources().getColor(R.color.amount_color));
        }else {
            double amount = data.get(position).getAmount();
            String text  = format(amount)+"元";
            holder.amount.setText(text);
            holder.amount.setTextColor(context.getResources().getColor(R.color.black));
        }

        return convertView;
    }

    public String format(double amount){

        return Util.formatTwo(amount);
    }
    class ViewHolder{
        TextView date;
        TextView name;
        TextView amount;
    }
}
