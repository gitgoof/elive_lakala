package com.lakala.ui.calendar;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * 自定义日期类<br/>
 * 分别返回年、月、日,及当前日期为周几
 * Created by xyz on 13-10-18.
 */
public class ResultCalendar implements Serializable {

    private Calendar mCalendar;

    private Date mDate;

    private String[] week = {"周日","周一","周二","周三","周四","周五","周六"};

    public ResultCalendar(Date date){
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(date);
        setDate(date);
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    /**
     * 获取日历中field属性对应的值
     * @param field
     * @return
     */
    public int get(int field){
        return mCalendar.get(field);
    }

    /**
     * 获取当天属于周几
     * @return
     */
    public String getWeek(){
        int iweek = mCalendar.get(Calendar.DAY_OF_WEEK)-1;
        return week[iweek];
    }

}
