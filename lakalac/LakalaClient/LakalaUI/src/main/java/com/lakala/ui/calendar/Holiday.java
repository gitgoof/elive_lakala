package com.lakala.ui.calendar;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 节假日<br/>
 * 保存一年之内的公历及农历假期
 * Created by xyz on 13-10-18.
 */
public class Holiday {

    /**
     * 保存节日数据，key为日期（201341，表示2013年4月1日），value为LunarCalendar对象
     */
    public static Map<String,LunarCalendar> holidays = new HashMap<String, LunarCalendar>();


    /**
     * 初始化一年之内的节日数据<br/>
     */
    public static synchronized void init(){
        if (holidays.isEmpty()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    initHolidays();
                }
            }).start();
        }
    }


    private static void initHolidays() {
        //今天
        Calendar lastMonth = Calendar.getInstance();
        lastMonth.add(Calendar.MONTH,-1);

        //明年
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        nextYear.add(Calendar.MONTH,-1);

        while (lastMonth.before(nextYear)) {
            LunarCalendar lunarCalendar = new LunarCalendar(lastMonth.getTimeInMillis());
            if (lunarCalendar.isHoliday()) {
                String key = String.format("%d%d%d",
                        lastMonth.get(Calendar.YEAR),
                        lastMonth.get(Calendar.MONTH),
                        lastMonth.get(Calendar.DAY_OF_MONTH));
                holidays.put(key,lunarCalendar);
                String holidayName = lunarCalendar.getSFestivalName();
                //首先获取公历节日名称，如果公历节日名称为空，则获取农历节日名称
                holidayName = "".equals(holidayName) ? lunarCalendar.getLFestivalName() : holidayName;
                Logr.d("key:%s,value:%s",key,holidayName);
            }
            lastMonth.add(Calendar.DAY_OF_MONTH, 1);
        }
    }
}
