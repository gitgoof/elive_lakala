package com.lakala.shoudan.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lakala.shoudan.R;

public class ListViewBaseAdapter extends BaseAdapter{
	
	
	public void setViewStyle(int position,View convertView,int count) {
		if (position == 0) {
			if (position == count - 1) {
				//只有一项
				convertView.setBackgroundResource(R.drawable.list_corner_round_selector);
			} else {
				//第一项
				convertView.setBackgroundResource(R.drawable.list_corner_round_top_selector);
			}
		} else if (position == (count - 1))
			//最后一项
			convertView.setBackgroundResource(R.drawable.list_corner_round_bottom_selector);
		else {
			//中间一项
			convertView.setBackgroundResource(R.drawable.list_corner_round_center_selector);
		}
	
	}

	@Override
	public int getCount() {
		
		return 0;
	}

	@Override
	public Object getItem(int position) {
		
		return null;
	}

	@Override
	public long getItemId(int position) {
		
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		return null;
	}

}
