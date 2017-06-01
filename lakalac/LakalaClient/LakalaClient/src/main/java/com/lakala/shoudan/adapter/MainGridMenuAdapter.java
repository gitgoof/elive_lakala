package com.lakala.shoudan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.datadefine.MainMenu;

import java.util.Arrays;
import java.util.List;

/**
 * Created by LMQ on 2015/12/17.
 */
public class MainGridMenuAdapter extends BaseAdapter {
    private Context context;
    private List<MainMenu> data;
    private boolean hasContribute = true;

    public MainGridMenuAdapter(Context context, List<MainMenu> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public MainMenu getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.adapter_grid_menu_item, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.type = (ImageView) convertView.findViewById(R.id.iv_type);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MainMenu item = getItem(position);
        holder.icon.setBackgroundResource(item.getIcon());

//        if (position == data.size() - 2 || position == data.size() - 3)
//            holder.icon.setVisibility(View.INVISIBLE);
//        else
//            holder.icon.setBackgroundResource(item.getIcon());
        int type = item.getType();
        if (type == 0) {
            holder.type.setVisibility(View.INVISIBLE);
        } else {
            holder.type.setBackgroundResource(type);
        }
        return convertView;
    }

    /**
     * 隐藏特约商户缴费
     */
    public void hideContribute() {
        if (data != null) {
            data.remove(MainMenu.特约商户缴费);
            hasContribute = false;
            notifyDataSetChanged();
        }
    }

    /**
     * 显示特约商户缴费
     */
    public void showContribute() {
        if (data != null && !hasContribute) {
            data.clear();
            data.addAll(Arrays.asList(MainMenu.values()));
            hasContribute = true;
            notifyDataSetChanged();
        }
    }

    public boolean hasContribute() {
        return hasContribute;
    }

    class ViewHolder {
        private ImageView icon;
        private ImageView type;
    }
}
