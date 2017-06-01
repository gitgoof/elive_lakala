package com.lakala.elive.common.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wenhaogu on 2015/12/30.
 */
public class VerifyUtil {
    /**
     * 密码
     *
     * @return
     */
    public static boolean isPwd(String pwd) {
        int length = pwd.length();
        if (length >= 6 && length < 20) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * author:huanc modify
     *
     * @param regex 正则表达式
     * @param str   要匹配的字符串
     * @return
     */
    private static boolean match(String regex, String str) {
        if (str == null || str.equals("")) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * author:huanc modify
     *
     * @param no
     * @return
     */
    public static boolean isPassword(String no) {
        String regex = "^[A-Za-z0-9]+$"; //
        return match(regex, no);
    }

    /**
     * author:huanc modify
     * 是否是手机号码
     */
    public static boolean isMobilehone(String no) {
        // 以13为开头的号码，以15开头的号码除了154的号码，18开头的号码[180.185.186.187.188.189].8位号码
        //新增了以17开头的号码
        String regex = "^1(3[0-9]|4[0-9]|5[0-9]|7[0-9]|8[0-9])\\d{8}$"; // 手机

        return match(regex, no);
    }

    /**
     * author:huanc modify
     * 住址
     */
    public static boolean isAddress(String address) {

        String regex = "^[\\u4E00-\\u9FA5A-Za-z\\d\\-\\_]+$"; // 手机

        return match(regex, address);
    }


    public static boolean isTelephone(String no) {

        String regex = "^0(10|2[0-5789]|\\d{3})\\d{7,8}$"; // 固话

        return match(regex, no);
    }


    /**
     * 手机号中间四位隐藏
     *
     * @param tel
     * @return
     */
    public static String phoneNuberHide(String tel) {
        tel = tel.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        return tel;
    }


    /**
     * author huangc
     *
     * @param context
     * @return 获取唯一序列号
     */
    public static String getIMEI(Context context) {
        TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        return TelephonyMgr.getDeviceId();

    }

    /**
     * 验证邮政编码
     *
     * @param post
     * @return
     */
    public static boolean checkPost(String post) {
        if (post.matches("[1-9]\\d{5}(?!\\d)")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isBirthday(String birthday) {
        if (birthday.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 验证邮箱
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String regex = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";

        return match(regex, email);
    }

    /**
     * 验证18为身份证号  年份 1800-2399
     *
     * @param IDNumber
     * @return
     */
    public static boolean isIDNumber(String IDNumber) {
        String regex = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";

        return match(regex, IDNumber);
    }
}
