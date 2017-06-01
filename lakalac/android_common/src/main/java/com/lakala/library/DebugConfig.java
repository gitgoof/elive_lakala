package com.lakala.library;

/**
 * Created by xyz on 13-12-23.
 */
public class DebugConfig {

    /**
     * Debug日志打印标志位
     * true 打印
     * false 不打印
     */
    public static boolean DEBUG = false;

    /**
     * true:测试环境
     * false:备机or生产
     */
    public static boolean DEV_ENVIRONMENT = true;

    public static void setDebug(boolean isDebug){
        DEBUG = isDebug;
    }
}
