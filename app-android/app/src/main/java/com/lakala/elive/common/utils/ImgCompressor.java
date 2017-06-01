package com.lakala.elive.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImgCompressor {

    private static ImgCompressor instance = null;

    private Context context;

    private CompressListener compressListener;

    private static final int DEFAULT_OUTWIDTH = 720;

    private static final int DEFAULT_OUTHEIGHT = 1080;

    private static final int DEFAULT_MAXFILESIZE = 1024;//KB

    private ImgCompressor(Context context) {
        this.context = context;
    }

    public static ImgCompressor getInstance(Context context) {
        if (instance == null) {
            synchronized (ImgCompressor.class) {
                if (instance == null)
                    instance = new ImgCompressor(context.getApplicationContext());
            }
        }
        return instance;
    }

    public ImgCompressor setListener(CompressListener compressListener) {
        this.compressListener = compressListener;
        return this;
    }


    public int  compressImage(String srcImagePath, String destSavePath,String fileName,int mOptions) {
        int ret = 0;
        //进行大小缩放来达到压缩的目的
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        //根据原始图片的宽高比和期望的输出图片的宽高比计算最终输出的图片的宽和高
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        options.inSampleSize = computSampleSize(options, DEFAULT_OUTWIDTH, DEFAULT_OUTHEIGHT);
        options.inJustDecodeBounds = false;
        Bitmap scaledBitmap = null;
        try {
            scaledBitmap = BitmapFactory.decodeFile(srcImagePath, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        //生成最终输出的bitmap
        Bitmap actualOutBitmap = Bitmap.createScaledBitmap(scaledBitmap, DEFAULT_OUTWIDTH,DEFAULT_OUTHEIGHT, true);
        if(actualOutBitmap != scaledBitmap){
            scaledBitmap.recycle();
        }
        //进行有损压缩
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//      int options_ = 100;

        int options_ = mOptions;
        actualOutBitmap.compress(Bitmap.CompressFormat.JPEG, options_, baos);//质量压缩方法，把压缩后的数据存放到baos中 (100表示不压缩，0表示压缩到最小)
        int baosLength = baos.toByteArray().length;

        while (baosLength / 1024 > DEFAULT_MAXFILESIZE) {//循环判断如果压缩后图片是否大于maxMemmorrySize,大于继续压缩
            baos.reset();//重置baos即让下一次的写入覆盖之前的内容
            options_ = Math.max(0, options_ - 10);//图片质量每次减少10
            actualOutBitmap.compress(Bitmap.CompressFormat.JPEG, options_, baos);//将压缩后的图片保存到baos中
            baosLength = baos.toByteArray().length;
            if (options_ == 0)//如果图片的质量已降到最低则，不再进行压缩
                break;
        }
        actualOutBitmap.recycle();

        //将bitmap保存到指定路径
        FileOutputStream fos = null;
        try {
            if(!FileUtils.isFileDirExist(destSavePath)) {
                FileUtils.createLocalSavePath(destSavePath);
            }

            fos = new FileOutputStream(destSavePath + fileName);

            //包装缓冲流,提高写入速度
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fos);
            bufferedOutputStream.write(baos.toByteArray());
            bufferedOutputStream.flush();

        } catch (Exception e) {
            ret = 500;
            e.printStackTrace();
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    private int computSampleSize(BitmapFactory.Options options, float reqWidth, float reqHeight) {
        float srcWidth = options.outWidth;//20
        float srcHeight = options.outHeight;//10
        int sampleSize = 1;
        if (srcWidth > reqWidth || srcHeight > reqHeight) {
            int withRatio = Math.round(srcWidth / reqWidth);
            int heightRatio = Math.round(srcHeight / reqHeight);
            sampleSize = Math.min(withRatio, heightRatio);
        }
        return sampleSize;
    }

    public void starCompressWithDefault(String srcImagePath,String destImagePath,String fileName,String options) {
        new CompressTask().execute(srcImagePath,destImagePath,fileName,options);
    }

    public static class CompressResult implements Parcelable{
        public static final int RESULT_OK = 0;
        public static final int RESULT_ERROR = 1;
        private int status = RESULT_OK;
        private String srcPath;
        private String outPath;

        public CompressResult(){

        }

        protected CompressResult(Parcel in) {
            status = in.readInt();
            srcPath = in.readString();
            outPath = in.readString();
        }

        public static final Creator<CompressResult> CREATOR = new Creator<CompressResult>() {
            @Override
            public CompressResult createFromParcel(Parcel in) {
                return new CompressResult(in);
            }

            @Override
            public CompressResult[] newArray(int size) {
                return new CompressResult[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(status);
            dest.writeString(srcPath);
            dest.writeString(outPath);
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getSrcPath() {
            return srcPath;
        }

        public void setSrcPath(String srcPath) {
            this.srcPath = srcPath;
        }

        public String getOutPath() {
            return outPath;
        }

        public void setOutPath(String outPath) {
            this.outPath = outPath;
        }
    }
    /**
     * 压缩结果回到监听类
     */
    public interface CompressListener {
        void onCompressStart();

        void onCompressEnd(CompressResult imageOutPath);
    }

    private class CompressTask extends AsyncTask<String, Void, CompressResult> {

        @Override
        protected CompressResult doInBackground(String... params) {

            String srcPath = params[0];
            String destPath = params[1];
            String fileName = params[2];
            int options = Integer.valueOf(params[3]);

            CompressResult compressResult = new CompressResult();

            try {
                if(compressImage(srcPath, destPath, fileName,options)!=0){
                    compressResult.setStatus(CompressResult.RESULT_ERROR);
                };
            }catch (Exception e){
                e.printStackTrace();
                compressResult.setStatus(CompressResult.RESULT_ERROR);
            }

            compressResult.setSrcPath(srcPath);
            compressResult.setOutPath(destPath + fileName);

            return compressResult;
        }

        @Override
        protected void onPreExecute() {
            if (compressListener != null) {
                compressListener.onCompressStart();
            }
        }

        @Override
        protected void onPostExecute(CompressResult compressResult) {
            if (compressListener != null) {
                compressListener.onCompressEnd(compressResult);
            }
        }
    }
}
