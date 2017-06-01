
package com.lakala.platform.cordovaplugin;

import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;

import com.lakala.core.cordova.cordova.CallbackContext;
import com.lakala.core.cordova.cordova.CordovaPlugin;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.common.PhoneBookManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vinchaos api on 14-1-16.
 */
public class PhoneBook extends CordovaPlugin implements PhoneBookManager.OnPhoneNumberSelectedListener {

    private static final String ACTION_PHONE_NUMBER = "get";
    private static final String ACTION_CALL_PHONE = "call";
    private static final String ACTION_GET_NAME = "getName";

    private static final int REQUEST = 99;

    private CallbackContext callbackContext;

    private String name = "";

    private String phone = "";

    /**
     * 是否过滤手机号
     */
    private boolean isFilter = true;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equalsIgnoreCase(ACTION_PHONE_NUMBER)) {
           return getPhoneNumber(args,callbackContext);
        }
        if (action.equalsIgnoreCase(ACTION_CALL_PHONE)) {
            return callMobilePhone(args, callbackContext);
        }
        if (action.equalsIgnoreCase(ACTION_GET_NAME)) {
            return getNameByNumber(args, callbackContext);
        }
        return false;
    }

    /**
     * 获取电话号码
     *
     * @param callbackContext
     * @return
     */
    private boolean getPhoneNumber(JSONArray args,CallbackContext callbackContext) {
        String type = "";
        if (args!=null) type = args.optString(0);
        if (StringUtil.isNotEmpty(type)) isFilter = false;
        this.callbackContext = callbackContext;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        this.cordova.startActivityForResult(this, intent, REQUEST);
        return true;
    }

    /**
     * 拨打电话
     *
     * @param args
     * @param callbackContext
     * @return
     */
    private boolean callMobilePhone(JSONArray args, CallbackContext callbackContext) {
        try {
            String phoneNumber = args.optString(0);
            final Uri uri = Uri.parse("tel:" + phoneNumber);
            final Intent intent = new Intent();
            intent.setAction(Intent.ACTION_CALL);
            intent.setData(uri);
            this.cordova.getActivity().startActivity(intent);
        } catch (Exception e) {
        }
        return true;
    }

    /**
     * 获取某一手机号在电话本中所存的姓名，若为空则返回""
     *
     * @param args
     * @param callbackContext
     * @return
     */
    private boolean getNameByNumber(JSONArray args, CallbackContext callbackContext) throws JSONException {
        String phoneNumber = args.optString(0);
        String name = PhoneBookManager.getNameByPhoneNumber(this.cordova.getActivity(), phoneNumber);
        String jsonObj = "{name : \""+name+"\"}";
        callbackContext.success(new JSONObject(jsonObj));
        return true;
    }

    /**
     * {name:name,phoneNumber:number}
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST:
                try{
                    if (intent == null || intent.getData() == null)
                        return;
                    Uri uri = intent.getData();
                    PhoneBookManager.getConcat(this.cordova.getActivity(),isFilter, uri, this);
                }catch (Exception e){
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onPhoneNumberSelected(String name, String phoneNumber) {
        JSONObject object = new JSONObject();
        try {
            object.put("name", name);
            object.put("phoneNumber", phoneNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callbackContext.success(object);
    }
}
