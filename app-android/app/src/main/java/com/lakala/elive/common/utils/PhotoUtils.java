package com.lakala.elive.common.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by wenhaogu on 2017/1/11.
 */

public class PhotoUtils {

    /**
     * 把绝对路径转换成content开头的URI
     *
     * @param selectedImage
     * @return
     */
    public static Uri getImageUrl(Context context, Uri selectedImage) {
        String path = selectedImage.getEncodedPath();
        if (path != null) {

            path = Uri.decode(path);

            ContentResolver cr = context.getContentResolver();

            StringBuffer buff = new StringBuffer();

            buff.append("(")

                    .append(MediaStore.Images.ImageColumns.DATA)

                    .append("=")

                    .append("'" + path + "'")

                    .append(")");

            Cursor cur = cr.query(

                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,

                    new String[]{MediaStore.Images.ImageColumns._ID},

                    buff.toString(), null, null);

            int index = 0;

            for (cur.moveToFirst(); !cur.isAfterLast(); cur

                    .moveToNext()) {

                index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);

// set _id value
                index = cur.getInt(index);
            }
            if (index == 0) {
//do nothing
            } else {
                Uri uri_temp = Uri
                        .parse("content://media/external/images/media/"
                                + index);
                if (uri_temp != null) {
                    selectedImage = uri_temp;
                }
            }
        }

        return selectedImage;
    }


    /**
     * 压缩图片大小到800k以下
     */
    public static File compressImage(File file, Context context) {
        if (file.length() < 1200 * 1200) {
            return file;
        }
        if (file.getPath().contains(".gif")) {
            return file;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(file.getPath(), options);
        //图片实际宽和高
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        int inSampleSize = 1;
        //图片 最大宽和高 设为屏幕宽高
        float maxHeight = 1200.0f;
        float maxWidth = 1200.0f;

        //实际大小 大于最大 宽高 时，根据宽高比重新 计算 实际宽高


        if (actualWidth > maxWidth && actualHeight > maxHeight) {
            // 计算出实际宽度和目标宽度的比率
            int widthRatio = Math.round((float) actualWidth / maxWidth);
            int heightRatio = Math.round((float) actualHeight / maxHeight);
            inSampleSize = Math.min(widthRatio, heightRatio);
        }

        options.inSampleSize = inSampleSize;
        Log.e("inSampleSize", options.inSampleSize + "");

        options.inJustDecodeBounds = false;
        FileOutputStream outputStream = null;
        File dir = context.getApplicationContext().getExternalCacheDir();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSSZ", Locale.CHINA);
        Date date = new Date(System.currentTimeMillis());
        String filename = format.format(date);
        File target = new File(dir, filename + ".jpg");
        try {
            outputStream = new FileOutputStream(target);
            Bitmap scaledBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            int imageDegree = readImageDegree(file.getPath());
            if (imageDegree > 0) {
                Bitmap rotatedBitmap = rotate(scaledBitmap, imageDegree);
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            } else {
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            }
            return target;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return target;
    }

    /**
     * 读取照片exif信息中的旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    public static int readImageDegree(String path) {
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

    public static Bitmap rotate(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        Log.e("inSampleSize", degree + "");
        matrix.postRotate(degree);
        int width = img.getWidth();
        int height = img.getHeight();
        img = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
        return img;
    }


    /**
     * 对图片进行base64
     * @param filePath
     * @return
     */
    public static String fileBase64String(String filePath) {
       //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = new FileInputStream(filePath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
        if(data == null || data.length == 0){
            return "";
        }
        //对字节数组Base64编码
        //返回Base64编码过的字节数组字符串
        return Base64.encodeToString(data, Base64.DEFAULT);

    }
}
