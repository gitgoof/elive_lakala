package com.lakala.shoudan.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.util.BitmapUtil;
import com.lakala.shoudan.util.ScreenUtil;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by linmq on 2016/6/23.
 */
public class CustomerCameraActivity extends AppBaseActivity implements SurfaceHolder.Callback {
    private static final String TEMP_FILE = "tempFile";
    private SurfaceView surface;
    private SurfaceHolder holder;
    private Camera camera;
    private boolean isSurfaceDestroyed;
    private String tempFile;
    Runnable autoFocusRun = new Runnable() {
        @Override
        public void run() {
            if (!isSurfaceDestroyed) {
                camera.autoFocus(null);
                if (surface != null) {
                    surface.postDelayed(this, 1000);
                }
            }
        }
    };
    private android.hardware.Camera.PictureCallback jpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap = BitmapUtil.decodeSampleBitmapFromBytes(data, Parameters.screenWidth,
                                                                   Parameters.screenHeight);
            if(isBackCamera){
                bitmap = BitmapUtil.rotaingImageView(degress,bitmap);
            }else {
                bitmap = BitmapUtil.rotaingImageView(cameraOrientation,bitmap);
            }
            File file = new File(tempFile);
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩的流里面
                bos.flush();// 刷新此缓冲区的输出流
                bos.close();// 关闭此输出流并释放与此流有关的所有系统资源
                camera.stopPreview();//关闭预览 处理数据
                bitmap.recycle();
                setResult(RESULT_OK);
                finish();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    private boolean isBackCamera;
    private int cameraOrientation;
    private int degress;

    /**
     *
     * @param fragment
     * @param tempFile
     * @param overlayType 覆盖人影类型  1：身份证，2：结算卡
     */
    public static void startForResult(Fragment fragment, String tempFile,int overlayType){
        Intent intent = new Intent(fragment.getActivity(),CustomerCameraActivity.class);
        intent.putExtra(TEMP_FILE,tempFile);
        intent.putExtra("overlayType",overlayType);
        fragment.startActivityForResult(intent,0x1234);
        if(Build.VERSION.SDK_INT > 5){
            fragment.getActivity()
                    .overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    protected void setDefaultOrientationPortrait() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//拍照过程屏幕一直处于高亮
        //设置手机屏幕朝向，一共有7种
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        setContentView(R.layout.activity_customer_camera);
        navigationBar.setVisibility(View.GONE);
        surface = (SurfaceView) findViewById(R.id.surfaceView);
        holder = surface.getHolder();
        holder.addCallback(this);
        openBackCamera();
        tempFile = getIntent().getStringExtra(TEMP_FILE);
        int overlayType = getIntent().getIntExtra("overlayType", 1);
        ImageView ivOverlay = (ImageView) findViewById(R.id.iv_overlay);
        TextView tvTips = (TextView)findViewById(R.id.tv_tips);
        switch (overlayType){
            case 1:{
                ivOverlay.setBackgroundResource(R.drawable.pic_tips_shooting_id_photos);
                tvTips.setText("请确保本人与身份证拍摄清晰");
                break;
            }
            case 2:{
                ivOverlay.setBackgroundResource(R.drawable.pic_tips_debit_card_photo_shoot);
                tvTips.setText("请确保本人与结算卡拍摄清晰");
                break;
            }
        }
        int frontCameraId = getFrontCameraId();
        if(frontCameraId == -1){
            findViewById(R.id.iv_reverse).setVisibility(View.INVISIBLE);
        }
        findViewById(R.id.iv_reverse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBackCamera = !isBackCamera;
                isSurfaceDestroyed = true;
                camera.stopPreview();
                camera.release();
                camera = null;
                openCamera();
                surfaceCreated(holder);
            }
        });
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.iv_take_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        ScreenUtil.getScrrenWidthAndHeight(this);
    }

    private void openCamera() {
        if(isBackCamera){
            openBackCamera();
        }else {
            openFrontCamera();
        }
    }

    private void openBackCamera() {
        isBackCamera = true;
        int cameraId = getBackCameraId();
        camera = Camera.open(cameraId);
        degress = cameraOrientation;
        camera.setDisplayOrientation(degress);
    }

    private void openFrontCamera() {
        isBackCamera = false;
        camera = Camera.open(getFrontCameraId());
        degress = (360-cameraOrientation)%360;
        camera.setDisplayOrientation(degress);
    }
    private void takePicture() {
        isSurfaceDestroyed = true;
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    Camera.Parameters params = camera.getParameters();
                    params.setPictureFormat(ImageFormat.JPEG);
                    camera.setParameters(params);
                    camera.takePicture(null, null, jpeg);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            isSurfaceDestroyed = true;
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
    private int getBackCameraId(){
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraOrientation = cameraInfo.orientation;
                return i;
            }
        }
        return -1;
    }

    private int getFrontCameraId(){
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraOrientation = cameraInfo.orientation;
                return i;
            }
        }
        return -1;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isSurfaceDestroyed = false;
        if(camera == null){
            openCamera();
        }
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.stopPreview();
        camera.startPreview();
        surface.post(autoFocusRun);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isSurfaceDestroyed = true;
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
}
