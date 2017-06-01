package com.lakala.platform.cordovaplugin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.core.cordova.cordova.CordovaInterface;
import com.lakala.library.util.ImageUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.R;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.cordovaplugin.CameraPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.HashMap;

/**
 * 获取手机图片管理设置管理类
 * Created by Blues on 14-12-24.
 */
public class UserPhotoSetting {

    public static final int REQUEST_CODE = 3;
    private static  UserPhotoSetting instance;
    private static CordovaInterface activity;
    private static CameraPlugin mCameraPlugin;
    private static int rootLayoutId;
    private static String userName = "";
    private PopupWindow menuPopup;
    private String filePath = "";
    private String fileName = "";
    private File tempFile;

    private UserPhotoSetting(){
    }

    /**
     * 获取手机图片管理
     * @param context      相机插件接口实例
     * @param cameraPlugin 相机插件实例
     * @param layoutId     使用选择对话框界面的 rootId
     * @param UserName     用户id
     * @return
     */
    public static UserPhotoSetting getInstance(CordovaInterface context,CameraPlugin cameraPlugin,int layoutId,String UserName){
        activity = context;
        rootLayoutId =layoutId;
        userName = UserName;
        if (StringUtil.isEmpty(userName)) userName = "lkl";
        mCameraPlugin = cameraPlugin;
        if (instance == null){
            instance = new UserPhotoSetting();
        }

        return  instance;
    }

    public static  class CropParameters{

        BigDecimal quality;

        int aspectX;

        int aspectY;


    }

    private CropParameters cropParameters;

    /**
     * 获取照片-----从用户相册中获取或拍照获取
     */
    public void starSetUserPhotos(boolean ifSelectPhoto, boolean ifTakePhoto, CropParameters cropParameters) {

        this.cropParameters = cropParameters;

        RelativeLayout layout = (RelativeLayout) RelativeLayout.inflate(activity.getActivity(), R.layout.set_photo_select_layout, null);
        TextView imageText =(TextView) layout.findViewById(R.id.id_select_photo_image);
        TextView photoText = (TextView)layout.findViewById(R.id.id_take_photo_image);
        TextView cancelText = (TextView)layout.findViewById(R.id.id_cancel);

        imageText.setVisibility(ifSelectPhoto?View.VISIBLE : View.GONE);
        photoText.setVisibility(ifTakePhoto?View.VISIBLE: View.GONE);
        imageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//相册选择图片
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                activity.startActivityForResult(mCameraPlugin,intent1,2);
                menuPopup.dismiss();
            }
        });

        photoText.setOnClickListener(new View.OnClickListener() {//拍照
            @Override
            public void onClick(View view) {
                Intent intent0 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent0.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(tempFile));
                activity.startActivityForResult(mCameraPlugin, intent0, 1);
                menuPopup.dismiss();
            }
        });

        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuPopup.dismiss();

            }
        });

        menuPopup = new PopupWindow(layout, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,  true);
        menuPopup.setBackgroundDrawable(new BitmapDrawable());
        menuPopup.setOutsideTouchable(true);
        menuPopup.showAtLocation(activity.getActivity().findViewById(rootLayoutId), Gravity.BOTTOM, 0, 0);
        menuPopup.update();
        initPhotoFile();

    }

    /**
     * 初始化图片文件设置
     */
    private void initPhotoFile(){

        fileName = userName+System.currentTimeMillis()+".jpg";
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"lklcm";

        }else {
            filePath = ApplicationEx.getInstance().getFilesDir().getAbsolutePath()+File.separator+"lklcm";
        }
        File folder = new File(filePath);
        if (!folder.exists() && !folder.isDirectory())
            folder.mkdirs();
        tempFile = new File(filePath, fileName);
    }


    /**
     * .移除菜单选项
     */
    public void cancellMenuPopup(){
        if (menuPopup!=null)
            menuPopup.dismiss();
    }

    /**
     * 菜单选项是否显示
     * @return
     */
    public boolean isMenuPopupShow(){
        if (menuPopup!=null && menuPopup.isShowing())
            return  true;

        return  false;
    }

    /**
     * 处理物理返回事件
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK ) && isMenuPopupShow()){
            cancellMenuPopup();
            return true;
        }
        return false;
    }


    /**
     * 裁剪图片方法实现
     * @param uri
     */
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", cropParameters.aspectX);
        intent.putExtra("aspectY", cropParameters.aspectY);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", cropParameters.aspectX * 100);
        intent.putExtra("outputY", cropParameters.aspectY * 100);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        activity.startActivityForResult(mCameraPlugin,intent, REQUEST_CODE);
    }

    /**
     * .处理拍照或者选择相册后返回的头像数据
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public HashMap<String,String> onActivityForResult(int requestCode, int resultCode, Intent data) {
        String imageBase64 ="";
        HashMap<String,String> results = new HashMap<String,String>();
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode) {
                // 如果是调用相机拍照时
                case 1:
                    startPhotoZoom(Uri.fromFile(tempFile));

                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(tempFile);
                    intent.setData(uri);
                    activity.getActivity().sendBroadcast(intent);
                    break;
                // 如果是直接从相册获取
                case 2:
                    if (data!=null)
                        startPhotoZoom(data.getData());
                    break;
                // 取得裁剪后的图片
                case 3:

                    if (StringUtil.isEmpty(userName)) userName = "lkl";



//                        try{
//                            OutputStream stream = new FileOutputStream(tempFile.getPath());
//                            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                            stream.flush();
//                            stream.close();
//                        }catch (Exception e){
//                            LogUtil.print(e.getMessage());
//                        }

                    Bitmap photo = ImageUtil.getBitmapByFile(tempFile.getAbsolutePath(), 10);

                    if (photo!=null) {

                        BigDecimal bigDecimal = cropParameters.quality.multiply(new BigDecimal(100));

                        int quality = bigDecimal.intValue();
                        byte[] photoData = ImageUtil.bitmap2Bytes(photo, quality);
                        imageBase64 = Base64.encodeToString(photoData, Base64.DEFAULT);
                        String path = tempFile.getAbsolutePath();
                        results.put("path",path);
                        results.put("data",imageBase64);
                    }

//                        Bundle extras = data.getExtras();
//                        if (extras != null) {
//                            Bitmap photo = extras.getParcelable("data");
//
//
//                        }
                    break;
                default:
                    break;
            }
        }
        return results;
    }
}
