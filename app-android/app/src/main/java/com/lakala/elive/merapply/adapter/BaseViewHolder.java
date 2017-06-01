package com.lakala.elive.merapply.adapter;

import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.lakala.elive.merapply.interfaces.MyAdapterItemClickListener;


/**
 * Created by wenhaogu on 2017/1/20.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    private final SparseArray<View> views = new SparseArray();
    private MyBaseAdapter adapter;
    public View convertView;
    private MyAdapterItemClickListener itemClickListener;

    public BaseViewHolder(MyBaseAdapter adapter, View itemView, MyAdapterItemClickListener itemClickListener) {
        super(itemView);
        this.adapter = adapter;
        this.convertView = itemView;
        this.itemClickListener = itemClickListener;
        itemView.setOnClickListener(this);
    }

    public BaseViewHolder(View itemView, int i) {
        super(itemView);
        return;
    }

    public View getConvertView() {
        return this.convertView;
    }


    public BaseViewHolder setText(int viewId, CharSequence value) {
        TextView view = (TextView) this.getView(viewId);
        view.setText(value);
        return this;
    }

    public BaseViewHolder setText(int viewId, @StringRes int strId) {
        TextView view = (TextView) this.getView(viewId);
        view.setText(strId);
        return this;
    }

    public BaseViewHolder setTextColor(int viewId, int textColor) {
        TextView view = (TextView) this.getView(viewId);
        view.setTextColor(textColor);
        return this;
    }


//    public BaseViewHolder setOnItemClickListener(int viewId, AdapterView.OnItemClickListener listener) {
//        AdapterView view = (AdapterView) this.getView(viewId);
//        view.setOnItemClickListener(listener);
//        return this;
//    }

    public <T extends View> T getView(int viewId) {
        View view = (View) this.views.get(viewId);
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
