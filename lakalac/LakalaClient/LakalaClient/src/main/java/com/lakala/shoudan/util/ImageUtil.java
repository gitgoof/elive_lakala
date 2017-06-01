package com.lakala.shoudan.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lakala.library.util.LogUtil;
import com.lakala.shoudan.BuildConfig;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.util.BitmapUtil;
import com.lakala.shoudan.common.util.IOUtil;
import com.lakala.shoudan.common.util.Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 图片处理工具类
 *
 * @author xyz
 */
public class ImageUtil {

    /**
     * 图片合成方向 左
     */
    public static final int LEFT = 0;
    /**
     * 图片合成方向 右
     */
    public static final int RIGHT = 1;
    /**
     * 图片合成方向 上
     */
    public static final int TOP = 3;
    /**
     * 图片合成方向 底
     */
    public static final int BOTTOM = 4;
    private static final String BASE_DATA_PATH = "data/data/" + BuildConfig.APPLICATION_ID + "/";

    /**
     * 通过图片的url,返回bitmap
     *
     * @param url 图片地址
     * @return 图片，Bitmap格式
     * @throws IOException IO异常
     */
    public static Bitmap getBitmapWithUrl(String url) throws IOException {
        URL fileUrl = null;
        Bitmap bitMap = null;
        if (url == null) {
            return null;
        }
        url = url.replaceAll(" ", "%20");
        try {
            fileUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) fileUrl.openConnection();
            // conn.setReadTimeout(1000); //设置连接超时1秒
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitMap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            LogUtil.print(e);
        }

