package com.lakala.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.ui.R;

/**
 * Created by jerry on 10/21/13.
 *
 *  +--------------------------+
 *  |               TextView
 *  |[icon]lable|            [>]
 *  |               TextView
 *  +--------------------------+
 */
public class LabelTwoLineText extends LabelItemView{

    private TextView mFirstLineText;
    private TextView mFirstLineRightText;
    private TextView mSecondLineText;
    private TextLayoutClickListener mClickListener;

    public LabelTwoLineText(Context context) {
        super(context);
    }

    public LabelTwoLineText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected ViewGroup loadLayout(ViewGroup container, AttributeSet attrs) {
        ViewGroup vg = super.loadLayout(container, attrs);

        //从布局文件
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.l_multiplelinetextview,vg);

        LinearLayout layout = (LinearLayout) findViewById(R.id.id_multipleline_textview_layout);
        mFirstLineText = (TextView)  findViewById(R.id.first_line_text);
        mFirstLineRightText = (TextView)  findViewById(R.id.first_line_right_text);
        mSecondLineText = (TextView)  findViewById(R.id.second_line_text);

        //设置点击事件
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null){
                    mClickListener.layoutClick(view);
                }
            }
        });
        //继续返回添加多行文本
        return layout;
    }

    @Override
    protected void init(AttributeSet attrs) {
        super.init(attrs);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LabelTwoLineText);

        //设置第一行信息
        String firstText = typedArray.getString(R.styleable.LabelTwoLineText_firstText);
        if (firstText != null){
            setFirstText(firstText);
        }

        int firstTextRes = typedArray.getResourceId(R.styleable.LabelTwoLineText_firstText, 0);
        if (firstTextRes != 0){
            setFirstText(firstTextRes);
        }

        float firstTextSize = typedArray.getDimension(R.styleable.LabelTwoLineText_firstTextSize,0.0f);
        if (firstTextSize != 0.0f){
            setFirstTextSize(firstTextSize);
        }

        int firstTextColor = typedArray.getColor(R.styleable.LabelTwoLineText_firstTextColor,0);
        if (firstTextColor != 0){
            setFirstTextColor(firstTextColor);
        }

        int firstTextStyle = typedArray.getInt(R.styleable.LabelTwoLineText_firstTextStyle,0);
        if (firstTextStyle != 0){
            setFirstTextStyle(firstTextStyle);
        }

        boolean firstTextIsSingleLine = typedArray.getBoolean(R.styleable.LabelTwoLineText_firstTextIsSingleLine,false);
        if (firstTextIsSingleLine){
            setFirstTextSingleLine();
        }

        int firstTextMaxLine = typedArray.getInteger(R.styleable.LabelTwoLineText_firstTextMaxLine,0);
        if (firstTextMaxLine != 0){
            setFirstTextMaxLine(firstTextMaxLine);
        }

        //设置第二行信息
        String secondText = typedArray.getString(R.styleable.LabelTwoLineText_secondText);
        if (secondText != null){
            setSecondText(secondText);
        }

        int secondTextRes = typedArray.getResourceId(R.styleable.LabelTwoLineText_secondText, 0);
        if (secondTextRes != 0){
            setSecondText(secondTextRes);
        }

        float secondTextSize = typedArray.getDimension(R.styleable.LabelTwoLineText_secondTextSize,0.0f);
        if (secondTextSize != 0.0f){
            setSecondTextSize(secondTextSize);
        }

        int secondTextColor = typedArray.getColor(R.styleable.LabelTwoLineText_secondTextColor,0);
        if (secondTextColor != 0){
            setSecondTextColor(secondTextColor);
        }

        int secondTextStyle = typedArray.getInt(R.styleable.LabelTwoLineText_secondTextStyle,0);
        if (secondTextStyle != 0){
            setSecondTextStyle(secondTextStyle);
        }

        boolean secondTextIsSingleLine = typedArray.getBoolean(R.styleable.LabelTwoLineText_secondTextIsSingleLine,false);
        if (secondTextIsSingleLine){
            setSecondTextSingleLine();
        }

        int secondTextMaxLine = typedArray.getInteger(R.styleable.LabelTwoLineText_secondTextMaxLine,0);
        if (secondTextMaxLine != 0){
            setSecondTextMaxLine(secondTextMaxLine);
        }

        typedArray.recycle();

    }

    /**
     * 设置第一行文本信息
     * @param textTop
     */
    public void setFirstText(String textTop) {
        mFirstLineText.setText(textTop);
    }

    public void setFirstText(int textTop) {
        mFirstLineText.setText(textTop);
    }

    public void setFirstText(CharSequence textTop) {
        mFirstLineText.setText(textTop);
    }
    
    public void appendFirstText(CharSequence textTop) {
        mFirstLineText.append(textTop);
    }
    
    /**
     * 设置第一行右文本信息（）
     * @param textTop
     */
    public void setFirstRightTextVsibility(int visibility) {
        mFirstLineRightText.setVisibility(visibility);
    }
    
    public void setFirstRightText(String textTop) {
        mFirstLineRightText.setText(textTop);
    }

    public void setFirstRightText(int textTop) {
    	mFirstLineRightText.setText(textTop);
    }

    public void setFirstRightText(CharSequence textTop) {
    	mFirstLineRightText.setText(textTop);
    }

    /**
     * 设置第一行文本字体大小
     * @param firstTextSize
     */
    public void setFirstTextSize(float firstTextSize) {
        mFirstLineText.setTextSize(TypedValue.COMPLEX_UNIT_PX,firstTextSize);
    }

    /**
     * 设置第一行文本字体颜色
     * @param firstTextColor
     */
    public void setFirstTextColor(int firstTextColor) {
        mFirstLineText.setTextColor(firstTextColor);
    }

    /**
     * 设置第一行文本文本style
     * @param firstTextStyle
     */
    public void setFirstTextStyle(int firstTextStyle) {
        TextPaint paint = mFirstLineText.getPaint();
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, firstTextStyle));
    }

    /**
     * 设置第一个文本是否单行现实
     */
    public void setFirstTextSingleLine() {
        mFirstLineText.setSingleLine();
    }

    /**
     * 设置第一个文本最大行数
     * @param firstTextMaxLine
     */
    public void setFirstTextMaxLine(int firstTextMaxLine) {
        mFirstLineText.setMaxLines(firstTextMaxLine);
    }

    /**
     * 设置第二行文本信息
     * @param textBottom
     */
    public void setSecondText(String textBottom) {
        mSecondLineText.setText(textBottom);
    }

    public void setSecondText(int textBottom) {
        mSecondLineText.setText(textBottom);
    }

    public void setSecondText(CharSequence textBottom) {
        mSecondLineText.setText(textBottom);
    }

    /**
     * 设置第二行文本字体大小
     * @param firstTextSize
     */
    public void setSecondTextSize(float firstTextSize) {
        mSecondLineText.setTextSize(TypedValue.COMPLEX_UNIT_PX,firstTextSize);
    }

    /**
     * 设置第二行文本字体颜色
     * @param firstTextColor
     */
    public void setSecondTextColor(int firstTextColor) {
        mSecondLineText.setTextColor(firstTextColor);
    }

    /**
     * 设置第二行文本style
     * @param firstTextStyle
     */
    public void setSecondTextStyle(int firstTextStyle) {
        TextPaint paint = mSecondLineText.getPaint();
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, firstTextStyle));
    }

    /**
     * 设置第二行文本是否为单行
     */
    public void setSecondTextSingleLine() {
        mSecondLineText.setSingleLine();
    }

    /**
     * 设置第二个文本最大行数
     * @param secondTextMaxLine
     */
    public void setSecondTextMaxLine(int secondTextMaxLine) {
        mSecondLineText.setMaxLines(secondTextMaxLine);
    }

    /**
     * 设置点击事件
     */
    public void setOnLayoutClickListener(TextLayoutClickListener clickListener){
        this.mClickListener = clickListener;
    }

    public interface TextLayoutClickListener{
        void layoutClick(View view);
    }

}
