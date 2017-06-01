package com.lakala.platform.statistic;

import android.content.Context;
import android.text.TextUtils;

import com.avos.avoscloud.AVAnalytics;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ConfigFileManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Blues on 14-3-3.
 */
public class StatisticManager {

    private static StatisticManager instance;
    /**
     * D+0
     */
    public static String D0_1 = "立即提款-1";//从主页点击立即提款
    public static String D0_2 = "立即提款-2";//从交易管理点击立即提款
    public static String D0_3 = "划款查询-1";//从交易管理点击划款记录

    /**
     * .业务级业面统计(用户进入各个业务的页面及退出统计)
     */
    public static String pageTrace = "pageTrace";
    /**
     * .刷卡状态
     */
    final public static String swiperStatus ="swiperStatus";

    /**
     * .刷卡成功
     */
    final public static String OK ="OK";

    /**
     * .刷卡失败
     */
    final public static String Failed ="Failed";
    /**
     * 获取 KSN 成功
     */
    final public static String getKsnSuccess = "KSNSuccess";
    /**
     * 获取 KSN 失败
     */
    final public static String getKSNFailure = "KSNFailed";
    /**
     * 获取 KSN 失败，默认刷卡器状态
     */
    final public static String getKSNFailureDefault = "DefaultKSNFailed";
    /**
     * 获取 KSN 失败，选择刷卡器型号时
     */
    final public static String getKSNFailureSelect = "SelectKSNFailed";

    private static JSONObject statisticConfig;

    public static synchronized StatisticManager getInstance() {

        if (instance == null) {
            instance = new StatisticManager();
        }

        if (statisticConfig == null) {
            try {
                JSONObject statistic = ConfigFileManager.getInstance().readStatisticConfig();
                statisticConfig = statistic.optJSONObject("config");
            } catch (Exception e) {

            }
        }
        return instance;
    }

    public void onEvent(String eventId, Context context){
        LogUtil.print("event",eventId);
        if (eventId == null){
            return;
        }
        onEvent(eventId,null,null,null,null, context);
    }

    /**
     * 数据统计
     *
     * @param eventId    事件Id.
     * @param label      事件标签.
     * @param count      事件的累计发生次数，可以将相同的事件一起发送，节约流量.
     * @param origin     .事件发生位置---标志发生所在业务
     * @param userMobile .登录用户手机号
     */
    public void onEvent(String eventId,
                        String label,
                        String count,
                        String origin,
                        String userMobile,
                        Context c) {
        onEvent(eventId, label, count, origin, userMobile, null, c);
    }


    /**
     * 数据统计
     *
     * @param eventId       事件Id.
     * @param label         事件标签.
     * @param count         事件的累计发生次数，可以将相同的事件一起发送，节约流量.
     * @param origin        .事件发生位置---标志发生所在业务
     * @param userMobile    .登录用户手机号
     * @param stringHashMap .事件添加自定义属性
     */
    public void onEvent(String eventId,
                        String label,
                        String count,
                        String origin,
                        String userMobile,
                        Map<String, String> stringHashMap,Context mContext) {

        if (stringHashMap == null) {
            stringHashMap = new HashMap<String, String>();
        }

        if(!TextUtils.isEmpty(label)){
            stringHashMap.put("label",label);
        }
        if(!TextUtils.isEmpty(count)){
            stringHashMap.put("count",count);
        }
        if(!TextUtils.isEmpty(origin)){
            stringHashMap.put("origin",origin);
        }
        if(!TextUtils.isEmpty(userMobile)){
            stringHashMap.put("userMobile",userMobile);
        }

        AVAnalytics.onEvent(mContext, eventId, stringHashMap);
    }


    /**
     * 同步统计PV
     *
     * @param className 类名
     */
    public void statisticActivityPV(String className, String mobile, Context c) {
        if (activityStatMap.containsKey(className)) {
            onEvent(pageTrace, activityStatMap.get(className), "1", "", mobile, c);
            LogUtil.print("err", "ClassName=" + className + "   ___ID=" + activityStatMap.get(className));
        }
    }

    /**
     * 同步统计PV
     *
     * @param fragmentName 类名
     */
    public void statisticFragmentPV(String fragmentName, String mobile, Context c) {
        if (fragmentStatMap.containsKey(fragmentName)) {
            onEvent(pageTrace, fragmentStatMap.get(fragmentName), "1", "", mobile, c);
            LogUtil.print("err", "ClassName=" + fragmentName + "   ___ID=" + fragmentStatMap.get(fragmentName));
        }
    }


    /**
     * 项目中所有activity类名与业务代码的Map,Map<类名,业务代码>,用于statistic进行数据统计.
     */
    private static Map<String, String> activityStatMap = new HashMap<String, String>() {
        {
            //todo , example
            put("Activity", "Code");

        }
    };

    /**
     * 项目中所有fragment类名与业务代码的Map,Map<类名,业务代码>,用于statistic进行数据统计.
     */
    public static Map<String, String> fragmentStatMap = new HashMap<String, String>() {
        {
            //todo , example
            put("Activity", "Code");

        }
    };
}
