package com.lakala.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.ui.R;
import com.lakala.ui.common.Dimension;

/**
 *  图标+文本, 用于首界面的图票
 *  +-------+
 *  |       |
 *  | icon  |
 *  |       |
 *  +-------+
 *    text
 */
public class IconText extends LinearLayout {
    private ImageView icon;
    private TextView textView;

    private int pressedColor;
    private int normalColor;

    public IconText(Context context) {
        super(context);
        loadLayout(this);
        init(null);
    }

    public IconText(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadLayout(this);
        init(attrs);
    }

    public ImageView getIcon() {
        return icon;
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

    /**
     * 设置图标
     * @param drawable   Drawable 对象
     */
    public void setIconDrawable(Drawable drawable){
        icon.setImageDrawable(drawable);
    }

    /**
     * 设置图标
     * @param bitmap   Bitmap 对象
     */
    public void setIconBitmap(Bitmap bitmap){
        icon.setImageBitmap(bitmap);
    }

    /**
     * 设置图标资源 ID
     * @param resid   资源id
     */
    public void setIconResource(int resid){
        icon.setImageResource(resid);
    }

    /**
     * 初始化变量、控件。
     * @param attrs    AttributeSet 对象
     */
    protected void init(AttributeSet attrs){
        if (attrs == null)
            return;

        int     color  = 0;
        int     style  = 0;
        int     resid  = 0;
        float   size   = 0.0f;
        String text   = "";

        //从 xml 布局文件读取控件属性设置
        TypedArray ta = getContext().obtainStyledAttributes(attrs,R.styleable.IconText);

        //属性 text
        text = ta.getString(R.styleable.IconText_text);
        if (text != null){
            setText(text);
        }

        //属性 rightHint
        text = ta.getString(R.styleable.IconText_hint);
        if (text != null){
            setHint(text);
        }

        //属性 textSize
        size = ta.getDimension(R.styleable.IconText_textSize, 0.0f);
        if (size != 0.0f){
            setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        }

        //属性 textColor
        color = ta.getColor(R.styleable.IconText_textColor,0);
        if (color != 0){
            setTextColor(color);
            normalColor = color;
        }

        pressedColor = ta.getColor(R.styleable.IconText_textPressedColor, 0);

        //属性 rightHintTextColor
        color = ta.getColor(R.styleable.IconText_hintTextColor,0);
        if (color != 0){
            setHintTextColor(color);
        }

        //属性 textStyle
        style = ta.getInt(R.styleable.IconText_textStyle, 0);
        setTextStyle(style);

        //属性 icon
        resid = ta.getResourceId(R.styleable.IconText_iconSrc,0);
        setIconResource(resid);

        ta.recycle();
    }

    /**
     * 载入 ui 布局，并返回控件内部的容器对象，如果控件不是一个容器则返回 null。
     * BaseItemView 内部的容器是一个垂直排列的 LinearLayout。
     * @param  container  UI元素的容器
     * @return 控件的容器对象,可以向其添加子视图，如果控件不是一个容器则返回null。
     */
    protected ViewGroup loadLayout(ViewGroup container){
        this.setOrientation(VERTICAL);

        icon  = new ImageView(getContext());
        textView  = new TextView(getContext());

        //设置图标图像的位置
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,0);
        int dp3 = Dimension.dip2px(3,getContext());
        params.setMargins(dp3,dp3,dp3,0);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.weight = 1;
        icon.setLayoutParams(params);

        //设置图标文本的位置
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER);

        container.addView(icon);
        container.addView(textView);

        setFocusable(true);
        setClickable(true);
        return null;

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if(pressedColor != 0){
            switch (ev.getAction()){
                case MotionEvent.ACTION_DOWN:
                    setTextColor(pressedColor);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    setTextColor(normalColor);
                    break;
                case MotionEvent.ACTION_UP:
                    setTextColor(normalColor);
                    break;
                default:
                    break;

            }
        }



        return super.dispatchTouchEvent(ev);
    }

}
