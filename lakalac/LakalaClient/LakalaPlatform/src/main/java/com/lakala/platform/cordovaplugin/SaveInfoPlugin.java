package com.lakala.platform.cordovaplugin;

import com.lakala.core.cordova.cordova.CallbackContext;
import com.lakala.core.cordova.cordova.CordovaPlugin;
import com.lakala.core.cordova.cordova.PluginResult;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.LklPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * B端保存，或提取存放在C端配置文件中的数据
 * Created by andy_lv on 2014/4/29.
 */
public class SaveInfoPlugin extends CordovaPlugin {

    /**
     * 与用户有关，key需加loginName作为前缀
     */
    private static final String ACTION_GET_VALUE = "getValue";
    private static final String ACTION_SET_VALUE = "setValue";

    /**
     * 与用户无关
     */
    private static final String ACTION_SET = "set";
    private static final String ACTION_GET = "get";


    //要存储的数据类型
    private enum ValueType {
        INT, BOOLEAN, FLOAT, LONG, STRING
    };

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        //获取当前登录用户手机号码
        User user = ApplicationEx.getInstance().getUser();
        String loginName = "";
        if (null != user) {
            loginName = user.getLoginName();
        }

        JSONObject resultJson = null;
        int length = 0;
        if (args == null) {
            resultJson = new JSONObject();
            resultJson.put("reason", "参数为空");
        } else {
            if (action.equalsIgnoreCase(ACTION_GET_VALUE) ||
                    action.equalsIgnoreCase(ACTION_GET) ||
                    action.equalsIgnoreCase(ACTION_SET)) {
                length = 2;
            } else if (action.equalsIgnoreCase(ACTION_SET_VALUE)) {
                length = 3;
            }
            if (args.length() < length) {
                resultJson = new JSONObject();
                resultJson.put("reason", "参数长度错误");
            }
        }
        if (null != resultJson) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, resultJson);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        }

        LklPreferences lklPreferences = LklPreferences.getInstance();

        if (action.equalsIgnoreCase(ACTION_GET_VALUE)) {
            getValueOfUser(loginName, args, lklPreferences, callbackContext);
        } else if (action.equalsIgnoreCase(ACTION_SET_VALUE)) {
            saveValueOfUser(loginName, args, lklPreferences, callbackContext);
        } else if (action.equalsIgnoreCase(ACTION_GET)) {
            getValue(args, lklPreferences, callbackContext);
        } else if (action.equalsIgnoreCase(ACTION_SET)) {
            saveValue(args, lklPreferences, callbackContext);
        }
        return super.execute(action, args, callbackContext);
    }


    /**
     * 保存与用户有关的数据
     *
     * @param loginName       登录名
     * @param args            参数
     * @param lklPreferences  LklPreferences
     * @param callbackContext CallbackContext
     */
    private boolean saveValueOfUser(String loginName, JSONArray args, LklPreferences lklPreferences,
                                    CallbackContext callbackContext) {
        String key = loginName + "_" + args.optString(0);
        String type = args.optString(1);
        if (type.equalsIgnoreCase(ValueType.INT.name())) {
            lklPreferences.putInt(key, args.optInt(2));
        } else if (type.equalsIgnoreCase(ValueType.BOOLEAN.name())) {
            lklPreferences.putBoolean(key, args.optBoolean(2));
        } else if (type.equalsIgnoreCase(ValueType.FLOAT.name())) {
            lklPreferences.putFloat(key, (float) args.optDouble(2));
        } else if (type.equalsIgnoreCase(ValueType.LONG.name())) {
            lklPreferences.putLong(key, args.optLong(2));
        } else if (type.equalsIgnoreCase(ValueType.STRING.name())) {
            lklPreferences.putString(key, args.optString(2));
        }
        callbackContext.success();
        return true;
    }


    /**
     * 得到与用户有关的数据
     *
     * @param loginName       登录名
     * @param args            参数
     * @param lklPreferences  LklPreferences
     * @param callbackContext CallbackContext
     * @return
     */
    private boolean getValueOfUser(String loginName, JSONArray args, LklPreferences lklPreferences
            , CallbackContext callbackContext) {
        String type = args.optString(1);
        String key = loginName + "_" + args.optString(0);

        PluginResult pluginResult = null;

        if (type.equalsIgnoreCase(ValueType.INT.name())) {

            pluginResult = new PluginResult(PluginResult.Status.OK,
                    lklPreferences.getInt(key));

        } else if (type.equalsIgnoreCase(ValueType.BOOLEAN.name())) {

            pluginResult = new PluginResult(PluginResult.Status.OK,
                    lklPreferences.getBoolean(key));

        } else if (type.equalsIgnoreCase(ValueType.FLOAT.name())) {

            pluginResult = new PluginResult(PluginResult.Status.OK,
                    lklPreferences.getFloat(key, 0f));

        } else if (type.equalsIgnoreCase(ValueType.LONG.name())) {

            pluginResult = new PluginResult(PluginResult.Status.OK,
                    lklPreferences.getLong(key, 0));

        } else if (type.equalsIgnoreCase(ValueType.STRING.name())) {

            pluginResult = new PluginResult(PluginResult.Status.OK,
                    lklPreferences.getString(key));

        }
        if (null == pluginResult) {
            return false;
        }

        callbackContext.sendPluginResult(pluginResult);
        return true;
    }


    /**
     * 保存与用户无关的数据
     *
     * @param args            参数
     * @param lklPreferences  LklPreferences
     * @param callbackContext CallbackContext
     */
    private boolean saveValue(JSONArray args, LklPreferences lklPreferences, CallbackContext callbackContext) {
        String key = args.optString(0);
        String value = args.optString(1);

        lklPreferences.putString(key, value);
        callbackContext.success();
        return true;
    }

    /**
     * 得到与用户无关的数据
     *
     * @param args
     * @param lklPreferences
     * @param callbackContext
     * @return
     */
    private boolean getValue(JSONArray args, LklPreferences lklPreferences, CallbackContext callbackContext) {
        String key = args.optString(0);
        String defaultValue = args.optString(1);

        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,
                lklPreferences.getString(key));
        callbackContext.sendPluginResult(pluginResult);
        return true;
    }

}
