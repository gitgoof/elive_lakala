package com.lakala.shoudan.activity.shoudan.loan;

import java.util.List;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.shoudan.loan.datafine.RegionInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RegionInfoAdapter extends BaseAdapter{
	private Context context;
    private List<RegionInfo> regionList;
    private int currentType;//0,1,2
    
    public RegionInfoAdapter(Context context, List<RegionInfo> regionList,int type) {
        this.context = context;
        this.regionList = regionList;
        this.currentType = type;
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
        switch (currentType) {
		case 0:
			 selection.setText(regionList.get(i).getpNm());
			break;
		case 1:
			 selection.setText(regionList.get(i).getcNm());
			break;
		case 2:
			 selection.setText(regionList.get(i).getaNm());
			break;
		}
       
        return view;
    }
    
    public void setRegionList(List<RegionInfo> regionList){
    	this.regionList = regionList;
    }
}
