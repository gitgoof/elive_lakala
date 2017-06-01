package com.lakala.shoudan.component;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.shoudan.R;

/**
 * Created by linmq on 2016/3/21.
 */
public class TakePicView extends FrameLayout {

    private ImageView ivPic;
    private TextView tvLine1;
    private TextView tvLine2;
    private Point mSize = null;
    private ImageView imageView;
    private ImageView imageView2;
    private View layout;

    public TakePicView(Context context) {
        super(context);
        init(context,null);
    }

    public TakePicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.custom_take_pic_layout, this);
        layout = findViewById(R.id.ll_tack_pic);
        ivPic = (ImageView)findViewById(R.id.iv_take_pic);
        tvLine1 = (TextView)findViewById(R.id.tv_line1_tag);
        tvLine2 = (TextView)findViewById(R.id.tv_line2_tag);
        mSize = getViewSize(layout);
        storageViewSize(this, mSize);
        imageView = (ImageView)findViewById(R.id.iv_pic);
        imageView2= (ImageView) findViewById(R.id.iv_pic2);
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        if(params != null){
            params.height = mSize.y;
            imageView.setLayoutParams(params);
        }
        ViewGroup.LayoutParams params2 = imageView2.getLayoutParams();
        if(params2 != null){
            params2.height = mSize.y;
            imageView2.setLayoutParams(params2);
        }
    }
    public TextView getTvLine1(){
        return tvLine1;
    }
    public TextView getTvLine2(){
        return tvLine2;
    }
    public View getLinearLayout(){
        return layout;
    }
    public ImageView getImageView(){
        return imageView;
    }
    public ImageView getImageView2(){
        return imageView2;
    }


    private Point getViewSize(View view){
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        Point point = new Point();
        point.x = view.getMeasuredWidth();
        point.y = view.getMeasuredHeight();
        return point;
    }
    private void storageViewSize(View view,Point size){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if(params != null){
            params.width = size.x;
            params.height = size.y;
            view.setLayoutParams(params);
        }
    }
}
