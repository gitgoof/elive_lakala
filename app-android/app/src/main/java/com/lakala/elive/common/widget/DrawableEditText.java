package com.lakala.elive.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * 可以让drawablelLeft图片与文字一起居中显示
 * Created by wenhaogu on 2016/12/13.
 */

public class DrawableEditText extends EditText {


    public DrawableEditText(Context context) {
        super(context);
    }

    public DrawableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        Drawable drawableLeft = drawables[0];
        if (drawableLeft != null) {
            float textWidth = getPaint().measureText(getText().toString());
            float hintWidth = getPaint().measureText(getHint().toString());
            int paddingLeft = getPaddingLeft();
            int paddingRight = getPaddingRight();
            int drawablePadding = getCompoundDrawablePadding();
            int drawableWidth;
            float bodyWidth;
            drawableWidth = drawableLeft.getIntrinsicWidth();
            if (textWidth < 1) {
                bodyWidth = hintWidth + drawableWidth + drawablePadding + paddingLeft + paddingRight;
            } else {
                bodyWidth = textWidth + drawableWidth + drawablePadding + paddingLeft + paddingRight;
            }
            if (bodyWidth < getWidth()) {
                canvas.translate((getWidth() - bodyWidth) / 2, 0);
            }

        }
        super.onDraw(canvas);
    }


}
