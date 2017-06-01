package com.lakala.platform.cordovaplugin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.lakala.core.cordova.cordova.CallbackContext;
import com.lakala.core.cordova.cordova.CordovaActivity;
import com.lakala.core.cordova.cordova.CordovaPlugin;
import com.lakala.library.util.TypeConvertionUtil;
import com.lakala.platform.launcher.BusinessLauncher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vinchaos api on 14-1-14.
 */
public class BusinessLauncherPlugin extends CordovaPlugin {
    //result状态码
    private static final String RESULT_STATUS               = "status";
    //result DATA
    private static final String RESULT_DATA                 = "data";
    //requestCode
    private static final String REQUEST_STATUS              = "requestCode";

    //action
    private static final String START_ACTIVITY              = "startPage";
    private static final String START_ACTIVITY_FOR_RESULT   = "startPageForResult";
    private static final String SET_RESULT                  = "setResult";
    private static final String RETURN_ACTIVITY             = "returnPage";
    private static final String OPEN_WITH_NAVIGATI_ON       = "openWithNavigation";
    private static final String OpEN_TAG_WITH_SINGLE_TOP    = "openTagWithSingleTop";


    //请求码
    private int REQUEST_CODE = 0;
    private CallbackContext callbackContext;
    private FragmentActivity activity;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.activity = (FragmentActivity) this.cordova.getActivity();
        this.callbackContext = callbackContext;
        if (action.equals(START_ACTIVITY)) {
            return startActivity(args, callbackContext);
        }
        if (action.equals(START_ACTIVITY_FOR_RESULT)) {
            return startActivityForResult(args);
        }
        if (action.equals(SET_RESULT)) {
            return setResult(args);
        }
        if (action.equals(RETURN_ACTIVITY)) {
            return returnActivity(args);
        }
        if (action.equals(OPEN_WITH_NAVIGATI_ON)) {
            return openWithNavigation(args);
        }
        if(action.equals(OpEN_TAG_WITH_SINGLE_TOP)){
            return openTagWithSingleTop(args);
        }
        return false;
    }

    /**
     * startActivity
     */
    private boolean startActivity(JSONArray args, CallbackContext callbackContext) {
        String      id        = args.optString(0);
        JSONObject  data      = args.optJSONObject(1);
        boolean     isFinish  = args.optBoolean(2, false);

        Intent intent = new Intent();
        if (data != null){
            Bundle bundle = TypeConvertionUtil.json2Bundle(data);
            intent.putExtras(bundle);
        }
        BusinessLauncher.getInstance().start(id,intent);

        if(isFinish){
            this.cordova.getActivity().finish();
        }

        return true;
    }

    /**
     * startActivityForResult
     */
    private boolean startActivityForResult(JSONArray args) {
        String id = args.optString(0);
        JSONObject data = args.optJSONObject(1);
        REQUEST_CODE = args.optInt(2, 0);

        Intent intent = new Intent();
        if (data != null){
            Bundle bundle = TypeConvertionUtil.json2Bundle(data);
            intent.putExtras(bundle);
        }
        ((CordovaActivity)this.cordova.getActivity()).setActivityResultCallback(this);
        BusinessLauncher.getInstance().startForResult(id,intent,REQUEST_CODE);
        return true;
    }

    /**
     * web端提供setResult方法
     */
    private boolean setResult(JSONArray args) {
        int status = args.optInt(0);
        JSONObject object = args.optJSONObject(1);

        Bundle bundle = TypeConvertionUtil.json2Bundle(object);
        this.cordova.getActivity().setResult(status, new Intent().putExtras(bundle));
        return true;
    }

    /**
     * 返回Activity
     *
     * @return
     */
    private boolean returnActivity(JSONArray args) {
        int index = args.optInt(0);

        if (index == 0){
            //返回首页
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            BusinessLauncher.getInstance().pop("home",0,null);
        }
        else if (index > 0){
            //弹出指定数量视图
            BusinessLauncher.getInstance().pop(index);
        }
        else{
            //返加首页选项卡上
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("tabIndex",index);
            BusinessLauncher.getInstance().start("home",intent);
        }

        return true;
    }
    /**
     *  跳转业务
     * （主要流程：返回首页并转到指定的选项卡上，然后由首页在打开由Tag 指定的业务）
     */
    private boolean openWithNavigation(JSONArray args) {
        String tag = args.optString(0);
        int    index =args.optInt(3);

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("tabIndex",index);
        intent.putExtra("tag",tag);
        BusinessLauncher.getInstance().start("home",intent);

        return true;
    }

    private boolean openTagWithSingleTop(JSONArray args){
          //OpenChoose 回到开通商户的选择界面
        String tag = args.optString(0);
        int    index =args.optInt(3);

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("tabIndex",index);
        intent.putExtra("tag",tag);
        BusinessLauncher.getInstance().start("OpenChoose",intent);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE) {
            JSONObject object = new JSONObject();
            try {
                Bundle bundle = intent == null ? null : intent.getExtras();
                object.put(RESULT_STATUS, resultCode);
                object.put(RESULT_DATA, TypeConvertionUtil.bundle2Json(bundle));
                object.put(REQUEST_STATUS, requestCode);
                callbackContext.success(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }
}
