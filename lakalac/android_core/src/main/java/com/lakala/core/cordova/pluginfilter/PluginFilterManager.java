package com.lakala.core.cordova.pluginfilter;

import android.net.Uri;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by andy_lv on 2014/7/23.
 */
public class PluginFilterManager {

    private PluginFilter pluginFilter;

    private String host;

    private JSONObject config;

    /**
     * 是否需要对插件进行过滤
     */
    private boolean isNeedFilter = false;

    /**
     * 根据host地址匹配插件过滤配置文件，生成插件允许列表和拒接列表
     *
     * @param url WebView 当前的 URL
     */
    public void makeFilter(String url) {
        if(!isNeedFilter){
            return ;
        }
        Uri uri = Uri.parse(url);
        if (uri != null){
            String newHost = uri.getHost();
            if (null == newHost || newHost.equals(host)){
                return;
            }

            host = newHost;
        }

        List<FilterEntry> pluginList = FilterFileManager.parseFile(config);
        if (null != pluginList){
           // LogUtil.print("startFilter", "-----pluginList==" + pluginList.size());
            pluginFilter = new PluginFilter(pluginList, host);
            pluginFilter.makePluginList();
        }
    }

    /**
     * 判断某个插件是否可以使用
     *
     * @param pluginName 插件名称
     * @param methodName 插件要使用的方法名称
     * @return if true 表示可以使用，false 表示不能使用
     */
    public boolean canUse(String pluginName, String methodName) {
        return !isNeedFilter || (pluginFilter != null &&
                pluginFilter.canUserPlugin(pluginName, methodName));
    }

    /**
     * 设置插件过滤配置文件JSONObject
     * @param config
     */
    public void setFilterConfig(JSONObject config)
    {
        this.config = config;
    }

    /**
     * 设置是否过滤插件
     * @param isNeedFilter
     */
    public void setNeedFilter(boolean isNeedFilter){
        this.isNeedFilter = isNeedFilter;
    }

}
