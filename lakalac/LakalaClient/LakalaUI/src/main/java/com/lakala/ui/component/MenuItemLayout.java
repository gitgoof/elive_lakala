package com.lakala.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.ui.R;
import com.lakala.ui.common.Dimension;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LMQ on 2015/3/12.
 */
public class MenuItemLayout extends LinearLayout {
    private View topDivide;
    private View bottomDivide;
    private Drawable bottomDrawable;
    private int bottomDivideHeight;
    private int topDivideHeight;
    private Drawable topDrawable;
    private int bottomDivideMarginLeft;
    private int bottomDivideMarginRight;
    private int topDivideMarginLeft;
    private int topDivideMarginRight;
    private ImageView leftIcon;
    private TextView leftText;
    private int alignTag;
    public static final int ALIGN_TOP_DIVIDE = 0;
    public static final int ALIGN_BOTTOM_DIVIDE = 1;
    private ImageView rightArrow;
    private List<View> controlDivider = null;

    public MenuItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadLayout(this);
        init(attrs);
    }

    protected ViewGroup loadLayout(ViewGroup viewGroup) {
        setOrientation(HORIZONTAL);
        setClickable(true);
        setFocusable(true);
        setEnabled(true);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.ui_menu_iitem, viewGroup);

        topDivide = findViewById(R.id.top_divide);
        topDivide.setFocusable(false);
        bottomDivide = findViewById(R.id.bottom_divide);
        bottomDivide.setFocusable(false);
        leftIcon = (ImageView) findViewById(R.id.icon);
        leftText = (TextView) findViewById(R.id.title);
        rightArrow = (ImageView) findViewById(R.id.arrow);
        setEnableOnClickItemEvents(false);
        return this;
    }

    public void setEnableOnClickItemEvents(boolean enableOnClickItemEvents) {
        leftIcon.setClickable(enableOnClickItemEvents);
        leftIcon.setFocusable(enableOnClickItemEvents);
        leftText.setClickable(enableOnClickItemEvents);
        leftText.setFocusable(enableOnClickItemEvents);
        rightArrow.setClickable(enableOnClickItemEvents);
        rightArrow.setFocusable(enableOnClickItemEvents);
    }

    protected void init(AttributeSet attrs) {
        if (attrs == null || topDivide == null || bottomDivide == null) {
            return;
        }
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.MenuItemLayout);
        alignTag = ta.getInt(R.styleable.MenuItemLayout_divideAlignTitle, -1);
        initTopDivide(ta);
        initBottomDivide(ta);
        initLeftText(ta);
        initLeftIcon(ta);
        initRightArrow(ta);
        ta.recycle();
    }

    private void initRightArrow(TypedArray ta) {
        //属性 rightArrowVisibility
        int iValue = ta.getInt(R.styleable.MenuItemLayout_arrowVisibility, GONE);
        setRightArrowVisibility(iValue);
        int marginRight = ta
                .getDimensionPixelSize(R.styleable.MenuItemLayout_rightArrowMarginRight, 0);
        setRightArrowMarginRight(marginRight);
    }

    private OnDispatchSetPressedListener onDispatchSetPressedListener;

    public OnDispatchSetPressedListener getOnDispatchSetPressedListener() {
        return onDispatchSetPressedListener;
    }

    public void setOnDispatchSetPressedListener(
            OnDispatchSetPressedListener onDispatchSetPressedListener) {
        this.onDispatchSetPressedListener = onDispatchSetPressedListener;
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {
        super.dispatchSetPressed(pressed);
        if(onDispatchSetPressedListener != null){
            onDispatchSetPressedListener.dispatchSetPressed(pressed);
        }
        if(controlDivider != null && controlDivider.size() != 0){
            for(View divider:controlDivider){
                if(pressed){
                    divider.setVisibility(View.INVISIBLE);
                }else{
                    divider.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 添加进来的divider受pressed状态控制，pressed为true是divider为View.INVISIBLE,否则View.VISIBLE
     * @param divider
     */
    public void addControlDivider(View divider){
        if(controlDivider == null){
            controlDivider = new ArrayList<View>();
        }
        controlDivider.add(divider);
    }

    public static interface OnDispatchSetPressedListener{
        public void dispatchSetPressed(boolean pressed);
    }

    public void setRightArrowMarginRight(int marginRight) {
        rightArrow.setPadding(rightArrow.getPaddingLeft(), rightArrow.getPaddingTop(), marginRight,
                              rightArrow.getPaddingBottom());
    }

    /**
     * 设置右侧箭头的显示属性
     *
     * @param visibility 显示属性
     */
    public void setRightArrowVisibility(int visibility) {
        rightArrow.setVisibility(visibility);
    }


    private void initLeftIcon(TypedArray ta) {
        int dp10 = Dimension.dip2px(10, getContext());
        //属性 leftIconSrc
        int resid = ta.getResourceId(R.styleable.MenuItemLayout_leftIconDrawable, 0);
        setLeftIconResource(resid);
        //属性 leftIconVisibility
        int iValue = ta.getInt(R.styleable.MenuItemLayout_iconVisibility, GONE);
        setLeftIconVisibility(iValue);
        int marginLeft = ta.getDimensionPixelSize(R.styleable.MenuItemLayout_iconMarginLeft, 0);
        int marginRight = ta
                .getDimensionPixelSize(R.styleable.MenuItemLayout_iconMarginRight, dp10);
        setLeftIconMarginLeft(marginLeft, marginRight);
    }

    public void setLeftIconMarginLeft(int marginLeft, int marginRight) {
        MarginLayoutParams params = (MarginLayoutParams) leftIcon.getLayoutParams();
        if (params == null) {
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                     RelativeLayout.LayoutParams.WRAP_CONTENT);
        }
        params.leftMargin = marginLeft;
        params.rightMargin = marginRight;
        leftIcon.setLayoutParams(params);
    }

    public TextView getLeftTextView() {
        return leftText;
    }

    public ImageView getLeftIconView() {
        return leftIcon;
    }

    /**
     * 设置左侧图标的显示属性
     *
     * @param visibility 显示属性
     */
    public void setLeftIconVisibility(int visibility) {
        leftIcon.setVisibility(visibility);
    }

    /**
     * 设置左侧图标资源 ID
     *
     * @param resid 资源id
     */
    public void setLeftIconResource(int resid) {
        leftIcon.setImageResource(resid);
    }

    public void setLeftIconBitmap(Bitmap bm) {
        leftIcon.setImageBitmap(bm);
    }

    /**
     * 设置左侧字体大小
     *
     * @param unit 单位
     * @param size 字体大小。
     * @see android.widget.TextView#setTextSize(int, float)
     */
    public void setLeftTextSize(int unit, float size) {
        leftText.setTextSize(unit, size);
    }

    public void initLeftText(TypedArray ta) {
        String text = ta.getString(R.styleable.MenuItemLayout_leftTitle);
        if (text != null) {
            setLeftText(text);
        }
        String hint = ta.getString(R.styleable.MenuItemLayout_leftHintText);
        if (hint != null) {
            setLeftHint(hint);
        }
        float size = ta.getDimension(R.styleable.MenuItemLayout_leftSize, 0.0f);
        if (size != 0.0f) {
            setLeftTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
        //属性 leftTextColor
        int color = ta.getColor(R.styleable.MenuItemLayout_leftColor, Color.BLACK);
        if (color != 0) {
            setLeftTextColor(color);
        }

        //属性 leftHintTextColor
        color = ta.getColor(R.styleable.MenuItemLayout_leftHintColor, 0);
        if (color != 0) {
            setLeftHintTextColor(color);
        }

        //属性 leftTextStyle
        int style = ta.getInt(R.styleable.MenuItemLayout_leftStyle, 0);
        setLeftTextStyle(style);
    }

    public void setLeftHint(String hint) {
        leftText.setHint(hint);
    }

    /**
     * 设置左侧字体样式（正常、粗体、斜体 的组合值）
     *
     * @param style 字体样式值。
     */
    public void setLeftTextStyle(int style) {
        TextPaint paint = leftText.getPaint();
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, style));
    }

    /**
     * 设置左侧 Hint 字体颜色
     *
     * @param color 颜色值。
     */
    public void setLeftHintTextColor(int color) {
        leftText.setHintTextColor(color);
    }

    /**
     * 设置左侧字体颜色
     *
     * @param color 颜色值。
     */
    public void setLeftTextColor(int color) {
        leftText.setTextColor(color);
    }

    private void initBottomDivide(TypedArray ta) {
        bottomDrawable = ta.getDrawable(R.styleable.MenuItemLayout_bottomDivideDrawable);
        if (bottomDrawable == null) {
            bottomDivide.setVisibility(View.GONE);
            return;
        } else {
            bottomDivide.setVisibility(View.VISIBLE);
            bottomDivide.setBackgroundDrawable(bottomDrawable);
        }

        bottomDivideHeight = ta
                .getDimensionPixelSize(R.styleable.MenuItemLayout_bottomDivideHeight, 0);
        bottomDivideMarginLeft = ta
                .getDimensionPixelSize(R.styleable.MenuItemLayout_bottomDivideMarginLeft, 0);
        bottomDivideMarginRight = ta
                .getDimensionPixelSize(R.styleable.MenuItemLayout_bottomDivideMarginRight, 0);
        if (bottomDivideHeight == 0) {
            bottomDivide.setVisibility(View.GONE);
            return;
        }
        MarginLayoutParams params = (MarginLayoutParams) bottomDivide.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.MATCH_PARENT, bottomDivideHeight);
        }
        params.height = bottomDivideHeight;
        params.rightMargin = bottomDivideMarginRight;
        params.leftMargin = bottomDivideMarginLeft;
        if(alignTag == ALIGN_BOTTOM_DIVIDE){
            setDivideAlign(params, alignTag);
        }
    }

    public View getTopDivide() {
        return topDivide;
    }

    public View getBottomDivide() {
        return bottomDivide;
    }

    public void setAlignParentLeft(String divide) {
        if ("top".equals(divide)) {
            MarginLayoutParams params = (MarginLayoutParams) topDivide.getLayoutParams();
            if (params instanceof RelativeLayout.LayoutParams && alignTag == ALIGN_TOP_DIVIDE) {
                RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) params;
                p.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }
            topDivide.setLayoutParams(params);
        } else if ("bottom".equals(divide)) {
            MarginLayoutParams params = (MarginLayoutParams) bottomDivide.getLayoutParams();
            if (params instanceof RelativeLayout.LayoutParams && alignTag == ALIGN_BOTTOM_DIVIDE) {
                RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) params;
                p.addRule(RelativeLayout.ALIGN_LEFT, leftText.getId());
            }
            bottomDivide.setLayoutParams(params);
        }
    }

    public void setDivideAlign(int alignTag) {
        MarginLayoutParams params = null;
        switch (alignTag) {
            case ALIGN_BOTTOM_DIVIDE: {
                params = (MarginLayoutParams) bottomDivide.getLayoutParams();
                break;
            }
            case ALIGN_TOP_DIVIDE: {
                params = (MarginLayoutParams) topDivide.getLayoutParams();
                break;
            }
        }
        if (params == null) {
            throw new NullPointerException(
                    "alignTag must be MenuItemLayout.ALIGN_BOTTOM_DIVIDE " + "OR MenuItemLayout.ALIGN_TOP_DIVIDE");
        }
        setDivideAlign(params, alignTag);
    }

    private void setDivideAlign(MarginLayoutParams params, int alignTag) {
        switch (alignTag) {
            case ALIGN_BOTTOM_DIVIDE: {
                if (params instanceof RelativeLayout.LayoutParams && alignTag == ALIGN_BOTTOM_DIVIDE) {
                    RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) params;
                    p.addRule(RelativeLayout.ALIGN_LEFT, leftText.getId());
                }
                bottomDivide.setLayoutParams(params);
                break;
            }
            case ALIGN_TOP_DIVIDE: {
                if (params instanceof RelativeLayout.LayoutParams && alignTag == ALIGN_TOP_DIVIDE) {
                    RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) params;
                    p.addRule(RelativeLayout.ALIGN_LEFT, leftText.getId());
                }
                topDivide.setLayoutParams(params);
                break;
            }
        }
    }

    private void initTopDivide(TypedArray ta) {
        topDrawable = ta.getDrawable(R.styleable.MenuItemLayout_topDivideDrawable);
        if (topDrawable == null) {
            topDivide.setVisibility(View.GONE);
            return;
        } else {
            topDivide.setVisibility(View.VISIBLE);
            topDivide.setBackgroundDrawable(topDrawable);
        }

        topDivideHeight = ta.getDimensionPixelSize(R.styleable.MenuItemLayout_topDivideHeight, 0);
        topDivideMarginLeft = ta
                .getDimensionPixelSize(R.styleable.MenuItemLayout_topDivideMarginLeft, 0);
        topDivideMarginRight = ta
                .getDimensionPixelSize(R.styleable.MenuItemLayout_topDivideMarginRight, 0);
        if (topDivideHeight == 0) {
            topDivide.setVisibility(View.GONE);
            return;
        }
        MarginLayoutParams params = (MarginLayoutParams) topDivide.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.MATCH_PARENT, topDivideHeight);
        }
        params.height = topDivideHeight;
        params.leftMargin = topDivideMarginLeft;
        params.rightMargin = topDivideMarginRight;
        if(alignTag == ALIGN_TOP_DIVIDE){
            setDivideAlign(params, alignTag);
        }
    }

    public void setLeftText(String text) {
        leftText.setText(text);
    }
}
