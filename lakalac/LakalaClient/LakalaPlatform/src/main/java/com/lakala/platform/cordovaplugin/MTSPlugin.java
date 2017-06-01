package com.lakala.platform.cordovaplugin;

import android.text.TextUtils;

import com.lakala.core.cordova.cordova.CallbackContext;
import com.lakala.core.cordova.cordova.CordovaArgs;
import com.lakala.core.cordova.cordova.CordovaPlugin;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.swiper.TerminalKey;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>Description  : save mts info.</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 15/8/4.</p>
 * <p>Time         : 下午4:56.</p>
 */
public class MTSPlugin extends CordovaPlugin{

    private static final String ACTION_SAVE = "save";

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        if (action.equalsIgnoreCase(ACTION_SAVE)){
            return save(args);
        }
        return super.execute(action, args, callbackContext);
    }

    private boolean save(CordovaArgs args){
        try {
            JSONObject response = args.getJSONObject(0);

            String workKey = response.optString("WorkKey");
            String macKey = response.optString("MacKey");
            String pinKey = response.optString("PinKey");
            String terminalId = response.optString("TerminalId");

            String questionFlagStr = response.optString("QuestionFlag");
            String trsPasswordFlagStr = response.optString("TrsPasswordFlag");
            String authFlag = response.optString("AuthFlag");
            String customerName = response.optString("CustomerName");
            String identifier = response.optString("Identifier");

            if (TextUtils.isEmpty(terminalId) ||
                    TextUtils.isEmpty(workKey) ||
                    TextUtils.isEmpty(macKey) ||
                    TextUtils.isEmpty(pinKey) ||
                    TextUtils.isEmpty(questionFlagStr) ||
                    TextUtils.isEmpty(trsPasswordFlagStr) ||
                    TextUtils.isEmpty(authFlag)){
                return true;
            }

            boolean questionFlag = "1".equals(questionFlagStr);
            boolean trsPasswordFlag = "1".equals(trsPasswordFlagStr);

            TerminalKey.setMac(terminalId, macKey);
            TerminalKey.setPik(terminalId, pinKey);
            TerminalKey.setTpk(terminalId, workKey);

            User user = ApplicationEx.getInstance().getUser();
            user.setQuestionFlag(questionFlag);
            user.setTrsPasswordFlag(trsPasswordFlag);
            user.setMtsTerminalId(terminalId);
            user.setMtsPinKey(pinKey);
            user.setMtsWorkKey(workKey);
            user.setMtsMacKey(macKey);
            user.setAuthFlag(authFlag);
            user.setMtsCustomerName(customerName);
            user.setIdentifier(identifier);
            user.save();

        } catch (JSONException e) {
            // Silent
        }
        return true;
    }
}
