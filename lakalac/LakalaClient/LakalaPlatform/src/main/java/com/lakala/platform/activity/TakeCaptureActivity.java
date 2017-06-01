package com.lakala.platform.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.core.launcher.ActivityLauncher;
import com.lakala.library.encryption.Digest;
import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.R;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ActivityResult;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TakeCaptureActivity extends BaseActionBarActivity {


    private TextView againText, useText;
    private ImageView takeImage;
    private Camera mCamera;
    private CameraPreview mPreview;
    private boolean openAutoFocus = true;

    private static final int FRONT = 1;//前置摄像头标记
    private static final int BACK = 0;//后置摄像头标记
    private int currentCameraId = 0;//当前打开的摄像头标记
    private int cameraCount = 0;

    /* 将照下来的图片保存在此 */
    private String fileName = "";
    private String filePath = "";
    public static final String SAVE_PHOTO_NAME = "save_photo_name";
    /**
     * 是否拍摄头像
     */
    private static final String SHOWAVATAR = "showAvatar";
    /**
     * 是否拍摄头像
     */
    private boolean isShowAvatar = false;
    private String base64String = "";
    private boolean isFromWeb = false;

    private String imageAbsoluteFilePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_capture);
        initUI();
    }

    private NavigationBar.OnNavBarClickListener onNavBarClickListener = new NavigationBar.OnNavBarClickListener() {
        @Override
        public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
            if (navBarItem == NavigationBar.NavigationBarItem.back) {
                finish();
            }
            if (navBarItem == NavigationBar.NavigationBarItem.action) {
                try {
                    switchPhotoOpen(false);
                    changeCamera();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void initUI() {
        navigationBar.setTitle(R.string.plat_take_photo);
        navigationBar.setActionBtnText(R.string.switch_phone_camera);
        navigationBar.setOnNavBarClickListener(onNavBarClickListener);
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        if (cameraCount > 1) {
            navigationBar.setActionBtnVisibility(View.VISIBLE);
        } else {
            navigationBar.setActionBtnVisibility(View.GONE);
        }

        if (getIntent() != null){
            String title = getIntent().getStringExtra(ActivityLauncher.INTENT_PARAM_KEY_TITLE);
            if (title != null){
                navigationBar.setTitle(title);
            }
        }

        initCamera();

        againText = (TextView) findViewById(R.id.id_again);
        useText = (TextView) findViewById(R.id.id_use);
        takeImage = (ImageView) findViewById(R.id.id_take);

        againText.setOnClickListener(this);
        useText.setOnClickListener(this);
        takeImage.setOnClickListener(this);
        User user = ApplicationEx.getInstance().getUser();
        fileName = Digest.md5("observePhoto" + user.getLoginName() + ".jpg");

        this.filePath = getCacheDir().getAbsolutePath() + File.separator + "lakala" + File.separator + "img";

        Intent data = getIntent();
        if (data != null) {
            String name = getIntent().getStringExtra(SAVE_PHOTO_NAME);
            if (StringUtil.isNotEmpty(name)) {
                this.fileName = name;
            } else {
                fileName = System.currentTimeMillis() + ".jpg";
            }
//TODO: 这个地方需要调整，不能用 BUNDLE_DATA_KEY 方式判断是否从 Web 来的
//            String obj = data.getStringExtra(BusinessLauncherPlugin.BUNDLE_DATA_KEY);
//            if (obj != null) {
//                isFromWeb = true;
//                try {
//                    isShowAvatar = new JSONObject(obj).optBoolean(SHOWAVATAR, false);
//                } catch (Exception e){
//                }
//            }

            if (isShowAvatar) {
                findViewById(R.id.id_photo_marker).setVisibility(View.VISIBLE);
            }
        }

        switchPhotoOpen(false);

    }

    private void initCamera() {
        // 检查设备是否支持摄像头
        if (checkCameraHardware(this) == false) {
            ToastUtil.toast(this, R.string.device_not_support_photo);
            return;
        }
        // 创建Camera实例
        mCamera = openCamera(BACK);
        setCameraParams(mCamera);
        // 创建Preview view并将其设为activity中的内容
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    /**
     * 切换前后摄像头切换view显示
     *
     * @param isPhotoDone
     */
    private void switchPhotoOpen(boolean isPhotoDone) {
        if (isPhotoDone) {
            againText.setVisibility(View.VISIBLE);
            useText.setVisibility(View.VISIBLE);
            takeImage.setVisibility(View.INVISIBLE);
            navigationBar.setActionBtnVisibility(View.GONE);
        } else {
            againText.setVisibility(View.GONE);
            useText.setVisibility(View.GONE);
            takeImage.setVisibility(View.VISIBLE);
            if (cameraCount > 1)
                navigationBar.setActionBtnVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onViewClick(View view) {
        super.onViewClick(view);
        if (view.getId() == R.id.id_again) {//重拍
            switchPhotoOpen(false);
            openAutoFocus = false;
            mCamera.startPreview();
        } else if (view.getId() == R.id.id_take) {//拍照
            openAutoFocus = true;
            takeFocusedPicture();
        } else if (view.getId() == R.id.id_use) {//使用照片
            if (isFromWeb) {//web页面过来
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("data", "data:image/jpeg;base64," + base64String);
                    jsonObject.put("path", imageAbsoluteFilePath);
                    ActivityResult.setResult(TakeCaptureActivity.this, jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @SuppressLint("NewApi")
    private Camera openCamera(int type) {
        int frontIndex = -1;
        int backIndex = -1;
        CameraInfo info = new CameraInfo();
        for (int cameraIndex = 0; cameraIndex < cameraCount; cameraIndex++) {
            Camera.getCameraInfo(cameraIndex, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                frontIndex = cameraIndex;
            } else if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                backIndex = cameraIndex;
            }
        }
        currentCameraId = type;
        if (type == FRONT && frontIndex != -1) {
            return Camera.open(frontIndex);
        } else if (type == BACK && backIndex != -1) {
            return Camera.open(backIndex);
        }
        return null;
    }

    /**
     * 切换摄像头
     *
     * @throws java.io.IOException
     */
    private void changeCamera() throws IOException {
        mCamera.stopPreview();
        mCamera.release();
        if (currentCameraId == FRONT) {
            mCamera = openCamera(BACK);
        } else if (currentCameraId == BACK) {
            mCamera = openCamera(FRONT);
        }
        setCameraParams(mCamera);
        mPreview.setCamera(mCamera);
        mCamera.startPreview();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = openCamera(currentCameraId);
            setCameraParams(mCamera);
            mPreview.setCamera(mCamera);
            mCamera.startPreview();
            switchPhotoOpen(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    // 先对焦后拍照
    public void takeFocusedPicture() {
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (camera != null && openAutoFocus) {
                    mCamera.takePicture(null, null, mPicture);
                }
            }
        });
    }

    // 设置摄像头参数
    protected void setCameraParams(Camera camera) {
        camera.setDisplayOrientation(90);
        Camera.Parameters params = camera.getParameters();
        if (currentCameraId == FRONT) {
            params.setRotation(270);
        } else if (currentCameraId == BACK) {
            params.setRotation(90);
        }
        camera.setParameters(params);
    }

    // 检查设备是否提供摄像头
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // 摄像头存在
            return true;
        } else {
            // 摄像头不存在
            return false;
        }
    }

    // 基本的摄像头预览类
    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

        private SurfaceHolder mHolder;
        private Camera mCamera;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;
            // 安装一个SurfaceHolder.Callback，
            // 这样创建和销毁底层surface时能够获得通知。
            mHolder = getHolder();
            mHolder.addCallback(this);
            // 已过期的设置，但版本低于3.0的Android还需要
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void startPreview() {
            mCamera.startPreview();
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // surface已被创建，现在把预览画面的位置通知摄像头
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            releaseCamera();
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // 如果预览无法更改或旋转，注意此处的事件
            // 确保在缩放或重排时停止预览
            if (mHolder.getSurface() == null) {
                // 预览surface不存在
                return;
            }
            // 更改时停止预览
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                // 忽略：试图停止不存在的预览
            }
            // 在此进行缩放、旋转和重新组织格式
            // 以新的设置启动预
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.setDisplayOrientation(90);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void setCamera(Camera camera) {
            try {
                mCamera = camera;
                camera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private PictureCallback mPicture = new PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            switchPhotoOpen(true);
            new Thread() {
                @Override
                public void run() {
                    saveImage(data);
                }
            }.start();

        }
    };

    /**
     * 释放摄像头
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * @param data
     */
    private void saveImage(byte[] data) {
        FileOutputStream fos = null;
        try {
            File dirFile = new File(filePath);
            if (!dirFile.exists()) dirFile.mkdirs();
            File picFile = new File(filePath, fileName);
            if (!picFile.exists()) picFile.createNewFile();
            fos = new FileOutputStream(picFile);
            fos.write(data);
            fos.close();
            int version = Build.VERSION.SDK_INT;
            if (version >= 13) {//系统版本大于4.0，拍照后代码关闭预览
                mCamera.stopPreview();
            }
            data = compressFile(picFile.getAbsolutePath());
            base64String = Base64.encodeToString(data, Base64.DEFAULT);
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 暂时按照80 x 80做，后期代码优化一下
     * 解析图片
     *
     * @param path
     * @return
     */
    private byte[] compressFile(String path) throws Exception {
        byte[] bytes = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        options.outHeight = 80;
        options.outWidth = 80;
        scanBitmap = BitmapFactory.decodeFile(path, options);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scanBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        bytes = baos.toByteArray();
        File picFile = new File(filePath, fileName);
        if (picFile.exists()) picFile.delete();
        picFile.createNewFile();
        FileOutputStream fos = null;
        fos = new FileOutputStream(picFile);
        fos.write(bytes);
        fos.close();
        imageAbsoluteFilePath = picFile.getAbsolutePath();
        return bytes;
    }
}

