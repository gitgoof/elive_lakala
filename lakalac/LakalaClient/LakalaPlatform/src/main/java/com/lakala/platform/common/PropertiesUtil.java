package com.lakala.platform.common;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.http.BusinessRequest;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by xyz on 14-1-19.
 */
public class PropertiesUtil {

    //是否使用配置文件信息
    public static boolean isUseConfig;

    public static String svnCode;
    //是否加载sencha
    private static boolean loadSencha = false;

    private static Properties properties;

    private static SharedPreferences defaultSP;

    public static String channelName;

    static {

        defaultSP = PreferenceManager.getDefaultSharedPreferences(ApplicationEx.getInstance());
        properties = new Properties();
        try {
            properties.load(PropertiesUtil.class.getResourceAsStream("/assets/config/config.properties"));
            isUseConfig = PropertiesUtil.isUseConfig();
            svnCode=PropertiesUtil.svnCode();
            channelName=PropertiesUtil.channelName();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean getLoadFlag() {
        return defaultSP.getBoolean("isIvoryRemote", false);
    }

    public static void setLoadFlag(boolean flag) {
        defaultSP.edit().putBoolean("isIvoryRemote", flag).commit();
    }

    public static boolean getLoadSenchaFlag() {
        return defaultSP.getBoolean("isSenchaRemote", false);
    }

    private static String url;

    public static String getUrl(){
        return url;
    }

    public static void setLoadSenchaFlag(boolean flag) {
        defaultSP.edit().putBoolean("isSenchaRemote", flag).commit();
    }

    public static void setUrl(String newUrl) {
        url = newUrl;
        BusinessRequest.setBASE_URL(url);
        defaultSP.edit().putString("url", url).commit();
    }

    public static boolean getLoadSencha() {
        return loadSencha;
    }

    public static void setLoadSencha(boolean flag) {
        loadSencha = flag;
    }

    public static String getSenchaUrl(){
        return defaultSP.getString("senchaUrl","http://192.168.2.111:63351");
    }

    public static void setSenchaUrl(String url) {
        defaultSP.edit().putString("senchaUrl", url).commit();
    }

    public static String getBuildDetail(){
        return properties.getProperty("buildDetail", "");
    }

    /**
     * 新添方法 获取 调试 状态 是否使用配置文件
     * */

    public static boolean isDebug(){
        return PropertiesUtil.getConfigBoolValueWithConfigKey("isDebug");
    }

    public static boolean isUseConfig(){
        return PropertiesUtil.getConfigBoolValueWithConfigKey("isUseConfig");
    }
    public static String svnCode(){
        return properties.getProperty("svnCode");
    }
    public static String channelName(){
        return properties.getProperty("channelName");
    }


    private static boolean getConfigBoolValueWithConfigKey(String configKey){

        if (configKey.length() == 0)
            return false;

        String value = properties.getProperty(configKey);
        return "true".equalsIgnoreCase(value);

    }
}
