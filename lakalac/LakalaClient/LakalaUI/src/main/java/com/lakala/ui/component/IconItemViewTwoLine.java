package com.lakala.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.ui.R;

/**
 * Created by jerry on 10/14/13.
 *   +---------------------------------------------------+
 *  | [icon] lable  LinearLaout                          |
 *  | [icon] lable  TextView    LinearLaout（Text..  [>]
 *                                          Text）       |
 *   +---------------------------------------------------+
 */
public class IconItemViewTwoLine extends BaseItemView implements View.OnClickListener{

    private ImageView mTopLeftImg;
    private TextView mTopLeftText;
    private TextView mBottomLeftText;
    private TextView mBottomRightText1;
    private TextView mBottomRightText2;
    private LinearLayout mTopBox;
    private LinearLayout mBottomBox;
    private BottomLayoutOnclickListener mBottomLayoutOnclick;
    private ImageView mBottomLeftIcon;
    private ImageView mRightArrow;
    private ImageView mBottomTopView;
    private LinearLayout mBottomBottomLayoutBox;

    public IconItemViewTwoLine(Context context) {
        super(context);
    }

    public IconItemViewTwoLine(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected ViewGroup loadLayout(ViewGroup container, AttributeSet attrs) {
        ViewGroup vg = super.loadLayout(container, attrs);

        LayoutInflater.from(getContext()).inflate(R.layout.l_label_topimgtext_bottomtext,vg);

        mTopBox = (LinearLayout) findViewById(R.id.top_box);
        mTopLeftImg = (ImageView) findViewById(R.id.top_left_icon);
        mTopLeftText = (TextView) findViewById(R.id.top_left_text);
        mBottomTopView = (ImageView) findViewById(R.id.bottom_top_view);
        mBottomBox = (LinearLayout) findViewById(R.id.bottom_box);
        mBottomBottomLayoutBox = (LinearLayout) findViewById(R.id.bottom_box_layout);
        LinearLayout bottomRightLayoutBox = (LinearLayout) findViewById(R.id.bottom_right_box);
        mBottomLeftIcon = (ImageView) findViewById(R.id.bottom_left_icon);
        mBottomLeftText = (TextView) findViewById(R.id.bottom_left_text);
        mBottomRightText1 = (TextView) findViewById(R.id.bottom_right_text_1);
        mBottomRightText2 = (TextView) findViewById(R.id.bottom_right_text_2);
        mRightArrow = (ImageView) findViewById(R.id.bottom_right_arrow);

        mBottomBox.setOnClickListener(this);
        mBottomLeftText.setOnClickListener(this);
        mBottomRightText1.setOnClickListener(this);
        mBottomRightText2.setOnClickListener(this);
        mRightArrow.setOnClickListener(this);

        return bottomRightLayoutBox;
    }

    @Override
    protected void init(AttributeSet attrs) {
        super.init(attrs);

        if (attrs == null) return;

//        int dp10 = Dimension.dip2px(10, getContext());

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.IconItemViewTwoLine);

        //设置顶部布局宽高度
        int topLayoutWidth = typedArray.getLayoutDimension(R.styleable.IconItemViewTwoLine_topLayoutWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        int topLayoutHeight = typedArray.getLayoutDimension(R.styleable.IconItemViewTwoLine_topLayoutHeight, LinearLayout.LayoutParams.WRAP_CONTENT);
        setTopBoxWidtgHeight(topLayoutWidth, topLayoutHeight);

        //设置底部布局宽高度
        int bottomLayoutWidth = typedArray.getLayoutDimension(R.styleable.IconItemViewTwoLine_bottomLayoutWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        int bottomLayoutHeight = typedArray.getLayoutDimension(R.styleable.IconItemViewTwoLine_bottomLayoutHeight, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams bottomLayoutParams = setBottomBoxWidthHeight(bottomLayoutWidth, bottomLayoutHeight);

        //设置底部下边布局宽高度
        int bottomBottomLayoutWidth = typedArray.getLayoutDimension(R.styleable.IconItemViewTwoLine_bottomBottomLayoutWidth, LinearLayout.LayoutParams.FILL_PARENT);
        int bottomBottomLayoutHeight = typedArray.getLayoutDimension(R.styleable.IconItemViewTwoLine_bottomBottomLayoutHeight, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams bottomBottomLayoutParams = setBottomBottomLayoutWidthHeight(bottomBottomLayoutWidth, bottomBottomLayoutHeight);

        //设置顶部布局内间距
        float topLayoutPadding = typedArray.getDimension(R.styleable.IconItemViewTwoLine_topLayoutPadding,0);
        if (topLayoutPadding != 0){
            setTopLayoutPadding((int) topLayoutPadding);
        }

        float topLayoutLeftPadding = typedArray.getDimension(R.styleable.IconItemViewTwoLine_topLayoutLeftPadding,topLayoutPadding);
        float topLayoutTopPadding = typedArray.getDimension(R.styleable.IconItemViewTwoLine_topLayoutTopPadding,topLayoutPadding);
        float topLayoutRightPadding = typedArray.getDimension(R.styleable.IconItemViewTwoLine_topLayoutRightPadding,topLayoutPadding);
        float topLayoutBottomPadding = typedArray.getDimension(R.styleable.IconItemViewTwoLine_topLayoutBottomPadding,topLayoutPadding);
        setTopLayoutPadding((int) topLayoutLeftPadding,(int) topLayoutTopPadding,
                  (int) topLayoutRightPadding,(int) topLayoutBottomPadding);

        //顶部左侧ImageView
        int topLeftIconSrc = typedArray.getResourceId(R.styleable.IconItemViewTwoLine_topLeftIconSrc,0);
        if (topLeftIconSrc != 0){
            setTopLeftIcon(topLeftIconSrc);
        }

        float topIconSpcingText = typedArray.getDimension(R.styleable.IconItemViewTwoLine_topLeftIconSpacingRight,0);
        if (topIconSpcingText != 0){
            setTopLeftIconSpacingRight((int) topIconSpcingText);
        }

        int topLeftIconVisibility = typedArray.getInt(R.styleable.IconItemViewTwoLine_topLeftIconVisibility,VISIBLE);
        setTopLeftIconVisibility(topLeftIconVisibility);

        //设置顶部左边文本属性
        String topLeftText = typedArray.getString(R.styleable.IconItemViewTwoLine_topLeftText);
        if (topLeftText != null){
            setTopLeftText(topLeftText);
        }

        int topLeftTextRes = typedArray.getResourceId(R.styleable.IconItemViewTwoLine_topLeftText,0);
        if (topLeftTextRes != 0){
            setTopLeftText(topLeftTextRes);
        }

        float topLeftTextSize = typedArray.getDimension(R.styleable.IconItemViewTwoLine_topLeftTextSize, 0.0f);
        if (topLeftTextSize != 0.0f){
            setTopLeftTextSize(TypedValue.COMPLEX_UNIT_PX,topLeftTextSize);
        }

        int topLeftTextColor = typedArray.getColor(R.styleable.IconItemViewTwoLine_topLeftTextColor, Color.BLACK);
        setTopLeftTextColor(topLeftTextColor);

        float topLeftTextSpacingLayout = typedArray.getDimension(R.styleable.IconItemViewTwoLine_topLeftTextSpacingLayout, 0);
        setTopLeftTextSpacingLayout((int) topLeftTextSpacingLayout);

        int topLeftTextStyle = typedArray.getInt(R.styleable.IconItemViewTwoLine_topLeftTextStyle, Typeface.NORMAL);
        setTopLeftTextStyle(topLeftTextStyle);

        int topLeftTextVisibility = typedArray.getInt(R.styleable.IconItemViewTwoLine_topLeftTextVisibility, Typeface.NORMAL);
        setTopLeftTextVisibility(topLeftTextVisibility);

        int topLeftTextGravity = typedArray.getInt(R.styleable.IconItemViewTwoLine_topLeftTextGravity, Gravity.CENTER_VERTICAL);
        setTopLeftTextGravity(topLeftTextGravity);

        //设置底部布局样式
        int bottomLayoutBg = typedArray.getResourceId(R.styleable.IconItemViewTwoLine_bottomLayoutStyle,0);
        if (bottomLayoutBg != 0){
            setBottomLayoutBg(bottomLayoutBg);
        }

        //设置底部布局内间距
        float bottomLayoutPadding = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomLayoutPadding,0);
        if (bottomLayoutPadding != 0){
            setBottomLayoutPadding((int) bottomLayoutPadding);
        }

        float bottomLayoutLeftPadding = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomLayoutLeftPadding,bottomLayoutPadding);
        float bottomLayoutTopPadding = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomLayoutTopPadding,bottomLayoutPadding);
        float bottomLayoutRightPadding = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomLayoutRightPadding,bottomLayoutPadding);
        float bottomLayoutBottomPadding = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomLayoutBottomPadding,bottomLayoutPadding);
        setBottomLayoutPadding((int) bottomLayoutLeftPadding, (int) bottomLayoutTopPadding,
                  (int) bottomLayoutRightPadding, (int) bottomLayoutBottomPadding);

