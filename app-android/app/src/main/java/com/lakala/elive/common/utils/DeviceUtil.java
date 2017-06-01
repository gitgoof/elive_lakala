package com.lakala.elive.common.utils;

import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


import com.lakala.elive.EliveApplication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DeviceUtil {
  /**
   * 获取屏幕分辨率
   */
  public static int[] getScreenSize(Resources resources) {
    int width = resources.getDisplayMetrics().widthPixels;
    int height = resources.getDisplayMetrics().heightPixels;
    int[] result = new int[2];
    result[0] = width;
    result[1] = height;
    return result;
  }



  /***
   * 获取设备相关唯一身份识别
   *
   * @param context context
   * @return 32位身份识别码或者空
   */
  public static String getDeviceIdentity(@NonNull Context context) {
    String complexStr = getMacAddress(context) + getDeviceId(context);
    return getMD5String(complexStr);
  }




  public static String getMacAddress(@NonNull Context context) {
    WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    String macAddress = wm.getConnectionInfo().getMacAddress();
    if (TextUtils.isEmpty(macAddress) || "02:00:00:00:00:00".equals(macAddress)) {
      return "";
    }
    return macAddress;
  }

  public static String getDeviceId(@NonNull Context context) {
    TelephonyManager TelephonyMgr =
        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    String deviceId = TelephonyMgr.getDeviceId();
    return TextUtils.isEmpty(deviceId) ? "" : deviceId;
  }

  /***
   * 获取String的MD5值
   * @param content String内容
   * @return md5
   */
  public static String getMD5String(@NonNull String content) {
    MessageDigest m = null;
    try {
      m = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    if (null != m && !TextUtils.isEmpty(content)) {
      m.update(content.getBytes(), 0, content.length());
      byte p_md5Data[] = m.digest();
      String m_szUniqueID = "";
      for (byte aP_md5Data : p_md5Data) {
        int b = (0xFF & aP_md5Data);
        if (b <= 0xF) m_szUniqueID += "0";
        m_szUniqueID += Integer.toHexString(b);
      }
      return m_szUniqueID.toUpperCase();
    } else {
      return content;
    }
  }
}
