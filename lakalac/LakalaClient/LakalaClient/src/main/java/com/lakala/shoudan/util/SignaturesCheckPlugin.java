package com.lakala.shoudan.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.text.TextUtils;

import java.security.MessageDigest;

/**
 * Created by linmq on 2016/5/4.
 */
public class SignaturesCheckPlugin {

    /**
     * 获取证书指纹的md5值(全小写,无冒号":")
     * @param context
     * @param packageName
     * @return
     */
    public static String getSignMd5(Context context, String packageName){
        Signature[] signs = getRawSignature(context, packageName);
        if(signs == null || signs.length == 0){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0;i<signs.length;i++){
            Signature sign = signs[i];
            sb.append(getMessageDigest(sign.toByteArray()));
            if(i != signs.length-1){
                sb.append("\n");
            }
        }
        return sb.toString();
    }
    public static String getSignMd5(Context context){
        String packageName = context.getPackageName();
        return getSignMd5(context, packageName);
    }
    public static boolean checkSignMd5(Context context, String validMd5){
        String signMd5 = getSignMd5(context);
        return TextUtils.equals(signMd5.toUpperCase(), validMd5.toUpperCase());
    }
    private static Signature[] getRawSignature(Context context, String packageName){
        if(TextUtils.isEmpty(packageName)){
            return null;
        }
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            if(info != null){
                return info.signatures;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static String getMessageDigest(byte[] buffer) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] digestByte = mdTemp.digest();
            int j = digestByte.length;
            char[] str = new char[(j * 2)];
            int k = 0;
            for (byte byte0 : digestByte) {
                int i = k + 1;
                str[k] = hexDigits[(byte0 >>> 4) & 15];
                k = i + 1;
                str[i] = hexDigits[byte0 & 15];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

}
