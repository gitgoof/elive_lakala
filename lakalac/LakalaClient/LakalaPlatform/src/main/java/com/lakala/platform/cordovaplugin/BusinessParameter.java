package com.lakala.platform.cordovaplugin;


import com.lakala.core.cordova.cordova.CallbackContext;
import com.lakala.core.cordova.cordova.CordovaPlugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by wangchao api on 14-1-14.
 */
public class BusinessParameter extends CordovaPlugin {

    //发送action key
    public static final String SEND_ACTION        = "send_action";
    //发送requestCode key
    public static final String SEND_REQUEST_CODE  = "send_request_code";
    //发送data key
    public static final String SEND_DATA          = "send_data";

    //需要执行的action
    private static final String ACTION_BUSINESS       = "getAction";
    private static final String ACTION_DATA           = "getData";
    private static final String ACTION_GET_PAGE_DATA  = "getPageData";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals(ACTION_BUSINESS)) {
            return getActionId(callbackContext);
        }
        if (action.equals(ACTION_DATA)) {
            return getData(callbackContext);
        }
            return action.equals(ACTION_GET_PAGE_DATA) && getPageData(callbackContext);
    }

    /**
     * 获取 action 信息,以及数据
     */
    private boolean getActionId(CallbackContext callbackContext) throws JSONException {
        JSONObject object = new JSONObject();
        JSONObject data   = (JSONObject)this.cordova.handleData(SEND_DATA);
        object.put("parameter", data);
        callbackContext.success(object);
        return true;
    }

    /**
     * 获取数据
     */
    private boolean getData(CallbackContext callbackContext) throws JSONException {
        JSONObject data = (JSONObject)this.cordova.handleData(SEND_DATA);
        callbackContext.success(data);
        return true;
    }

    /**
     * 获取当前的页面的参数
     *
     * @param callbackContext 插件回调上下文
     * @return 是否执行成功
     * @throws org.json.JSONException
     */
    private boolean getPageData(CallbackContext callbackContext) throws JSONException{
        JSONObject object = new JSONObject();
        object.put("requestCode", this.cordova.handleData(SEND_REQUEST_CODE));
        object.put("action", this.cordova.handleData(SEND_ACTION));
        JSONObject data = (JSONObject)this.cordova.handleData(SEND_DATA);
        object.put("parameter", data);
        callbackContext.success(object);
        return true;
    }
}
