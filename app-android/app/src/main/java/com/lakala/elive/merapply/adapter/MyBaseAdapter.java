package com.lakala.elive.merapply.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lakala.elive.merapply.interfaces.MyAdapterItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenhaogu on 2017/1/17.
 */

public abstract class MyBaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;

    private int mLayoutResId;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private final List<T> mDatas;
    private View mHeaderView;
    private MyAdapterItemClickListener mOnItemClickListener;

    public MyBaseAdapter(int layoutResId) {
        this.mLayoutResId = layoutResId;
        mDatas = new ArrayList<>();
    }

    public void setHeaderView(View headerView) {
        this.mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public void setNewData(List<T> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void addData(List<T> datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(MyAdapterItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public List<T> getData() {
        return this.mDatas;
    }

    public T getItemData(int position) {
        return this.mDatas.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (null == mHeaderView) return TYPE_NORMAL;
        if (position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }

    protected View getItemView(int layoutResId, ViewGroup parent) {
        return this.mLayoutInflater.inflate(layoutResId, parent, false);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        if (null != mHeaderView && viewType == TYPE_HEADER) {
            return new BaseViewHolder(mHeaderView, TYPE_HEADER);
        }
        this.mLayoutInflater = LayoutInflater.from(this.mContext);
        return new BaseViewHolder(this,getItemView(mLayoutResId, parent), this.mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) return;
        convert((BaseViewHolder) holder, this.mDatas.get(getRealPosition(holder)));
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return null == mHeaderView ? position : position - 1;
    }

    protected abstract void convert(BaseViewHolder viewHolder, T data);

    @Override
    public int getItemCount() {
        return null == mHeaderView ? mDatas.size() : mDatas.size() + 1;
    }

}
