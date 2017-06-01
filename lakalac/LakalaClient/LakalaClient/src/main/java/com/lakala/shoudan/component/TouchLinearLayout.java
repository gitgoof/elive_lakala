package com.lakala.shoudan.component;

import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by ZhangMY on 2015/3/10.
 */
public class TouchLinearLayout extends LinearLayout{

    private static final String TAG = TouchLinearLayout.class.getName();
    private static final int MIN_FLING_DISTANCE = 50;

    private float startY = 0;

    public TouchLinearLayout(android.content.Context context) {
        super(context);

    }

    public TouchLinearLayout(android.content.Context context, android.util.AttributeSet attrs) {
        super(context,attrs);
    }

    public float getStartY(){
        return startY;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        int what = ev.getAction();
        switch (what) {
            case MotionEvent.ACTION_DOWN:
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.w(TAG, "action_move");
                break;
            case MotionEvent.ACTION_UP:
//                Log.w(TAG, "action_up");
                break;
            case MotionEvent.ACTION_CANCEL:
//                Log.w(TAG, "action_cancel");
                break;
            default:
                break;
        }
        final float y = ev.getY();
        // 如果X方向滑动的距离大于指定距离则拦截手势动作，执行本View的OnTouch事件
        if(Math.abs(y-startY) > MIN_FLING_DISTANCE)
        {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                float y = event.getY();
//                if(y > startY)
//                {
//                    Toast.makeText(getContext(), "MainViewOnTouch down", Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                    Toast.makeText(getContext(), "MainViewOnTouch up", Toast.LENGTH_SHORT).show();
//                }
                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }

}
