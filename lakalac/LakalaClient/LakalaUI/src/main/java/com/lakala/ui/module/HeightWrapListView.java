package com.lakala.ui.module;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 高度包裹内容的ListView
 * Created by WangCheng on 2016/8/25.
 */
public class HeightWrapListView extends ListView{
    public HeightWrapListView(Context context) {
        super(context);
    }

    public HeightWrapListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeightWrapListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int myHeight=MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, myHeight);
    }
}
