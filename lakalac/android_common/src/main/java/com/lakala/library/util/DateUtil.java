package com.lakala.library.util;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Vinchaos api on 13-12-12.
 * 日期工具类
 * 对日期基本的格式化,基本运算处理
 */
public class DateUtil {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat();

    /**
     * formatSystemDate
     * 将系统时间转换成指定格式的字符串
     *
     * @param pattern 转化格式字符串
     * @return String
     */
    public static String formatSystemDate(final String pattern) {
        return formatDate(null, pattern);
    }

    /**
     * formatDate
     * 转换Date类型的时间为指定格式的字符串
     *
     * @param date    Date类型的时间，如果为 null，则转换系统时间
     * @param pattern 转换格式
     * @return String
     */
    public static String formatDate(Date date, final String pattern) {
        dateFormat.applyPattern(pattern);
        String result;
        if (date == null) {
            result = dateFormat.format(new Date());
        } else {
            result = dateFormat.format(date);
        }
        return result;
    }

    /**
     * formatDateStr
     * 将指定的 milliseconds 转换为特定格式的日期字符串
     *
     * @param milliseconds 需要转换的时间（毫秒）
     * @param pattern      转换的格式
     * @return String
     */
    public static String formatDateStr(final long milliseconds, final String pattern) {
        dateFormat.applyPattern(pattern);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milliseconds);
        return dateFormat.format(c.getTime());
    }


    /**
     * 将时间字符串转换成指定格式的时间字符串
     *
     * @param date    需要转换的时间字符串
     * @param pattern 转换的格式
     * @return
     */
    public static String formatDateStr(final String date, final String pattern) {
        long timeString = Long.parseLong(date);
        dateFormat.applyPattern(pattern);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeString);
        String result = dateFormat.format(c.getTime());
        return result;
    }

    public static String formatDateStr(String date) {

        Date date1 = null;
        if (TextUtils.isEmpty(date)) {
            return "";
        }
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date1 = format1.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

        return format2.format(date1);
    }
    public static String formatDateStr_msgTime(String date) {

        Date date1 = null;
        if (TextUtils.isEmpty(date)) {
            return "";
        }
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        try {
            date1 = format1.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

        return format2.format(date1);
    }

    public static String formatDateStr2(String date) {

        Date date1 = null;
        if (TextUtils.isEmpty(date)) {
            return "";
        }
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            date1 = format1.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

        return format2.format(date1);
    }

    public static String formatDetailDate(String date) {

        return formatDateStr(date).replace("-", "/");

    }

    /**
     * formatDateStrToPattern
     * 时间字符串从一种格式转换成另一种格式
     *
     * @param date          20130516
     * @param pattern       example :"yyyyMMdd"
     * @param targetPattern example:"yyyy-MM-dd "
     * @return
     */
    public static String formatDateStrToPattern(String date, String pattern, String targetPattern) throws ParseException {
        dateFormat.applyPattern(pattern);
        Date d = dateFormat.parse(date);
        date = formatDate(d, targetPattern);
        return date;
    }

    /**
     * 将xxxxxxxx格式成xxxx-xx-xx
     */
    public static String formatDate(String date) {
        String date1 = date.substring(0, 4);
        String date2 = date.substring(4, 6);
        String date3 = date.substring(6, 8);
        return date1 + "-" + date2 + "-" + date3;
    }

    /**
     * 将xxxxxx格式成xx:xx:xx
     */
    public static String formatTime(String time) {
        String time1 = time.substring(0, 2);
        String time2 = time.substring(2, 4);
        String time3 = time.substring(4, 6);
        return time1 + ":" + time2 + ":" + time3;
    }

    /**
     * 将xxxxxxxx格式化成xxxx.xx.xx
     *
     * @return
     */
    public static String formatDateWithDian(String date) {
        String date1 = date.substring(0, 4);
        String date2 = date.substring(4, 6);
        String date3 = date.substring(6, 8);
        return date1 + "." + date2 + "." + date3;
    }

    /**
     * getWeekOfDate
     * 获取当前星期几
     *
     * @return 返回值为 int，1-7，其中1为周日，2为周一 ... 7为周六
     */
    public static int getWeekOfDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static String getNowTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(new Date());
    }

    public static String stringToDate(String strTime) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = null;
            date = formatter.parse(strTime);
            return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);
        } catch (Exception e) {
            LogUtil.print(e);
        }
        return getNowTime();
    }

    public static Date formateStringToDate(String dateStr) {
        Date date = null;
        if (TextUtils.isEmpty(dateStr)) {
            return date;
        }
        DateFormat sdf = new SimpleDateFormat(dateStr);
        try {
            date = sdf.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return date;
        }
    }

    public static String formateDateTransTime(String dateValue) {
        String result = "";
        if (TextUtils.isEmpty(dateValue)) {//交易时间按为空  获取当前时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(new Date());
        }
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            Date date2 = df.parse(calendar.get(Calendar.YEAR) + dateValue);
            SimpleDateFormat dfto = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dfto.format(date2);
        } catch (Exception e) {
            e.printStackTrace();
            return dateValue;
        }
    }

    public static String formateDateTransTime2(String dateValue) {
        String result = "";
        if (TextUtils.isEmpty(dateValue)) {//交易时间按为空  获取当前时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return sdf.format(new Date());
        }
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            Date date2 = df.parse(calendar.get(Calendar.YEAR) + dateValue);
            SimpleDateFormat dfto = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return dfto.format(date2);
        } catch (Exception e) {
            e.printStackTrace();
            return dateValue;
        }
    }

}
