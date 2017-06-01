package com.lakala.core.cordova.pluginfilter;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 解析Cordova插件过滤文件
 * <p/>
 * Created by andy_lv on 2014/7/16.
 */
public class FilterFileManager {
    /**
     * 解析Cordova插件过滤文件
     *
     * @param fileJson 插件过滤文档解析后的JSONObject
     * @return 解析成List数据
     */
    public static List<FilterEntry> parseFile(JSONObject fileJson) {
        List<FilterEntry> filterEntryList = null;
        try {
            if (null != fileJson) {
                JSONArray jsonArray = fileJson.optJSONArray(FilterCharacterLable.whiteList.name());
                if (jsonArray != null) {
                    filterEntryList = new ArrayList<FilterEntry>();
                    int length = jsonArray.length();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        if (null != jsonObject) {
                            FilterEntry filterEntry = new FilterEntry();
                            String hosts = jsonObject.optString(FilterCharacterLable.hosts.name())
                                    .replace("[", "").replace("]", "");
                            hosts = hosts.replaceAll("\"", "").replaceAll("\"", "");
                            filterEntry.setHosts(hosts);

                            String allow = jsonObject.optString(FilterCharacterLable.allow.name());
                            if (allow.equals("\"*\"")) {
                                allow = "*";
                            }
                            filterEntry.setAllow(allow);

                            String deind = jsonObject.optString(FilterCharacterLable.deind.name());
                            if (deind.equals("\"*\"")) {
                                deind = "*";
                            }
                            filterEntry.setDeind(deind);
                            filterEntryList.add(filterEntry);
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return filterEntryList;
    }
}
