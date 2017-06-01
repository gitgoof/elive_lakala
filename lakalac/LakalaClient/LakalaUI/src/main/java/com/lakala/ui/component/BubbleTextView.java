package com.lakala.ui.component;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lakala.ui.R;


/**
 * Created by LMQ on 2015/3/4.
 * 气泡背景TextView
 */
public class BubbleTextView extends LinearLayout {
    private static final int DEF_TRIANGLE_WIDTH = 10;
    private static final int DEF_TRIANGLE_MARGIN_END = 5;
    public static final int DEF_RADIUS = 5;
    private TextView textView;
    private View triangleView;
    private Paint paint;
    private int mRadius;

    public BubbleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.BubbleTextView);
        loadLayout(ta);
        init(ta);
        ta.recycle();
    }

    private void init(TypedArray ta) {
        int color = ta.getColor(R.styleable.BubbleTextView_bubbleTextColor, Color.BLACK);
        setTextColor(color);
        int textSize = ta.getDimensionPixelSize(R.styleable.BubbleTextView_bubbleTextSize, 14);
        setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);

        paint = new Paint();
        ColorStateList backgroundColor = ta
                .getColorStateList(R.styleable.BubbleTextView_bubble_background_color);
        paint.setAntiAlias(true);
        paint.setColor(backgroundColor.getDefaultColor());

        mRadius = ta.getDimensionPixelSize(R.styleable.BubbleTextView_bubble_radius, DEF_RADIUS);
    }

    public void setTextSize(int textSize) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
    }

    /**
     * Set the default text size to a given unit and value.  See {@link
     * TypedValue} for the possible dimension units.
     *
     * @param unit The desired dimension unit.
     * @param size The desired size in the given units.
     *
     * @attr ref android.R.styleable#TextView_textSize
     */
    public void setTextSize(int unit, float size) {
        textView.setTextSize(unit,size);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
    }
    public void setTextColor(ColorStateList colors){
        textView.setTextColor(colors);
    }

    public void setText(CharSequence text) {
        textView.setText(text);
    }

    private void loadLayout(TypedArray ta) {
        setOrientation(LinearLayout.VERTICAL);
        textView = new TextView(getContext()) {
            @Override
            protected void onDraw(Canvas canvas) {
                //绘制圆角矩形背景
                Rect rect = canvas.getClipBounds();
                RectF rectF = new RectF(0, 0, rect.right, rect.bottom);
                float rx = mRadius;
                float ry = mRadius;
                canvas.drawRoundRect(rectF, rx, ry, paint);
                super.onDraw(canvas);
            }
        };
        triangleView = new View(getContext()) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                Path path = new Path();
                int width = triangleView.getWidth();
                int height = width * 2 / 3;
                path.moveTo(0, 0);
                path.lineTo(width, 0);
                path.lineTo(width / 2, height);
                path.close();
                canvas.drawPath(path, paint);
            }
        };

        addView(textView);
        addView(triangleView);

        int textWidth = ta.getDimensionPixelSize(R.styleable.BubbleTextView_bubble_width,
                                                 LayoutParams.MATCH_PARENT);
        int textheight = ta.getDimensionPixelSize(R.styleable.BubbleTextView_bubble_height,
                                                  LayoutParams.WRAP_CONTENT);
        LayoutParams params = new LayoutParams(textWidth, textheight);
        textView.setPadding(20,20,20,20);
        textView.setLayoutParams(params);

        int triangleWidth = ta.getDimensionPixelSize(R.styleable.BubbleTextView_triangle_width,
                                                     DEF_TRIANGLE_WIDTH);
        params = new LayoutParams(triangleWidth, triangleWidth);
        params.gravity = Gravity.RIGHT;
        int right = ta.getDimensionPixelSize(R.styleable.BubbleTextView_triangle_margin_end,
                                             DEF_TRIANGLE_MARGIN_END);
        params.setMargins(0, 0, right, 0);
        triangleView.setLayoutParams(params);

        setFocusable(true);
        setClickable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
