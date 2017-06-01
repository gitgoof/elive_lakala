package com.lakala.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.TextView;

import com.lakala.ui.R;

/**
 * Created by LMQ on 2015/7/9.
 */
public class ScreenWeightTextView extends TextView {
    private int screenWidth;
    public ScreenWeightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable
                                                                    .ScreenWeightTextView);
        init(ta);
        ta.recycle();
    }

    private void init(TypedArray ta) {
        float widthFloat = ta.getFloat(R.styleable.ScreenWeightTextView_widthFloat,
                                              -1);
        if(widthFloat != -1){
            setWidth((int)(screenWidth*widthFloat));
        }
    }
}
