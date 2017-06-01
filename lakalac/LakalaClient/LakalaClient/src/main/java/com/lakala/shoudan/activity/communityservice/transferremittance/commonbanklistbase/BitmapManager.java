package com.lakala.shoudan.activity.communityservice.transferremittance.commonbanklistbase;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by HUASHO on 2015/1/29.
 * 图片管理
 */
public class BitmapManager {
    private static BitmapManager bitmapManager;
    private LruCache<String, Bitmap> lruCache;

    public BitmapManager(){
        init();
    }

    public static BitmapManager getInstance(){
        if(bitmapManager == null){
            bitmapManager = new BitmapManager();
        }
        return bitmapManager;
    }

    public void init(){
        int maxMenory = (int) (Runtime.getRuntime().maxMemory()/1024);

        int cacheSize = maxMenory/8;

        lruCache = new LruCache<String,Bitmap>(cacheSize){

            @Override
            protected void entryRemoved(boolean evicted, String key,
                                        Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
            }

            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes()*value.getHeight()/1024;
            }
        };
    }

    public void addBitmapToCache(String key,Bitmap bitmap){
        synchronized (lruCache) {

            if(null == getBitmapFromCache(key)){
                lruCache.put(key, bitmap);
            }
        }
    }

    public Bitmap getBitmapFromCache(String key){
        return lruCache.get(key);
    }
}
