package com.lakala.shoudan.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间转化工具类
 * @author jack
 */
public class DateUtil {

	private static SimpleDateFormat dateFormat = new SimpleDateFormat();

	/**
	 * 将系统时间转换成指定格式的字符串
	 * @param pattern  转化格式字符串
	 * @return
	 */
	public static String formatSystemDate(final String pattern) {
		String date = formatDate(null, pattern);
		return date;
	}

	/**
	 * 转换Date类型的时间为指定格式的字符串
	 * @param date     Date类型的时间，如果为 null，则转换系统时间
	 * @param pattern  转换格式
	 * @return
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
	 * 将时间字符串转换成指定格式的时间字符串
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

	public static String formatDateStr(String date){
		return date.replace("-","/");
	}
	/**
	 * 时间字符串从一种格式转换成另一种格式
	 * @param date     20130516
	 * @param pattern  yyyyMMdd
	 * @param targetPattern  yyyy-MM-dd (2013-05-16) 
	 * @return
	 */
	public static String formatDateStrToPattern(String date , String pattern, String targetPattern){
		dateFormat.applyPattern(pattern);
	    Date d;
        try {
            d = dateFormat.parse(date);
            return formatDate(d,targetPattern);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date ;	  
	}
}
