package com.lakala.platform.config;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.lakala.platform.R;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.PackageFileManager;
import com.lakala.platform.common.PropertiesUtil;
import com.lakala.platform.swiper.mts.protocal.ProtocalUtil;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by wangchao on 14/10/22.
 * 所有配置都在这里控制
 */
public class Config {

    private static Properties properties;
    private static String isDebug;
    private static String urlFormProperty;
    private static String mtsURL;

    static {
        properties = new Properties();
        try {
            properties.load(Config.class.getResourceAsStream("/assets/config/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static SharedPreferences getPreference() {
        return PreferenceManager.getDefaultSharedPreferences(ApplicationEx.getInstance());
    }

    /**
     * 获取Debug标记
     */
    public static boolean isDebug() {
        if (TextUtils.isEmpty(isDebug)){
            isDebug = properties.getProperty("isDebug", "false");
        }
        return "true".equals(isDebug);
    }

    /**
     * 获取请求URL
     */
    public static String getBaseRequestUrl() {
        return isDebug() ? getBaseUrlFromPreference() : getBaseUrlFromProperty();
    }

    /**
     * 获取Property文件中设置的Url
     */
    public static String getBaseUrlFromProperty() {
        if (TextUtils.isEmpty(urlFormProperty)){
            urlFormProperty = properties.getProperty("url","");

            if (!urlFormProperty.endsWith("/")){
                urlFormProperty = urlFormProperty.concat("/");
            }
        }
        return urlFormProperty;
    }

    /**
     * 获取手动设置的Url
     */
    public static String getBaseUrlFromPreference() {
        String url = getPreference().getString(ApplicationEx.getInstance().getString(R.string.debug_key_server_address), getBaseUrlFromProperty());

        if (url != null && !url.endsWith("/")){
            url = url.concat("/");
        }

        return url;
    }

    /**
     * 获取 mts url
     *
     * @return URL
     */
    public static String getMTSUrl(){
        if (TextUtils.isEmpty(mtsURL)){
            mtsURL = PropertiesUtil.getUrl().replace("api", "product/financial");
        }
        return mtsURL;
    }

    /**
     * 获取 WebApp Index 页面地址
     * @return
     */
    public static String getWebAppIndexPage() {
        String rootPath = isRemoteWebApp() ? getRemoteWebAppRootPath() : getLocalWebAppRooPath();
        return rootPath.concat("index-android.html#");
    }

    /**
     * 获取 WebApp 根路径，结尾包含分隔符"/"。
     * @return 根路径
     */
    public static String getWebAppRootPath() {
        return isRemoteWebApp() ? getRemoteWebAppRootPath() : getLocalWebAppRooPath();
    }

    /**
     * 获取Sencha远程调试开关
     */
    public static boolean isRemoteWebApp() {
        return getPreference().getBoolean(ApplicationEx.getInstance().getString(R.string.debug_key_used_remote_sencha), false);
    }

    /**
     * 返回本地 WebApp URL 根路径，结尾包含分隔符"/"。
     * @return 本地 WebApp URL 根路径
     */
    public static String getLocalWebAppRooPath(){
        String path = "file://" + PackageFileManager.getInstance().getPmobilePath();
        if (!path.endsWith("/")){
            path = path.concat("/");
        }

        return path;
    }

    /**
     * 返回远程 WebApp URL 根路径，结尾包含分隔符"/"。
     * @return 远程 WebApp URL 根路径
     */
    public static String getRemoteWebAppRootPath(){
        String path = getPreference().getString(ApplicationEx.getInstance().getString(R.string.debug_key_remote_sencha), "");
        if (!path.endsWith("/")){
            path = path.concat("/");
        }

        return path;
    }
}