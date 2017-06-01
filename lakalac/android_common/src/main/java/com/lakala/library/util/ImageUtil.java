package com.lakala.library.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StatFs;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Vinchaos api on 13-12-12.
 * 图片处理工具类
 */
public class ImageUtil {

    public static int densityDpi = 240;
    /**
     * 图片缩放
     * resizeImage
     *
     * @param bitmap
     * @param w
     * @param h
     * @return
     */
    public static Drawable resizeImage(Context context, Bitmap bitmap, int w, int h) {

        if (context == null){
            return null;
        }

        if(bitmap == null){
            return null;
        }

        Bitmap bitmapOrg = bitmap;

        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap reSizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width,
                height, matrix, true);
        return new BitmapDrawable(context.getResources(), reSizedBitmap);

    }

    /**
     * 图片格式转换
     * drawable2Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if(drawable == null){
            return null;
        }

        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Bitmap2Drawable
     */
    public static Drawable bitmap2Drawable(Context context, Bitmap bitmap) {
        if (context == null){
            return null;
        }
        if(bitmap == null)return null;
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    /**
     * Bitmap2Bytes
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap, int quality) {
        if(bitmap == null)return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, stream);
        return stream.toByteArray();
    }

    /**
     * Bitmap2Bytes
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        if(bitmap == null)return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * Bytes2Bimap
     *
     * @param b
     * @return
     */
    public static Bitmap bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * View2Bitmap
     *
     * @param view
     * @return
     */
    public static Bitmap view2Bitmap(View view) {
        if(view == null)return null;
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    /**
     * Bitmap2file
     *
     * @param bmp
     * @param fileName
     * @return
     */
    public static boolean saveBitmap2file(Bitmap bmp, String fileName) throws Exception {
        if(bmp == null || StringUtil.isEmpty(fileName))return false;
        Bitmap.CompressFormat format = Bitmap.CompressFormat.PNG;
        int quality = 100;
        // 判断SDCard状态
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            // 错误提示
            LogUtil.e("LKL", "SDCard不可用");
            return false;
        }
        // 检查SDCard空间
        File SDCardRoot = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(SDCardRoot.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = statFs.getBlockSize();
        //空闲的数据块的数量
        long freeBlocks = statFs.getAvailableBlocks();
        if (blockSize * freeBlocks < 10000) {
            // 弹出对话框提示用户空间不够
            LogUtil.e("LKL", "存储空间不够");
            return false;
        }
        // 在SDCard创建文件夹及文件
        File bitmapFile = new File(SDCardRoot.getPath() + fileName);
        bitmapFile.getParentFile().mkdirs();
        OutputStream stream = new FileOutputStream(SDCardRoot.getPath() + fileName);
        return bmp.compress(format, quality, stream);
    }

    /**
     * printScreen
     *
     * @return
     */
    public static Bitmap printScreen(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        View view = activity.getWindow().getDecorView();
        view.layout(0, 0, dm.widthPixels, dm.heightPixels);
        view.setDrawingCacheEnabled(true);
        return Bitmap.createBitmap(view.getDrawingCache());
    }

    /**
     * bitmap2String
     *
     * @param bitmap
     * @return
     */
    public static String bitmap2String(Bitmap bitmap) {
        if(bitmap == null) return "";
        String string;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

    /**
     * string2Bitmap
     *
     * @param string
     * @return
     */
    public static Bitmap string2Bitmap(String string) throws Exception {
        if(StringUtil.isEmpty(string)) return null;
        byte[] bitmapArray = Base64.decode(string, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
    }


    /**
     *。保存图片到私有文件夹
     * @param bitmap
     * @return
     */
    public static boolean saveLoacalPhotoImage(Context mContext,String loginName,Bitmap bitmap){
        if(mContext == null || StringUtil.isEmpty(loginName) || bitmap == null) return false;
        try{
            String fileName = loginName.hashCode()+".png";
            FileOutputStream localFileOutputStream1 = mContext.openFileOutput(fileName, 0);
            Bitmap.CompressFormat localCompressFormat = Bitmap.CompressFormat.PNG;
            bitmap.compress(localCompressFormat, 100, localFileOutputStream1);
            localFileOutputStream1.close();
        }catch (Exception e){
            return  false;
        }
        return  true;
    }

    /**
     * 获取私有文件图片文件
     *
     * @param context
     * @return
     */
    public static Bitmap getLoacalPhotoImage(Context context,String loginName) {
        if(context == null || StringUtil.isEmpty(loginName)) return null;
        FileInputStream fis = null;
        Bitmap bitmap = null;
        String fileName = loginName.hashCode()+".png";
        try {
            fis = context.openFileInput(fileName);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return bitmap;
    }

    /**
     * 获取Assets目录下存放的图片
     * @param context   上下文
     * @param path      文件路径
     * @return          Drawable对象
     */
    public static Bitmap getBitmapInAssets(Context context, String path){
        if(context == null || StringUtil.isEmpty(path)) return null;
        AssetManager assetManager   = context.getAssets();
        Bitmap bitmap               = null;
        InputStream in              = null;
        try {
            in      = assetManager.open(path);
            bitmap  = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtil.closeIO(in);
        }
        return bitmap;
    }

    /**
     *  从文件中加载图片,1/scale缩放
     * @param path      文件路径
     * @return          Bitmap 对象
     */
    public static Bitmap getBitmapByFile(String path, int scale){
        if(StringUtil.isEmpty(path)) return null;
        Bitmap bitmap = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = scale;
            bitmap = BitmapFactory.decodeStream(in, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            FileUtil.closeIO(in);
        }
        return bitmap;
    }

    /**
     *  从文件中加载图片
     * @param path      文件路径
     * @return          Bitmap 对象
     */
    public static Bitmap getBitmapByFile(String path){
        if(StringUtil.isEmpty(path)) return null;
        Bitmap bitmap = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(path);
            bitmap = BitmapFactory.decodeStream(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            FileUtil.closeIO(in);
        }
        return bitmap;
    }
    /**
     * Bitmap 转为 Drawable
     *
     * @param bitmap
     *            Bitmap类型
     * @return Drawable Drawable类型
     */
    public static Drawable BitmapToDrawable(Bitmap bitmap) {
        if (bitmap == null)
            return null;
        BitmapDrawable drawable = null;
        try {
            drawable = (BitmapDrawable) new BitmapDrawable(bitmap);
            drawable.setTargetDensity(densityDpi);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawable;
    }

    /**
     * 根据放在Drawable目录下的图片名字获取图片
     * @param context     上下文
     * @param identifier  文件名字
     * @return   Drawable对象
     */
    public static Bitmap getBitmapByIdentifier(Context context, String identifier){
        if(context == null || StringUtil.isEmpty(identifier)) return null;
        return BitmapFactory.decodeResource(context.getResources(),getDrawableIdentifier(context,identifier));
    }

    /**
     * 根据文件名字获取 R.drawable.identfier 的int值
     * @param context    上下文
     * @param identifier  文件名字
     * @return     Res   int
     */
    public static int getDrawableIdentifier(Context context,String identifier){
        if(context == null || StringUtil.isEmpty(identifier)) return 0;
        Resources resources    = context.getResources();
        String packageName     = context.getPackageName();
        return resources.getIdentifier(identifier, "drawable", packageName);
    }

    /**
     * 转换图片成圆形
     * @param source 传入Bitmap对象
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap source) {
        if (source == null)
            return null;

        int width   = source.getWidth();
        int height  = source.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, src, dst, paint);
        return output;
    }


    /**
     *
     *
     * 收单
     *
     *
     */
    /**
     * 获取图片字节流  生成宽为480 高为800 大小为300k 的图片
     *
     * @param file 文件
     * @return 字节流
     */
    public static byte[] getImgByte(File file, Context context) {
        ByteArrayOutputStream out = getThumbnailByte(
                file.getAbsolutePath(),
                600,
                800,
                300);
        byte[] b = out.toByteArray();
        //关闭流
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
            }
        }

        return b;
    }

    /**
     * 获取文件缩略图
     *
     * @param srcPath 文件路径
     * @param width   压缩图片宽度
     * @param height  压缩图片高度
     * @param size    质量（KB）
     */
    public static ByteArrayOutputStream getThumbnailByte(String srcPath, float width, float height, long size) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = height;//这里设置高度为800f
        float ww = width;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        ByteArrayOutputStream  byteArrayOutputStream= compressImage(bitmap,size);
        if(!bitmap.isRecycled())
            bitmap.recycle();
        return byteArrayOutputStream;//压缩好比例大小后再进行质量压缩
    }

    /**
     * 按质量压缩图片
     *
     * @param size 质量（KB）
     */
    public static ByteArrayOutputStream compressImage(Bitmap image, long size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > size && options > 0) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 5;//每次都减少10
        }
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return baos;
    }

    public static File createPhotoFile(Context context, String fileName) {
        String cacheDirPath = "";
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            cacheDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    File.separator +
                    "lakala" +
                    File.separator +
                    "img";
        } else {
            cacheDirPath = context.getCacheDir().getAbsolutePath() +
                    File.separator +
                    "lakala" +
                    File.separator +
                    "img";
        }
        File dir = new File(cacheDirPath);
        if (!dir.exists()) dir.mkdirs();
        //localTempImgDir和localTempImageFileName是自己定义的名字
        return new File(dir, fileName);
    }

}
