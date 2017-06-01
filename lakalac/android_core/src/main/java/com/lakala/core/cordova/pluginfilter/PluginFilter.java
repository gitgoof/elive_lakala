package com.lakala.core.cordova.pluginfilter;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 通过匹配Cordova插件过滤文件，生成允许，拒绝插件列表
 * Created by andy_lv on 2014/7/16.
 */
public class PluginFilter {

    // 过滤文件解析后的List列表
    private List<FilterEntry> filterList = null;

    // 与host匹配的数据List列表
    private List<FilterEntry> hostMatchedList = new ArrayList<FilterEntry>();

    // 配置host的allow 数据
    private HashMap<String, String> allowHashMap = null;

    // 配置host的deind 数据
    private HashMap<String, String> deindHashMap = null;

    // 当前加载的网页的host地址
    private String host;

    /**
     * @param filterList
     */
    public PluginFilter(List<FilterEntry> filterList, String host) {
        this.filterList = filterList;
        this.host = host;

        this.hostMatchedList = new ArrayList<FilterEntry>();
        this.allowHashMap = new HashMap<String, String>();
        this.deindHashMap = new HashMap<String, String>();
    }

    /**
     * 开始进行插件过滤，先对hosts数据过滤，生成满足host条件的
     * 插件数据列表，然后又生成“allow”插件数据列表，“deind”
     * 插件数据队列
     */
    public void makePluginList() {
        hostFilter();
        allowFilter();
        deindFilter();
        filterList = null;
        hostMatchedList = null;
    }

    /**
     * 判断某个插件是否可以使用
     *
     * @param pluginName 插件名称
     * @param action     插件方法名称
     * @return if true 表示可以使用，false表示拒绝使用
     */
    public boolean canUserPlugin(String pluginName, String action) {
        boolean isResult = false;
        int allowWeightValue = getWeightValue(FilterCharacterLable.allow.name(), pluginName, action);
        int deindWeightValue = getWeightValue(FilterCharacterLable.deind.name(), pluginName, action);
        isResult = allowWeightValue > deindWeightValue;
        return isResult;
    }

    /**
     * host地址过滤
     */
    private void hostFilter() {
        String regExPatternStr = getRegExPattern();
        int size = filterList.size();
        for (int i = 0; i < size; i++) {
            FilterEntry fileEntry = filterList.get(i);
            String hosts = fileEntry.getHosts();
            if (null != hosts) {
                String[] arrray = hosts.split(",");
                int length = arrray.length;
                for (int j = 0; j < length; j++) {
                    try {
                        String host = arrray[j];
                        if (null != host) {
                            if (host.matches(regExPatternStr)) {
                                hostMatchedList.add(fileEntry);
                                break;
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }
    }

    /**
     * 从已经匹配出的host队列中过滤allow插件数据
     */
    private void allowFilter() {
        filterAllowAndDeind(FilterCharacterLable.allow.name());
    }

    /**
     * 从已经匹配的host队列中过滤deind插件数据
     */
    private void deindFilter() {
        filterAllowAndDeind(FilterCharacterLable.deind.name());
    }

    /**
     * ______________________________
     * |    allow   |     deind     |
     * ------------------------------
     * |____*(1)____|_____*(2)______|
     * |____A.*(3)__|_____A".*(4)___|
     * |____A.B(4)__|_____A".B"(5)__|
     * <p/>
     * type="allow",表示计算此插件在允许列表中的权重值；
     * type="deind",表示计算此插件在拒绝列表中的权重值；
     *
     * @param type   "allow","deind"
     * @param action 插件名称
     * @param action 插件方法名称
     * @return 此插件的方法在允许，拒绝列表中的权重值
     */
    private int getWeightValue(String type, String plugin, String action) {
        int weightValue = 0;
        Map<String, String> map = type.equals(FilterCharacterLable.allow.name()) ? allowHashMap : deindHashMap;
        Set<String> set = map.keySet();

        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = map.get(key);
            if (key.equals("*")) {
                weightValue = type.equals(FilterCharacterLable.allow.name()) ? 1 : 2;
            } else if (key.equals(plugin)) {
                if (value.equals("*")) {
                    weightValue = type.equals(FilterCharacterLable.allow.name()) ? 3 : 4;
                } else if (value.contains(action)) {
                    weightValue = type.equals(FilterCharacterLable.allow.name()) ? 5 : 6;
                }
            }
        }
        return weightValue;
    }

    /**
     * if type ="allow",过滤allow数据;
     * type="deind",过滤deind数据
     *
     * @param type
     */
    private void filterAllowAndDeind(String type) {
        Iterator<FilterEntry> iterator = hostMatchedList.listIterator();
        while (iterator.hasNext()) {
            FilterEntry filterEntry = iterator.next();
            String filter = type.equals(FilterCharacterLable.allow.name()) ?
                    filterEntry.getAllow() : filterEntry.getDeind();
            HashMap<String, String> hashMap = type.equals(FilterCharacterLable.allow.name()) ?
                    allowHashMap : deindHashMap;

            if (filter.equals("*") && !hashMap.containsKey("*")) {
                hashMap.put("*", "*");
            } else {
                try {
                    if (hashMap.containsKey("*")) {
                        hashMap.remove("*");
                    }
                    JSONObject jsonObj = new JSONObject(filter);
                    Iterator<String> keyIterator = jsonObj.keys();
                    while (keyIterator.hasNext()) {
                        String key = keyIterator.next();
                        String value = jsonObj.optJSONArray(key) != null ?
                                jsonObj.optJSONArray(key).toString() : jsonObj.optString(key);
                        value = value.replace("[", "").replace("]", "");
                        if (value.equals("\"*\"")) {
                            value = "*";
                        }
                        if (!hashMap.containsKey(key)) {
                            hashMap.put(key, value);
                        } else {
                            if (hashMap.containsKey(key) && !value.equals("*")) {
                                value = hashMap.get(key) + "," + value;
                                hashMap.put(key, value);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 返回要匹配host的正则表达式,格式如下：
     * 如果host="www.baidu.com",则正则表达式如下：
     * "\\*?|((www|\\*?)\\.(baidu|\\*?)\\.(com|\\*?))"
     *
     * @return
     */
    private String getRegExPattern() {
        String returnStr = "", newHost = "";
        int index = host.indexOf(":");
        newHost = (index != -1) ? host.substring(0, index) : host;

        String[] dotSplit = newHost.split("\\.");
        if (null != dotSplit) {
            int length = dotSplit.length;
            for (int i = 0; i < length; i++) {
                if (i != length - 1) {
                    returnStr += "(" + dotSplit[i] + "|\\*?)\\.";
                } else {
                    returnStr += "(" + dotSplit[i] + "|\\*?)";
                }
            }
        }
        returnStr = "\\*?|(" + returnStr + ")";
        return returnStr;
    }
}
