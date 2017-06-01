package com.lakala.platform.nativeplugin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.lakala.library.encryption.Mac;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;

import java.io.File;

/**
 * Created by More on 15/12/2.
 *
 * 自定义相机调用,通过设置CameraCallListener得知调用的结果CameraAction表示调用中间时序
 *
 * 必须在调用的Activity的onActivityResult传入回调参数,方可触发回调
 *
 *
 */
public class CameraController {

    private Activity context;

    /**
     * 相册选取
     */
    private final int REQUEST_CODE_PICK = 888;
    /**
     * 调用相机拍照时
     */
    private final int REQUEST_CODE_CAPTURE = 889;
    /**
     * 裁剪照片
     */
    private final int REQUEST_CODE_CROP = 890;


    public CameraController(Activity context, CameraCallListener cameraCallListener) {
        this.context = context;
        this.cameraCallListener = cameraCallListener;
        init();

    }

    private CameraCallListener cameraCallListener;

    /**
     * 选取照片后裁剪
     * @param cropX 裁剪的x轴长 = cropX * 100
     * @param cropY 裁剪的y轴长 = cropY * 100
     */
    public void startActionPick(int cropX, int cropY){
        tempFile = new File(filePath, System.currentTimeMillis()+".jpg");
        this.cropX = Math.abs(cropX);
        this.cropY = Math.abs(cropY);
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        context.startActivityForResult(intent, REQUEST_CODE_PICK);

    }

    private String filePath;

    private void init(){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"lklcm";
        }else {
            filePath = ApplicationEx.getInstance().getFilesDir().getAbsolutePath()+File.separator+"lklcm";
        }
        File folder = new File(filePath);
        if (!folder.exists() && !folder.isDirectory())
            folder.mkdirs();
    }

    /**
     * 拍照完后裁剪
     * @param cropX 裁剪的x轴长 = cropX * 100
     * @param cropY 裁剪的y轴长 = cropY * 100
     */
    public void startActionImageCapture(int cropX, int cropY){
        this.cropX = Math.abs(cropX);
        this.cropY = Math.abs(cropY);
        tempFile = new File(filePath, System.currentTimeMillis()+".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        context.startActivityForResult(intent, REQUEST_CODE_CAPTURE);

    }

    private File tempFile;

    private int cropX = 4;
    private int cropY = 4;

    private int whoCallCrop;

    /**
     * 裁剪图片方法实现
     * @param uri
     */
    private void startActionCrop(int whoCallCrop, Uri uri) {

        this.whoCallCrop = whoCallCrop;

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", cropX);
        intent.putExtra("aspectY", cropY);

        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", (cropX * 100));
        intent.putExtra("outputY", (cropY * 100));

        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        context.startActivityForResult(intent, REQUEST_CODE_CROP);
    }

    public interface CameraCallListener {

        /**
         * @param cameraAction 当前操作
         * @param filePath  照片路径
         */
        public void onSucceed(CameraAction cameraAction, String filePath);
        public void onFailed(CameraAction cameraAction);

    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {

            switch (requestCode){

                case REQUEST_CODE_CAPTURE:

                    if(resultCode != context.RESULT_OK){
                        callFailed(CameraAction.CAPTURE);

                        return;
                    }

//                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                    Uri uri = Uri.fromFile(tempFile);
//                    intent.setData(uri);
//                    context.sendBroadcast(intent);
                    callSucceed(CameraAction.CROP_AFTER_CAPTURE, Uri.fromFile(tempFile).toString());
//                    startActionCrop(REQUEST_CODE_CAPTURE, Uri.fromFile(tempFile));

                    break;
                case REQUEST_CODE_CROP:


//                    if(resultCode != context.RESULT_OK){
//                        if(whoCallCrop == REQUEST_CODE_CAPTURE){
//                            callFailed(CameraAction.CROP_AFTER_CAPTURE);
//                        }else if(whoCallCrop ==REQUEST_CODE_PICK){
//                            callFailed(CameraAction.CROP_AFTER_PICK);
//                        }else{
//                            Exception exception = new Exception("Unkown Caller of ActionCrop");
//                            LogUtil.print(exception);
//                        }
//
//                        return;
//                    }
//
//                    if(whoCallCrop == REQUEST_CODE_CAPTURE){
//
//                    }else if(whoCallCrop ==REQUEST_CODE_PICK){
//
//                    }else{
//                        Exception exception = new Exception("Unkown Caller of ActionCrop");
//                        LogUtil.print(exception);
//                    }

                    break;
                case REQUEST_CODE_PICK:

                    if(resultCode != context.RESULT_OK){
                        callFailed(CameraAction.PICK);
                        return;
                    }

                    if (data!=null){
                        callSucceed(CameraAction.CROP_AFTER_PICK, data.getData().toString());
                    }

                    break;
                default:
                    break;

            }

    }

    private String pickCropUriString;


    public enum CameraAction{

        PICK,
        CROP_AFTER_PICK,

        CAPTURE,
        CROP_AFTER_CAPTURE,

    }

    private void callSucceed(CameraAction cameraAction, String filePath){
        if(cameraCallListener != null){
            cameraCallListener.onSucceed(cameraAction, filePath);
        }
    }

    private void callFailed(CameraAction cameraAction){
        if(cameraCallListener != null){
            cameraCallListener.onFailed(cameraAction);
        }
    }


}
