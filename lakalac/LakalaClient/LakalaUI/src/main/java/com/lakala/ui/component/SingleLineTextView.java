package com.lakala.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.ui.R;
import com.lakala.ui.common.Dimension;

/**
 *  单行label
 *  +--------------------------+
 *  |[icon]label           [>] |
 *  +--------------------------+
 */
public class SingleLineTextView extends IconItemView {
    private TextView leftText;

    public SingleLineTextView(Context context) {
        super(context);
    }

    public SingleLineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置左侧文本
     * @param text  文本
     */
    public void setLeftText(CharSequence text){
        leftText.setText(text);
    }

    public TextView getLeftText() {
        return leftText;
    }

    /**
     * 设置左侧文本
     * @param resid 文本资源 id。
     */
    public void setLeftText(int resid){
        leftText.setText(resid);
    }

    /**
     * 设置左侧 Hint
     * @param text  文本
     */
    public void setLeftHint(CharSequence text){
        leftText.setHint(text);
    }

    /**
     * 设置左侧 Hint
     * @param resid 文本资源 id。
     */
    public void setLeftHint(int resid){
        leftText.setHint(resid);
    }

    /**
     * 设置左侧字体大小
     * @param unit  单位
     * @param size  字体大小。
     * @see android.widget.TextView#setTextSize(int, float)
     */
    public void setLeftTextSize(int unit,float size){
        leftText.setTextSize(unit,size);
    }

    /**
     * 设置左侧字体颜色
     * @param color  颜色值。
     */
    public void setLeftTextColor(int color){
        leftText.setTextColor(color);
    }

    /**
     * 设置左侧 Hint 字体颜色
     * @param color  颜色值。
     */
    public void setLeftHintTextColor(int color){
        leftText.setHintTextColor(color);
    }

    /**
     * 设置左侧字体样式（正常、粗体、斜体 的组合值）
     * @param style  字体样式值。
     */
    public void setLeftTextStyle(int style){
        TextPaint paint = leftText.getPaint();
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, style));
    }

    @Override
    protected void init(AttributeSet attrs) {

        if (attrs == null || leftText == null)
            return;

        //从 xml 布局文件读取控件属性设置
        TypedArray ta = getContext().obtainStyledAttributes(attrs,R.styleable.SingleLineTextView);

        int     color  = 0;
        int     style  = 0;
        float   size   = 0.0f;
        String text   = "";

        int dp10 = Dimension.dip2px(10, getContext());
        int dp0  = 0;

        //属性 leftText
        text = ta.getString(R.styleable.SingleLineTextView_leftText);
        if (text != null){
            setLeftText(text);
        }

        //属性 leftHint
        text = ta.getString(R.styleable.SingleLineTextView_leftHint);
        if (text != null){
            setLeftHint(text);
        }

        //属性 leftTextSize
        size = ta.getDimension(R.styleable.SingleLineTextView_leftTextSize, 0.0f);
        if (size != 0.0f){
            setLeftTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        }

        //属性 leftTextColor
        color = ta.getColor(R.styleable.SingleLineTextView_leftTextColor, Color.BLACK);
        if (color != 0){
            setLeftTextColor(color);
        }

        //属性 leftHintTextColor
        color = ta.getColor(R.styleable.SingleLineTextView_leftHintTextColor,0);
        if (color != 0){
            setLeftHintTextColor(color);
        }

        //属性 leftTextStyle
        style = ta.getInt(R.styleable.SingleLineTextView_leftTextStyle,0);
        setLeftTextStyle(style);

        ta.recycle();

        super.init(attrs);
    }

    @Override
    protected ViewGroup loadLayout(ViewGroup container, AttributeSet attrs) {
        ViewGroup vg = super.loadLayout(container, attrs);
        if (vg == null) return null;

        leftText = new TextView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        leftText.setLayoutParams(params);
        leftText.setVisibility(VISIBLE);
        leftText.setSingleLine();

        vg.addView(leftText);

        //继续返回 vg 充许在单行文本左侧添加其它组件
        return vg;
    }
}
