package com.lakala.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.ui.R;

/**
 * Created by jerry on 10/21/13.
 */
public class LabelMultiLineText extends LabelTwoLineText{

    private TextView mThirdLineText;
    private ViewGroup vg;

    public LabelMultiLineText(Context context) {
        super(context);
    }

    public LabelMultiLineText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected ViewGroup loadLayout(ViewGroup container, AttributeSet attrs) {
        vg = super.loadLayout(container, attrs);

        mThirdLineText = addVerticalTextview();

        //继续返回  vg充许在两行文本右侧添加其它组件
        return vg;
    }

    /**
     * 添加垂直文本
     */
    public TextView addVerticalTextview() {

        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setEllipsize(TextUtils.TruncateAt.END);
        vg.addView(textView);

        return textView;
    }

    @Override
    protected void init(AttributeSet attrs) {
        super.init(attrs);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LabelMultiLineText);

        //设置第三行信息
        String thirdText = typedArray.getString(R.styleable.LabelMultiLineText_thirdText);
        if (thirdText != null){
            setThirdText(thirdText);
        }

        int thirdTextRes = typedArray.getResourceId(R.styleable.LabelMultiLineText_thirdText, 0);
        if (thirdTextRes != 0){
            setThirdText(thirdTextRes);
        }

        float thirdTextSize = typedArray.getDimension(R.styleable.LabelMultiLineText_thirdTextSize,0.0f);
        if (thirdTextSize != 0.0f){
            setThirdTextSize(thirdTextSize);
        }

        int thirdTextColor = typedArray.getColor(R.styleable.LabelMultiLineText_thirdTextColor,0);
        if (thirdTextColor != 0){
            setThirdTextColor(thirdTextColor);
        }

        int thirdTextStyle = typedArray.getInt(R.styleable.LabelMultiLineText_thirdTextStyle,0);
        if (thirdTextStyle != 0){
            setThirdTextStyle(thirdTextStyle);
        }

        boolean thirdTextIsSingleLine = typedArray.getBoolean(R.styleable.LabelMultiLineText_thirdTextIsSingleLine,false);
        if (thirdTextIsSingleLine){
            setThirdTextSingleLine();
        }

        int thirdTextMaxLine = typedArray.getInteger(R.styleable.LabelMultiLineText_thirdTextMaxLine,0);
        if (thirdTextMaxLine != 0){
            setThirdTextMaxLine(thirdTextMaxLine);
        }

        typedArray.recycle();

    }

    /**
     * 设置第三行行文本信息
     * @param textTop
     */
    public void setThirdText(String textTop) {
        mThirdLineText.setText(textTop);
    }

    public void setThirdText(int textTop) {
        mThirdLineText.setText(textTop);
    }

    /**
     * 设置第三行文本字体大小
     * @param thirdTextSize
     */
    public void setThirdTextSize(float thirdTextSize) {
        mThirdLineText.setTextSize(TypedValue.COMPLEX_UNIT_PX,thirdTextSize);
    }

    /**
     * 设置第三行文本字体颜色
     * @param thirdTextColor
     */
    public void setThirdTextColor(int thirdTextColor) {
        mThirdLineText.setTextColor(thirdTextColor);
    }

    /**
     * 设置第三行文本文本style
     * @param thirdTextStyle
     */
    public void setThirdTextStyle(int thirdTextStyle) {
        TextPaint paint = mThirdLineText.getPaint();
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, thirdTextStyle));
    }

    /**
     * 设置第三行文本是否单行现实
     */
    public void setThirdTextSingleLine() {
        mThirdLineText.setSingleLine();
    }

    /**
     * 设置第三行文本最大行数
     * @param thirdTextMaxLine
     */
    public void setThirdTextMaxLine(int thirdTextMaxLine) {
        mThirdLineText.setMaxLines(thirdTextMaxLine);
    }

}
