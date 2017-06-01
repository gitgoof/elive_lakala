
package com.lakala.platform.cordovaplugin;

import android.content.Intent;
import android.view.View;

import com.lakala.core.cordova.cordova.AuthenticationToken;
import com.lakala.core.cordova.cordova.CallbackContext;
import com.lakala.core.cordova.cordova.CordovaArgs;
import com.lakala.core.cordova.cordova.CordovaPlugin;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.R;
import com.lakala.platform.common.ApplicationEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 相机插件（相册获取照片，拍照）
 */
public class CameraPlugin extends CordovaPlugin {

    private static final String ACTION_PHONE_NUMBER = "picker";
    private static final String ACTION_TAKE_PHOTO = "takePhoto";
    private static final String ACTION_SELECT_PHOTO = "selectPic";


    private CallbackContext callbackContext;
    private UserPhotoSetting userPhotoSetting;
    private OnPhoto onPhoto;

//    private List<String> items = new ArrayList<String>();
//    private List<View.OnClickListener> listeners = new ArrayList<View.OnClickListener>();

    public interface OnPhoto{
        void success(String path);
        void error();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equalsIgnoreCase(ACTION_PHONE_NUMBER)) {
            return getPhoto(args, callbackContext);
        }
        return false;
    }


    /**
     * 获取图片
     * @param callbackContext
     * @return
     */
    private boolean getPhoto(JSONArray args,CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        String LoginId = ApplicationEx.getInstance().getUser().getLoginName();
        JSONObject data;
        if (args == null){
            data = new JSONObject();
        }else {
            data = args.getJSONObject(0);
        }

        boolean ifSelectPhoto = data.optBoolean(ACTION_SELECT_PHOTO, true);
        boolean ifTakePhoto = data.optBoolean(ACTION_TAKE_PHOTO, true);

        userPhotoSetting = UserPhotoSetting.getInstance(this.cordova, this, R.id.base_container, LoginId);//LoginId);
        UserPhotoSetting.CropParameters cropParameters = new UserPhotoSetting.CropParameters();
        cropParameters.quality = new BigDecimal(data.optString("quality", "1"));
        cropParameters.aspectX = data.optInt("aspectX", 2);
        cropParameters.aspectY = data.optInt("aspectY",1);
        userPhotoSetting.starSetUserPhotos(ifSelectPhoto, ifTakePhoto, cropParameters);
        File file;
        this.cordova.getActivity().getFilesDir().getAbsolutePath();

        return true;
    }


    /**
     * 提供给客户端获取照片文件路径的方法
     * @return
     */
    public void getPhoto(OnPhoto onPhoto) throws JSONException {
//        if (items != null && listeners != null){
//            this.items = items;
//            this.listeners = listeners;
//        }
        this.onPhoto = onPhoto;
        this.getPhoto(null,null);
    }
    /**
     * 获取图片回调
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

            if (userPhotoSetting != null) {
                HashMap<String,String> results= userPhotoSetting.onActivityForResult(requestCode, resultCode, intent);
                if (requestCode == UserPhotoSetting.REQUEST_CODE) {//裁剪返回
                    if (results!=null && results.size()>0) {
                        String imageBase64 = results.get("data");
                        String path        = results.get("path");
                        LogUtil.print("imageBase64====" + imageBase64);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("data", "data:image/jpeg;base64," + imageBase64);
                            jsonObject.put("path",path);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        LogUtil.print("jsonObject====" + jsonObject.toString());
                        if (callbackContext != null){
                            callbackContext.success(jsonObject);
                        }else {
                            onPhoto.success(path);
                        }
                    } else {
                        if (callbackContext != null){
                            callbackContext.error("");
                        }
                        onPhoto.error();
                    }
                }
            }
            super.onActivityResult(requestCode, resultCode, intent);
        }

}
