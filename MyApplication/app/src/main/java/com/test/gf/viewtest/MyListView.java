package com.test.gf.viewtest;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by gaofeng on 2017/4/21.
 */

public class MyListView extends ListView implements AbsListView.OnScrollListener{
    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOnScrollListener(this);
        initThis();
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setOnScrollListener(this);
    }

    private TextView mTvScrollBar;
    private void initThis(){
        mTvScrollBar = new TextView(getContext());
        mTvScrollBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        int duction = ViewConfiguration.getScrollBarFadeDuration();
        mTvScrollBar.setText("我的tvScrollBar");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mTvScrollBar != null){
            measureChild(mTvScrollBar,widthMeasureSpec,heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        // 确定View的摆放位置
        Log.i("g--","onLayout----l:" + l + "<>" + t + "<>" + r + "<>" + b);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        // 在ViewGroup绘制分发完，再在上面绘画一个自己的气泡
        // view本身就包含了坐标情况 -- onLayout() 确定View的摆放位置
        if(mTvScrollBar != null){
            drawChild(canvas,mTvScrollBar,getDrawingTime());
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    protected boolean awakenScrollBars(int startDelay, boolean invalidate) {
        // 监听判断系统的滑块是否唤醒。true为唤醒。
        final boolean state = super.awakenScrollBars(startDelay, invalidate);
//        Log.i("g--","scrollbar 的状态:" + state);
        return state;
    }

    private int mThumbOffset = 0;
    private int mScrollBarPosition = 0;
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(mTvScrollBar == null)return;
        // 不断的去求这个ScrolBarPanelPosition
        // 把气泡控件摆放在该位置
//        requestLayout();
        // 滑块的高度：  滑块的高度／ListView的高度 = extent／range。

        int scrollExtent = computeVerticalScrollExtent();// 滑动块的高度
        int scrollOffset = computeVerticalScrollOffset();// 滑动块的偏移量。从0开始
        int scrollRange = computeVerticalScrollRange();// 滑动块的移动范围
        int scrollH = Math.round(1.0f*getMeasuredHeight() * scrollExtent/scrollRange);
        int meaH = mTvScrollBar.getMeasuredHeight();
        int meaW = mTvScrollBar.getMeasuredWidth();

        // 滑块中间的位置。滑块高度/extent = thumboffset/offset;
        mThumbOffset = (int) (1.0f*scrollH*scrollOffset/scrollExtent);
        mThumbOffset += scrollH/2;

        mScrollBarPosition = mThumbOffset - meaH/2;

        int left = getMeasuredWidth() - meaW - getVerticalScrollbarWidth();
        Log.i("g--","滑动的--Scrolbar的属性----:" + mScrollBarPosition + "<>" + left);
        mTvScrollBar.layout(
                left,
                mScrollBarPosition,
                left + meaW,
                mScrollBarPosition + meaH);
    }
}