        return bitMap;
    }

    /**
     * 通过图片的url,返回bitmap
     *
     * @param url  图片地址
     * @param path "data/data/"+ApplicationExtension.getInstance().getPackageName()+"/"目录下子目录名称
     * @param name 图片名称
     * @throws IOException IO异常
     */
    public static void getImageWithUrlAndSave(String url, String path, String name)
            throws IOException {
        URL fileUrl = null;
        Bitmap bitMap = null;
        if (url == null) {
            return;
        }
        url = url.replaceAll(" ", "%20");
        try {
            fileUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) fileUrl.openConnection();
            conn.setReadTimeout(10000); //设置连接超时10秒
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitMap = BitmapFactory.decodeStream(is);
            File rootFile = new File(path);
            if (!rootFile.exists() && !rootFile.isDirectory())
                rootFile.mkdirs();
            File file = new File(rootFile, name);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(file);
            if (bitMap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
            if (bitMap != null) {
                bitMap.recycle();
            }
            is.close();
        } catch (Exception e) {
            LogUtil.print(e);
        }
    }


    /**
     * 通过图片的url,返回drawable
     *
     * @param url 图片的url字符串
     * @return 返回图片的Drawable
     * @throws IOException IO异常
     * @see #getBitmapWithUrl(String)
     */
    public static Drawable getDrawableWithUrl(String url) throws IOException {
        Drawable drawable = null;
        try {
            Bitmap bitmap = getBitmapWithUrl(url);
            if (bitmap != null) {
                drawable = new BitmapDrawable(bitmap);
            }
        } catch (Exception e) {
            LogUtil.print(e);
        }
        return drawable;
    }

    /**
     * Drawable 转为 Bitmap
     *
     * @param drawable Drawable类型
     * @return Bitmap Bitmap类型
     */
    public static Bitmap DrawableToBitmap(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        return bitmap;
    }

    /**
     * Bitmap 转为 Drawable
     *
     * @param bitmap Bitmap类型
     * @return Drawable Drawable类型
     */
    public static Drawable BitmapToDrawable(Bitmap bitmap) {
        if (bitmap == null)
            return null;

        BitmapDrawable drawable = null;
        try {
            drawable = (BitmapDrawable) new BitmapDrawable(bitmap);
            drawable.setTargetDensity(240);
        } catch (Exception e) {
            LogUtil.print(e);
        }
        return drawable;
    }

    /** */
    /**
     * 把图片变成圆角
     *
     * @param bitmap 需要修改的图片
     * @param pixels 圆角的弧度
     * @return 圆角图片
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(
                bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888
        );
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /** */
    /**
     * 使圆角功能支持BitampDrawable
     *
     * @param bitmapDrawable BitampDrawable格式
     * @param pixels         圆角的弧度
     * @return 圆角图片
     * @see # toRoundCorner(Bitmap, int)
     */
    public static BitmapDrawable toRoundCorner(BitmapDrawable bitmapDrawable, int pixels) {
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bitmapDrawable = new BitmapDrawable(toRoundCorner(bitmap, pixels));
        return bitmapDrawable;
    }

    /**
     * 读取路径中的图片，然后将其转化为缩放后的bitmap
     *
     * @param path 图片路径
     */
    public static void saveBefore(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回bm为空
        options.inJustDecodeBounds = false;
        // 计算缩放比
        int be = (int) (options.outHeight / (float) 200);
        if (be <= 0)
            be = 1;
        options.inSampleSize = 2; // 图片长宽各缩小二分之一
        // 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
        bitmap = BitmapFactory.decodeFile(path, options);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
//		System.out.println(w + " " + h);
        // savePNG_After(bitmap,path);
        saveJPGE_After(bitmap, path);
    }

    /**
     * 保存图片为PNG  保存目录sdcard/lakala/adX.png
     *
     * @param bitmap 图片
     * @param name   图片名
     */
    public static void savePNG_After(Bitmap bitmap, String name) {
        File upgradeFilePath = Environment.getExternalStorageDirectory();
        File rootFile = new File(upgradeFilePath, "/lakala/adImage");
        if (!rootFile.exists() && !rootFile.isDirectory())
            rootFile.mkdirs();
        File file = new File(rootFile, name);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存图为png格式  存储在在内存应用数据包内
     *
     * @param bitmap 图片
     * @param name   图片名
     * @param name
     * @throws Exception
     * @context 上下文
     */
    public static void savePNG_After(Context context, Bitmap bitmap, String name) throws Exception {
        try {
            FileOutputStream out = context.openFileOutput(
                    name, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
            );
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
            System.gc();
        } catch (Exception e) {
            throw e;
        }
    }


    public static Bitmap readPNG_After(Context context, String name) {
        Bitmap bitmap = null;
        try {
            FileInputStream finputStream = context.openFileInput(name);
            bitmap = getBitmap(finputStream);
            System.gc();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 保存图片为JPEG
     *
     * @param bitmap 图片
     * @param path   图片名或路径
     */
    public static void saveJPGE_After(Bitmap bitmap, String path) {
        File file = new File(path);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 水印
     *
     * @param src       图片
     * @param watermark 水印图片
     * @return 加过水印效果的图片
     */
    public static Bitmap createBitmapForWatermark(Bitmap src, Bitmap watermark) {
        if (src == null) {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        int ww = watermark.getWidth();
        int wh = watermark.getHeight();
        // create the new blank bitmap
        Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        // draw src into
        cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
        // draw watermark into
        cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, null);// 在src的右下角画入水印
        // save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        // store
        cv.restore();// 存储
        return newb;
    }

    /**
     * 生产带倒影的图片
     *
     * @param bitmap 合成带倒影的图片
     * @return
     */
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
        final int reflectionGap = 4;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(
                bitmap, 0, height / 2, width, height / 2, matrix, false
        );

        Bitmap bitmapWithReflection = Bitmap.createBitmap(
                width, (height + height / 2), Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(
                0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap,
                0x70ffffff, 0x00ffffff, TileMode.CLAMP
        );
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);

        return bitmapWithReflection;
    }

    /**
     * 生产倒影图片
     *
     * @param bitmap 倒影图片
     * @return
     */
    public static Bitmap createReflectionImg(Bitmap bitmap) {
        final int reflectionGap = 4;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        /**
         * 处理翻转
         * */
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionImage = Bitmap.createBitmap(
                bitmap, 0, height / 2, width, height / 2, matrix, false
        );
        //绘制边框bitmap
        Bitmap bitmapWithReflection = Bitmap.createBitmap(
                width, height + reflectionGap, Config.ARGB_8888
        );
        //画布
        Canvas canvas = new Canvas(bitmapWithReflection);
//         canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();

        canvas.drawRect(0, 0, width, reflectionGap, deafalutPaint);
        //起始坐标
        canvas.drawBitmap(reflectionImage, 0, reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(
                0, 0, 0, height / 2 + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP
        );
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, 0, width, height / 2 + reflectionGap, paint);

        return bitmapWithReflection;
    }

    /**
     * 图片合成 int 合成方向 Bitmap... 要合成的图片们
     *
     * @return 合成后的图片
     * @see #createBitmapForFotoMix(Bitmap, Bitmap, int)
     */
    public static Bitmap potoMix(int direction, Bitmap... bitmaps) {
        if (bitmaps.length <= 0) {
            return null;
        }
        if (bitmaps.length == 1) {
            return bitmaps[0];
        }
        Bitmap newBitmap = bitmaps[0];
        // newBitmap = createBitmapForFotoMix(bitmaps[0],bitmaps[1],direction);
        for (int i = 1; i < bitmaps.length; i++) {
            newBitmap = createBitmapForFotoMix(newBitmap, bitmaps[i], direction);
        }
        return newBitmap;
    }

    /**
     * 合成图片
     *
     * @param first     第一张图片
     * @param second    第二张图片
     * @param direction 合成方向
     * @return 返回合成后的图片
     */
    private static Bitmap createBitmapForFotoMix(Bitmap first, Bitmap second, int direction) {
        if (first == null) {
            return null;
        }
        if (second == null) {
            return first;
        }
        int fw = first.getWidth();
        int fh = first.getHeight();
        int sw = second.getWidth();
        int sh = second.getHeight();
        Bitmap newBitmap = null;
        if (direction == LEFT) {
            newBitmap = Bitmap.createBitmap(
                    fw + sw, fh > sh ? fh : sh, Config.ARGB_8888
            );
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawBitmap(first, sw, 0, null);
            canvas.drawBitmap(second, 0, 0, null);
        } else if (direction == RIGHT) {
            newBitmap = Bitmap.createBitmap(
                    fw + sw, fh > sh ? fh : sh, Config.ARGB_8888
            );
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawBitmap(first, 0, 0, null);
            canvas.drawBitmap(second, fw, 0, null);
        } else if (direction == TOP) {
            newBitmap = Bitmap.createBitmap(
                    sw > fw ? sw : fw, fh + sh, Config.ARGB_8888
            );
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawBitmap(first, 0, sh, null);
            canvas.drawBitmap(second, 0, 0, null);
        } else if (direction == BOTTOM) {
            newBitmap = Bitmap.createBitmap(
                    sw > fw ? sw : fw, fh + sh, Config.ARGB_8888
            );
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawBitmap(first, 0, 0, null);
            canvas.drawBitmap(second, 0, fh, null);
        }
        return newBitmap;
    }

    /**
     * 将Bitmap转换成指定大小
     *
     * @param bitmap 图片
     * @param width  宽度
     * @param height 高度
     * @return 转换后的图片
     */
    public static Bitmap createBitmapBySize(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    /**
     * 将二进制流转换成图片
     *
     * @param temp 图片二进制流
     * @return 图片
     */
    public static Bitmap getBitmapFromByte(byte[] temp) {
        if (temp != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * 将流转换成图片
     *
     * @param inputStream 图片流
     * @return 图片
     */
    public static Bitmap getBitmap(InputStream inputStream) {
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeStream(inputStream);
        return bitmap;
    }

    public static Bitmap decodeBitmapFromStream(byte[] bytes, int width, int height)
            throws Exception {

        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        options.inSampleSize = BitmapUtil.calculateInSampleSize(options, (width), (height));
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        return bitmap;
    }


    /**
     * 图片缩放
     *
     * @param inputStream
     * @param size        缩放比例 1/size
     * @return
     */
    public static Bitmap decodeBitmapFromStream(InputStream inputStream, int size) {

        Bitmap bitmap = null;
        final BitmapFactory.Options options = new BitmapFactory.Options();
//	    options.inJustDecodeBounds = true;
//	    BitmapFactory.decodeStream(inputStream, null, options);
        options.inSampleSize = size;
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        return bitmap;
    }

    //上传图片压缩至300k以下
    public static byte[] decocdeBitmapFromStream(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 80;//个人喜欢从80开始,
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length / 1024 > 300) {
            baos.reset();
            options -= 10;
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        return baos.toByteArray();
    }


    /**
     * 通过啊assets目录中的图片名称，获取Drawable对象
     *
     * @param imageFullName
     * @return
     */
    public static Drawable getDrawbleFromAssets(Context context,String imageFullName) {
        Drawable drawable = null;
        try {
            InputStream inputStream = context.getAssets().open("bank_icon/"+imageFullName);
            Bitmap bitmap = getBitmap(inputStream);
            drawable = BitmapToDrawable(bitmap);
        } catch (IOException e) {
            LogUtil.print(e);
        }
        return drawable;
    }

    static int MB = 1024;
    static int FREE_SD_SPACE_NEEDED_TO_CACHE = 10;
    static IOUtil ioUtil;
    public static String cacheRoot = "/mnt/sdcard/lakala/cache/";

    static {
        ioUtil = new IOUtil();
    }

    public static Bitmap getBitmapFromSd(String url) {
        File file = new File(cacheRoot + url);
        Bitmap bitmap = null;
        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(cacheRoot + url);
            LogUtil.e("LAKALA", "Get Bitmap from Sdcard  " + (bitmap == null));
        }
        return bitmap;
    }

    public static boolean saveBmpToSd(Bitmap bm, String url) {
        if (bm == null) {
            return false;
        }

        if (!ioUtil.isSdCardAvailable()) {
            return false;
        }

        File root = new File(cacheRoot);
        if (!root.exists()) {
            root.mkdirs();
        }
        String filename = URLEncoder.encode(url);
        File file = new File(root + "/" + filename);
        if (file.exists()) {
            LogUtil.e("LAKALA", "existsed");
            if (!file.delete())//删除失败
                return false;
        }
        // 判断sdcard上的空间
        if (FREE_SD_SPACE_NEEDED_TO_CACHE > ioUtil.getFreeMemory()) {
            LogUtil.e("LAKALA", "Low free space onsd, do not cache");
            return false;
        }

        try {
            file.createNewFile();
            OutputStream outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            LogUtil.print("LAKALA", "Image saved tosd");
            return true;
        } catch (FileNotFoundException e) {
            LogUtil.e("LAKALA", "FileNotFoundException");
            return false;
        } catch (IOException e) {
            LogUtil.e("LAKALA", "IOException");
            return false;
        }
    }


    /**
     * 设置按钮状态
     *
     * @param view
     * @param normal
     * @param pressed
     */
    public static void setPressedBg(View view, Drawable normal, Drawable pressed) {
        StateListDrawable bg = new StateListDrawable();
        int[][] states = new int[6][];
        states[0] = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_enabled, android.R.attr.state_focused};
        states[2] = new int[]{android.R.attr.state_enabled};
        states[3] = new int[]{android.R.attr.state_focused, android.R.attr.state_window_focused};
        states[4] = new int[]{android.R.attr.state_window_focused};
        bg.addState(states[0], pressed);
//	    bg.addState(states[3], focused); 
        bg.addState(states[2], normal);
        view.setBackgroundDrawable(bg);
    }

    /**
     * 设置ImageView状态
     *
     * @param view
     * @param normal
     * @param pressed
     */
    public static void setPressedSrc(ImageView view, Drawable normal, Drawable pressed) {
        StateListDrawable bg = new StateListDrawable();
        int[][] states = new int[6][];
        states[0] = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_enabled, android.R.attr.state_focused};
        states[2] = new int[]{android.R.attr.state_enabled};
        states[3] = new int[]{android.R.attr.state_focused, android.R.attr.state_window_focused};
        states[4] = new int[]{android.R.attr.state_window_focused};
        bg.addState(states[0], pressed);
        bg.addState(states[2], normal);
        bg.addState(states[4], pressed);
        view.setImageDrawable(bg);
    }

//	/**
//	 * Bitmap 转为 BitmapDrawable 
//	 * Bitmap转换Drawable时防止图片缩小
//	 * 
//	 * @param bitmap
//	 *            Bitmap类型
//	 * @return BitmapDrawable BitmapDrawable类型
//	 */
//	public static BitmapDrawable BitmapToBitmapDrawable(Context context,Bitmap bitmap) {
//		BitmapDrawable drawable = null;
//		try {
//			drawable = new BitmapDrawable(bitmap);
//			drawable.setTargetDensity(getScreenDensidy(context));
//		} catch (Exception e) {
//			new Debugger().log(e);
//		}
//		return drawable;
//	}
//	
//	/**
//	 * 获取屏幕密度
//	 */
//	public static int getScreenDensidy(Context context) {
//		DisplayMetrics dm = null;
//		if (context instanceof Activity) {
//			Activity activity = (Activity) context;
//			dm = new DisplayMetrics();
//			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
//		}
//		return dm.densityDpi;
//	}

    /**
     * 处理照片变灰的方法
     *
     * @param bitmap
     * @return
     */
    public static Bitmap createGrayBitmap(Bitmap bitmap) {
        Bitmap grayImg = null;
        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            grayImg = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            Canvas canvas = new Canvas(grayImg);
            Paint paint = new Paint();
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0);
            ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
                    colorMatrix
            );
            paint.setColorFilter(colorMatrixFilter);
            canvas.drawBitmap(bitmap, 0, 0, paint);

		       /*另一种方式
                * BlurMaskFilter blurFilter = new BlurMaskFilter(8, BlurMaskFilter.Blur.INNER);
		        Paint shadowPaint = new Paint();
		        shadowPaint.setMaskFilter(blurFilter);

		        int[] offsetXY = new int[2];
		        grayImg = bitmap.extractAlpha(shadowPaint, offsetXY);
		        grayImg = grayImg.copy(Config.ARGB_8888, true);
		        Canvas c = new Canvas(grayImg);
		        c.drawBitmap(bitmap, -offsetXY[0], -offsetXY[1], null);*/
		        
		       /*释放
		       bitmap.recycle();
		       grayImg.recycle();*/
        } catch (Exception e) {
        }
        return grayImg;
    }


    public static Bitmap setBitmapAssignSize(Bitmap bitmap, int maxSize) {
        double scal;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width > height) {
            scal = ((double) width) / maxSize;
            return Bitmap.createScaledBitmap(bitmap, maxSize, (int) (height / scal), false);
        } else {
            scal = ((double) height) / maxSize;
            return Bitmap.createScaledBitmap(bitmap, (int) (width / scal), maxSize, false);
        }
    }

    /**
     * 优化照片
     *
     * @param bitmap
     * @param width
     * @param height
     * @param size
     * @return
     */
    public static Bitmap setBitmapSize(Bitmap bitmap, int width, int height, int size) {
        double scal;
        if (width > height) {
            scal = ((double) width) / size;
            return Bitmap.createScaledBitmap(bitmap, size, (int) (height / scal), false);
        } else {
            scal = ((double) height) / size;
            return Bitmap.createScaledBitmap(bitmap, (int) (width / scal), size, false);
        }
    }

    /**
     * 优化bitmap
     *
     * @param cr
     * @param uri0
     * @return
     */
    public static Bitmap decodeBitmap(ContentResolver cr, Uri uri0) {
        Bitmap bitmap = null;
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            InputStream inputStream = cr.openInputStream(uri0);
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            int scale = 1;
            int IMAGE_MAX_SIZE = 1256;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(
                        2, (int) Math.round(
                        Math.log(
                                IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)
                        ) / Math.log(0.5)
                )
                );
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            InputStream inputStream2 = cr.openInputStream(uri0);
            bitmap = BitmapFactory.decodeStream(inputStream2, null, o2);
            inputStream2.close();
        } catch (IOException e) {
        }

        return bitmap;
    }

    /**
     * 压缩图片
     *
     * @param bitmap1  照片bitmap1
     * @param bitmap2  照片bitmap1
     * @return
     */
    private static int BITMAP_SIZE = 600;

    public static Bitmap[] decodeBitmap(Bitmap bitmap1, Bitmap bitmap2) {
        Bitmap[] bitmaps = new Bitmap[2];
        int height1 = bitmap1.getHeight();
        int width1 = bitmap1.getWidth();
        int height2 = bitmap2.getHeight();
        int width2 = bitmap2.getWidth();
        if (height1 < BITMAP_SIZE && width1 < BITMAP_SIZE) {
            bitmaps[0] = bitmap1;
        } else {
            bitmaps[0] = ImageUtil.setBitmapSize(bitmap1, width1, height1, BITMAP_SIZE);
        }
        if (width2 < BITMAP_SIZE && height2 < BITMAP_SIZE) {
            bitmaps[1] = bitmap2;
        } else {
            bitmaps[1] = ImageUtil.setBitmapSize(bitmap2, width2, height2, BITMAP_SIZE);
        }
        return bitmaps;
    }

    public static Bitmap[] decodeBitmap(Bitmap bitmap1) {
        Bitmap[] bitmaps = new Bitmap[1];
        int height1 = bitmap1.getHeight();
        int width1 = bitmap1.getWidth();
        if (height1 < BITMAP_SIZE && width1 < BITMAP_SIZE) {
            bitmaps[0] = bitmap1;
        } else {
            bitmaps[0] = ImageUtil.setBitmapSize(bitmap1, width1, height1, BITMAP_SIZE);
        }
        return bitmaps;
    }

    /**
     * 通过bitmap获取byte类型的数据
     *
     * @param bitmap
     * @return
     * @throws Exception
     */
    public static byte[] getUpdata(Bitmap bitmap) {
        InputStream inputStream = null;
        byte data[];
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            inputStream = new ByteArrayInputStream(baos.toByteArray());
            data = new byte[inputStream.available()];
            inputStream.read(data);
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
        return data;
    }

    /**替你还缩略图**/
    /**
     * 获取文件缩略图
     *
     * @param srcPath 文件路径
     * @param width   压缩图片宽度
     * @param height  压缩图片高度
     * @param size    质量（KB）
     */
    public static Bitmap getThumbnail(String srcPath, float width, float height, long size) {
        ByteArrayOutputStream outputStream = getThumbnailByte(srcPath, width, height, size);
        //把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(outputStream.toByteArray());
        //把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    /**
     * 获取文件缩略图
     *
     * @param bitmap 原始图
     * @param size   质量（KB）
     */
    public static Bitmap getThumbnail(Bitmap bitmap, long size) {
        ByteArrayOutputStream out = compressImage(bitmap, size);
        //把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(out.toByteArray());
        //把ByteArrayInputStream数据生成图片
        Bitmap compressBitmap = BitmapFactory.decodeStream(isBm, null, null);
        return compressBitmap;
    }

    /**
     * 获取文件缩略图
     *
     * @param srcPath 文件路径
     * @param width   压缩图片宽度
     * @param height  压缩图片高度
     * @param size    质量（KB）
     */
    public static ByteArrayOutputStream getThumbnailByte(Context context, Uri srcPath, float width,
                                                         float height, long size)
            throws IOException {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory
                .decodeStream(getContactPhotoStream(srcPath, context), null, newOpts);//此时返回bm为空

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
        newOpts.inPreferredConfig = Config.RGB_565;
        LogUtil.print("------>",
                "Bitmap inSampleSize = " + newOpts.inSampleSize + " width =" + width + "height = " + height
        );
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeStream(getContactPhotoStream(srcPath, context), null, newOpts);

        ByteArrayOutputStream byteArrayOutputStream = compressImage(bitmap, size);
        if (!bitmap.isRecycled())
            bitmap.recycle();
        return byteArrayOutputStream;//压缩好比例大小后再进行质量压缩
    }

    /**
     * 获取文件缩略图
     *
     * @param srcPath 文件路径
     * @param width   压缩图片宽度
     * @param height  压缩图片高度
     * @param size    质量（KB）
     */
    public static ByteArrayOutputStream getThumbnailByte2(Context context, Uri srcPath, float width,
                                                         float height)
            throws IOException {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory
                .decodeStream(getContactPhotoStream(srcPath, context), null, newOpts);//此时返回bm为空

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
        newOpts.inPreferredConfig = Config.RGB_565;
        LogUtil.print("------>",
                "Bitmap inSampleSize = " + newOpts.inSampleSize + " width =" + width + "height = " + height
        );
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeStream(getContactPhotoStream(srcPath, context), null, newOpts);

        ByteArrayOutputStream byteArrayOutputStream = compressImage2(bitmap);
        if (!bitmap.isRecycled())
            bitmap.recycle();
        return byteArrayOutputStream;//压缩好比例大小后再进行质量压缩
    }
    /**
     * 获取文件缩略图
     *
     * @param srcPath 文件路径
     * @param width   压缩图片宽度
     * @param height  压缩图片高度
     * @param size    质量（KB）
     */
    public static ByteArrayOutputStream getThumbnailByte(String srcPath, float width, float height,
                                                         long size) {
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
        newOpts.inPreferredConfig = Config.RGB_565;
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        LogUtil.print(
                "Bitmap inSampleSize = " + newOpts.inSampleSize + " width =" + width + "height = " + height);
        ByteArrayOutputStream byteArrayOutputStream = compressImage(bitmap, size);
        if (!bitmap.isRecycled())
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
        while (baos
                .toByteArray().length / 1024 > size && options > 0) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 5;//每次都减少10
        }
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return baos;
    }

    /**
     * 通用压缩图片
     *
     * @param size 质量（KB）
     */
    public static ByteArrayOutputStream compressImage2(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        File file=new File(getImageCacheDir(),"asskd.png");
//        try {
//            FileOutputStream fos = new FileOutputStream(file);
//            image.compress(Bitmap.CompressFormat.JPEG, 90, fos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        image.compress(Bitmap.CompressFormat.JPEG, 90, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        return baos;
    }
    public static String getImageCacheDir() {

        String dir =  Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/IdlePaint/Image/";
        File file = new File(dir);
        if (!file.exists()) file.mkdirs();
        return dir;
    }

    public static File createPhotoFile(Context context, String fileName) {
        String cacheDirPath = "";
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED
        )) {
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
        if (!dir.exists())
            dir.mkdirs();
        //localTempImgDir和localTempImageFileName是自己定义的名字
        return new File(dir, fileName);
    }

    /**
     * 获取图片字节流  生成宽为480 高为800 大小为300k 的图片
     *
     * @param file 文件
     * @return 字节流
     */
    public static byte[] getImgByte(File file, Context context) {
        ByteArrayOutputStream out = ImageUtil.getThumbnailByte(
                file.getAbsolutePath(), 600, 800, 300
        );
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
     * 获取图片字节流  生成宽为480 高为800 大小为300k 的图片
     *
     * @param file 文件
     * @return 字节流
     */
    public static byte[] getImgByte(Uri file, Context context) throws IOException {
        ByteArrayOutputStream out = ImageUtil.getThumbnailByte2(
                context, file, 720, 1280
        );
        LogUtil.print("------>","fileSize:"+out.size());
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


    public static InputStream getContactPhotoStream(Uri uri, Context context)
            throws FileNotFoundException {
        ContentResolver res = context.getContentResolver();
        return res.openInputStream(uri);
    }


    public static Bitmap bytes2Bitmap(byte[] bytes) {

        if (bytes != null && bytes.length != 0) {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return null;
    }

    public byte[] bitmap2bytes(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }
}
