package com.lakala.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.ui.R;

/**
 * Created by LMQ on 2015/12/1.
 */
public final class OneIconTwoTextView extends BaseItemView {
    private ImageView icon;
    private TextView topTextView;
    private TextView bottomTextView;

    public OneIconTwoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OneIconTwoTextView(Context context) {
        super(context);
    }

    @Override
    protected ViewGroup loadLayout(ViewGroup container, AttributeSet attrs) {
        ViewGroup vg = super.loadLayout(container, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.l_one_icon_two_text,vg);
        icon = (ImageView)findViewById(R.id.icon);
        topTextView = (TextView)findViewById(R.id.top_text);
        bottomTextView = (TextView)findViewById(R.id.bottom_text);
        return null;
    }
    public void setBottomTextVisibility(int visibility){
        if(bottomTextView != null){
            bottomTextView.setVisibility(visibility);
        }
    }

    public ImageView getIcon() {
        return icon;
    }

    public TextView getTopTextView() {
        return topTextView;
    }

    public TextView getBottomTextView() {
        return bottomTextView;
    }

    @Override
    protected void init(AttributeSet attrs) {
        super.init(attrs);

        if(attrs == null){
            return;
        }
        TypedArray typedArray = getContext()
                .obtainStyledAttributes(attrs, R.styleable.OneIconTwoTextView);
        int iconRes = typedArray.getResourceId(R.styleable.OneIconTwoTextView_iconRes, 0);
        setIconResource(iconRes);

        String topTextStr = typedArray.getString(R.styleable.OneIconTwoTextView_topText);
        setTopText(topTextStr);
        int topTextColor = typedArray.getColor(R.styleable.OneIconTwoTextView_topTextColor, Color.BLACK);
        setTopTextColor(topTextColor);
        float topTextSize = typedArray
                .getDimension(R.styleable.OneIconTwoTextView_topTextSize, 20f);
        setTopTextSize(topTextSize);

        String bottomTextStr = typedArray.getString(R.styleable.OneIconTwoTextView_bottomText);
        setBottomText(bottomTextStr);
        int bottomTextColor = typedArray.getColor(R.styleable.OneIconTwoTextView_bottomTextColor,
                                                  Color.BLACK);
        setBottomTextColor(bottomTextColor);
        float bottomTextSize = typedArray.getDimension(R.styleable
                                                               .OneIconTwoTextView_bottomTextSize,20f);
        setBottomTextSize(bottomTextSize);
    }
    public void setBottomTextSize(float size){
        setTextSize(bottomTextView,size);
    }
    public void setBottomTextColor(int color){
        setTextColor(bottomTextView,color);
    }
    public void setBottomText(String text){
        setText(bottomTextView,text);
    }
    public void setTopTextSize(float size){
        setTextSize(topTextView,size);
    }
    public void setTopTextColor(int color){
        setTextColor(topTextView,color);
    }
    public void setTopText(int res){
        String text = getContext().getString(res);
        setTopText(text);
    }
    public void setTopText(String text){
        setText(topTextView,text);
    }

    public void setIconResource(int res){
        if(res == 0){
            return;
        }
        icon.setImageResource(res);
    }
    public void setIconBitmap(Bitmap bitmap){
        if(bitmap == null){
            return;
        }
        icon.setImageBitmap(bitmap);
    }


    private void setTextSize(TextView textView,float size){
        if(textView != null){
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        }
    }
    private void setText(TextView textView,String text){
        if(TextUtils.isEmpty(text)){
            text = "";
        }
        if(textView != null){
            textView.setText(text);
        }
    }
    private void setTextColor(TextView textView,int color){
        if(textView != null){
            textView.setTextColor(color);
        }
    }
}
