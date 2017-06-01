package com.lakala.ui.calendar;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * 日历选择页面传入参数类
 * Created by xyz on 13-10-22.
 */

public class CalendarParams implements Serializable {

    private Date selectedDate;

    private Calendar startCalendar;
    /**
     * 票类型
     */
    public enum TicketType{
        AIR,        //飞机票
        TRAIN       //火车票
    }

    public TicketType ticketType;   //车票类型
    public int preSalePeriod;       //如果是火车票，则显示预售期

    /**
     * 构造日历选择器
     * @param ticketType    票类型，飞机票或火车票
     * @param preSalePeriod 预售期，如果是飞机票可忽略此参数，火车票可设置此参数，如果不设置，默认预售期为20天
     */
    public CalendarParams(TicketType ticketType, int preSalePeriod){
        this.ticketType = ticketType;
        //默认显示20天的预售期
        this.preSalePeriod =  preSalePeriod > 0 ? preSalePeriod : 20;
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }

    /**
     * 得到出发日期，如果没有设置出发日期，则返回当前日期
     * @return
     */
    public Calendar getStartCalendar() {
        return startCalendar == null ? Calendar.getInstance() : startCalendar;
    }

    /**
     * 设置出发日期
     * @param startCalendar
     */
    public void setStartCalendar(Calendar startCalendar) {
        this.startCalendar = startCalendar;
    }
}
