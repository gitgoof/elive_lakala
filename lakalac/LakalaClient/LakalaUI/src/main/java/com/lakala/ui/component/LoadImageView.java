package com.lakala.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.lakala.ui.R;

/**
 * Created by wangchao on 14-2-10.
 * 图片加载
 * 默认为菊花,当且仅当设置ImageView时,显示图片
 */
public class LoadImageView extends LinearLayout {

    private ProgressBar ui_id_progressBar;
    private ImageView ui_id_imageView;

    /**
     * 图片宽度
     */
    private float imageWidth;
    /**
     * 图片高度
     */
    private float imageHeight;

    public LoadImageView(Context context) {
        this(context, null);
    }

    public LoadImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadImageView);
            try {
                imageWidth = typedArray.getDimension(R.styleable.LoadImageView_imageWidth, R.dimen.dimen_64);
                imageHeight = typedArray.getDimension(R.styleable.LoadImageView_imageHeight, R.dimen.dimen_64);
            } finally {
                typedArray.recycle();
            }
        }
        LayoutInflater.from(context).inflate(R.layout.ui_load_imageview, this, true);
        ui_id_imageView = (ImageView) findViewById(R.id.ui_id_imageView);
        ui_id_progressBar = (ProgressBar) findViewById(R.id.ui_id_progressBar);

    }

    /**
     * 设置imageView
     *
     * @param id 资源id
     */
    public void setImageView(int id) {
        ui_id_imageView.setBackgroundResource(id);
        ui_id_imageView.setVisibility(View.VISIBLE);
        ui_id_progressBar.setVisibility(View.GONE);
    }

    /**
     * 设置imageView
     *
     * @param drawable
     */
    public void setImageView(Drawable drawable) {
        ui_id_imageView.setBackgroundDrawable(drawable);
        ui_id_imageView.setVisibility(View.VISIBLE);
        ui_id_progressBar.setVisibility(View.GONE);
    }

}
