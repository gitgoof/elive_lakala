package com.lakala.library.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * Created by Vinchaos api on 14-1-2.
 * App信息
 */
public class AppUtil {

    /**
     * 获取目前软件版本code
     *
     * @param  context Context
     * @return String
     */
    public static String getAppVersionCode(Context context) {
        if (context == null){
            return "";
        }
        String versionCode;
        try {
            PackageManager  pm = context.getPackageManager();

            PackageInfo     pi = pm.getPackageInfo(context.getPackageName(), 0);
            StringBuffer verBuffer = new StringBuffer();
//            verBuffer.append("M");
//            if(pi.versionCode < 10){
//                verBuffer.append("0");
//            }
//            if(pi.versionCode < 100){
//                verBuffer.append(0);
//            }
            verBuffer.append(pi.versionCode);

            versionCode        = verBuffer.toString();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return versionCode;
    }

    /**
     * 获取目前软件版本name
     *
     * @param  context Context
     * @return String
     */
    public static String getAppVersionName(Context context) {
        if (context == null){
            return "";
        }
        String versionName;
        try {
            PackageManager  pm = context.getPackageManager();

            PackageInfo     pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName        = pi.versionName + "";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return versionName;
    }

    /**
     * 从AndroidManifest中获取channel值
     *
     * @param  context Context
     * @return String
     */
    public static String getAppChannel(Context context){
        if (context == null){
            return "";
        }
        int channel;
        try {
            PackageManager  pm    = context.getPackageManager();
            ApplicationInfo info  = pm.getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);

            channel               = info.metaData.getInt("Channel ID");
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        return channel+"";
    }

    /**
     * 从AndroidManifest中获取crashVersion
     *
     * @param  context Context
     * @return String
     */
    public static String getCrashVersion(Context context){
        if (context == null){
            return "";
        }
        String crashVersion;
        try {
            PackageManager  pm    = context.getPackageManager();
            ApplicationInfo info  = pm.getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
            crashVersion          = info.metaData.getString("CRASH");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return crashVersion;
    }

    /**
     * app是否为前台可见
     *
     * @param  context Context
     * @return boolean
     */
    public static boolean isAppRunningForeground(Context context){
        if (context == null){
            return false;
        }
        String packageName        = context.getPackageName();
        String topActivityPkgName = getTopActivityPkgName(context);

        return packageName!=null && topActivityPkgName!=null
                && topActivityPkgName.equals(packageName);
    }

    /**
     * 获取Device栈顶程序包名
     *
     * @param  context Context
     * @return String
     */
    public static String getTopActivityPkgName(Context context){
        if (context == null){
            return "";
        }

        String          topActivityPkgName  =   null;
        ActivityManager activityManager     =   (ActivityManager)(context.getSystemService(android.content.Context.ACTIVITY_SERVICE )) ;
        //android.app.ActivityManager.getRunningTasks(int maxNum)
        //int maxNum--->The maximum number of entries to return in the list
        //即最多取得的运行中的任务信息(RunningTaskInfo)数量
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        //修改程序列表为内容为空
        if(runningTaskInfos != null && runningTaskInfos.size()>0){
            ComponentName f=runningTaskInfos.get(0).topActivity;
            topActivityPkgName = f.getPackageName();
        }
        //按下Home键盘后 topActivityClassName=com.android.launcher2.Launcher
        return topActivityPkgName;
    }

}
