package com.lakala.platform.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.net.HttpRequestParams;
import com.lakala.platform.http.BusinessRequest;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;

import java.io.File;
import java.io.InputStream;

/**
 * Created by LMQ on 2015/6/4.
 */
public class UILUtils {
    private static final int DEFAULT_DISK_CACHE_SIZE = 50 * 1024 * 1024;//50 MIB
    private static final int DEFAULT_MEMORY_CACHE_SIZE = 5 * 1024 * 1024;//5 MIB

    public static void init(Context context){
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        DisplayImageOptions defaultDisplayImageOptions = getDefaultOptionsBuilder().build();
        config.defaultDisplayImageOptions(defaultDisplayImageOptions);
        config.diskCacheSize(DEFAULT_DISK_CACHE_SIZE);
        config.memoryCacheSize(DEFAULT_MEMORY_CACHE_SIZE);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    /**
     * uri<br/>
     * "http://site.com/image.png" // from Webicon<br/>
     * "file:///mnt/sdcard/image.png" // from SD cardicon<br/>
     * "file:///mnt/sdcard/video.mp4" // from SD card (video thumbnail)icon<br/>
     * "content://media/external/images/media/13" // from content providericon<br/>
     * "content://media/external/video/media/13" // from content provider (video thumbnail)icon<br/>
     * "assets://image.png" // from assetsicon<br/>
     * "drawable://" + R.drawable.img // from drawables (non-9patch images)icon<br/>
     */
    public static void display(String uri, ImageView imageView){
        ImageLoader.getInstance().displayImage(uri,imageView);
    }
    public static void display(String uri,ImageView imageView,boolean cacheInMemory){
        display(uri, imageView, getDefaultOptionsBuilder().cacheInMemory(cacheInMemory).build());
    }
    public static void display(String uri,ImageView imageView,DisplayImageOptions options){
        if(options == null){
            options = getDefaultOptionsBuilder().build();
        }
        ImageLoader.getInstance().displayImage(uri, imageView, options);
    }
    public static void displayWithoutCache(String uri, ImageView imageView){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .build();
        display(uri, imageView, options, true);
    }
    public static void displayBackground(String uri,ImageView imageView){
        display(uri, imageView,getDefaultOptionsBuilder().build(),true);
    }

    public static void display(String uri,ImageView imageView,DisplayImageOptions options,boolean
            showInBackground){
        CommonViewAware aware = new CommonViewAware(imageView);
        aware.setShowInBackground(showInBackground);
        ImageLoader.getInstance().displayImage(uri,aware,options);
    }
    public static void load(String uri){
        load(uri, null);
    }
    public static void load(String uri,ImageLoadingListener listener){
        load(uri, listener, null);
    }

    public static void load(String uri, ImageSize targetImageSize, ImageLoadingListener listener) {
        ImageLoader.getInstance().loadImage(
                uri, targetImageSize, getDefaultOptionsBuilder().build(), listener
        );
    }
    public static void loadWithError(String uri, final ImageView iv) {//功夫贷加载logo失败后加载默认图片
        ImageLoader.getInstance().displayImage(uri, iv, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                iv.setImageResource(com.lakala.ui.R.drawable.gfd_logo);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                iv.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }
    public static void load(String uri,ImageLoadingListener listener,DisplayImageOptions options){
        if(options == null){
            options = getDefaultOptionsBuilder().build();
        }
        ImageLoader.getInstance().loadImage(uri, options, listener);
    }
    public static Bitmap loadSync(String uri){
        return ImageLoader.getInstance().loadImageSync(uri);
    }
    public static void getIdcardImg(String idCardImageName,final ImageView imageView){

        BusinessRequest request = BusinessRequest.obtainRequest(idCardImageName, HttpRequest.RequestMethod.GET);
        HttpRequestParams params = request.getRequestParams();
        params.put("idCardImageName",idCardImageName);
//        params.add("userToken",userToken);
        request.setRequestURL(BusinessRequest.DEFAULT_REQUEST_VERSION + "/" + "getUserIdCardImage");
        request.setResponseHandler(new InputStreamResponseHandler() {
            @Override
            public void onSuccess(InputStream inputStream) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if(imageView != null){
                    imageView.setImageBitmap(bitmap);
                }
            }
        });
        request.execute();
    }
    private static DisplayImageOptions.Builder getDefaultOptionsBuilder(){
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
                .cacheOnDisk(true);
        return builder;
    }

    public static File findInCache(String imageUri){
        return DiskCacheUtils.findInCache(imageUri, ImageLoader.getInstance().getDiskCache());

    }

}
