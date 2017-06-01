package com.lakala.shoudan.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by linmq on 2016/6/12.
 */
public class CustomTextView extends TextView {
    private int textGravity = -1;

    public CustomTextView(Context context) {
        super(context);
        init(context,null);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setHeight(getFontHeight());
    }

    public CustomTextView setTextGravity(int textGravity) {
        this.textGravity = textGravity;
        return this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        TextPaint mTextPaint = getPaint();
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextPaint.setColor(getCurrentTextColor());
        int x = getPaddingLeft();
        int y = (int) (Math.abs(fontMetrics.ascent) - Math.abs
                        (fontMetrics.descent)+3+getPaddingTop());
        canvas.drawText(getText().toString(), x,y, mTextPaint);
//        if(Gravity.TOP == textGravity){
//            canvas.drawText(getText().toString(),0,Math.abs(fontMetrics.ascent)-Math.abs
// (fontMetrics.descent),mTextPaint);
//        }else if(Gravity.BOTTOM == textGravity){
//            canvas.drawText(getText().toString(),0,Math.abs(fontMetrics.top)+Math.abs
// (fontMetrics.descent),mTextPaint);
//        }else {
//            super.onDraw(canvas);
//        }
    }

    public int getFontHeight() {
        TextPaint mTextPaint = getPaint();
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        int height = getPaddingTop() + getPaddingBottom() +
                (int) (Math.abs(fontMetrics.ascent) - Math.abs(fontMetrics.descent)) + 6;
        return height;
    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//        int width = 0;
//        int height = 0;
//        TextPaint mTextPaint = getPaint();
//        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
//        if (widthMode == MeasureSpec.EXACTLY) {
//            width = widthSize;
//        } else if (widthMode == MeasureSpec.AT_MOST) {
//            width = (int) mTextPaint.measureText(getText().toString());
//        }
//        if (heightMode == MeasureSpec.EXACTLY) {
//            height = heightSize;
//        } else if (heightMode == MeasureSpec.AT_MOST) {
////            height = (int) (getMeasuredHeight() - Math.abs(fontMetrics.descent) * 2);
//            height = (int) (Math.abs(fontMetrics.ascent) - Math.abs(fontMetrics.descent)) + 6;
//        }
//        setMeasuredDimension(width, height);
//    }
}
