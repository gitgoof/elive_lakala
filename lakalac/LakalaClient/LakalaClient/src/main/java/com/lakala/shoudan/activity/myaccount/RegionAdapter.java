package com.lakala.shoudan.activity.myaccount;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lakala.shoudan.R;

/**
 * 区域adapter
 * @author zmy
 *
 */
public class RegionAdapter extends BaseAdapter{

    private Context context;
    private List<Region> regionList;
    public RegionAdapter(Context context, List<Region> regionList) {
        this.context = context;
        this.regionList = regionList;
    }

    @Override
    public int getCount() {
        return regionList.size();
    }

    @Override
    public Object getItem(int i) {
        return regionList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
            view = LayoutInflater.from(context).inflate(R.layout.region_list_item, null);
        TextView selection = (TextView)view.findViewById(R.id.tv);
        selection.setText(regionList.get(i).getName());
        return view;
    }
}
