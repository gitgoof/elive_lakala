package com.lakala.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.ui.R;

/**
 *
 *  +-----------------------------------------+
 *  | [icon]lable       editText  rightText > |
 *  +-----------------------------------------+
 */
public class LabelEditText extends LabelItemView{
    private TextView editText;
    private boolean isText = false;

    public LabelEditText(Context context) {
        super(context);
    }

    public LabelEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置文本
     * @param text  文本
     */
    public void setText(CharSequence text){
        editText.setText(text);
    }
    public CharSequence getText(){
        return editText.getText();
    }

    /**
     * 设置文本
     * @param resid 文本资源 id。
     */
    public void setText(int resid){
        editText.setText(resid);
    }

    /**
     * 设置 Hint
     * @param text  文本
     */
    public void setHint(CharSequence text){
        editText.setHint(text);
    }

    /**
     * 设置 Hint
     * @param resid 文本资源 id。
     */
    public void setHint(int resid){
        editText.setHint(resid);
    }

    /**
     * 设置字体大小
     * @param unit  单位
     * @param size  字体大小。
     * @see android.widget.TextView#setTextSize(int, float)
     */
    public void setTextSize(int unit,float size){
        editText.setTextSize(unit,size);
    }

    /**
     * 设置字体颜色
     * @param color  颜色值。
     */
    public void setTextColor(int color){
        editText.setTextColor(color);
    }

    /**
     * 设置 Hint 字体颜色
     * @param color  颜色值。
     */
    public void setHintTextColor(int color){
        editText.setHintTextColor(color);
    }

    /**
     * 设置字体样式（正常、粗体、斜体 的组合值）
     * @param style  字体样式值。
     */
    public void setTextStyle(int style){
        TextPaint paint = editText.getPaint();
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, style));
    }

    /**
     * 设置EditText 的输入类型
     * @param type 输入类型
     */
    public void setInputType(int type){
        editText.setInputType(type);
    }

    /**
     * 获取EditText 实例
     * @return EditText 实例
     */
    public EditText getEditText(){
        return (EditText) editText;
    }
    public TextView getTextView(){
        return editText;
    }

    public View getContentView(){
        return editText;
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
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.LabelEditText);

        //属性 text
        text = ta.getString(R.styleable.LabelEditText_text);
        if (text != null){
            setText(text);
        }

        //属性 rightHint
        text = ta.getString(R.styleable.LabelEditText_hint);
        if (text != null){
            setHint(text);
        }

        //属性 textSize
        size = ta.getDimension(R.styleable.LabelEditText_textSize, 0.0f);
        if (size != 0.0f){
            setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        }

        //属性 textColor
        color = ta.getColor(R.styleable.LabelEditText_textColor,0);
        if (color != 0){
            setTextColor(color);
        }

        //属性 rightHintTextColor
        color = ta.getColor(R.styleable.LabelEditText_hintTextColor,0);
        if (color != 0){
            setHintTextColor(color);
        }

        //属性 textStyle
        style = ta.getInt(R.styleable.LabelEditText_textStyle,0);
        setTextStyle(style);

        //属性 inputType
        style = ta.getInt(R.styleable.LabelEditText_inputType,1);
        setInputType(style);

        int gravity = ta.getInt(R.styleable.LabelEditText_editGravity,Gravity.LEFT);
        editText.setGravity(gravity);
        editText.setOnTouchListener(
                new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        onTouchEvent(event);
                        return false;
                    }
                }
        );
        ta.recycle();

        super.init(attrs);
    }

    @Override
    protected ViewGroup loadLayout(ViewGroup container, AttributeSet attrs) {
        if(attrs != null){
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.LabelEditText);
            isText = ta.getBoolean(R.styleable.LabelEditText_isText,false);
        }
        if(isText){
            editText = new TextView(getContext());
        }else {
            editText = new ClearEditText(getContext());
        }
        ViewGroup mContainer = super.loadLayout(container, attrs);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.FILL_PARENT);
        params.weight  = 1;
        editText.setSingleLine(true);
        editText.setGravity(Gravity.CENTER_VERTICAL);
        editText.setPadding(0,0,getResources().getDimensionPixelSize(R.dimen.dimen_10),0);
        editText.setBackgroundResource(0);//隐藏edittext默认背景
        editText.setLayoutParams(params);

        mContainer.addView(editText);
        return mContainer;
    }
}
