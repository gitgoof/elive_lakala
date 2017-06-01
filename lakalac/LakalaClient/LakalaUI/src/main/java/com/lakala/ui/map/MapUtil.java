package com.lakala.ui.map;

import android.Manifest;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

/**
 * Created by Blues on 14-1-7.
 */
public class MapUtil {

    /**
     * 检查gps是否可用
     * @return
     */
    public static boolean isGpsAvaiable(Context context) {
        if(PackageManager.PERMISSION_GRANTED != context.getPackageManager()
                .checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, context.getPackageName())){
            return false;
        }

        if(!((LocationManager) context.getSystemService(Context.LOCATION_SERVICE))
                .isProviderEnabled(LocationManager.GPS_PROVIDER)){
            return false;
        }

        return true;
    }

    /**
     * 打开gps或者关闭gps
     * @param context
     */
    public static void autoGps(Context context) {
        try {
            Intent GPSIntent = new Intent();// 代码自动打开gps
            GPSIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
            GPSIntent.setData(Uri.parse("custom:3"));
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (CanceledException e) {
        }
    }

    /**
     * 网络是否可用
     *
     * @return true可用 </br> false不可用
     */
    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivity = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                return true;
            }
        }
        return false;
    }
}
