package com.lakala.shoudan.component;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by LMQ on 2015/11/20.
 */
public class AdViewPager extends ViewPager {
    private boolean isRunning = false;
    public AdViewPager(Context context) {
        super(context);
    }

    public AdViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    int startX,startY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) ev.getX();
                startY = (int) ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_CANCEL:
                if(Math.abs(ev.getX()-startX)>Math.abs(ev.getY()-startY)){
                    getParent().requestDisallowInterceptTouchEvent(true);
                }else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }

            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;

            default:
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    /**
     *
     * @param interval 广告显示时间，单位秒
     */
    public void startAdvertise(final int interval){
        isRunning = true;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                PagerAdapter adapter = getAdapter();
                if(adapter == null || !isRunning){
                    return;
                }
                int count = adapter.getCount();
                if(count == 1){
                    return;
                }
                int currentItem = getCurrentItem();
                setCurrentItem(currentItem+1);
                postDelayed(this,interval*1000);
            }
        };
        post(runnable);
    }
    public void stopAdvertise(){
        isRunning = false;
    }
}
