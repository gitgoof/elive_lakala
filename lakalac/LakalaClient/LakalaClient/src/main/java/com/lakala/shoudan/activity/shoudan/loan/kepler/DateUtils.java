package com.lakala.shoudan.activity.shoudan.loan.kepler;

import android.annotation.SuppressLint;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日期工具类
 * 
 * @author EX-LIAOZUSHENG001
 * 
 */
@SuppressLint("SimpleDateFormat")
public class DateUtils {
	public static final String DATE_FORMAT_SP = "yyyy-MM-dd";
	public static final String DATE_FORMAT = "yyyyMMdd";
	public static final String NUMBER_FORMAT = "#,###";
	public static final String NUMBER_FORMAT_DIAN ="0.00";
	public static final String NUMBER_FORMAT_QIAN_DIAN ="#,###.00";
	public static final String NUMBER_LILV = "0.####";

	/**
	 * 获得日期  格式：X月Y日
	 * @param todayStr
	 * @param dateFormat
	 * @return String 格式：X月Y日
	 */
	public static String getFormatByString(String todayStr,String dateFormat){
		Date today = getDateByStr(todayStr, dateFormat);
		return getCurMonth(today)+"月"+getCurDay(today)+"日";
	}
	/**
	 * 根据dateformat获得格式日期字符串 
	 * @param date
	 * @param dateFormat
	 * @return 格式日期字符串
	 */
	public static String getFormatDate(Date date,String dateFormat){
		return new SimpleDateFormat(dateFormat).format(date);
	}
	/**
	 * 将yyyyMMdd格式的日期字符串  转换成yyyy-MM-dd形式输出
	 * @param dayStr
	 * @return 日期 以yyyy-MM-dd形式输出
	 */
	public static String getStringByDateStr(String dayStr){
		return DateUtils.getFormatDate(DateUtils.getDateByStr(dayStr, DateUtils.DATE_FORMAT), DateUtils.DATE_FORMAT_SP);
	}
	
	
	/**
	 * 根据Date 显示 X月Y日
	 * @param date
	 * @return 显示 X月Y日
	 */
	public static String getStringByDate(Date date){
		return getCurYear(date)+"年"+getCurMonth(date)+"月"+getCurDay(date)+"日";
	}
	
	
	
	/**
	 * 获取下一个固定还款日
	 * 
	 * @param y
	 *            固定还款日
	 * @param todayStr
	 *            今天日期的字符串
	 * @param dateFormat
	 *            日期格式化
	 * @return 下一个固定还款日  Date类型
	 */
	public static Date getNextGuDingRepayDay(int y, String todayStr,
			String dateFormat) {
		Date todayDate = getDateByStr(todayStr, dateFormat);
		int year = getCurYear(todayDate);
		int t = getCurMonth(todayDate);
		int x = getCurDay(todayDate);

		if (x >= y) {
			t += 2;
		} else {
			t++;
		}
		if (t > 12) {
			year++;
		}
		t = t % 12;
		int lastDay = getLastDayByDate(getDateByYMD(year, t, y));
		if (y > lastDay) {
			y = lastDay;
		}
		
		return getDateByYMD(year, t, y); 
	}

	/**
	 * 获取下一个账单日
	 * 
	 * @param dateStr
	 *            当前日期字符串
	 * @param dateFormat
	 *            yyyyMMdd
	 * @return Date  下一个账单日
	 */
	public static Date getNextZhangDanRi(String dateStr, String dateFormat) {
		Date todayDate = getDateByStr(dateStr, dateFormat);
		int year = getCurYear(todayDate);
		int tMonth = getCurMonth(todayDate);
		int yDay = getCurDay(todayDate);
		//如果为当月1日 则取本月最后一天为下一个账单日
		if(yDay==1){
			int lastDay = getLastDayByDate(getDateByYMD(year, tMonth, yDay));
			return getDateByYMD(year, tMonth, lastDay); 
		}
		
		tMonth++;
		if (tMonth > 12) {
			year++;
		}
		tMonth = tMonth % 12;
		yDay--;
		int lastDay = getLastDayByDate(getDateByYMD(year, tMonth, yDay));
		if (yDay > lastDay) {
			yDay = lastDay;
		}
		return getDateByYMD(year, tMonth, yDay);
	}

