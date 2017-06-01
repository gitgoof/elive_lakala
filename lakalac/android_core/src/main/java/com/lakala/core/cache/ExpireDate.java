package com.lakala.core.cache;

import android.annotation.SuppressLint;

import com.lakala.library.util.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 数据缓存过期时间，该对象内部使用UTC时间。
 * @author Michael
 */
@SuppressLint("SimpleDateFormat")
public class ExpireDate {
	private static SimpleDateFormat dateFormat;
	
	//初始化静态变量
	static{
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	private Date expireDate;

	@SuppressLint("SimpleDateFormat")
	private ExpireDate(Date expireDate){
		this.expireDate = expireDate;

	}
	
	@Override
	public String toString() {
		//返回日期的 IS0-8601 格式（UTC）。 
		return dateFormat.format(expireDate);
	}
	
	/**
	 * 判定当前对象是否过期
	 * @return
	 */
	public boolean isExpire(){
		return (new Date()).compareTo(expireDate) > 0;
	}
	
	/**
	 * 返回过期时间
	 * @return
	 */
	public Date getExpireDate(){
		return expireDate;
	}
	/**
	 * 返回一个过期时间
	 * @param seconds  以当前时间为起点的过期秒数。
	 * @return
	 */
	public static ExpireDate expiredAfterSeconds(int seconds){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.SECOND, seconds);
		return new ExpireDate(calendar.getTime());
	}
	
	/**
	 * 返回一个过期时间
	 * @param minutes  以当前时间为起点的过期分钟数。
	 * @return
	 */
	public static ExpireDate expiredAfterMinutes(int minutes){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, minutes);
		return new ExpireDate(calendar.getTime());
	}
	
	/**
	 * 返回一个过期时间
	 * @param hours  以当前时间为起点的过期小时数。
	 * @return
	 */
	public static ExpireDate expiredAfterHours(int hours){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.HOUR, hours);
		return new ExpireDate(calendar.getTime());
	}
	
	/**
	 * 返回一个过期时间
	 * @param days  以当前时间为起点的过期天数。
	 * @return
	 */
	public static ExpireDate expiredAfterDays(int days){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_YEAR, days);
		return new ExpireDate(calendar.getTime());
	}
	
	
	/**
	 * 返回一个过期时间
	 * @param weeks  以当前时间为起点的过期月数。
	 * @return
	 */
	public static ExpireDate expiredAfterWeeks(int weeks){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.WEEK_OF_YEAR,weeks);
		return new ExpireDate(calendar.getTime());
	}
	
	/**
	 * 返回一个过期时间
	 * @param months 以当前时间为起点的过期月数。
	 * @return
	 */
	public static ExpireDate expiredAfterMonths(int months){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH,months);
		return new ExpireDate(calendar.getTime());
	}
	
	/**
	 * 返回一个过期时间
	 * @param day     以当前时间为起点的过期天数。
	 * @param hour    以当前时间为起点的过期小时数。
	 * @param minute  以当前时间为起点的过期分钟数。
	 * @return
	 */
	public static ExpireDate expiredAfterDayHourMinute(int day, int hour, int minute){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_YEAR, day);
		calendar.add(Calendar.HOUR, hour);
		calendar.add(Calendar.MINUTE, minute);
		return new ExpireDate(calendar.getTime());
	}
	
	/**
	 * 从一个 IS0-8601 格式的 UTC 时间字符串解析出过期时间对象，如果时间格式错误则返回 null。
	 * @param utc  IS0-8601 格式的 UTC 时间 "YYYY-MM-DD HH:MM:SS.SSS"。
	 * @return
	 */
	public static ExpireDate expireWithUTC(String utc){
		if (StringUtil.isEmpty(utc)){
			return null;
		}
		
		try {
			return new ExpireDate(dateFormat.parse(utc));
		} catch (ParseException e) {
			return null;
		}
	}
}
