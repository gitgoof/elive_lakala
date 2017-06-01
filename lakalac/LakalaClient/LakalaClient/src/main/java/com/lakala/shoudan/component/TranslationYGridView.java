package com.lakala.shoudan.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.lakala.library.util.LogUtil;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Created by LMQ on 2015/12/28.
 */
public class TranslationYGridView extends GridView {
    private ObjectAnimator mAnimator;
    private int startY;
    private int endY;

    public TranslationYGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public TranslationYGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public TranslationYGridView setStartY(int startY) {
        this.startY = startY;
        return this;
    }

    public TranslationYGridView setEndY(int endY) {
        this.endY = endY;
        return this;
    }

    private void init(Context context, AttributeSet attrs) {
    }
    public void reverse(){
        if(mAnimator != null){
            mAnimator.reverse();
        }
    }
    public void start(){
        if(mAnimator == null){
            mAnimator = ObjectAnimator.ofFloat(this,"y",startY,endY);
            mAnimator.setDuration(300);
            mAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
        mAnimator.start();
    }
}
