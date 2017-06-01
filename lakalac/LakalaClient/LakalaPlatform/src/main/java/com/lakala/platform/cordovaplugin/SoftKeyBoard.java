package com.lakala.platform.cordovaplugin;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.lakala.core.cordova.cordova.CallbackContext;
import com.lakala.core.cordova.cordova.CordovaPlugin;

import org.json.JSONArray;

/**
 * Created by wangchao on 14-2-22.
 */
public class SoftKeyBoard extends CordovaPlugin {

    public SoftKeyBoard() {
    }

    public void showKeyBoard() {
        InputMethodManager mgr = (InputMethodManager) cordova.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(webView, InputMethodManager.SHOW_IMPLICIT);

        ((InputMethodManager) cordova.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(webView, 0);
    }

    public void hideKeyBoard() {
        InputMethodManager mgr = (InputMethodManager) cordova.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(webView.getWindowToken(), 0);
    }

    public boolean isKeyBoardShowing() {

        int heightDiff = webView.getRootView().getHeight() - webView.getHeight();
        return (100 < heightDiff);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        if (action.equals("show")) {
            this.showKeyBoard();
            callbackContext.success("done");
            return true;
        }
        else if (action.equals("hide")) {
            this.hideKeyBoard();
            callbackContext.success();
            return true;
        }
        else if (action.equals("isShowing")) {
            callbackContext.success(Boolean.toString(this.isKeyBoardShowing()));
            return true;
        }
        else {
            return false;
        }
    }
}
