package com.lakala.platform.cordovaplugin;

import com.lakala.core.cordova.cordova.CallbackContext;
import com.lakala.core.cordova.cordova.CordovaPlugin;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wangchao on 14-2-21.
 */
public class Session extends CordovaPlugin {


    private static final String ACTION_GET_SERIES = "getSeries";

    private static final String ACTION_GET_SESSION = "getSession";

    private static final String ACTION_GET_USER = "getUser";

    private static final String ACTION_SET_USER = "setUser";


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equalsIgnoreCase(ACTION_GET_SERIES)) {
            return getSeries(callbackContext);
        }
        if (action.equalsIgnoreCase(ACTION_GET_SESSION)) {
            return getSession(callbackContext);
        }

        if (action.equalsIgnoreCase(ACTION_GET_USER)) {
            return getUser(callbackContext);
        }

        if (action.equalsIgnoreCase(ACTION_SET_USER)){
            return setUser(args.optJSONObject(0));
        }

        return super.execute(action, args, callbackContext);
    }

    /**
     * 获取Series
     *
     * @param callbackContext
     * @return
     * @throws org.json.JSONException
     */
    private boolean getSeries(CallbackContext callbackContext) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("loginName", ApplicationEx.getInstance().getUser().getLoginName());
        callbackContext.success(object);
        return true;
    }

    /**
     * 获取session
     *
     * @param callbackContext
     * @return
     * @throws org.json.JSONException
     */
    private boolean getSession(CallbackContext callbackContext) throws JSONException {
        JSONObject object = new JSONObject();
        //免密额度
//        object.put("smallAmount", ApplicationEx.getInstance().getUser().getSmallAmountLimit());
        //虚拟终端号vtsn
        object.put("vtsn", ApplicationEx.getInstance().getUser().getTerminalId());
        //用户手机号
        object.put("mobile", ApplicationEx.getInstance().getUser().getLoginName());
        //用户姓名
        object.put("userName", ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName());
        //用户ID
        object.put("userId", ApplicationEx.getInstance().getUser().getUserId());

        callbackContext.success(object);
        return true;
    }

    /**
     * 获取当前用户信息
     *
     * @param callbackContext
     * @return
     */
    private boolean getUser(CallbackContext callbackContext) throws JSONException {
        JSONObject userObj = new JSONObject();

        User user = ApplicationEx.getInstance().getUser();
        if (user != null) {
            userObj.put("Mobile", user.getLoginName());
            userObj.put("UserId", user.getUserId());
            userObj.put("psamNo", ApplicationEx.getInstance().getSession().getCurrentKSN());
            userObj.put("merchantNo", user.getMerchantInfo().getMerNo());
            userObj.put("TrsPasswordFlag", user.isTrsPasswordFlag() ? "1" : "0");
            userObj.put("TerminalId", user.getTerminalId());
            userObj.put("QuestionFlag", user.isQuestionFlag() ? "1" : "0");
            userObj.put("AuthFlag", user.getAuthFlag());
            userObj.put("CustomerName", user.getMtsCustomerName());
            userObj.put("Identifier", user.getIdentifier());
        }
        callbackContext.success(userObj);
        return true;
    }

    private boolean setUser(JSONObject data){
        if (data == null){
            return false;
        }
        if(data.has("isFromPosApply")){
            boolean isFromPosApply = data.optBoolean("isFromPosApply");
            ApplicationEx.getInstance().getSession().setFromPosApply(isFromPosApply);
        }

        User user = ApplicationEx.getInstance().getUser();
        try {
            user.initMerchantAttrWithJson(data);
        } catch (JSONException e) {
            LogUtil.print(e);
        }
        user.save();
        return true;
    }

}
