package com.lakala.shoudan.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.GridView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Created by LMQ on 2015/12/17.
 */
public class AlphaGridView extends GridView implements Animator.AnimatorListener {
    private ObjectAnimator animator;
    private boolean isStart;
    private boolean isReverse;
    private Runnable reverseListener;
    private long duration;

    public AlphaGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public AlphaGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setVisibility(View.GONE);
        animator = ObjectAnimator.ofFloat(this,"alpha",0,1);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(this);
    }

    public AlphaGridView setDuration(long duration) {
        this.duration = duration;
        animator.setDuration(duration);
        return this;
    }

    public AlphaGridView setOnReverseEndListener(Runnable listener){
        reverseListener = listener;
        return this;
    }

    public void start(){
        startDelay(0);
    }
    public void startDelay(long delay){
        if(animator != null){
            isStart = true;
            isReverse = false;
            if(delay != 0){
                animator.setStartDelay(delay);
            }
            animator.start();
        }
    }
    public void reverse(){
        if(animator != null){
            isStart = false;
            isReverse = true;
            animator.reverse();
        }
    }


    @Override
    public void onAnimationStart(Animator animator) {
        setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        if(isReverse){
            setVisibility(View.GONE);
            if(reverseListener != null){
                reverseListener.run();
            }
        }
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}
