package com.lakala.ui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by linmq on 2016/3/31.
 */
public class CustomScrollView extends ScrollView {
    private View ignoreView;

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ignoreView != null && checkArea(ignoreView, ev)) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }
    /**
     *  测试view是否在点击范围内
     * @param
     * @param v
     * @return
     */
    private boolean checkArea(View v, MotionEvent event){
        float x = event.getRawX();
        float y = event.getRawY();
        int[] locate = new int[2];
        v.getLocationOnScreen(locate);
        int l = locate[0];
        int r = l + v.getWidth();
        int t = locate[1];
        int b = t + v.getHeight();
        if (l < x && x < r && t < y && y < b) {
            return true;
        }
        return false;
    }
    public View getIgnoreView() {
        return ignoreView;
    }

    public CustomScrollView setIgnoreView(View ignoreView) {
        this.ignoreView = ignoreView;
        return this;
    }
}
