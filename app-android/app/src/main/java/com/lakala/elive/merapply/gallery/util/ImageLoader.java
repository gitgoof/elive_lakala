package com.lakala.elive.merapply.gallery.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


/**
 * 图片加载类
 * Created by wenhaogu on 16/6/14.
 */
public class ImageLoader {
    private static ImageLoader mInstance;
    //图片的核心对象
    private LruCache<String, Bitmap> mLruChache;
    //线程池
    private ExecutorService mThreadPool;
    //默认线程数
    private static final int DEAFULT_THREAD_COUNT = 1;
    //队列的调度方式
    private Type mType = Type.LIFO;
    //任务队列
    private LinkedList<Runnable> mTaskQueue;
    //后台轮循线程
    private Thread mPoolThread;
    private Handler mPoolThreadHandler;
    //UI线程的handler
    private Handler mUIHandler;

    private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);
    private Semaphore mSemaphoreThreadPool;


    public enum Type {
        FIFO, LIFO;
    }

    private ImageLoader() {
    }

    ;

    private ImageLoader(int threadCount, Type type) {
        init(threadCount, type);
    }

    /**
     * 初始化
     *
     * @param threadCount
     * @param type
     */
    private void init(int threadCount, Type type) {
        //后台轮询线程
        mPoolThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mPoolThreadHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        //线程池取出一个任务进行执行
                        mThreadPool.execute(getTask());
                        try {
                            mSemaphoreThreadPool.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //释放一个信号量
                mSemaphorePoolThreadHandler.release();
                Looper.loop();

            }
        };
        mPoolThread.start();
        //获取应用最多可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;

        mLruChache = new LruCache<String, Bitmap>(cacheMemory) {

            @Override
            protected int sizeOf(String key, Bitmap value) {
                //图片的行字节*图片的高度
                return value.getRowBytes() * value.getHeight();
            }
        };
        //创建线程池
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mTaskQueue = new LinkedList<Runnable>();
        mType = type;
        mSemaphoreThreadPool = new Semaphore(threadCount);
    }

    /**
     * 从任务队列取出一个方法
     *
     * @return
     */
    private Runnable getTask() {

        if (mType == Type.FIFO) {
            return mTaskQueue.removeFirst();
        } else if (mType == Type.LIFO) {
            try{
                return mTaskQueue.removeLast();
            } catch (Exception e){
            }
        }
        return null;
    }

    public static ImageLoader getInstance(int threadConut, Type type) {
        if (mInstance == null) {
            synchronized (ImageLoader.class) {

                if (mInstance == null) {

                    mInstance = new ImageLoader(threadConut, type);
                }

            }
        }
        return mInstance;
    }

    /**
     * 根据path为imageView设置图片1
     *
     * @param path
     * @param imageView
     */
    public void loadImage(final String path, final ImageView imageView) {

        if (TextUtils.isEmpty(path)) {
            Toast.makeText(imageView.getContext(), "图片错误,请重新选择", Toast.LENGTH_SHORT).show();
            return;
        }
        //设置标识,防止错乱
        imageView.setTag(path);

        if (mUIHandler == null) {
            mUIHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    //获取得到的图片,为imageView回掉设置图片
                    ImagBeanHolder holder = (ImagBeanHolder) msg.obj;
                    Bitmap bitmap = holder.bitmap;
                    ImageView imageView = holder.imageView;
                    String path = holder.path;
                    //将path与getTag存储路径进行比较
                    if (imageView.getTag().toString().equals(path)) {
//                        imageView.setImageBitmap(bitmap);
                        imageView.setImageBitmap(rotaingImageView(readPictureDegree(path), bitmap));

                    }
                }
            };
        }
        //根据path获取缓存中的bitmap
        Bitmap bm = getBitmapFromCache(path);

        if (bm != null) {
            refreashBitmap(bm, imageView, path);
        } else {
            addTask(new Runnable() {
                @Override
                public void run() {
                    //加载图片//对图片进行压缩
                    //1.获取图片需要显示的大小
                    ImageSize imageSize = getImageViewSize(imageView);
                    //2.压缩图片
                    Bitmap bm = decodeSampledBitmapFromPath(path, imageSize.width, imageSize.height);
                    //3.把图片加入到缓存
                    addBitmapToLruCache(path, bm);
                    refreashBitmap(bm, imageView, path);

                    mSemaphoreThreadPool.release();
                }
            });
        }


    }

    private void refreashBitmap(Bitmap bm, ImageView imageView, String path) {
        Message message = Message.obtain();
        ImagBeanHolder holder = new ImagBeanHolder();
        holder.bitmap = bm;
        holder.imageView = imageView;
        holder.path = path;
        message.obj = holder;
        mUIHandler.sendMessage(message);
    }

    /**
     * 把图片加入到缓存
     *
     * @param path
     * @param bm
     */
    private void addBitmapToLruCache(String path, Bitmap bm) {

        if (getBitmapFromCache(path) == null) {
            if (bm != null) {
                //addBitmapToLruCache(path,bm);
                mLruChache.put(path, bm);
            }

        }
    }

    /**
     * 根据图片需要显示的宽高进行压缩
     *
     * @param path
     * @param width
     * @param height
     * @return
     */
    private Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {
        //获取图片的宽高,并不把图片加载到内存中去
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = caculateInSampaleSize(options, width, height);

        //使用获取到的inSampleSize再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        return bitmap;
    }

    /**
     * 根据需求的宽和高以及图片实际的宽和高计算出sampleSize
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int caculateInSampaleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;

        int inSampleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            int widthRadio = Math.round(width * 1.0f / reqWidth);
            int heightRadio = Math.round(height * 1.0f / reqHeight);

            inSampleSize = Math.max(widthRadio, heightRadio);

        }

        return inSampleSize;
    }

    /**
     * 根据imageView获取适当压缩的宽和高
     *
     * @param imageView
     * @return
     */

    private ImageSize getImageViewSize(ImageView imageView) {
        ImageSize imageSize = new ImageSize();

        DisplayMetrics metrics = imageView.getContext().getResources().getDisplayMetrics();
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        int width = imageView.getWidth();//获取实际宽度
        if (width <= 0) {
            width = lp.width;//获取到布局里的宽度
        }
        if (width <= 0) {
            //width = imageView.getMaxWidth();//检查最大值
            width = getImageViewFieldValue(imageView, "mMaxWidth");
        }
        if (width <= 0) {
            width = metrics.widthPixels;//获取屏幕的宽度
        }

        int height = imageView.getHeight();
        if (height <= 0) {
            height = lp.height;//获取加载到布局里的宽度
        }
        if (height <= 0) {
            // height = imageView.getMaxHeight();//检查最大值
            height = getImageViewFieldValue(imageView, "mMaxHeight");
        }
        if (height <= 0) {
            height = metrics.heightPixels;
        }

        imageSize.width = width;
        imageSize.height = height;
        return imageSize;
    }

    private synchronized void addTask(Runnable runnable) {

        mTaskQueue.add(runnable);
        try {
            if (mPoolThreadHandler == null) {
                mSemaphorePoolThreadHandler.acquire();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    /**
     * 根据path获取缓存中的bitmap
     *
     * @param key
     * @return
     */
    private Bitmap getBitmapFromCache(String key) {
        return mLruChache.get(key);
    }

    private class ImageSize {
        int width;
        int height;
    }

    private class ImagBeanHolder {
        Bitmap bitmap;
        ImageView imageView;
        String path;
    }

    /**
     * 通过反射获取imageview的某个值
     *
     * @param object
     * @param fieldName
     * @return
     */
    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }


    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /*
        * 旋转图片
        * @param angle
        * @param bitmap
        * @return Bitmap
        */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        if (null != bitmap) {
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return resizedBitmap;
        } else {
            return bitmap;
        }

    }
}
