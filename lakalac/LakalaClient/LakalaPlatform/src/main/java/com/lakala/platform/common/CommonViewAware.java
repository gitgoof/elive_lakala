package com.lakala.platform.common;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * Created by LMQ on 2015/11/4.
 */
public class CommonViewAware extends ImageViewAware {

    public boolean showInBackground = false;

    public boolean isShowInBackground() {
        return showInBackground;
    }

    public CommonViewAware setShowInBackground(boolean showInBackground) {
        this.showInBackground = showInBackground;
        return this;
    }

    public CommonViewAware(ImageView imageView) {
        super(imageView);
    }

    public CommonViewAware(ImageView imageView, boolean checkActualViewSize) {
        super(imageView, checkActualViewSize);
    }

    @Override
    protected void setImageDrawableInto(Drawable drawable, View view) {
        if(!showInBackground){
            super.setImageDrawableInto(drawable,view);
        }else{
            view.setBackgroundDrawable(drawable);
        }
    }

    @Override
    protected void setImageBitmapInto(Bitmap bitmap, View view) {
        if(!showInBackground){
            super.setImageBitmapInto(bitmap, view);
        }else{
            view.setBackgroundDrawable(new BitmapDrawable(bitmap));
        }
    }
}
