package com.lakala.elive.preenterpiece.swapmenurecyleview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

import com.lakala.elive.merapply.interfaces.MyAdapterItemClickListener;
import com.lakala.elive.preenterpiece.swapmenurecyleview.SwapWrapperUtils;
import com.lakala.elive.preenterpiece.swapmenurecyleview.SwipeMenuBuilder;
import com.lakala.elive.preenterpiece.swapmenurecyleview.view.SwipeMenuLayout;
import com.lakala.elive.preenterpiece.swapmenurecyleview.view.SwipeMenuView;

import java.util.ArrayList;
import java.util.List;

/**
 */
public abstract class SwapMyBaseAdapter<T> extends RecyclerView.Adapter {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;

    private int mLayoutResId;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private final List<T> mDatas;
    private View mHeaderView;
    private MyAdapterItemClickListener mOnItemClickListener;

    private SwipeMenuBuilder swipeMenuBuilder;

    public SwapMyBaseAdapter(int layoutResId, Context context) {
        this.mLayoutResId = layoutResId;
        mDatas = new ArrayList<>();
        mContext = context;
        swipeMenuBuilder = (SwipeMenuBuilder) context;
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
    public SwapBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据数据创建右边的操作view
        SwipeMenuView menuView = swipeMenuBuilder.create();
        //包装用户的item布局
        SwipeMenuLayout swipeMenuLayout = SwapWrapperUtils.wrap(parent, mLayoutResId, menuView, new BounceInterpolator(), new LinearInterpolator());
        this.mContext = parent.getContext();
        if (null != mHeaderView && viewType == TYPE_HEADER) {
            return new SwapBaseViewHolder(mHeaderView, TYPE_HEADER);
        }
        this.mLayoutInflater = LayoutInflater.from(this.mContext);
        return new SwapBaseViewHolder(this, swipeMenuLayout, this.mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) return;
        convert((SwapBaseViewHolder) holder, this.mDatas.get(getRealPosition(holder)));
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return null == mHeaderView ? position : position - 1;
    }

    protected abstract void convert(SwapBaseViewHolder viewHolder, T data);

    @Override
    public int getItemCount() {
        return null == mHeaderView ? mDatas.size() : mDatas.size() + 1;
    }

}
