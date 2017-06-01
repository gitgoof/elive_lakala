package com.lakala.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.lakala.ui.R;

/**
 * Created by linmq on 2016/3/29.
 */
public class DynamicHeightListView extends ListView {

    private int maxHeight = (int) getContext().getResources().getDimension(R.dimen.dynamic_list_max_height);
    private boolean useMaxHeight = false;

    public DynamicHeightListView(Context context) {
        super(context);
        init(context,null);
    }

    public DynamicHeightListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DynamicHeightListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context,AttributeSet attrs){
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DynamicHeightListView);
        useMaxHeight = ta.getBoolean(R.styleable.DynamicHeightListView_useMaxHeight,false);
        ta.recycle();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(useMaxHeight){
            try {
                setViewHeightBasedOnChildren();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void setViewHeightBasedOnChildren() {
        ListAdapter listAdapter = this.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // int h = 10;
        // int itemHeight = BdUtilHelper.getDimens(getContext(), R.dimen.ds30);
        int sumHeight = 0;
        int size = listAdapter.getCount();


        for (int i = 0; i < size; i++) {
            View v = listAdapter.getView(i, null, this);
            if(v == null){
                continue;
            }
            v.measure(0, 0);
            sumHeight += v.getMeasuredHeight();
        }


        if (sumHeight > maxHeight) {
            sumHeight = maxHeight;
        }
        android.view.ViewGroup.LayoutParams params = this.getLayoutParams();
        // this.getLayoutParams();
        params.height = sumHeight;


        this.setLayoutParams(params);
    }


    public int getMaxHeight() {
        return maxHeight;
    }


    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

}
