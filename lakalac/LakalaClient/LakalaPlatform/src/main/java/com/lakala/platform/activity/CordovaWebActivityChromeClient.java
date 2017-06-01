package com.lakala.platform.activity;

import android.text.TextUtils;
import android.webkit.ConsoleMessage;

import com.lakala.core.cordova.cordova.CordovaChromeClient;
import com.lakala.core.cordova.cordova.CordovaInterface;
import com.lakala.core.cordova.cordova.CordovaWebView;

/**
 * Created by Michael on 15-1-4.
 */
public class CordovaWebActivityChromeClient extends CordovaChromeClient {
    private static final String CMD_SCHEME = "ExecuteNativeCmd://";

    public CordovaWebActivityChromeClient(CordovaInterface cordova) {
        super(cordova);
    }

    public CordovaWebActivityChromeClient(CordovaInterface ctx, CordovaWebView app) {
        super(ctx, app);
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        String message = consoleMessage.message();
        //如果拦截到 "ExecuteNativeCmd://" 则表示是要执行一个我们自定义的命令。
        if (!TextUtils.isEmpty(message) && message.startsWith(CMD_SCHEME)){
            String cmd = message.substring(CMD_SCHEME.length());
            if("goBack".equals(cmd)){
                goBack();
            }
        }
        return super.onConsoleMessage(consoleMessage);
    }

    public void goBack(){
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean flag = appView.canGoBack();
                if (flag) {
                    appView.goBack();
                } else {
                    cordova.getActivity().finish();
                }
            }
        });
    }
}