        //设置底部布局外间距
        float bottomLayoutMargin = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomLayoutMargin,0);
        if (bottomLayoutMargin != 0){
            setBottomLayoutMargin(bottomLayoutParams,(int) bottomLayoutMargin);
        }

        float bottomLayoutLeftMargin = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomLayoutLeftMargin,bottomLayoutMargin);
        float bottomLayoutTopMargin = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomLayoutTopMargin,bottomLayoutMargin);
        float bottomLayoutRightMargin = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomLayoutRightMargin,bottomLayoutMargin);
        float bottomLayoutBottomMargin = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomLayoutBottomMargin,bottomLayoutMargin);
        setBottomLayoutMargin(bottomLayoutParams,(int) bottomLayoutLeftMargin,
                  (int) bottomLayoutTopMargin,(int) bottomLayoutRightMargin,
                  (int) bottomLayoutBottomMargin);

        //设置底部下布局内间距
        float bottomBottomLayoutPadding = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomBottomLayoutPadding,0);
        if (bottomBottomLayoutPadding != 0){
            setBottomBottomLayoutPadding((int) bottomBottomLayoutPadding);
        }

        float bottomBottomLayoutLeftPadding = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomBottomLayoutLeftPadding,bottomBottomLayoutPadding);
        float bottomBottomLayoutTopPadding = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomBottomLayoutTopPadding,bottomBottomLayoutPadding);
        float bottomBottomLayoutRightPadding = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomBottomLayoutRightPadding,bottomBottomLayoutPadding);
        float bottomBottomLayoutBottomPadding = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomBottomLayoutBottomPadding,bottomBottomLayoutPadding);
        setBottomBottomLayoutPadding((int) bottomBottomLayoutLeftPadding, (int) bottomBottomLayoutTopPadding,
                  (int) bottomBottomLayoutRightPadding, (int) bottomBottomLayoutBottomPadding);

        //设置底部下布局外间距
        float bottomBottomLayoutMargin = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomBottomLayoutMargin,0);
        if (bottomBottomLayoutMargin != 0){
            setBottomBottomLayoutMargin(bottomBottomLayoutParams,
                      (int) bottomBottomLayoutMargin);
        }

        float bottomBottomLayoutLeftMargin = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomBottomLayoutLeftMargin,bottomBottomLayoutMargin);
        float bottomBottomLayoutTopMargin = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomBottomLayoutTopMargin,bottomBottomLayoutMargin);
        float bottomBottomLayoutRightMargin = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomBottomLayoutRightMargin,bottomBottomLayoutMargin);
        float bottomBottomLayoutBottomMargin = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomBottomLayoutBottomMargin,bottomBottomLayoutMargin);
        setBottomBottomLayoutMargin(bottomBottomLayoutParams,
                  (int) bottomBottomLayoutLeftMargin,
                  (int) bottomBottomLayoutTopMargin,
                  (int) bottomBottomLayoutRightMargin,
                  (int) bottomBottomLayoutBottomMargin);

        // 底部布局上图标
        int bottomTopViewBg = typedArray.getResourceId(R.styleable.IconItemViewTwoLine_bottomTopViewBg,0);
        if (bottomTopViewBg != 0){
            setBottomTopViewBg(bottomTopViewBg);
        }

        float bottomTopViewMargin = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomTopViewMargin,0);
        if (bottomTopViewMargin != 0){
            setBottomTopViewMargin((int) bottomTopViewMargin);
        }

        float bottomTopViewLeftMargin = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomTopViewLeftMargin,bottomTopViewMargin);
        float bottomTopViewTopMargin = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomTopViewTopMargin,bottomTopViewMargin);
        float bottomTopViewRightMargin = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomTopViewRightMargin,bottomTopViewMargin);
        float bottomTopViewBottomMargin = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomTopViewBottomMargin,bottomTopViewMargin);
        setBottomTopViewMargin((int) bottomTopViewLeftMargin, (int) bottomTopViewTopMargin,
                  (int) bottomTopViewRightMargin, (int) bottomTopViewBottomMargin);

        int bottomTopViewVisibility = typedArray.getInt(R.styleable.IconItemViewTwoLine_bottomTopViewVisibility,VISIBLE);
        setBottomTopViewVisibility(bottomTopViewVisibility);

        //底部左侧ImageView
        int bottomLeftIconSrc = typedArray.getResourceId(R.styleable.IconItemViewTwoLine_bottomLeftIconSrc,0);
        if (bottomLeftIconSrc != 0){
            setBottomLeftIcon(bottomLeftIconSrc);
        }

        float bottomIconSpcingLeft = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomLeftIconSpacingLeft,0);
        if (bottomIconSpcingLeft != 0){
            setBottomLeftIconSpacingLeft((int) bottomIconSpcingLeft);
        }

        float bottomIconSpcingText = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomLeftIconSpacingRight,0);
        if (bottomIconSpcingText != 0){
            setBottomLeftIconSpacingRight((int) bottomIconSpcingText);
        }

        int bottomLeftIconVisibility = typedArray.getInt(R.styleable.IconItemViewTwoLine_bottomLeftIconVisibility,VISIBLE);
        setBottomLeftIconVisibility(bottomLeftIconVisibility);

        //设置底部左边文本属性
        String bottomLeftText = typedArray.getString(R.styleable.IconItemViewTwoLine_bottomLeftText);
        if (bottomLeftText != null){
            setBottomLeftText(bottomLeftText);
        }

        int bottomLeftTextRes = typedArray.getResourceId(R.styleable.IconItemViewTwoLine_bottomLeftText, 0);
        if (bottomLeftTextRes != 0){
            setBottomLeftText(bottomLeftTextRes);
        }

        float bottomLeftTextSize = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomLeftTextSize, 0.0f);
        if (bottomLeftTextSize != 0.0f){
            setBottomLeftTextSize(TypedValue.COMPLEX_UNIT_PX,bottomLeftTextSize);
        }

        int leftTextColor = typedArray.getColor(R.styleable.IconItemViewTwoLine_bottomLeftTextColor, Color.BLACK);
        setBottomLeftTextColor(leftTextColor);

        float bottomLeftTextSpacingLayout = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomLeftTextSpacingLayout, 0);
        setBottomLeftTextSpacingLayout((int) bottomLeftTextSpacingLayout);

        int bottomLeftTextStyle = typedArray.getInt(R.styleable.IconItemViewTwoLine_bottomLeftTextStyle, Typeface.NORMAL);
        setBottomLeftTextStyle(bottomLeftTextStyle);

        int bottomLeftTextVisibility = typedArray.getInt(R.styleable.IconItemViewTwoLine_bottomLeftTextVisibility, Typeface.NORMAL);
        setBottomLeftTextVisibility(bottomLeftTextVisibility);

        int bottomLeftTextGravity = typedArray.getInt(R.styleable.IconItemViewTwoLine_bottomLeftTextGravity, Gravity.CENTER_VERTICAL);
        setBottomLeftTextGravity(bottomLeftTextGravity);

        //设置底部右边文本1属性
        String bottomRightTextOne = typedArray.getString(R.styleable.IconItemViewTwoLine_bottomRightTextOne);
        if (bottomRightTextOne != null){
            setBottomRightTextOne(bottomRightTextOne);
        }

        int bottomRightTextOneRes = typedArray.getResourceId(R.styleable.IconItemViewTwoLine_bottomRightTextOne,0);
        if (bottomRightTextOneRes != 0){
            setBottomRightTextOne(bottomRightTextOneRes);
        }

        float bottomRightTextOneSize = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomRightTextOneSize, 0.0f);
        if (bottomRightTextOneSize != 0){
            setBottomRightTextOneSize(TypedValue.COMPLEX_UNIT_PX,bottomRightTextOneSize);
        }

        int rightTextOneColor = typedArray.getColor(R.styleable.IconItemViewTwoLine_bottomRightTextOneColor, Color.BLACK);
        setBottomRightTextOneColor(rightTextOneColor);

        float bottomRightTextOneSpacingLayout = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomRightTextOneSpacingLayout, 0);
        setRightTextOneSpacingLayout((int) bottomRightTextOneSpacingLayout);

        int bottomRightTextOneStyle = typedArray.getInt(R.styleable.IconItemViewTwoLine_bottomRightTextOneStyle, Typeface.NORMAL);
        setBottomRightTextOneStyle(bottomRightTextOneStyle);

        int bottomRightTextOneVisibility = typedArray.getInt(R.styleable.IconItemViewTwoLine_bottomRightTextOneVisibility, Typeface.NORMAL);
        setBottomRightTextOneVisibility(bottomRightTextOneVisibility);

        int  bottomRightTextOneGravity = typedArray.getInt(R.styleable.IconItemViewTwoLine_bottomRightTextOneGravity, Gravity.CENTER_VERTICAL);
        setBottomRightTextOneGravity(bottomRightTextOneGravity);

        //设置底部右边文本2属性
        String bottomRightTextTwo = typedArray.getString(R.styleable.IconItemViewTwoLine_bottomRightTextTwo);
        if (bottomRightTextTwo != null){
            setBottomRightTextTwo(bottomRightTextTwo);
        }

        int bottomRightTextTwoRes = typedArray.getResourceId(R.styleable.IconItemViewTwoLine_bottomRightTextTwo,0);
        if (bottomRightTextTwoRes != 0){
            setBottomRightTextTwo(bottomRightTextTwoRes);
        }

        float bottomRightTextTwoSize = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomRightTextTwoSize, 0.0f);
        if (bottomRightTextTwoSize != 0.0f){
            setBottomRightTextTwoSize(TypedValue.COMPLEX_UNIT_PX,bottomRightTextTwoSize);
        }

        int rightTextTwoColor = typedArray.getColor(R.styleable.IconItemViewTwoLine_bottomRightTextTwoColor, Color.BLACK);
        setBottomRightTextTwoColor(rightTextTwoColor);

        float bottomRightTextTwoSpacingLayout = typedArray.getDimension(R.styleable.IconItemViewTwoLine_bottomRightTextTwoSpacingLayout, 0);
        setRightTextTwoSpacingLayout((int) bottomRightTextTwoSpacingLayout);

        int bottomRightTextTwoStyle = typedArray.getInt(R.styleable.IconItemViewTwoLine_bottomRightTextTwoStyle, Typeface.NORMAL);
        setBottomRightTextTwoStyle(bottomRightTextTwoStyle);

        int bottomRightTextTwoVisibility = typedArray.getInt(R.styleable.IconItemViewTwoLine_bottomRightTextTwoVisibility, Typeface.NORMAL);
        setBottomRightTextTwoVisibility(bottomRightTextTwoVisibility);

        int  bottomRightTextTwoGravity = typedArray.getInt(R.styleable.IconItemViewTwoLine_bottomRightTextTwoGravity, Gravity.CENTER_VERTICAL);
        setBottomRightTextTwoGravity(bottomRightTextTwoGravity);

        int visibility = typedArray.getInt(R.styleable.IconItemViewTwoLine_bottomRightArrowVisibility,VISIBLE);
        setBottomRightArrowVisibility(visibility);

        typedArray.recycle();
    }

    /**
     * 设置点击事件
     */
    public interface BottomLayoutOnclickListener{
        void bottomOnclick(View view);
    }

    public void setBottomLayoutClickListener(BottomLayoutOnclickListener mBottomLayoutOnclick){
        if (mBottomLayoutOnclick == null) throw new NullPointerException("BottomLayoutOnclickListener is null at com.lakala.library.IconItemViewTwoLine");
        this.mBottomLayoutOnclick = mBottomLayoutOnclick;
    }

    @Override
    public void onClick(View view) {
        if (mBottomLayoutOnclick != null){
            mBottomLayoutOnclick.bottomOnclick(view);
        }
    }

    /**
     * 设置顶部布局宽高
     * @param topLayoutWidth
     * @param topLayoutHeight
     */
    public LinearLayout.LayoutParams setTopBoxWidtgHeight(int topLayoutWidth, int topLayoutHeight) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(topLayoutWidth,topLayoutHeight);
        mTopBox.setLayoutParams(params);
        return params;
    }

    /**
     * 设置底部布局宽高
     * @param topLayoutWidth
     * @param topLayoutHeight
     */
    public LinearLayout.LayoutParams setBottomBoxWidthHeight(int topLayoutWidth, int topLayoutHeight) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(topLayoutWidth,topLayoutHeight);
        mBottomBox.setLayoutParams(params);
        return params;
    }

    /**
     * 设置底部布局宽高
     * @param topLayoutWidth
     * @param topLayoutHeight
     */
    public LinearLayout.LayoutParams setBottomBottomLayoutWidthHeight(int topLayoutWidth, int topLayoutHeight) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(topLayoutWidth,topLayoutHeight);
        mBottomBottomLayoutBox.setLayoutParams(params);
        return params;
    }

    /**
     *设置上布局内间距
     */
    public void setTopLayoutPadding(int padding){
          mTopBox.setPadding(padding,padding,padding,padding);
    }

    /**
     *设置上布局内间距
     */
    public void setTopLayoutPadding(int leftPadding,int topPadding,
                                    int rightPadding,int bottomPadding){
           mTopBox.setPadding(leftPadding,topPadding,rightPadding,bottomPadding);
    }

    /**
     *设置上布局图片资源
     */
    public void setTopLeftIcon(int imgResouce){
           mTopLeftImg.setImageResource(imgResouce);
    }

    /**
     *设置上布局左侧
     */
    public void setTopLeftIconSpacingRight(int spacing){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                  LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,spacing,0);
        mTopLeftImg.setLayoutParams(params);
    }

    /**
     *设置上布局图片是否显示
     */
    public void setTopLeftIconVisibility(int imgVisibility){
        mTopLeftImg.setVisibility(imgVisibility);
    }

    /**
     * 设置上部左边文本
     */
    public void setTopLeftText(CharSequence text){
        mTopLeftText.setText(text);
    }

    public void setTopLeftText(int text){
        mTopLeftText.setText(text);
    }

    /**
     * 设置上部左边文本大小
     */
    public void setTopLeftTextSize(int unit,float textSize){
        mTopLeftText.setTextSize(unit, textSize);
    }

    /**
     * 设置上部左边文本颜色
     * @param leftTextColor
     */
    public void setTopLeftTextColor(int leftTextColor) {
        mTopLeftText.setTextColor(leftTextColor);
    }

    /**
     * 设置上部左边文本与右侧布局间距
     * @param leftTextSpacingLayout
     */
    public void setTopLeftTextSpacingLayout(int leftTextSpacingLayout) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                  LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0, (int) leftTextSpacingLayout,0);
        mTopLeftText.setLayoutParams(params);
    }

    /**
     * 设置上部左文本风格
     * @param bottomLeftTextStyle
     */
    public void setTopLeftTextStyle(int bottomLeftTextStyle) {
        Paint paint = mTopLeftText.getPaint();
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, bottomLeftTextStyle));
    }

    /**
     * 顶部左边文本是否显示
     * @param topLeftTextVisibility
     */
    public void setTopLeftTextVisibility(int topLeftTextVisibility) {
        mTopLeftText.setVisibility(topLeftTextVisibility);
    }

    /**
     * 设置上部左边文本内容位置
     * @param gravity
     */
    public void setTopLeftTextGravity(int gravity) {
        mTopLeftText.setGravity(gravity);
    }

    /**
     * 设置底部布局样式
     */
    public void setBottomLayoutBg(int resId) {
        mBottomBox.setBackgroundResource(resId);
    }

    /**
     *设置底部布局内间距
     */
    public void setBottomLayoutPadding(int padding){
        mBottomBox.setPadding(padding, padding, padding, padding);
    }

    /**
     *设置底部布局内间距
     */
    public void setBottomLayoutPadding(int leftPadding,int topPadding,
                                    int rightPadding,int bottomPadding){
        mBottomBox.setPadding(leftPadding,topPadding,rightPadding,bottomPadding);
    }

    /**
     *设置底部布局内间距
     */
    public void setBottomLayoutMargin(LinearLayout.LayoutParams params,int margin){
        params.setMargins(margin,margin,margin,margin);
        mBottomBox.setLayoutParams(params);
    }

    /**
     *设置底部布局内间距
     */
    public void setBottomLayoutMargin(LinearLayout.LayoutParams params,int leftMargin,int topMargin,
                                    int rightMargin,int bottomMargin){
        params.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        mBottomBox.setLayoutParams(params);
    }

    /**
     * 设置底部上边组件背景
     * @param backgroundRes
     */
    public void setBottomTopViewBg(int backgroundRes) {
        mBottomTopView.setBackgroundResource(backgroundRes);
    }

    /**
     * 设置底部上边组件间距
     * @param margin
     */
    public void setBottomTopViewMargin(int margin) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                  LinearLayout.LayoutParams.FILL_PARENT,
                  LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin,margin,margin,margin);
        mBottomTopView.setLayoutParams(params);
    }

    /**
     * 设置底部上边组件间距
     */
    public void setBottomTopViewMargin(int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                  LinearLayout.LayoutParams.FILL_PARENT,
                  LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(leftMargin,topMargin,rightMargin,bottomMargin);
        mBottomTopView.setLayoutParams(params);
    }

    /**
     * 设置底部上边组件是否显示
     */
    public void setBottomTopViewVisibility(int visibility) {
        mBottomTopView.setVisibility(visibility);
    }

    /**
     *设置底部下边布局内间距
     */
    public void setBottomBottomLayoutPadding(int padding){
        mBottomBottomLayoutBox.setPadding(padding, padding, padding, padding);
    }

    /**
     *设置底部下边布局内间距
     */
    public void setBottomBottomLayoutPadding(int leftPadding,int topPadding,
                                       int rightPadding,int bottomPadding){
        mBottomBottomLayoutBox.setPadding(leftPadding,topPadding,rightPadding,bottomPadding);
    }

    /**
     *设置底部下边布局外间距
     */
    public void setBottomBottomLayoutMargin(LinearLayout.LayoutParams params, int margin){
        params.setMargins(margin,margin,margin,margin);
        mBottomBottomLayoutBox.setLayoutParams(params);
    }

    /**
     *设置底部下边布局外间距
     */
    public void setBottomBottomLayoutMargin(LinearLayout.LayoutParams params,
                                      int leftMargin,int topMargin,
                                      int rightMargin,int bottomMargin){
        params.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        mBottomBottomLayoutBox.setLayoutParams(params);
    }

    /**
     *设置底部布局左边图片资源
     */
    public void setBottomLeftIcon(int imgResouce){
        mBottomLeftIcon.setImageResource(imgResouce);
    }

    /**
     *设置底部布局左边图片与右侧间距
     */
    public void setBottomLeftIconSpacingLeft(int spacing){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                  LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(spacing,0,0,0);
        mBottomLeftIcon.setLayoutParams(params);
    }

    /**
     *设置底部布局左边图片与右侧间距
     */
    public void setBottomLeftIconSpacingRight(int spacing){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                  LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,spacing,0);
        mBottomLeftIcon.setLayoutParams(params);
    }

    /**
     *设置底部布局左边图片是否显示
     */
    public void setBottomLeftIconVisibility(int imgVisibility){
        mBottomLeftIcon.setVisibility(imgVisibility);
    }


    /**
     * 设置底部左边文本
     */
    public void setBottomLeftText(CharSequence text){
        mBottomLeftText.setText(text);
    }

    public void setBottomLeftText(int text){
        mBottomLeftText.setText(text);
    }

    /**
     * 设置底部左边文本大小
     */
    public void setBottomLeftTextSize(int unit,float textSize){
        mBottomLeftText.setTextSize(unit, textSize);
    }

    /**
     * 设置底部左边文本颜色
     * @param leftTextColor
     */
    public void setBottomLeftTextColor(int leftTextColor) {
        mBottomLeftText.setTextColor(leftTextColor);
    }

    /**
     * 设置底部左边文本与右侧布局间距
     * @param leftTextSpacingLayout
     */
    public void setBottomLeftTextSpacingLayout(int leftTextSpacingLayout) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                  ViewGroup.LayoutParams.WRAP_CONTENT,
                  ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        params.setMargins(0,0, (int) leftTextSpacingLayout,0);
        mBottomLeftText.setLayoutParams(params);
    }

    /**
     * 设置底部左文本风格
     * @param bottomLeftTextStyle
     */
    public void setBottomLeftTextStyle(int bottomLeftTextStyle) {
        Paint paint = mBottomLeftText.getPaint();
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, bottomLeftTextStyle));
    }

    /**
     * 底部左边文本是否显示
     * @param bottomLeftTextVisibility
     */
    public void setBottomLeftTextVisibility(int bottomLeftTextVisibility) {
        mBottomLeftText.setVisibility(bottomLeftTextVisibility);
    }

    /**
     * 设置底部左文本风格Gravity
     * @param gravity
     */
    public void setBottomLeftTextGravity(int gravity){
        mBottomLeftText.setGravity(gravity);
    }

    /**
     * 设置底部右边文本1
     */
    public void setBottomRightTextOne(CharSequence text){
        mBottomRightText1.setText(text);
    }

    public void setBottomRightTextOne(int text){
        mBottomRightText1.setText(text);
    }

    /**
     * 设置底部右边文本1大小
     */
    public void setBottomRightTextOneSize(int unit,float textSize){
        mBottomRightText1.setTextSize(unit, textSize);
    }

    /**
     * 设置底部右边文本1颜色
     * @param leftTextColor
     */
    public void setBottomRightTextOneColor(int leftTextColor) {
        mBottomRightText1.setTextColor(leftTextColor);
    }

    /**
     * 设置底部右边文本1与右侧布局间距
     * @param leftTextSpacingLayout
     */
    public void setRightTextOneSpacingLayout(int leftTextSpacingLayout) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                  ViewGroup.LayoutParams.WRAP_CONTENT,
                  ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0, (int) leftTextSpacingLayout,0);
        mBottomRightText1.setLayoutParams(params);
    }

    /**
     * 设置底部左文本风格
     * @param bottomTextStyle
     */
    public void setBottomRightTextOneStyle(int bottomTextStyle) {
        Paint paint = mBottomRightText1.getPaint();
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, bottomTextStyle));
    }

    /**
     * 底部右边文本1是否显示
     * @param bottomRightTextOneVisibility
     */
    public void setBottomRightTextOneVisibility(int bottomRightTextOneVisibility) {
        mBottomRightText1.setVisibility(bottomRightTextOneVisibility);
    }

    /**
     * 设置底部右边文本1内容Gravity
     * @param gravity
     */
    public void setBottomRightTextOneGravity(int gravity){
        mBottomRightText1.setGravity(gravity);
    }

    /**
     * 设置底部右边文本2
     */
    public void setBottomRightTextTwo(CharSequence text){
        mBottomRightText2.setText(text);
    }

    public void setBottomRightTextTwo(int text){
        mBottomRightText2.setText(text);
    }

    /**
     * 设置底部右边文本2大小
     */
    public void setBottomRightTextTwoSize(int unit,float textSize){
        mBottomRightText2.setTextSize(unit, textSize);
    }

    /**
     * 设置底部右边文本2颜色
     * @param leftTextColor
     */
    public void setBottomRightTextTwoColor(int leftTextColor) {
        mBottomRightText2.setTextColor(leftTextColor);
    }

    /**
     * 设置底部右边文本2与右侧布局间距
     * @param TextTwoSpacingLayout
     */
    public void setRightTextTwoSpacingLayout(int TextTwoSpacingLayout) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                  ViewGroup.LayoutParams.WRAP_CONTENT,
                  ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0, (int) TextTwoSpacingLayout,0);
        mBottomRightText2.setLayoutParams(params);
    }

    /**
     * 设置底部右边文本2风格Gravity
     * @param gravity
     */
    public void setBottomRightTextTwoGravity(int gravity){
        mBottomRightText2.setGravity(gravity);
    }

    /**
     * 设置底部左文本风格
     * @param bottomLeftTextStyle
     */
    public void setBottomRightTextTwoStyle(int bottomLeftTextStyle) {
        Paint paint = mBottomRightText2.getPaint();
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, bottomLeftTextStyle));
    }

    /**
     * 底部右边文本2是否显示
     * @param bottomRightTextTwoVisibility
     */
    public void setBottomRightTextTwoVisibility(int bottomRightTextTwoVisibility) {
        mBottomRightText2.setVisibility(bottomRightTextTwoVisibility);
    }

    /**
     * 设置右侧箭头图标是否显示
     */
    public void setBottomRightArrowVisibility(int visibility){
        mRightArrow.setVisibility(visibility);
    }

    /**
     * 获取底部左文本内容
     */
    public CharSequence getLeftText(){
        return mBottomLeftText.getText();
    }

   /**
     * 获取底部右文本1内容
     */
    public CharSequence getBottomRightOneText(){
        return mBottomRightText1.getText();
    }

    /**
     * 获取底部右文本2内容
     */
    public CharSequence getBottomRightTwoText(){
        return mBottomRightText2.getText();
    }
}
