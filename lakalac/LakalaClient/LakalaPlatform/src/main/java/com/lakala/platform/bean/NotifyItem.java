package com.lakala.platform.bean;

import java.io.Serializable;

/**
 * Created by HUASHO on 2015/1/20.
 * 提醒Item
 */
public class NotifyItem implements Serializable{

    public int categoryID; // 类别ID
    public int flagCategory = 0; // 提醒类型标识（1.还款提醒 0.提醒服务）
    public String notifyCategory; // 提醒类别
    public boolean isDefaultCategory; // 是否是缺省的类别
    public boolean isEnable; // 是否开启提醒
    public int notifyDay; // 提醒的日
    public int notifyDateTimeHour;// 提醒的时间(小时)
    public int notifyDateTimeMinute;// 提醒的时间(分钟)
    public boolean isRepeat;// 是否每月重复
    public String notifyContent; // 提醒内容
    public String dataString; // 信用卡号
    public boolean isVisible;// 是否显示在列表
    public boolean isNeedAdd;// 是否要添加到列表
}
