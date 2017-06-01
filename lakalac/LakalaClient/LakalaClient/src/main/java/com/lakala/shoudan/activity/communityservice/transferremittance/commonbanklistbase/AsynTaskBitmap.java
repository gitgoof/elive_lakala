package com.lakala.shoudan.activity.communityservice.transferremittance.commonbanklistbase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;

import com.lakala.shoudan.common.Parameters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by HUASHO on 2015/1/29.
 * 异步加载图片
 */
public class AsynTaskBitmap extends AsyncTask<String, Void, Bitmap> {
    private WeakReference<ImageView> weakReferences ;
    public String data;
    private Context context;
    public AsynTaskBitmap(ImageView imageView,Context context){
        weakReferences = new WeakReference<ImageView>(imageView);
        this.context = context;
    }


    @Override
    protected Bitmap doInBackground(String... params) {
        try{
            data = params[0];
            Bitmap bitmap = null;
            //从SD卡中获取图片
            File tempFile = new File(getCachePath(data.hashCode(),context));
            if(tempFile.exists() && tempFile.isFile()){

                bitmap = BitmapFactory.decodeStream(new FileInputStream(tempFile));
            }
            if(bitmap == null){
                bitmap = downloadBitmap(data);//加载网络图片
            }

            if(bitmap != null){
                bitmap = ImageUtils.decodeSampledBitmapFromResource(bitmap);
                BitmapManager.getInstance().addBitmapToCache(data, bitmap);//添加到缓存
                return bitmap;
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private static String getCachePath(int hashCode, Context context) {
        if (context == null)
            return "";

        return String.format("%s%08X.png", getLocalCacheDir(context), hashCode);
    }

    /**
     * 获取缓存图片目录
     *
     * @param context
     * @return
     */
    private static String getLocalCacheDir(Context context) {
        if (context == null)
            return "";

        String cacheDir = "";

        // 获取外部存储器的状态，当为可读写时启用SD卡缓存。
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            // 取SD卡路径
            cacheDir = Environment.getExternalStorageDirectory().getPath()
                    + Parameters.imageCachePath;
        } else {
            // 外部存储器不可用，使用内部存储器
            cacheDir = context.getFilesDir().getPath()
                    + Parameters.imageCachePath;
        }

        File file = new File(cacheDir);
        if(!file.exists() || !file.isDirectory()){
            file.mkdirs();
        }

        return cacheDir;
    }

    private Bitmap downloadBitmap(String imageUrl)throws Exception{
        URL url= new URL(imageUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
//		conn.setDoOutput(true);//保留此句，报405错误
        conn.setReadTimeout(5000);
        conn.setConnectTimeout(5000);
        conn.connect();
        Bitmap bitmap = null;
        if(conn.getResponseCode()==200){

            InputStream is = conn.getInputStream();
            if(is != null && is.available()>0){
                bitmap = BitmapFactory.decodeStream(is);
                //保存图片到sd
                OutputStream os = new FileOutputStream(new File(getCachePath(data.hashCode(), context)));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                return bitmap;
            }
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if(isCancelled()){
            result = null;//取消加载
        }

        if(null != weakReferences && null != result){

            ImageView imageView = weakReferences.get();//获取当前要加载图片的imageView
            AsynTaskBitmap asynTaskBitmap = ImageUtils.getAsynTaskBitmap(imageView);

            if(null != imageView && null != asynTaskBitmap){
                imageView.setImageBitmap(result);
            }
        }
    }


    /**
     *drawable 存放一个软引用 asynctask
     * @author ZhangMY
     *
     */
    static class AsyncDrawable extends BitmapDrawable {

        private WeakReference<AsynTaskBitmap> mAsynTaskBitmap;

        public AsyncDrawable(Bitmap bitmap,AsynTaskBitmap asynTaskBitmap){
            super(bitmap);
            mAsynTaskBitmap = new WeakReference<AsynTaskBitmap>(asynTaskBitmap);
        }



        public AsynTaskBitmap getAsynTaskBitmap(){
            return mAsynTaskBitmap.get();
        }
    }
}
