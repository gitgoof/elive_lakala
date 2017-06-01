package com.lakala.platform.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lakala.ui.common.SuperViewHolder;

import java.util.List;


/**
 * Created by Administrator on 2016/4/9 0009.
 */
public abstract class MyBasesAdapter<T> extends BaseAdapter{
    protected Context mCotext;
    protected List<T>mData;
    private int mItemLayoutId;
    public MyBasesAdapter(Context mCotext, List<T>mData, int mItemLayoutId){
        this.mCotext=mCotext;
        this.mData=mData;
        this.mItemLayoutId=mItemLayoutId;
    }
    public void clear(){
        mData.clear();
        notifyDataSetChanged();
    }
    public void reflash(List<T>data){
        mData.clear();
        if (data.size()>0){
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SuperViewHolder viewHolder=getViewHolder(position,convertView,parent);
        convert(viewHolder,getItem(position));
        return viewHolder.getConvertView();
    }

    public  void convert(SuperViewHolder viewHolder,T item){};//填充数据
    private SuperViewHolder getViewHolder(int position,View convertView, ViewGroup parent){
            return SuperViewHolder.getViewHolder(mCotext,convertView,parent,mItemLayoutId,position);
    }
}

