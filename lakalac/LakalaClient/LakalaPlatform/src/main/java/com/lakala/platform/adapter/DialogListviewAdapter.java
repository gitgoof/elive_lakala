package com.lakala.platform.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.platform.R;

import java.util.ArrayList;

/**
 * Created by HUASHO on 2015/2/4.
 * 对话框中电话列表的适配器
 */
public class DialogListviewAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<String> list;
    private class Holder{
        TextView tv_phone;
    }
    public DialogListviewAdapter(Context context,ArrayList<String> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = RelativeLayout.inflate(context, R.layout.item_listview_dialog, null);
            holder = new Holder();
            holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }
        holder.tv_phone.setText(list.get(position));
        holder.tv_phone.setTag(position);
        return convertView;
    }
}
