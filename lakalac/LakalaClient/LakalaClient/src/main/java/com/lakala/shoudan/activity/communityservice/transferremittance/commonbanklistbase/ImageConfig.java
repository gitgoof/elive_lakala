package com.lakala.shoudan.activity.communityservice.transferremittance.commonbanklistbase;

import android.graphics.Bitmap;

/**
 * Created by HUASHO on 2015/1/29.
 * 使用时必须配置config 1.设置默认bitmap
 */
public class ImageConfig {
    public Bitmap defaultBitmap;//默认图片

    public String sdCachepath;//sd缓存路径

    public String getSdCachepath() {
        return sdCachepath;
    }

    public void setSdCachepath(String sdCachepath) {
        this.sdCachepath = sdCachepath;
    }

    public Bitmap getDefaultBitmap() {
        return defaultBitmap;
    }

    public void setDefaultBitmap(Bitmap defaultBitmap) {
        this.defaultBitmap = defaultBitmap;
    }
}
