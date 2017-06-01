package com.lakala.shoudan.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.nativeplugin.CameraController;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.component.CallerCameraPopWindow;
import com.lakala.ui.module.IPhoneStylePopupWindow;

/**
 *
 * Created by More on 15/12/2.
 *
 * 调用相机的BaseActivity 通过点击事件将Image path 关联起来
 *
 */
public abstract class CallCameraActivity extends AppBaseActivity {

    private CameraController cameraController;
    private CallerCameraPopWindow callerCameraPopWindow;
    protected int preClickId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraController = new CameraController(this, cameraCallListener);
        callerCameraPopWindow =new CallerCameraPopWindow(this,itemClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        cameraController.handleActivityResult(requestCode,resultCode, data);
        ApplicationEx.getInstance().getUser();
        ApplicationEx.getInstance().getSession();
        ApplicationEx.getInstance().getUser().getMerchantInfo();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private CameraController.CameraCallListener cameraCallListener = new CameraController.CameraCallListener() {
        @Override
        public void onSucceed(CameraController.CameraAction cameraAction, String filePath) {
            CallCameraActivity.this.onCameraRequestSucceed(preClickId, filePath);
        }

        @Override
        public void onFailed(CameraController.CameraAction cameraAction) {
            CallCameraActivity.this.onCameraRequestFailed();
        }
    };

    private IPhoneStylePopupWindow.ItemClickListener itemClickListener = new IPhoneStylePopupWindow.ItemClickListener() {
        @Override
        public void onItemClick(String itemName) {
            if("用户相册".equals(itemName)){
                startActionPick(preClickId);
            }else{
                startActionCapture(preClickId);
            }
            callerCameraPopWindow.dismiss();
        }
    };

    /**
     *
     * @param clickId 为点击呼出pop window对应的view, 会在对应图片返回接口中返回对应id
     */
    protected void showCallCameraPopWindow(int clickId, View parent){
        preClickId = clickId;
        callerCameraPopWindow.show(parent);
    }

    /**
     *
     * @param clickId 为点击呼出pop window对应的view, 会在对应图片返回接口中返回对应id
     */
    protected void showCallCameraPopWindow(int clickId){
        preClickId = clickId;
        callerCameraPopWindow.show(findViewById(R.id.base_container));
    }

    protected abstract void onCameraRequestFailed();

    protected abstract void onCameraRequestSucceed(int clickId, String picFilePath);

    private int cropX = 4;
    private int cropY = 4;

    protected void startActionCapture(int clickID){
        preClickId = clickID;
        cameraController.startActionImageCapture(cropX, cropY);
    }

    protected void startActionPick(int clickId){
        preClickId = clickId;
        cameraController.startActionPick(cropX,cropY);
    }


}
