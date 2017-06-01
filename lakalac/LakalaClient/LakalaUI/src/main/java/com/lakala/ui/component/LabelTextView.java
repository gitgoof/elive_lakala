package com.lakala.ui.component;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.ui.R;

/**
 *
 *  +--------------------------------------+
 *  | [icon]lable    textView  rightText > |
 *  +--------------------------------------+
 */
public class LabelTextView extends LabelItemView {
    private TextView textView;

    public LabelTextView(Context context) {
        super(context);
    }

    public LabelTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置文本
     * @param text  文本
     */
    public void setText(CharSequence text){
        textView.setText(text);
    }

    /**
     * 设置文本
     * @param resid 文本资源 id。
     */
    public void setText(int resid){
        textView.setText(resid);
    }

    /**
     * 设置 Hint
     * @param text  文本
     */
    public void setHint(CharSequence text){
        textView.setHint(text);
    }

    /**
     * 设置 Hint
     * @param resid 文本资源 id。
     */
    public void setHint(int resid){
        textView.setHint(resid);
    }

    /**
     * 设置字体大小
     * @param unit  单位
     * @param size  字体大小。
     * @see android.widget.TextView#setTextSize(int, float)
     */
    public void setTextSize(int unit,float size){
        textView.setTextSize(unit,size);
    }

    /**
     * 设置字体颜色
     * @param color  颜色值。
     */
    public void setTextColor(int color){
        textView.setTextColor(color);
    }

    /**
     * 设置 Hint 字体颜色
     * @param color  颜色值。
     */
    public void setHintTextColor(int color){
        textView.setHintTextColor(color);
    }

    /**
     * 设置字体样式（正常、粗体、斜体 的组合值）
     * @param style  字体样式值。
     */
    public void setTextStyle(int style){
        TextPaint paint = textView.getPaint();
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, style));
    }

    @Override
    protected void init(AttributeSet attrs) {
        if (attrs == null)
            return;

        int     color  = 0;
        int     style  = 0;
        float   size   = 0.0f;
        String text   = "";

        //从 xml 布局文件读取控件属性设置
        TypedArray ta = getContext().obtainStyledAttributes(attrs,R.styleable.LabelTextView);

        //属性 text
        text = ta.getString(R.styleable.LabelTextView_text);
        if (text != null){
            setText(text);
        }

        //属性 rightHint
        text = ta.getString(R.styleable.LabelTextView_hint);
        if (text != null){
            setHint(text);
        }

        //属性 textSize
        size = ta.getDimension(R.styleable.LabelTextView_textSize, 0.0f);
        if (size != 0.0f){
            setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        }

        //属性 textColor
        color = ta.getColor(R.styleable.LabelTextView_textColor,0);
        if (color != 0){
            setTextColor(color);
        }

        //属性 rightHintTextColor
        color = ta.getColor(R.styleable.LabelTextView_hintTextColor,0);
        if (color != 0){
            setHintTextColor(color);
        }

        //属性 textStyle
        style = ta.getInt(R.styleable.LabelTextView_textStyle,0);
        setTextStyle(style);

        ta.recycle();

        super.init(attrs);
    }

    @Override
    protected ViewGroup loadLayout(ViewGroup container, AttributeSet attrs) {
        ViewGroup vg = super.loadLayout(container, attrs);
        textView  = new TextView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setLayoutParams(params);

        vg.addView(textView);
        return vg;
    }
}
