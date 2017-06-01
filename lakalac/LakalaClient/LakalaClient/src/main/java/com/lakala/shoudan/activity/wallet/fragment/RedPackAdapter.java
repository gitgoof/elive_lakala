package com.lakala.shoudan.activity.wallet.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lakala.library.util.DateUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.wallet.bean.RedPackageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengx on 2015/12/1.
 */
public class RedPackAdapter extends BaseAdapter{

    private Context context;
    private RedPackageInfo data;
    private Type type;

    public RedPackAdapter(Context context, RedPackageInfo data,Type type) {
        this.context = context;
        this.data = data;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }


    @Override
    public int getCount() {

        return data.getGiftList().size();
    }

    @Override
    public Object getItem(int position) {
        return data.getGiftList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_red_package,null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.red_package_name);
            holder.usage = (TextView) convertView.findViewById(R.id.red_package_usage);
            holder.usage1 = (TextView) convertView.findViewById(R.id.usage);
            holder.date = (TextView) convertView.findViewById(R.id.red_package_date);
            holder.date1 = (TextView) convertView.findViewById(R.id.date);
            holder.amount = (TextView) convertView.findViewById(R.id.red_package_amount);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        RedPackageInfo.GiftListEntity entity = data.getGiftList().get(position);
        holder.name.setText(entity.getGiftName());
        holder.usage.setText("抵扣使用");//红包用途写死：抵扣使用
        holder.date.setText(DateUtil.formatDateWithDian(entity.getGiftStartDate()) + "-" + DateUtil.formatDateWithDian(entity.getGiftEndDate()));
        holder.amount.setText(entity.getGiftBalance() + "元");

        //如果是未使用的红包
        if (type.getType() == Type.UNUSE.getType()){
            holder.name.setTextColor(context.getResources().getColor(R.color.gray_333333));
            holder.usage.setTextColor(context.getResources().getColor(R.color.gray_666666));
            holder.usage1.setTextColor(context.getResources().getColor(R.color.gray_666666));
            holder.date.setTextColor(context.getResources().getColor(R.color.gray_666666));
            holder.date1.setTextColor(context.getResources().getColor(R.color.gray_666666));
            holder.amount.setTextColor(context.getResources().getColor(R.color.red_package_amount));
        }else {
            holder.name.setTextColor(context.getResources().getColor(R.color.gray_999999));
            holder.usage.setTextColor(context.getResources().getColor(R.color.gray_999999));
            holder.usage1.setTextColor(context.getResources().getColor(R.color.gray_999999));
            holder.date.setTextColor(context.getResources().getColor(R.color.gray_999999));
            holder.date1.setTextColor(context.getResources().getColor(R.color.gray_999999));
            holder.amount.setTextColor(context.getResources().getColor(R.color.gray_999999));
        }
        return convertView;
    }

    class ViewHolder{
        TextView name;
        TextView usage;
        TextView usage1;
        TextView date;
        TextView date1;
        TextView amount;
    }

    public enum  Type{
        UNUSE(0),
        USED(1),
        OUTDATE(2);


        private int type;

        Type(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }
}
