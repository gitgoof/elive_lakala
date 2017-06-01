package com.lakala.elive.merapply.merutils;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.baoyz.actionsheet.ActionSheet;
import com.lakala.elive.merapply.activity.BaseActivity;
import com.lakala.elive.merapply.gallery.GalleryActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 相机工具类
 * Created by wenhaogu on 2017/3/11.
 */

public class OpenCameraUtil {
    public final int REQUEST_CODE_PICK_IMAGE = 110;//打开相册
    public final int REQUEST_CAMERA = 111;//拍照

    public Uri imageUri;

    private static OpenCameraUtil openCameraUtil;

    private OpenCameraUtil() {
    }

    public static OpenCameraUtil newInstace() {
        if (null == openCameraUtil) {
            synchronized (OpenCameraUtil.class) {
                if (null == openCameraUtil) {
                    openCameraUtil = new OpenCameraUtil();
                }
            }
        }
        return openCameraUtil;
    }

    private BaseActivity mActivity;

    /**
     * 选择照片或拍照
     */
    public void showSelect(BaseActivity activity) {
        this.mActivity = activity;
        ActionSheet.createBuilder(activity, activity.getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles("打开相册", "拍照")
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        switch (index) {
                            case 0://相册
                                getImageFromAlbum(mActivity);
                                break;
                            case 1://拍照
                                photograph(mActivity);
                                break;
                        }
                    }

                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                    }
                }).show();
    }

    //打开相册
    public void getImageFromAlbum(BaseActivity activity) {
        activity.startActivityForResult(new Intent(activity, GalleryActivity.class), REQUEST_CODE_PICK_IMAGE);
    }

    //打开相机
    public void photograph(BaseActivity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSSS", Locale.CHINA);
        Date date = new Date(System.currentTimeMillis());
        String filename = format.format(date);
        //存储至DCIM文件夹
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File output = new File(path, filename + ".jpg");
        imageUri = Uri.fromFile(output);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activity.startActivityForResult(intent, REQUEST_CAMERA);
    }
}
