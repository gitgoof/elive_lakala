package com.lakala.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.lakala.ui.R;
import com.lakala.ui.common.Dimension;

/**
 * 可现示文本的 进度条
 * Created by 葛威 on 13-11-1.
 */
public class TextProgressBar extends ProgressBar {

    private int    mTextColor;
    private float  mTextSize;
    private String mText;
    private Paint mPaint;

    public TextProgressBar(Context context) {
        super(context);
        init(null);
    }

    public TextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    /**
     * 获取文字颜色
     * @return  颜色值
     */
    public int getTextColor() {
        return mTextColor;
    }

    /**
     * 设置文字颜色
     * @param textColor  颜色值
     */
    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
        mPaint.setColor(mTextColor);
    }

    /**
     * 获取文字大小
     * @return 像素值
     */
    public int getTextSize() {
        return (int) mTextSize;
    }

    /**
     * 设置文字大小
     * @param textSize 像素值
     */
    public void setTextSize(int textSize)
    {
        this.mTextSize = textSize;
        mPaint.setTextSize(mTextSize);
    }

    /**
     * 获取文本
     * @return  文本
     */
    public String getText()
    {
        return mText;
    }

    /**
     * 设置文本
     * @param text 文本
     */
    public void setText(String text)
    {
        this.mText = text;
        invalidate();
    }

    public void setText(int resid)
    {
        this.mText = getResources().getString(resid);
        invalidate();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Rect rect = new Rect();
        mPaint.getTextBounds(mText, 0, this.mText.length(), rect);

        float width  = getWidth();
        float height = getHeight();

        float padingLeft  = getPaddingLeft();
        float padingRight = getPaddingRight();
        float padingTop   = getPaddingTop();
        float padingBt    = getPaddingBottom();

        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        // 计算文字高度
        float fontHeight = fontMetrics.bottom - fontMetrics.top;

        //画布的坐标是从margin内部开始计算的。
        float x = padingLeft + (width - padingLeft - padingRight) / 2 - rect.centerX();
        // 计算文字 baseline（文字的 fontMetrics.top 可能是从一个负坐标开始的）。
        float y = padingTop + (height - padingTop - padingBt - fontHeight) / 2 - fontMetrics.top;
        canvas.drawText(mText, x, y, mPaint);
    }

    protected void init(AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG); //开启反锯齿

        if (attrs == null)
            return;

        //从 xml 布局文件读取控件属性设置
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.TextProgressBar);

        int sp10 = Dimension.sp2px(10, getContext());

        //获取文字颜色
        mTextColor = ta.getColor(R.styleable.TextProgressBar_textColor, Color.BLACK);
        //获取文字大小
        mTextSize = ta.getDimension(R.styleable.TextProgressBar_textSize,sp10);
        //获取文字
        mText = ta.getString(R.styleable.TextProgressBar_text);

        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);

        ta.recycle();
    }

}
