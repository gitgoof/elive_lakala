package com.lakala.ui.component;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TableLayout;

import com.lakala.library.util.LogUtil;

/**
 * Created by linmq on 2016/3/31.
 */
public class CustomTableListView extends TableLayout {
    private BaseAdapter mAdapter;
    private DataSetObserver dataSetObserver = null;

    public CustomTableListView(Context context) {
        super(context);
        init(context, null);
    }

    public CustomTableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
    }

    private void resetList() {
        removeAllViews();
    }

    public void setAdapter(BaseAdapter adapter) {
        if (mAdapter != null && dataSetObserver != null) {
            mAdapter.unregisterDataSetObserver(dataSetObserver);
        }
        resetList();
        mAdapter = adapter;
        if (mAdapter != null) {
            dataSetObserver = new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    int count = mAdapter.getCount();
                    int childCount = getChildCount();
                    for(int i = 0;i<count;i++){
                        View convertView = getChildAt(i);
                        if(convertView != null){
                            mAdapter.getView(i,convertView,CustomTableListView.this);
                        }else {
                            convertView = mAdapter.getView(i,null,CustomTableListView.this);
                            addView(convertView);
                        }
                    }
                    if(childCount > count){
                        removeViews(count,childCount-count);
                    }
                }

                @Override
                public void onInvalidated() {
                    super.onInvalidated();
                }
            };
            mAdapter.registerDataSetObserver(dataSetObserver);
            int count = mAdapter.getCount();
            for (int i = 0; i < count; i++) {
                View child = mAdapter.getView(i, null, this);
                addView(child);
            }
        }
    }
}
