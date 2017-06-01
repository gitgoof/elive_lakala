package com.lakala.shoudan.common.util;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtil {
    /**
     * 将字符串中的第一个金额格式化成红字。
     * @param text 字符串
     *             @return 返回格式化后的字符串对象 SpannableStringBuilder。
     */
    public static SpannableStringBuilder formatAmountToRedWord(String text){
        SpannableStringBuilder style=new SpannableStringBuilder(text);
        String split[] = text.split("[0-9]+\\.[0-9]+\\s*元",2);
        int from = 0;
        int to = 0;

        if (split != null && split.length == 1){
            from = split[0].length();
            to = text.length();
        }else if(split != null && split.length == 2){
            from = split[0].length();
            to = text.length() - split[1].length();
        }

        style.setSpan(new ForegroundColorSpan(Color.RED),from,to, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return style;
    }
    
    /**
     * 电话格式 NNN **** NNNN
     * @param phoneno
     * @return
     */
    public static String formatPhoneN3S4N4(String phoneno){
    	if (phoneno == null || phoneno.length() <11)
        return "";

        int length = phoneno.length();
        return String.format("%s **** %s",phoneno.substring(0,3),phoneno.substring(length-4,length));
   }
    
    /**
     * 将银行卡卡号格式化成 “NNNN **** **** NNNN” 形式。
     * @param cardNumber  原卡号
     * @return 格式化后的卡号
     */
    public static String formatCardNumberN4S8N4(String cardNumber){
    	 if (cardNumber == null || cardNumber.length() <8)
             return "";

         int length = cardNumber.length();
         return String.format("%s **** **** %s",cardNumber.substring(0,4),cardNumber.substring(length-4,length));
    }

    /**
     * 将银行卡卡号格式化成 “NNNN****NNNN” 形式。
     * @param cardNumber  原卡号
     * @return 格式化后的卡号
     */
    public static String formatCardNumberN4S4N4(String cardNumber){
        if (cardNumber == null || cardNumber.length() <8)
            return "";

        int length = cardNumber.length();
        return String.format("%s****%s",cardNumber.substring(0,4),cardNumber.substring(length-4,length));
    }

    /**
     * 将银行卡卡号格式化成 “NNNNNN****NNNN” 形式。
     * @param cardNumber  原卡号
     * @return 格式化后的卡号
     */
    public static String formatCardNumberN6S4N4(String cardNumber){
        if (cardNumber == null || cardNumber.length() <10)
            return "";

        int length = cardNumber.length();
        int temp = length - 10;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0;i<temp;i++){
            stringBuilder.append("*");
        }
        return cardNumber.substring(0,6) + stringBuilder.toString() + cardNumber.substring(length-4,length);
    }
    
    
    public static String removeTrim(String str){
        if(str == null){
            return "";
        }
    	return str.replace(" ", "");
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

    /**
     * 格式化金额两位小数
     * @param amount
     * @return
     */
    public static String formatTwo(double amount){
        String patten = "#0.00";
        DecimalFormat format = new DecimalFormat();
        format.applyPattern(patten);
        return format.format(amount);
    }

    public static String getWalletRechargeHint(double perLimit,double dailyLimit,double monthlyLimit){
        StringBuilder builder = new StringBuilder();
        builder.append("单笔充值限额:储蓄卡单笔充值")
                .append(perLimit)
                .append("元,单日累计最高")
                .append(dailyLimit)
                .append("元;暂不支持信用卡");
        return builder.toString();
    }
    public static String matchChinese(String input){
        String patternStr = "[\u4e00-\u9fa5]";
        return matchTarget(patternStr,input);
    }
    public static String matchTarget(String patternStr,String input){
        StringBuilder result = new StringBuilder();
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()){
            result.append(matcher.group());
        }
        return result.toString();
    }
}
