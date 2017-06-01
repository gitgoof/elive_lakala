package com.lakala.platform.nativeplugin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.ui.module.IPhoneStylePopupWindow;

import java.io.File;
import java.io.Serializable;

/**
 * Created by linmq on 2016/5/27.
 */
public class CameraPlugin extends Fragment {
    private String filePath;

    private Bundle extras;

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
    protected File tempFile;
    private CameraListener cameraListener;

    public CameraPlugin register(FragmentActivity activity, CameraListener listener) {
        this.cameraListener = listener;
        extras = new Bundle();
        activity.getSupportFragmentManager()
                .beginTransaction().add(this, CameraPlugin.class.getName()).commit();
        return this;
    }

    protected String getFilePath() {
        return filePath;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                       File.separator + "lklcm";
        } else {
            filePath =
                    ApplicationEx.getInstance().getFilesDir().getAbsolutePath() + File.separator +
                    "lklcm";
        }
        File folder = new File(filePath);
        if (!folder.exists() && !folder.isDirectory())
            folder.mkdirs();
    }

    public <T extends Serializable> void put(String key, T value){
        extras.putSerializable(key,value);
    }

    public <T extends Serializable> T get(String key){
        return (T) extras.getSerializable(key);
    }

    /**
     * 选取照片
     */
    public void startActionPick() {
        tempFile = null;
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK);
    }

    /**
     * 拍照
     */
    public void startActionImageCapture() {
        tempFile = new File(filePath, System.currentTimeMillis() + ".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        startActivityForResult(intent, REQUEST_CODE_CAPTURE);
    }

    /**
     * 弹出框选择方式
     */
    public void showActionChoose() {
        final String[] items = new String[]{"用户相册","拍照"};
        final IPhoneStylePopupWindow window = new IPhoneStylePopupWindow(
                getActivity(),items);
        window.setItemClickListener(new IPhoneStylePopupWindow.ItemClickListener() {
            @Override
            public void onItemClick(String itemName) {
                if(TextUtils.equals(itemName,items[0])){
                    startActionPick();
                }else if(TextUtils.equals(itemName,items[1])){
                    startActionImageCapture();
                }
                window.dismiss();
            }
        });
        window.showPop(getActivity().getWindow().getDecorView());
    }

    /**
     * 裁剪图片方法实现
     *
     * @param cropX
     * @param cropY
     * @param uri
     */
    private void startActionCrop(int cropX, int cropY, Uri uri) {
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

        startActivityForResult(intent, REQUEST_CODE_CROP);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (cameraListener == null) {
            return;
        }
        File file=tempFile;
        String filePath = null;
        if (null!=file && resultCode == Activity.RESULT_OK){
            //拍照处理
            filePath= Uri.fromFile(tempFile).toString();
            cameraListener.onSuccess(filePath);
        }else{
            if (resultCode == Activity.RESULT_OK) {
                if(requestCode == REQUEST_CODE_PICK){
                    //相册选择处理
                    filePath = data.getData().toString();
                }
                cameraListener.onSuccess(filePath);
            } else {
                cameraListener.onFailed();
            }
        }

    }

    public interface CameraListener {
        void onSuccess(String filePath);
        void onFailed();
    }
}
