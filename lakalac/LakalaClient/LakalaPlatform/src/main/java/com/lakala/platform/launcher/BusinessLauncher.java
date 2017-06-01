package com.lakala.platform.launcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.lakala.core.launcher.ActivityLauncher;
import com.lakala.platform.common.ConfigFileManager;

import org.json.JSONObject;

/**
 * Created by wangchao on 14/11/10.
 */
public class BusinessLauncher extends ActivityLauncher {
    private static BusinessLauncher instance;

    /** ShowControlBar Key */
    public static final String INTENT_PARAM_KEY_SHOWCONTROLBAR = "acShowControlBar";

    public static synchronized BusinessLauncher getInstance(){
        if(instance == null){
            instance = new BusinessLauncher();
        }

        return instance;
    }

    private BusinessLauncher(){
        super();
    }

    public synchronized void initConfig(){
        if (getConfigs() == null || getConfigs().length() == 0){
            setKeyConfigs(ConfigFileManager.getInstance().readBusinessLauncherConfig());
        }
    }

    @Override
    protected String getClassNameByType(Context context,String type,String action) {
        if(ACTIVITY_TYPE_WEBAPP.equalsIgnoreCase(type)){
            return "com.lakala.platform.activity.BaseCordovaWebActivity";
        }
        else if(ACTIVITY_TYPE_WEBVIEW.equalsIgnoreCase(type)){
            return "com.lakala.platform.activity.CommonWebViewActivity";
        }
        else{
            return super.getClassNameByType(context,type,action);
        }
    }

    @Override
    protected void setIntentByConfig(JSONObject config,Intent intent){
        String type = config.optString("type","WebApp");

        if(ACTIVITY_TYPE_WEBAPP.equals(type)){
            //处理 WebApp 类型的一些事情
        }
        else if(ACTIVITY_TYPE_WEBVIEW.equals(type)){
            //处理 WebView 类型的一些事情
            if (!intent.hasExtra("url")){
                intent.putExtra(INTENT_PARAM_KEY_URL,config.optString("url",""));
            } else {
                intent.putExtra(INTENT_PARAM_KEY_URL,intent.getStringExtra("url"));
            }
            intent.putExtra(INTENT_PARAM_KEY_SHOWCONTROLBAR,config.optBoolean("showControlBar",false));
        }
        else{
            super.setIntentByConfig(config,intent);
        }
    }

    private void checkConfig(){
        //程序崩溃  config丢失
        if(getConfigs() == null ||getConfigByKey("home") == null){
            initConfig();
        }
    }

    @Override
    public boolean start(String key) {
        checkConfig();
        return super.start(key);
    }

    @Override
    public boolean start(String key, Intent intent) {
        checkConfig();
        return super.start(key, intent);
    }

    @Override
    public boolean startForResult(String key, Intent intent, int requestCode) {
        checkConfig();
        return super.startForResult(key, intent, requestCode);
    }

    @Override
    public boolean startForResult(Activity current, String key, Intent intent, int requestCode) {
        checkConfig();
        return super.startForResult(current, key, intent, requestCode);
    }

}
