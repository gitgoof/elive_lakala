package com.lakala.shoudan.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.R;
import com.lakala.shoudan.datadefine.PayType;

import java.util.List;

/**
 * 选择支付类型适配器
 */
public class PayChooseTypeAdapter extends BaseAdapter {
    private List<PayType> data;
    Context ctx;
    private LayoutInflater layoutInflater;
    public PayChooseTypeAdapter(Context context,List<PayType> data) {
        this.data = data;
        ctx=context;
        this.layoutInflater = LayoutInflater.from(context);
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
        ViewHolder holder = null;
        if(null==convertView){
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.dialog_pay_choose_type_item,null);
            holder.tv_type = (TextView)convertView.findViewById(R.id.tv_type);
            holder.tv_hint=(TextView)convertView.findViewById(R.id.tv_hint);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        PayType item = getItem(position);
        holder.tv_type.setText(item.getShowText());
        if(position==getCount()-1){
            int userType= ApplicationEx.getInstance().getUser().getUserType();
                if(userType==1){
                    holder.tv_hint.setVisibility(View.VISIBLE);
                }else{
                    holder.tv_hint.setVisibility(View.GONE);
                }

        }else{
            holder.tv_hint.setVisibility(View.GONE);
        }
        return convertView;
    }
    public final class ViewHolder{
        TextView tv_type;
        TextView tv_hint;
    }
}
