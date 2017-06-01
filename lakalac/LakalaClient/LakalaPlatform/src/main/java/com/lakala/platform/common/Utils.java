package com.lakala.platform.common;

import android.text.TextUtils;

import com.lakala.platform.consts.ConstKey;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by More on 15/2/9.
 */
public class Utils {

    /**
     * 省略用户姓名名用*号代替 保留最后一个字，其余用*代替
     * @param username
     * @return
     */
    public static String formateUserNameKeepLast(String username){
        if (null==username||username.equals("")) {
            return null;
        }
        char lastChar = username.charAt(username.length()-1);
        StringBuilder sbBuilder = new StringBuilder();
        for(int i=0;i<username.length()-1;i++){
            sbBuilder.append("*");
        }
        sbBuilder.append(lastChar);
        return sbBuilder.toString();
    }


    /**
     * 创建发送方跟踪号(6为数字)Series,递增
     * @return
     */
    public static String createSeries(){

        LklPreferences pref = LklPreferences.getInstance();
        int value = pref.getInt(ConstKey.SERIES);
        if(value < 999999){
            value = value + 1;
        }else{
            value = 0 ;
        }
        String series = addMarkToStringFront(String.valueOf(value), "0", 6);
        pref.putInt(ConstKey.SERIES, value);
        return series;
    }


    /**
     * 在字符串前添加指定的字符
     * @param string 需要补全的字符
     * @param mark   填充的字符
     * @param len    补全后的字符长度
     * @return
     */
    public static String addMarkToStringFront(String string,String mark, int len) {
        StringBuilder stringBuilder = new StringBuilder();
        int length = string.length();
        int appendCount = length < len ? len-length : 0;

        for (int i = 0; i < appendCount; i++) {
            stringBuilder.append(mark);
        }
        return  stringBuilder.append(string).toString();
    }


    /**
     * 钱包请求使用的时间格式
     * @return
     */
    public static String createTdtm() {
        String date = format("yyMMddHHmmss");
        return date;
    }

    /**
     * 按照指定格式来格式系统时间
     * @deprecated 使用DateUtil类中的方法
     * @param pattern 格式(yyMMdd HH:mm:ss  ....)
     * @return
     */
    public static String format(String pattern){
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        dateFormat.applyPattern(pattern);
        String date = dateFormat.format(new Date());
        return date;
    }


    /**
     * 将分转换为元,带两个小数点
     * @param fenStr
     * @return
     */
    public static String fen2Yuan(String fenStr) {

        if (fenStr == null) return "0.00";

        //过滤非数字的情况，服务端可能返回“null”字符串，此时返回“0”
        boolean isAllDigit = Pattern.matches("[\\d.]+", fenStr);
        if (!isAllDigit) {
            return "0.00";
        }
        String yuanString = new BigDecimal(fenStr).divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        return yuanString;
    }

    /**
     * 将一个 double 型的以“元”为单位的金额转换成“分”字符串。
     * @param yuan 以“元”为单位的金额
     * @return 按12位补齐的 “分”字符串。
     */
    public static String yuan2Fen(String yuan){
        if (yuan == null || yuan.length() ==0){
            yuan = "0.0";
        }

        BigDecimal bigDecimal = new BigDecimal(yuan);

        bigDecimal = bigDecimal.multiply(new BigDecimal(100));
        bigDecimal = bigDecimal.setScale(0);

        Long fen = bigDecimal.longValue();

        return String.format("%012d", fen);
    }

    /**
     * 去除字符中间的 "空格/-/," 等间隔符
     *
     * @param string
     *            要格式化的字符
     * @return 格式化后的字符
     */
    public static String formatString(String string) {
        if(string == null) return "";
        String newString = string.replaceAll(" ", "")
                .replaceAll("-", "")
                .replaceAll(",", "");
        return newString;
    }

}
