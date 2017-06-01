package com.lakala.platform.common;

//import com.crashlytics.android.Crashlytics;
import com.lakala.library.DebugConfig;

/**
 * Created by jerry on 14-9-1.
 * 程序异常及奔溃统计
 */
public class CrashlyticsUtil {

    public static void log(String msg){
        if (!DebugConfig.DEBUG){
            //            Crashlytics.log(msg);
        }

    }

    public static void log(int priority,String tag,String msg){
        if (!DebugConfig.DEBUG){
            //            Crashlytics.log(priority,tag,msg);
        }

    }

    public static void logException(Throwable throwable){
        if (!DebugConfig.DEBUG){
            //            Crashlytics.logException(throwable);
        }

    }

}
