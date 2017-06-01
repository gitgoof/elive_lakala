package com.lakala.shoudan.activity.shoudan.barcodecollection;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dtr.zbar.build.ZBarDecoder;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.coupon.activity.CouponCollectionsActivity;
import com.lakala.shoudan.activity.payment.BillSignatureActivity;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.shoudan.activity.shoudan.barcodecollection.revocation.ScancodeCancelActivity;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.camere.CameraManager;
import com.lakala.shoudan.common.camere.CameraPreview;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.component.VerticalListView;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
/**
 *
 */
public class CaptureActivity extends AppBaseActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private CameraManager mCameraManager;

    private String scanRlt;
    private FrameLayout scanPreview;
    private RelativeLayout scanContainer;
    private RelativeLayout scanCropView;
    private ImageView scanLine;
    private FrameLayout capturePreview;

    private Rect mCropRect = null;
    private boolean barcodeScanned = false;
    private boolean previewing = true;
    private BarCodeCollectionTransInfo barcodeInfo;
    private static final String DELAYOUT_CODE = "0100C0";
    public final static int RESULT_CODE = 0X123;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cap);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        barcodeInfo = (BarCodeCollectionTransInfo) getIntent().getSerializableExtra(Constants.IntentKey.TRANS_INFO);
        BusinessLauncher.getInstance().getActivities().add(this);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle(barcodeInfo.getTransTitle());
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back){
                    finish();
                }
            }
        });

        scanPreview = (FrameLayout) findViewById(R.id.capture_preview);
        scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
        scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
        capturePreview = (FrameLayout) findViewById(R.id.capture_preview);

        autoFocusHandler = new Handler();
        mCameraManager = new CameraManager(this);
        int retryCnt = 3;
        while (retryCnt>=0){
            try {
                mCameraManager.openDriver();
                break;
            } catch (Exception e) {
                e.printStackTrace();
                if(retryCnt == 0){
                    setResult(RESULT_CODE);
                    finish();
                    return;
                }
            }
            retryCnt--;
        }

        mCamera = mCameraManager.getCamera();
        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        scanPreview.addView(mPreview);

        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.85f);
        animation.setDuration(3000);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.REVERSE);
        scanLine.startAnimation(animation);
    }


    private void addEvents() {

//        capturePreview.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                barcodeScanned = false;
//                mCamera.setPreviewCallback(previewCb);
//                mCamera.startPreview();
//                previewing = true;
//                mCamera.autoFocus(autoFocusCB);
//                return false;
//            }
//        });
//		scanRestart.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				if (barcodeScanned) {
//					barcodeScanned = false;
////					scanResult.setText("Scanning...");
//					mCamera.setPreviewCallback(previewCb);
//					mCamera.startPreview();
//					previewing = true;
//					mCamera.autoFocus(autoFocusCB);
//				}
//			}
//		});
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Size size = camera.getParameters().getPreviewSize();

            // 这里需要将获取的data翻转一下，因为相机默认拿的的横屏的数据
            byte[] rotatedData = new byte[data.length];
            for (int y = 0; y < size.height; y++) {
                for (int x = 0; x < size.width; x++)
                    rotatedData[x * size.height + size.height - y - 1] = data[x + y * size.width];
            }

            // 宽高也要调整
            int tmp = size.width;
            size.width = size.height;
            size.height = tmp;

            initCrop();
            ZBarDecoder zBarDecoder = new ZBarDecoder();
            String result = zBarDecoder.decodeCrop(rotatedData, size.width, size.height, mCropRect.left, mCropRect.top, mCropRect.width(), mCropRect.height());

            if (!TextUtils.isEmpty(result)) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                scanRlt = result;
                barcodeScanned = true;

                //对交易类型进行判断
                if (barcodeInfo.getTransTitle().equals("扫码收款")){
                    startCollection(scanRlt);
                }else {
                    Intent intent = new Intent();
                    intent.putExtra("code",scanRlt);
                    if (barcodeInfo.getTransTitle().equals("扫描代金券码")){
                        setResult(CouponCollectionsActivity.RESULT_OK,intent);
                    }else{
                        setResult(ScancodeCancelActivity.RESULT_OK, intent);
                    }
                    finish();
                }
            }
        }
    };

    /**
     * 1.扫码
     2.送交易
     3.服务端返回  C0 跳5
     4.服务端返回  非C0 或 超时 跳6
     5.调用2.20.2去查询状态
     6.返回结果
     * @param code
     */
    private void startCollection(final String code){


        showProgressWithNoMsg();
        //设置超时时间，默认60S，每次查询扫码开通情况时调用通用接口去请求超时时间、重发次数、重发间隔三个参数设置到Paramters
        ShoudanService.getInstance().scancodeCollection(barcodeInfo.getAmount(), code, Parameters.timeout * 1000, new ServiceResultCallback() {

            JSONObject jsonObject;
            @Override
            public void onSuccess(ResultServices resultServices) {

                try {
                    jsonObject = new JSONObject(resultServices.retData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String type = jsonObject.optString("scanCodeType");
                    if (type.equals(BarCodeCollectionTransInfo.WECHAT)){
                        barcodeInfo.setType(VerticalListView.IconType.WECHAT);
                    }else if (type.equals(BarCodeCollectionTransInfo.ALIPAY)){
                        barcodeInfo.setType(VerticalListView.IconType.ALIPAY);
                    }else if (type.equals(BarCodeCollectionTransInfo.BAIDUPAY)){
                        barcodeInfo.setType(VerticalListView.IconType.BAIDUPAY);
                    }else if (type.equals(BarCodeCollectionTransInfo.UNIONPAY)){
                        barcodeInfo.setType(VerticalListView.IconType.UNIONPAY);
                    }else {
                        barcodeInfo.setType(VerticalListView.IconType.UNKNOWN);
                    }

                //判断服务端返回状态000000或者0100C0来决定是否轮询,PS:交易失败返回码前面补01
                if (resultServices.isRetCodeSuccess()) {
                    barcodeInfo.setTransResult(TransResult.SUCCESS);
                    String sid = jsonObject.optString("sid");
                    barcodeInfo.setSid(sid);
                    handleCollectionSuccessResult();
                    hideProgressDialog();
                } else if ("0100C0".equals(resultServices.retCode)) {
                    queryBarCodeCollectionResult(jsonObject.optString("sid"));
                } else {
                    barcodeInfo.setTransResult(TransResult.FAILED);
                    barcodeInfo.setMsg(resultServices.retMsg);
                    handleCollectionResult();
                    hideProgressDialog();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                barcodeInfo.setTransResult(TransResult.TIMEOUT);
                barcodeInfo.setType(VerticalListView.IconType.UNKNOWN);
                barcodeInfo.setMsg("网络异常");
                handleCollectionResult();
                hideProgressDialog();
            }
        });

    }

    private void handleCollectionSuccessResult(){
        Intent intent = new Intent(context, BillSignatureActivity.class);
        intent.putExtra(ConstKey.TRANS_INFO,barcodeInfo);
        startActivity(intent);
    }

    private void handleCollectionResult(){
        forwardResultActivity();
    }

    private void scancodeQuery(final String sid){

        if(gotCertainResult  || repeatCount <=0){
            handleCollectionResult();
            return;
        }

        repeatCount--;

        ShoudanService.getInstance().scancodeQuery(sid, new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if(resultServices.isRetCodeSuccess()){

                    gotCertainResult = true;
                    barcodeInfo.setTransResult(TransResult.SUCCESS);
                    handleCollectionSuccessResult();
                    hideProgressDialog();
                }else if(DELAYOUT_CODE.equals(resultServices.retCode)){
                    gotCertainResult = false;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scancodeQuery(sid);
                        }
                    }, Parameters.repeatTime * 1000);

                }else{

                    gotCertainResult = true;
                    barcodeInfo.setTransResult(TransResult.FAILED);
                    barcodeInfo.setMsg(resultServices.retMsg);
                    handleCollectionResult();
                    hideProgressDialog();

                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                gotCertainResult = true;
                barcodeInfo.setTransResult(TransResult.TIMEOUT);
                handleCollectionResult();
            }
        });
    }

    private boolean gotCertainResult = false;
    int repeatCount = Parameters.repeatCount;

    private void queryBarCodeCollectionResult(String sid) {

        gotCertainResult = false;

        /**
         * 重试次数
         */
        repeatCount = Parameters.repeatCount;

        barcodeInfo.setTransResult(TransResult.TIMEOUT);

        //服务端配置连接超时,需要使用新的HttpClient

        scancodeQuery(sid);



    }
    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    private void forwardResultActivity(){
        startActivity(new Intent(CaptureActivity.this, BarcodeResultActivity.class).putExtra(Constants.IntentKey.TRANS_INFO, barcodeInfo));
        finish();
    }
    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int cameraWidth = mCameraManager.getCameraResolution().y;
        int cameraHeight = mCameraManager.getCameraResolution().x;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
