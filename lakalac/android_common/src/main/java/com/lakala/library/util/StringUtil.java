package com.lakala.library.util;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xyz on 13-12-9.
 * 应用程序级通用的处理,如 格式化等长,加前缀等.
 */
public class StringUtil {

    /**
     * isNotEmpty
     *
     * @param string
     * @return
     */
    public static boolean isNotEmpty(String string) {
        return string != null && !"".equals(string.trim()) && !"null".equals(string.trim());
    }

    /**
     * 判断字符串是否为空
     *
     * @param string
     * @return 空返回true, 非空返回false
     */
    public static boolean isEmpty(String string) {
        return !isNotEmpty(string);
    }

    /**
     * trim
     *
     * @param string
     * @return
     */
    public static String trim(String string) {
        if (string == null || string.equals("null"))
            return "";
        else
            return string.trim();
    }

    /**
     * @param o
     * @return
     */
    public static String toString(Object o) {
        return o == null ? "" : o.toString();
    }

    /**
     * formatString
     * 将string中的所有replace去掉
     *
     * @param string
     * @param replace
     * @return
     */
    public static String formatString(String string, String replace) {
        if (string == null) return "";
        String newString = string.replaceAll(replace, "");
        return newString;
    }


    /**
     * 字符串模糊处理，*号代替
     *
     * @param string 原字符
     * @param start  模糊开始下标
     * @param end    模糊结束下标
     * @return 138****5678
     */
    public static String formatStringVague(String string, int start, int end) {
        if (StringUtil.isEmpty(string)) {
            return string;
        }

        if (start < 0 || end > (string.length() - 1)) {
            return string;
        }

        StringBuilder sb = new StringBuilder();
        char[] c = string.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (i >= start && i <= end) {
                sb.append("*");
            } else {
                sb.append(c[i]);
            }
        }
        return sb.toString();
    }

    /**
     * formatString
     * 去除字符中间的 "空格/-/," 等间隔符
     *
     * @param string 要格式化的字符
     * @return 格式化后的字符
     */
    public static String formatString(String string) {
        if (string == null) return "";
        String newString = string.replaceAll(" ", "")
                .replaceAll("-", "")
                .replaceAll(",", "");
        return newString;
    }

    /**
     * 格式话文件内容，去空格换行符
     */
    public static String formatFileContent(String string) {
        if (string == null) return "";
        String newString = string.replaceAll("\n", "")
                .replaceAll("\t", "")
                .replaceAll("\r", "");
        return newString;
    }

    public static String getWalletDrawInfo(String hint1, String hint2, double amount) {
        String str = hint1 + amount + hint2;
        return str;
    }

    /**
     * suffixSpaceToString
     * 字符串后端加全角空格，对齐成指定数量个汉字
     *
     * @param string
     * @param len
     * @return
     */
    public static String suffixSpaceToString(String string, int len) {
        StringBuilder stringBuilder = new StringBuilder();
        int length = string.length();
        int appendCount = length < len ? len - length : 0;

        for (int i = 0; i < appendCount; i++) {
            stringBuilder.append("　");
        }
        return string + stringBuilder.toString();
    }

    /**
     * 字符串前端加全角空格，对齐成5个汉字
     *
     * @param string
     * @return
     */
    public static String addSpaceToStringFront(String string) {
        return addSpaceToStringFront(string, 5);
    }

    /**
     * 电话格式 NNN **** NNNN
     *
     * @param phoneno
     * @return
     */
    public static String formatPhoneN3S4N4(String phoneno) {
        if (phoneno == null || phoneno.length() < 11)
            return "";

        int length = phoneno.length();
        return String.format("%s **** %s", phoneno.substring(0, 3), phoneno.substring(length - 4, length));
    }

    /**
     * addSpaceToStringFront
     * 字符串前端加全角空格，对齐成指定数量个汉字
     *
     * @param string
     * @param len    指定对齐位数
     * @return
     */
    public static String addSpaceToStringFront(String string, int len) {
        StringBuilder stringBuilder = new StringBuilder();
        int length = string.length();
        int appendCount = length < len ? len - length : 0;

        for (int i = 0; i < appendCount; i++) {
            stringBuilder.append("　");
        }
        return stringBuilder.toString() + string;
    }

    /**
     * 判断某字符串是否是网址
     *
     * @param urlString
     * @return
     */
    public static boolean isURL(String urlString) {
        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern patt = Pattern.compile(regex);
        Matcher matcher = patt.matcher(urlString);
        boolean isMatch = matcher.matches();
        return isMatch;
    }

    /**
     * 将一个字符串转换成整型值，如果 number 不能转换成整型则返回 defaultValue
     *
     * @param number 一个数字字符串
     * @return 转换后的整型值。
     */
    public static int StringToInt(String number, int defaultValue) {
        if (StringUtil.isEmpty(number)) {
            return defaultValue;
        } else {
            try {
                return Integer.valueOf(number);
            } catch (Exception e) {
                return defaultValue;
            }
        }
    }

    /**
     * 将一个字符串转换成浮点型值，如果 number 不能转换成整型则返回 defaultValue
     *
     * @param number 一个数字字符串
     * @return 转换后的整型值。
     */
    public static Float StringTofloat(String number, float defaultValue) {
        if (StringUtil.isEmpty(number)) {
            return defaultValue;
        } else {
            try {
                return Float.valueOf(number);
            } catch (Exception e) {
                return defaultValue;
            }
        }
    }

    /**
     * 将字符串中的所有的数字格式化成指定颜色。
     *
     * @param text  字符串
     * @param color 颜色
     * @return 返回格式化后的字符串对象 SpannableStringBuilder。
     */
    public static SpannableStringBuilder formatNumberColor(String text, int color) {
        return formatTextColor(text, "[0-9]+\\.*[0-9]*", color);
    }

    /**
     * 将字符串中与正则表达式匹配的文字设置成指定颜色。
     *
     * @param text          字符串
     * @param patternString 用于筛选的正则表达式。
     * @param color         颜色
     * @return 返回格式化后的字符串对象 SpannableStringBuilder。
     */
    public static SpannableStringBuilder formatTextColor(String text, String patternString, int color) {
        if (text == null)
            return new SpannableStringBuilder("");

        SpannableStringBuilder style = new SpannableStringBuilder(text);

        if (patternString == null)
            return style;

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            style.setSpan(new ForegroundColorSpan(color), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return style;
    }


    /**
     * 。是否是纯数字字符串
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 是否为纯字母
     *
     * @param str
     * @return
     */
    public static boolean isLetters(String str) {
        return str.matches("[a-zA-Z]+");
    }

    /**
     * 判断字符串中是否有连续相同字符长度超过4位
     *
     * @param str
     * @return
     */
    public static boolean isSeriesSame(String str) {
        return isSeriesSame(str, 4);
    }

    /**
     * 判断字符串中是否有连续相同字符
     *
     * @param str             String
     * @param MAX_SAME_LENGTH 可以存在连续相同字符串的最大长度
     * @return boolean
     */
    public static boolean isSeriesSame(String str, int MAX_SAME_LENGTH) {
        boolean same = false;
        String regex;
        String temp;
        int MAX = MAX_SAME_LENGTH + 1;
        for (int i = 0; i < str.length(); i++) {
            //如果校验剩余长度小于或等于允许的最大长度，那么 break
            if ((str.length() - i) < MAX) {
                break;
            }

            regex = getSeriesString(str.substring(i, i + 1), MAX);
            temp = str.substring(i, MAX + i);
            same = temp.equals(regex);

            //如果相同则跳出循环
            if (same) {
                break;
            }
        }
        return same;
    }

    private static String getSeriesString(String item, int len) {
        String temp = "";
        for (int i = 0; i < len; i++) {
            temp = temp.concat(item);
        }
        return temp;
    }

    //升序
    private static String upOrderStr;
    //降序
    private static String downOrderStr;

    static {
        for (int i = 33; i < 127; i++) {
            upOrderStr += Character.toChars(i)[0];
        }
        downOrderStr = new StringBuilder(upOrderStr).reverse().toString();
    }

    /**
     * 判断字符串是否有顺序
     *
     * @param str
     * @return
     */
    public static boolean isOrder(String str) {
        if (!str.matches("((\\d)|([a-z])|([A-Z]))+")) {
            return false;
        }
        return upOrderStr.contains(str) || downOrderStr.contains(str);
    }

    /**
     * 判定输入汉字
     *
     * @param c
     * @return
     */
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 检测String是否全是中文
     *
     * @param name
     * @return
     */
    public static boolean checkNameChese(String name) {
        boolean res = true;
        char[] cTemp = name.toCharArray();
        for (int i = 0; i < name.length(); i++) {
            if (!isChinese(cTemp[i])) {
                res = false;
                break;
            }
        }
        return res;
    }

    /**
     * 返回长度为【strLength】的随机数，在前面补0
     */
    public static String getRandom(int strLength) {

        Random rm = new Random();
        // 获得随机数
        double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);
        // 将获得的获得随机数转化为字符串
        String fixLenthString = String.valueOf(pross);
        // 返回固定的长度的随机数
        return fixLenthString.substring(1, strLength + 1);
    }


    /**********************
     * 金额 ------开始
     ******************************/
    public static String formatDisplayAmount(String amount) {
        return formatAmount(amount) + "元";
    }

    /**
     * formatAmount
     * 金额格式化
     *
     * @param s 金额
     * @return 格式后的金额(###,###.##)
     */
    public static String formatAmount(String s) {
        return formatAmount(s, false);
    }

    /**
     * formatAmount
     * 金额格式化
     *
     * @param s          金额
     * @param isInitNull 是否初始化为空字符
     * @return 格式后的金额(###,###.##)
     */
    public static String formatAmount(String s, boolean isInitNull) {
        String result = "";
        if (!isNotEmpty(s)) return "";
        String temp = s;
        s = formatString(s);// 去除string可能的分隔符
        double num = 0.0;
        try {
            num = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            //TODO throw something..
        }
        if (num == 0) {
            if (isInitNull) {
                return "";
            } else {
                return "0.00";
            }
        }
        if (num < 1) {//小于1情况特殊处理
            if (s.length() == 4) {//0.05
                return temp;
            } else if (s.length() == 3) {//0.5
                return temp + "0";
            }
        }
        NumberFormat forMater = new DecimalFormat("#,###.00");
        result = forMater.format(num);

        if (result.startsWith(".")) {
            result = "0" + result;
        }
        return result;
    }

    /**
     * 检查金额是否有效
     *
     * @param amountString 金额
     * @return
     */
    public static boolean isAmountVaild(String amountString) {
        if (amountString != null && amountString.length() > 0) {
            double amount = 0.0;
            try {
                amount = Double.parseDouble(amountString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 金额必须大于0
            if (amount > 0.001) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将银行卡卡号格式化成 “NNNNNN****NNNN” 形式。
     *
     * @param cardNumber 原卡号
     * @return 格式化后的卡号
     */
    public static String formatCardNumberN6S4N4(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 10)
            return "";
        String replaceString = "";
        int length = cardNumber.length();
        if (length == 19) {
            replaceString = "%s*********%s";
        } else if (length == 17) {
            replaceString = "%s*******%s";
        } else if (length == 16) {
            replaceString = "%s******%s";
        }else if(length==18){
            replaceString = "%s********%s";
        }else{
            replaceString="%s*****%s";
        }
        return String.format(replaceString, cardNumber.substring(0, 6), cardNumber.substring(length - 4, length));
    }

    public static String formatRevocationLastFour(String cardNo, String bankName, String cardType) {

        return "尾号" + cardNo + "的" + bankName + cardType;
    }

    /**
     * 企业级账户号format
     *
     * @param cardNumber
     * @return
     */
    public static String formatCompanyAccount(String cardNumber) {

        try {

            int length = cardNumber.length();
            StringBuilder builder = new StringBuilder();
            builder.append(cardNumber.substring(0, 1));
            for (int i = 0; i < (length - 1); i++) {
                builder.append("*");
            }
            builder.append(cardNumber.substring(length - 1, length));

            return builder.toString();

        } catch (Exception e) {
            return "";
        }
    }

    public enum PasswordLvl {

        SIMPLE,
        LENGTH_ERROR,
        CHAR_REPEAT_4_TIMES,
        EMPTY,
        NORMAL


    }

    /**
     * 检测密码强度
     *
     * @param password
     * @return 好：GOOD 一般：GENERAL 坏：BAD
     */
    public static PasswordLvl checkPWLevel(String password, int max, int min) {

        if (TextUtils.isEmpty(password)) {
            return PasswordLvl.EMPTY;
        }

        if (password.length() > max || password.length() < min) {
            return PasswordLvl.LENGTH_ERROR;
        }

        //判断是否有连续四个相同字符
        if (Pattern.compile("([0-9a-zA-Z])\\1{3}").matcher(password).find()) {

            return PasswordLvl.CHAR_REPEAT_4_TIMES;
        }


        //判断是否有a-z A-Z
        int count = 0;
        if (Pattern.compile("(?i)[a-zA-Z]").matcher(password).find()) {
            count += 10;
        }
        //判断是否有数字
        if (Pattern.compile("(?i)[0-9]").matcher(password).find()) {
            count += 10;
        }
        //判断是否有其他字符
        if (Pattern.compile("(?i)[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]").matcher(password).find()) {
            count += 10;
        }

        if (count <= 10) {
            return PasswordLvl.SIMPLE;
        }


        return PasswordLvl.NORMAL;
    }

}
