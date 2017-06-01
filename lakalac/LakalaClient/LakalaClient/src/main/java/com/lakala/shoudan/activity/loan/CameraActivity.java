package com.lakala.shoudan.activity.loan;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.Text;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.github.lzyzsd.jsbridge.TakePicture;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.component.SharePopupWindow;
import com.lakala.ui.component.NavigationBar;
import com.sensetime.library.finance.liveness.LivenessCode;
import com.sensetime.library.finance.liveness.MotionLivenessApi;
import com.sensetime.sample.core.LivenessActivity;
//import com.sensetime.stlivenesslibrary.ui.CameraOverlapFragment;
//import com.sensetime.stlivenesslibrary.ui.LivenessActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2016/12/23.
 */
public class CameraActivity extends AppBaseActivity{

    private static CallBackFunction callBackFunction;
    private BridgeWebView webView;
    private String mUrl;
    private Button button;
    private Boolean netWork_page = true;
    private String httpString = "http://s";
    String storageFolder = Utils.storageFolder;

    int RESULT_CODE = 0;
    ValueCallback<Uri> mUploadMessage;
    public static final int STATE_START = 0;
    public static final int STATE_RESULT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mUrl = getIntent().getStringExtra("litan");
        initView();

        TextView backButton = navigationBar.getBackButton();
        backButton.setCompoundDrawablePadding(
                (int)context.getResources().getDimension(R.dimen.dimen_10));
        backButton.setText("返回");
        backButton.setTextColor(
                getResources().getColorStateList(R.color.action_text_color_selector));
        navigationBar.setActionBtnEnabled(false);
        navigationBar.setActionBtnVisibility(View.GONE);
        navigationBar.getNavClose().setTextColor(getResources().getColorStateList(R.color.action_text_color_selector));
        navigationBar.getNavClose().setVisibility(View.VISIBLE);
        navigationBar.getNavClose().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
//                startLivenessActivity();
            }
        });
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    onBackPressed();
//                    startLivenessActivity();
                }
            }
        });
        String source=getIntent().getStringExtra("source");
        if(!TextUtils.isEmpty(source)&&source.equals("TNH")){
            navigationBar.setTitle("替你还");
        }else{
            navigationBar.setTitle("易分期");
        }

    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else {
            finish();
        }
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void initView() {
        webView = (BridgeWebView) findViewById(R.id.mWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setDefaultHandler(new DefaultHandler());
        webView.loadUrl(mUrl);
        webView.registerHandler("submitFromWeb", new TakePicture());//相机
        //添加WebChromeClient
        webView.setWebChromeClient(new CustomWebChromeClient() {
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType, String capture) {
                this.openFileChooser(uploadMsg);
            }

            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType) {
                this.openFileChooser(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                Intent chooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
                chooserIntent.setType("image/*");
                startActivityForResult(chooserIntent, 0);
            }
        });

        webView.addJavascriptInterface(new JsHandler(this),JsHandler.TAG);
    }


    //将当前页面url与起始url进行对比   实现物理返回键按下时依次返回
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (webView.getUrl().equals(mUrl)) {
                    finish();
                } else {
                    webView.goBack();
                }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public class CustomWebChromeClient extends WebChromeClient {

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            return super.onJsConfirm(view, url, message, result);
        }

    }

    public void startLivenessActivity() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        boolean soundNotice = sp.getBoolean(Constants.NOTICE, true);
        Intent intent = new Intent(this, LivenessActivity.class);
        intent.putExtra(LivenessActivity.EXTRA_DIFFICULTY, Settings.INSTANCE.getDifficulty(this));
        intent.putExtra(LivenessActivity.EXTRA_VOICE, soundNotice);
        intent.putExtra(LivenessActivity.EXTRA_SEQUENCES, Settings.INSTANCE.getSequencesInt(this));
        this.startActivityForResult(intent,
                0);// No need to set requestCode.
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode) {
            case RESULT_OK:
                byte[] imageData = MotionLivenessApi.getInstance().getLastDetectImages().get(0);
                String url=base64(BitmapFactory.decodeByteArray(imageData, 0, imageData.length));
                webView.loadUrl("javascript:kaolazhengxin.faceRecongnitonCallBack('"+"ss"+"','"+"00"+"','"+url+"')");
                break;
            case 111:
                if( mUploadMessage == null)
                    return;
                Uri result = data == null || resultCode != RESULT_OK ? null
                        : data.getData();
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
                break;
            default:
                if(!TextUtils.isEmpty(showErrorMessage(LivenessActivity.EXTRA_MESSAGE))){
                    ToastUtil.toast(CameraActivity.this, showErrorMessage(LivenessActivity.EXTRA_MESSAGE));
                }
                break;
        }

    }

    private String showErrorMessage(String message) {
        if (TextUtils.isEmpty(message)) {
            return "";
        }
        String messageCode = "";
        if (LivenessActivity.MESSAGE_CANCELED.equals(message)) {
            messageCode = "活体检测已取消";
        } else if (LivenessActivity.MESSAGE_ERROR_CAMERA.equals(message)) {
            messageCode = "初始化相机失败";
        } else if (LivenessActivity.MESSAGE_ERROR_NO_PERMISSIONS.equals(message)) {
            messageCode = "权限检测失败，请检查应用权限设置";
        } else if (LivenessActivity.MESSAGE_ERROR_TIMEOUT.equals(message)) {
            messageCode = "检测超时，请重试一次";
        } else if(LivenessActivity.MESSAGE_ACTION_OVER.equals(message)) {
            messageCode = "动作幅度过大，请保持人脸在屏幕中央，重试一次";
        } else if (LivenessCode.ERROR_MODEL_FILE_NOT_FOUND.name().equals(message)) {
            messageCode = "模型文件不存在";
        } else if (LivenessCode.ERROR_LICENSE_FILE_NOT_FOUND.name().equals(message)) {
            messageCode = "授权文件不存在";
        } else if (LivenessCode.ERROR_CHECK_CONFIG_FAIL.name().equals(message)) {
            messageCode = "启动配置错误";
        } else if (LivenessCode.ERROR_CHECK_LICENSE_FAIL.name().equals(message)) {
            messageCode = "未通过授权验证";
        } else if (LivenessCode.ERROR_CHECK_MODEL_FAIL.name().equals(message)) {
            messageCode = "模型文件错误";
        } else if (LivenessCode.ERROR_LICENSE_EXPIRE.name().equals(message)) {
            messageCode = "授权文件过期";
        } else if (LivenessCode.ERROR_LICENSE_PACKAGE_NAME_MISMATCH.name().equals(message)) {
            messageCode = "绑定包名错误";
        } else if (LivenessCode.ERROR_WRONG_STATE.name().equals(message)) {
            messageCode = "错误的方法状态调用";
        }
        return messageCode;
    }

    public void goCamera(){
//        if(Build.VERSION.SDK_INT >= 23){
//            List<String> permissions = null;
//            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
//                    PackageManager.PERMISSION_GRANTED) {
//                permissions = new ArrayList<>();
//                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            }
//            if (checkSelfPermission(Manifest.permission.CAMERA) !=
//                    PackageManager.PERMISSION_GRANTED) {
//                if (permissions == null) {
//                    permissions = new ArrayList<>();
//                }
//                permissions.add(Manifest.permission.CAMERA);
//            }
//            if (permissions == null) {
//                startLivenessActivity();
//            } else {
//                String[] permissionArray = new String[permissions.size()];
//                permissions.toArray(permissionArray);
//                // Request the permission. The result will be received
//                // in onRequestPermissionResult()
//                requestPermissions(permissionArray, 0);
//            }
//        } else {
//            startLivenessActivity();
//        }
        startLivenessActivity();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == 0) {
//            // Request for WRITE_EXTERNAL_STORAGE permission.
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startLivenessActivity();
//            } else {
//                // Permission request was denied.
//                Toast.makeText(CameraActivity.this, "权限检测失败，请检查应用权限设置", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    //将bitmap转换成Base64
    public static String base64(Bitmap bitmap){
        String result;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        result = Base64.encodeToString(bytes, Base64.NO_WRAP);
        return result;
    }
}
