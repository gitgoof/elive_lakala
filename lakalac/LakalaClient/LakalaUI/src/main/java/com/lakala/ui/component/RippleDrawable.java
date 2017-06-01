package com.lakala.ui.component;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.util.Property;

/**
 * Created by LMQ on 2015/12/16.
 */
public class RippleDrawable extends Drawable implements ValueAnimator.AnimatorUpdateListener {
    private float startRadius = 0;
    private float endRadius = -1;
    private float radius;
    private final Paint paint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ObjectAnimator ripple;
    private long duration = 2000;
    private int rippleColor;
    private RippleAnimatorListener rippleAnimatorListener;

    public RippleDrawable(double screenW, double screenH) {
        init(screenW,screenH);
    }

    public RippleDrawable setRippleAnimatorListener(RippleAnimatorListener rippleAnimatorListener) {
        this.rippleAnimatorListener = rippleAnimatorListener;
        if(ripple != null){
            ripple.addListener(rippleAnimatorListener);
        }
        return this;
    }

    /**
     * 设置波纹颜色
     * @param rippleColor
     * @return
     */
    public RippleDrawable setRippleColor(int rippleColor) {
        this.rippleColor = rippleColor;
        paint.setColor(rippleColor);
        return this;
    }

    /**
     * 设置动画持续时间
     * @param duration
     * @return
     */
    public RippleDrawable setDuration(long duration) {
        this.duration = duration;
        ripple.setDuration(duration);
        return this;
    }

    /**
     * 设置开始半径
     * @param startRadius
     * @return
     */
    public RippleDrawable setStartRadius(float startRadius) {
        this.startRadius = startRadius;
        return this;
    }

    /**
     * 设置结束半径
     * @param endRadius
     * @return
     */
    public RippleDrawable setEndRadius(float endRadius) {
        this.endRadius = endRadius;
        return this;
    }

    public RippleDrawable setRadius(float radius) {
        this.radius = radius;
        return this;
    }

    public float getRadius() {
        return radius;
    }

    /*
            * Animations
            */
    private Property<RippleDrawable, Float> radiusProperty
            = new Property<RippleDrawable, Float>(Float.class, "radius") {
        @Override
        public Float get(RippleDrawable object) {
            return object.getRadius();
        }

        @Override
        public void set(RippleDrawable object, Float value) {
            object.setRadius(value);
        }
    };

    @Override
    public void draw(Canvas canvas) {
        Rect rect = getBounds();
        canvas.drawCircle(rect.centerX(), rect.bottom, radius, paint);
    }

    private void init(double screenW, double screenH) {
        if (endRadius == -1) {
            endRadius = (float) (Math
                    .sqrt(Math.pow(screenW/2, 2) + Math.pow(screenH, 2)) * 1.2f);
        }
        ripple = ObjectAnimator.ofFloat(this, radiusProperty, startRadius, endRadius);
        ripple.setDuration(duration);
        ripple.setInterpolator(new LinearInterpolator());
        ripple.addUpdateListener(this);
        if(rippleAnimatorListener != null){
            ripple.addListener(rippleAnimatorListener);
        }
    }

    public static abstract class RippleAnimatorListener implements Animator.AnimatorListener{
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }

    public void startAnim(){
        ripple.start();
    }
    public void reverseAnim(){
        ripple.reverse();
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        invalidateSelf();
    }
}
