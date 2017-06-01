package com.lakala.platform.swiper.mts.payment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * ListView 数据适配器,带有泛型
 * @author jack
 * @param <T>
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {

	protected Context mContext ;
	protected LayoutInflater mInflater ;
	protected List<T> mdatas;
	
	@Override
	public int getCount() {
		return mdatas != null ? mdatas.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mdatas.get(position);
	}

	@Override
	public long getItemId(int postion) {
		return 0;
	}

	@Override
	public View getView(final int position , View currentView, ViewGroup root) {
		
		T data = mdatas.get(position);
		currentView = bindView(data, position, currentView, root);
		return currentView;
	}
	
	protected abstract View bindView(T data,final int position , View currentView , ViewGroup root);
}
