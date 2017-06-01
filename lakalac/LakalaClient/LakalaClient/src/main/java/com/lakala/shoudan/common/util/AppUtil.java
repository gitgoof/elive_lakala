package com.lakala.shoudan.common.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.lakala.library.encryption.Digest;
import com.lakala.platform.common.ApplicationEx;

import java.util.Calendar;

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
            verBuffer.append("V");
            if(pi.versionCode < 10){
                verBuffer.append("0");
            }
            if(pi.versionCode < 100){
                verBuffer.append(0);
            }
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
    
    public static String getDeviceId(Context context){
    	if(null == context){
    		return "";
    	}
    	String deviceId;
    	try{
    		TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    		deviceId = mTelephonyManager.getDeviceId();
    	}catch(Exception e){
    		throw new RuntimeException(e);
    	}
    	return deviceId;
    }
    
    public static String getDeviceMode(){
    	Build build = new Build();
    	return build.MODEL;
    }
    
    public static String getImsi(Context context){
    	if(null == context){
    		return "";
    	}
    	String imsi;
    	try{
    		TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    		imsi = mTelephonyManager.getSubscriberId();
    	}catch(Exception e){
    		throw new RuntimeException(e);
    	}
    	return imsi;
    }

    public static String createDeviceID(Context context){
        if (context == null){
            return "";
        }

        String imei = PhoneUtils.getIMEI();
        if(!TextUtils.isEmpty(imei)){
            return imei;
        }

        StringBuilder stringBuilder = new StringBuilder();

        //使用设备信息的字符串，拼接一个类似imei的15位串
        stringBuilder.append("35");
        stringBuilder.append(Build.BOARD.length()%10);
        stringBuilder.append(Build.BRAND.length()%10);
        stringBuilder.append(Build.CPU_ABI.length()%10);
        stringBuilder.append(Build.DEVICE.length()%10);
        stringBuilder.append(Build.DISPLAY.length()%10);
        stringBuilder.append(Build.HOST.length()%10);
        stringBuilder.append(Build.ID.length()%10);
        stringBuilder.append(Build.MANUFACTURER.length()%10);
        stringBuilder.append(Build.MODEL.length()%10);
        stringBuilder.append(Build.PRODUCT.length()%10);
        stringBuilder.append(Build.TAGS.length()%10);
        stringBuilder.append(Build.TYPE.length()%10);
        stringBuilder.append(Build.USER.length()%10);

        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        stringBuilder.append(androidId);

        stringBuilder.append(getMac(context));

        return Digest.md5(stringBuilder.toString());
    }
    /**
     * getMac
     * 获取Mac地址
     *
     * @return 返回 MAC 地址
     */
    public static String getMac(Context context) {
        if (context == null){
            return "";
        }
        WifiManager wifi  = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info  = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    public static String createGuid(){

        String deviceId = AppUtil.createDeviceID(ApplicationEx.getInstance());
        Calendar calendar = Calendar.getInstance();

        //deviceid 年月日时分秒 5位随机数
        String guid = String.format("%s%tY%tm%td%tH%tM%tS%s",
                deviceId,
                calendar,
                calendar,
                calendar,
                calendar,
                calendar,
                calendar,
                StringUtil.getRandom(5)
        );

        return Digest.md5(guid);
    }

}
