package com.lakala.elive.preenterpiece.swapmenurecyleview.adapter;

import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.lakala.elive.merapply.interfaces.MyAdapterItemClickListener;


/**
 */
public class SwapBaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final SparseArray<View> views = new SparseArray();
    private SwapMyBaseAdapter adapter;
    public View convertView;
    private MyAdapterItemClickListener itemClickListener;

    public SwapBaseViewHolder(SwapMyBaseAdapter adapter, View itemView, MyAdapterItemClickListener itemClickListener) {
        super(itemView);
        this.adapter = adapter;
        this.convertView = itemView;
        this.itemClickListener = itemClickListener;
        itemView.setOnClickListener(this);
    }

    public SwapBaseViewHolder(View itemView, int i) {
        super(itemView);
        return;
    }

    public View getConvertView() {
        return this.convertView;
    }


    public SwapBaseViewHolder setText(int viewId, CharSequence value) {
        TextView view = this.getView(viewId);
        view.setText(value);
        return this;
    }

    public SwapBaseViewHolder setText(int viewId, @StringRes int strId) {
        TextView view = this.getView(viewId);
        view.setText(strId);
        return this;
    }

    public SwapBaseViewHolder setTextColor(int viewId, int textColor) {
        TextView view = this.getView(viewId);
        view.setTextColor(textColor);
        return this;
    }


    public <T extends View> T getView(int viewId) {
        View view = this.views.get(viewId);
        if (view == null) {
            view = this.convertView.findViewById(viewId);
            this.views.put(viewId, view);
        }
        return (T) view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == this.itemView.getId() && null != this.itemClickListener) {
            this.itemClickListener.onAdapterItemClick(v, adapter.getRealPosition(this));
        }
    }
}
