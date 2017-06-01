package com.lakala.library.util;

import android.text.TextUtils;

/**
 * Created by HUASHO on 2015/1/27.
 * 卡号处理工具类
 */
public class CardUtil {

    /**
     * 诊断卡号是否合法
     * @param cardNumber
     * @return
     */
    private static boolean checkCardNumber(String cardNumber) {
        if (cardNumber != null && cardNumber.length() >= 15
                && cardNumber.length() <= 19) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 对公账户，格式话，前1后1，其余用*代替
     * @param cardNumber
     * @return
     */
    public static String formateCardNumberOfCompany(String cardNumber){
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

    /**
     * 格式化卡号，前6后4，中间根据位数以星号显示
     * @param cardNumber  卡号
     */
    public static String formatCardNumberWithStar(String cardNumber) {

        return formatCardNumberWithStar(cardNumber, "*");
    }

    /**
     * 格式化卡号，前6后4，中间根据位数以星号显示
     * @param cardNumber  卡号
     * @param interval	  分隔符
     */
    public static String formatCardNumberWithStar(String cardNumber,String interval) {
        if (!checkCardNumber(cardNumber)) {
            return cardNumber;
        }
        int length = cardNumber.length();
        StringBuilder builder = new StringBuilder();
        builder.append(cardNumber.substring(0, 6));
        for (int i = 0; i < (length - 10); i++) {
            builder.append(interval);
        }
        builder.append(cardNumber.substring(length - 4, length));

        return builder.toString();
    }

    /**
     * 格式化卡号，四个一组，中间以空格隔开
     * @param cardNumber 卡号
     * @return 格式化之后的卡号
     */
    public static String formatCardNumberWithSpace(String cardNumber) {
        if(TextUtils.isEmpty(cardNumber)){
            return "";
        }
        cardNumber = cardNumber.replaceAll(" ", "");
        char[] chars = cardNumber.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (i != 0 && i % 4 == 0) {
                builder.append(" ");
            }
            builder.append(chars[i]);
        }
        return builder.toString();
    }
}
