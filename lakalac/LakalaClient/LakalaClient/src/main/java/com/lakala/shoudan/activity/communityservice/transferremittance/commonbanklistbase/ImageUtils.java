package com.lakala.shoudan.activity.communityservice.transferremittance.commonbanklistbase;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.ImageView;

import com.lakala.shoudan.common.Parameters;

import java.io.File;

/**
 * Created by HUASHO on 2015/1/29.
 * 图片工具类
 */
public class ImageUtils {
    private static ImageUtils mImageUtil;

    private ImageConfig mImageConfig;//下载参数配置

    private Context context;

    private ImageUtils(Context context) {
        this.context = context;
    }

    public static ImageUtils getInstance(Context context) {
        if (null == mImageUtil) {
            mImageUtil = new ImageUtils(context);
        }
        return mImageUtil;
    }

    public void initImageConfig(Bitmap defaultBitmap,String sdCacheDir){
        setDefalutBitmap(defaultBitmap);//设置默认图片
        initSdCacheDir(sdCacheDir);//设置sd卡缓存路径
    }

    public void initSdCacheDir(String sdCacheDir){
        if(!TextUtils.isEmpty(sdCacheDir)){//设置sd卡缓存路径
            File file = getDiskFile(context, sdCacheDir);
            if(!file.exists()){
                file.mkdirs();
            }
        }
    }

    public void setImageConfigSdCacheDir(String sdCacheDir){
        this.mImageConfig.setSdCachepath(sdCacheDir);
        initSdCacheDir(sdCacheDir);
    }


    public void setImageConfig(ImageConfig mImageConfig) {
        this.mImageConfig = mImageConfig;
    }

    public void setDefalutBitmap(Bitmap defaultBitmap) {
        this.mImageConfig.defaultBitmap = defaultBitmap;
    }

    // 加载图片
    public void loadBitmap(String url, ImageView mImageView) {
        String key = url;
        if(key == null){
            key = "";
        }
        Bitmap bitmap = BitmapManager.getInstance().getBitmapFromCache(key);
        if (null != bitmap) {
            mImageView.setImageBitmap(bitmap);
        } else if (cancelPotentialWork(url, mImageView)) {// 加载之前判断这个imageView是否已经在加载，如果是已经在加载且加载的时同一张图片，则不再加载

            AsynTaskBitmap asynTaskBitmap = new AsynTaskBitmap(mImageView,context);
            AsynTaskBitmap.AsyncDrawable asyncDrawable = new AsynTaskBitmap.AsyncDrawable(
                    mImageConfig.defaultBitmap,// 默认的图片,用来占位置
                    asynTaskBitmap);
            mImageView.setImageDrawable(asyncDrawable);
            asynTaskBitmap.execute(url);
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap.createBitmap(

                drawable.getIntrinsicWidth(),

                drawable.getIntrinsicHeight(),

                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        //canvas.setBitmap(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        drawable.draw(canvas);

        return bitmap;

    }

    /**
     * 是否是加载新的图片
     *
     * @param data
     * @param mImageView
     * @return
     */
    public boolean cancelPotentialWork(String data, ImageView mImageView) {
        AsynTaskBitmap mAsynTaskBitmap = getAsynTaskBitmap(mImageView);
        if (null != mAsynTaskBitmap) {
            String bitData = mAsynTaskBitmap.data;
            if (!TextUtils.isEmpty(bitData) && !bitData.equals(data)) {
                mAsynTaskBitmap.cancel(true);// 取消旧图下载
            } else {
                return false;// 加载的原来的一样，则不变
            }
        }
        return true;
    }

    // 获取iamgeView中原有的加载图片asynTastbitmap
    public static AsynTaskBitmap getAsynTaskBitmap(ImageView imageView) {
        if (null != imageView) {
            Drawable drawable = imageView.getDrawable();
            if (null != drawable && drawable instanceof  AsynTaskBitmap.AsyncDrawable) {
                AsynTaskBitmap.AsyncDrawable mAsyncDrawable = ( AsynTaskBitmap.AsyncDrawable) drawable;
                return mAsyncDrawable.getAsynTaskBitmap();
            }
        }
        return null;
    }

    /**
     * 压缩一次图片
     *
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res,
                                                         int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 只读取bitmap宽高等信息
        BitmapFactory.decodeResource(res, resId, options);

        /**
         * 计算压缩比例
         */
        // options.inSampleSize = caculateInSampleSize(options, reqWidth,reqHeight);
        options.inSampleSize = caculateInSampleSize(options, BITMAP_SIZE,
                BITMAP_SIZE);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * xUtil
     *
     * @param options
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int maxWidth, int maxHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (width > maxWidth || height > maxHeight) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) maxHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) maxWidth);
            }

            final float totalPixels = width * height;

            final float maxTotalPixels = maxWidth * maxHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > maxTotalPixels) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private static final int BITMAP_SIZE = 600;

    /**
     *
     * @param bitmap
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width < BITMAP_SIZE && height < BITMAP_SIZE) {
            return bitmap;
        } else {
            double scal;
            if (width > height) {
                scal = (double) width / BITMAP_SIZE;
                return Bitmap.createScaledBitmap(bitmap, BITMAP_SIZE,
                        (int) (height / scal), true);
            } else {
                scal = (double) height / BITMAP_SIZE;
                return Bitmap.createScaledBitmap(bitmap, (int) (width / scal),
                        BITMAP_SIZE, true);
            }
        }
    }

    /**
     * android 官网
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int caculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        int width = options.outWidth;// 图片宽高
        int height = options.outHeight;

        int inSampleSize = 1;

        if (width > BITMAP_SIZE || height > BITMAP_SIZE) {// 长宽等比缩放
            int halfWidth = width / 2;
            int halfHeight = height / 2;

            while (halfWidth / inSampleSize > reqWidth
                    || halfHeight / inSampleSize > reqHeight) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static File getDiskFile(Context context,String uniqueName){
        String cacheDir = null;
        //获取外部存储器的状态，当为可读写时启用SD卡缓存。
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            //取SD卡路径
            cacheDir = Environment.getExternalStorageDirectory().getAbsolutePath() + Parameters.imageCachePath;
        }
        else
        {
            //外部存储器不可用，使用内部存储器
            cacheDir = context.getFilesDir().getAbsolutePath() + Parameters.imageCachePath;
        }

        if (cacheDir != null)
        {
            File dir = new File(cacheDir);

            //如果缓存路径不存在，则创建它。
            if (!dir.exists())
            {
                dir.mkdirs();
            }
            else if (!dir.isDirectory())
            {
                dir.delete();
                dir.mkdirs();
            }
        }
        return new File(cacheDir+File.separator+uniqueName);
    }

    @TargetApi(VERSION_CODES.FROYO)
    private static File getDiskPath(Context context){
        if(Build.VERSION.SDK_INT>=VERSION_CODES.FROYO){
            return context.getExternalCacheDir();
        }
        final String path = "/Android/data/"+context.getPackageName()+"/cache/";
        return new File(Environment.getDownloadCacheDirectory().getPath()+path);
    }

    public static boolean isExternalStorateMounted(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