	/**
	 * 格式化额度显示
	 * 
	 * @param money
	 * @param moneyFormat
	 * @return String  格式化金额显示
	 */
	public static String getMoneyByFormat(double money, String moneyFormat) {
		DecimalFormat df = new DecimalFormat(moneyFormat);
		return df.format(money);
	}
	/**
	 * 格式化额度显示
	 * @param lilv
	 * @param moneyFormat
	 * @return float 格式化额度显示
	 */
	public static float getlilvByFormat(double lilv, String moneyFormat) {
		DecimalFormat df = new DecimalFormat(moneyFormat);
		return Float.parseFloat(df.format(lilv));
	}
	/**
	 * 获得最小还款额
	 * @param nextZDR 下一个账单日
	 * @param todayStr 今天
	 * @param danbaolv 每日担保费率
	 * @param daylilv 每日利率
	 * @param dyEdu 本次动用额度
	 * @param usedEdu 已用额度   在借款页面 已用额度为0， 在动用额度页面，有已用额度的数值
	 * @return String 获得最小还款额
	 */
	public static String getMinRepayEdu(Date nextZDR, Date todayStr, double danbaolv, double daylilv, double dyEdu,
			double usedEdu) {
		int betweenDays = getBetweenDay(nextZDR, todayStr);
		float lilv = getlilvByFormat((danbaolv+daylilv),NUMBER_LILV);
		String minRepay = getMoneyByFormat((betweenDays*lilv*(dyEdu+usedEdu)),NUMBER_FORMAT_QIAN_DIAN);
		return minRepay;
	}

	
	/**
	 * 获得间隔天数
	 * @param nextZDR 下一个日期
	 * @param today   今天
	 * @return int 间隔天数
	 */
	public static int getBetweenDay(Date nextZDR, Date today) {
		long betweenDays = 0;
		long DAY = 24L*60L*60L*1000L;
		try {
			betweenDays = (nextZDR.getTime()-today.getTime())/DAY+1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (int)betweenDays;
	}

	
	
	public static int getDaysBetween(Date startDate, Date endDate) {   
        Calendar fromCalendar = Calendar.getInstance();   
        fromCalendar.setTime(startDate);   
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);   
        fromCalendar.set(Calendar.MINUTE, 0);   
        fromCalendar.set(Calendar.SECOND, 0);   
        fromCalendar.set(Calendar.MILLISECOND, 0);   
  
        Calendar toCalendar = Calendar.getInstance();   
        toCalendar.setTime(endDate);   
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);   
        toCalendar.set(Calendar.MINUTE, 0);   
        toCalendar.set(Calendar.SECOND, 0);   
        toCalendar.set(Calendar.MILLISECOND, 0);   
  
        return (int)(toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24);   
    }  
	
	/**
	 * 获得间隔天数
	 * @param nextZDR 下一个日期
	 * @param today 今天
	 * @param formatStr
	 * @return
	 */
	public static int getBetweenDay(String nextZDR, String today,String formatStr) {
		long betweenDays = 0;
		long DAY = 24L*60L*60L*1000L;
		
		try {
			betweenDays = (getDateByStr(nextZDR,formatStr).getTime()-getDateByStr(today,formatStr).getTime())/DAY+1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (int)betweenDays;
	}
	
	/**
	 * 根据年月日获得date
	 * @param year
	 * @param month
	 * @param day
	 * @return 根据年月日获得date
	 */
	public static Date getDateByYMD(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month-1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		return cal.getTime();
	}

	/**
	 * 根据日期字符串获得Date
	 * @param dateStr
	 * @param format
	 * @return Date类型 日期字符串获得Date
	 */
	public static Date getDateByStr(String dateStr, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	/**
	 * 根据日期Date类型 获得年
	 * @param date
	 * @return int 
	 */
	public static int getCurYear(Date date) {
		int year = 1;
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
			year = cal.get(Calendar.YEAR);
		}
		return year;
	}
	/**
	 * 根据日期Date类型 获得月
	 * @param date
	 * @return int 
	 */
	public static int getCurMonth(Date date) {
		int month = 0;
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
			month = cal.get(Calendar.MONTH);
		}
		return month + 1;
	}
	/**
	 * 根据日期Date类型 获得日
	 * @param date
	 * @return int 
	 */
	public static int getCurDay(Date date) {
		int day = 0;
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
			day = cal.get(Calendar.DAY_OF_MONTH);
		}
		return day;
	}

	/**
	 * 获得日期中的当月最后一天
	 * 
	 * @param date
	 * @return int
	 */
	private static int getLastDayByDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		return lastDay;
	}
	/**
	 * 获得日期列表 用来填充到下拉列表中
	 * @param startDayStr 开始时间
	 * @param endDayStr   结束时间
	 * @param dateFormat
	 * @return List<Integer>
	 */
	public static List<Integer> getDayArrayByBetweenStartAndEnd(String startDayStr,String endDayStr,String dateFormat){
		List<Integer> list = new ArrayList<Integer>();
		Date startDate = getDateByStr(startDayStr,dateFormat);
		Date endDate = getDateByStr(endDayStr, dateFormat);
		
		int startMonth = getCurMonth(startDate);
		int startDay = getCurDay(startDate);
		
		int endMonth = getCurMonth(endDate);
		int endDay = getCurDay(endDate);
		
		if(startMonth == endMonth){
			for(int i=startDay;i<=endDay;i++){
				list.add(i);
			}
		}else {
			for(int i=startDay;i<=31;i++){
				list.add(i);
			}
			for(int i=1;i<=endDay;i++){
				list.add(i);
			}
		}
		return list;
	}
	
	
	public static String getNowDate(String format){
		
//		Date date = new Date();

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date());
	}
	
	
	
	

}
