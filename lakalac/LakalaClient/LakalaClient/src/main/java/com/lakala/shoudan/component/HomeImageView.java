package com.lakala.shoudan.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;

/**
 * Created by LMQ on 2015/12/28.
 */
public class HomeImageView extends ImageView {
    private ObjectAnimator mAnimator;
    private static final int DURATION = 300;

    public HomeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HomeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1, 0.6f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY",1,0.6f);
        mAnimator = ObjectAnimator
                .ofPropertyValuesHolder(this, scaleX, scaleY);
        mAnimator.setDuration(DURATION);
        mAnimator.setInterpolator(new LinearInterpolator());
    }
    private boolean isStarting = false;
    private void start(){
        if(!isStarting){
            mAnimator.start();
            isStarting = true;
        }
    }
    private void reverse(){
        if(isStarting){
            mAnimator.reverse();
            isStarting = false;
        }
    }

    private long downTime = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean tag = false;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                if (mAnimator != null && !isStarting) {
                    downTime = System.currentTimeMillis();
                    start();
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:{
                tag = true;
                break;
            }
            case MotionEvent.ACTION_UP:{
                long upTime = System.currentTimeMillis();
                long duration = DURATION - (upTime - downTime);
                if(mAnimator == null && !isStarting){
                    break;
                }
                if(duration <= 0){
                    reverse();
                }else{
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reverse();
                        }
                    },duration);
                }
                break;
            }
        }
        return tag?tag:super.dispatchTouchEvent(event);
    }
}
