package com.test.gf.viewtest;

/**
 * Created by gaofeng on 2017/4/25.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.ViewUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ListView;

import com.test.gf.myandroidtest.R;

/**
 * 类功能描述：</br>
 *
 * @author yuyahao
 * @version 1.0 </p> 修改时间：</br> 修改备注：</br>
 */

public class NetListView extends ListView implements AbsListView.OnScrollListener {
    private View mScrollPanelView;
    private int mMeasureWidthSpect;
    private int mMeasureHeghtSpect;
    /**
     * 定义y轴的滑动变量，在onScroll里不断去判断和负值
     */
    private int mScrollbarPanelPosition = 0;
    private Animation mComeInAnimation = null;
    private Animation mGoOutAnimation = null;

    /**
     * 定义指示器在Y轴的高度
     */
    private int mThumbOffset = 0;
    private int mLastPosition = -1;
//    private OnPositionListener onPositionListener;

    public NetListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOnScrollListener(this);
/*
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.MyScrollbarListview);
        int LayoutId = -1, inAnimation = -1, outAnimation = -1;
        if (typeArray != null) {
            LayoutId = typeArray.getResourceId(R.styleable.MyScrollbarListview_scrollBarPanel, -1);
            inAnimation = typeArray.getResourceId(R.styleable.MyScrollbarListview_scrollBarComeInAnamation, -1);
            outAnimation = typeArray.getResourceId(R.styleable.MyScrollbarListview_scrollBarGoOutAnamation, -1);
            typeArray.recycle();
        }
        if (inAnimation != -1) {
            mComeInAnimation = AnimationUtils.loadAnimation(context, inAnimation);
        } else {
            mComeInAnimation = AnimationUtils.loadAnimation(context, R.anim.transation_animation_left);
        }
        if (outAnimation != -1) {
            mGoOutAnimation = AnimationUtils.loadAnimation(context, outAnimation);
        } else {
            mGoOutAnimation = AnimationUtils.loadAnimation(context, R.anim.transation_animation_right);
        }
        */
        setMyPanelViewId();
        /*
        int drutionMmillis = ViewConfiguration.getScrollBarFadeDuration();//得到系统默认的淡出的时间
        mGoOutAnimation.setDuration(drutionMmillis);
        mGoOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mScrollPanelView != null) {
                    mScrollPanelView.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });*/
    }

    private void setMyPanelViewId() {
        mScrollPanelView = LayoutInflater.from(getContext()).inflate(R.layout.item_layout, this, false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);//先测量父容器，在对子布局进行测量
        if (mScrollPanelView != null && getAdapter() != null) {
            measureChild(mScrollPanelView, widthMeasureSpec, heightMeasureSpec);
            this.mMeasureWidthSpect = widthMeasureSpec;
            this.mMeasureHeghtSpect = heightMeasureSpec;
//            requestLayout();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mScrollPanelView != null) {
            int left = getMeasuredWidth() - mScrollPanelView.getMeasuredWidth() - mScrollPanelView.getVerticalScrollbarWidth();
            mScrollPanelView.layout(left,
                    mScrollbarPanelPosition,
                    left + mScrollPanelView.getMeasuredWidth(),
                    mScrollbarPanelPosition + mScrollPanelView.getMeasuredHeight()
            );
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //在ViewGroup绘制的事后
        if (mScrollPanelView != null && mScrollPanelView.getVisibility() == VISIBLE) {
            drawChild(canvas, mScrollPanelView, getDrawingTime());
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstItem, int visisbleTotle, int allTotle) {
        //监听系统滑块在那个位置，设置总结的位置
        //监听回调---》去设置位置---》position
        if (mScrollPanelView != null) {
            /**
             * computeHorizontalScrollExtent //滑动条在纵向范围内 自身的高度的幅度--》放大后的
             * computeHorizontalScrollOffset //滑动条在纵向 的位置--》放大后的
             * computeHorizontalScrollRange //比如0-5000,滑动范围--》放大后的
             */
            //利用 平行线分线段成比例定理进行 取值
            //1.划片的高度 / listView的高度 = computeHorizontalScrollExtent / computeHorizontalScrollRange
            int height = Math.round(1.0f * computeVerticalScrollExtent() * getMeasuredHeight() / computeVerticalScrollRange());
            mThumbOffset = (int) (1.0f * computeVerticalScrollOffset() * height / computeVerticalScrollExtent());
            int left2 = getMeasuredWidth() - mScrollPanelView.getMeasuredWidth() - mScrollPanelView.getVerticalScrollbarWidth();
            //2得到 滑块Y正中央的位置Y
            mThumbOffset = mThumbOffset + height / 2;
            mScrollbarPanelPosition = mThumbOffset - height / 2;
            mScrollPanelView.layout(
                    left2,
                    mScrollbarPanelPosition,
                    left2 + mScrollPanelView.getMeasuredWidth(),
                    mScrollbarPanelPosition + mScrollPanelView.getMeasuredHeight()

            );
            /*
            for (int j = 0; j < getChildCount(); j++) {
                View view = getChildAt(j);
                if (view != null) {
                    if (mThumbOffset + height / 2 > view.getTop() && mThumbOffset + height / 2 < view.getBottom()) {
                        if (mLastPosition != firstItem + j) {
                            mLastPosition = firstItem + j;
                            measureChild(mScrollPanelView, mMeasureWidthSpect, mMeasureHeghtSpect);
                        }
                    }
                }
            }
            */
        }

    }

    @Override
    protected boolean awakenScrollBars(int startDelay, boolean invalidate) {
        boolean isWake = super.awakenScrollBars(startDelay, invalidate);
        /*
        if (isWake && mScrollPanelView != null) {
            if (mScrollPanelView.getVisibility() == GONE) {
                mScrollPanelView.setVisibility(VISIBLE);
                if (mComeInAnimation != null) {
                    mScrollPanelView.startAnimation(mComeInAnimation);
                }
            }
            mHandler.removeCallbacksAndMessages(null);
            mHandler.removeCallbacks(runTask);
            //设置小时
            mHandler.postAtTime(runTask, startDelay + AnimationUtils.currentAnimationTimeMillis());
        }
        */
        return isWake;
    }

    private Handler mHandler = new Handler();
    private Runnable runTask = new Runnable() {
        @Override
        public void run() {
            if (mGoOutAnimation != null) {
                mScrollPanelView.startAnimation(mGoOutAnimation);
            }
        }
    };

}
