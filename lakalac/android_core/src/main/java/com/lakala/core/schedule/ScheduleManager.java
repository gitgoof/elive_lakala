package com.lakala.core.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


/**
 *  Created by L.L on 13-12-10.
 *  <p>计划任务处理类,使用说明 :
 *  <p>1.添加删除计划任务必须有 uniqueKey 与之对应
 *  <p>2.创建 {@link ScheduleExecutor} 的子类来实现具体的业务逻辑;
 *  <p>3.使用 {@link ScheduleDate} 来设置计划任务时间;
 *  <p>4.在manifest中注册 {@link ScheduleReceiver} 或者其子类;
 *  <p>5.添加Action : "packagename.schedule" (e.g: "com.lakala.mobilebank.schedule") 过滤,添加设置闹钟的权限
 */
public final class ScheduleManager {
    public static final String END_OF_ACTION = ".schedule";

    /**
     * 广播Action  根据包名来构建   packagename.schedule
     */
    private String action;
    private Context context ;
    private AlarmManager alarmManager;

    public ScheduleManager(Context context) {
        this.context        = context;
        this.action         = context.getPackageName() + END_OF_ACTION;
        this.alarmManager   = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * 添加计划任务
     * @param uniqueKey                                     计划任务标示
     * @param date  {@link ScheduleDate}                    计划任务执行时间相关参数
     * @param scheduleExecutor   {@link ScheduleExecutor}   计划任务执行单元
     */
    public void addTask(String uniqueKey,ScheduleDate date,ScheduleExecutor scheduleExecutor) {
        removeTask(uniqueKey);
        Intent intent = new Intent(action);
        intent.putExtra(ScheduleManager.class.getName(), uniqueKey);
        intent.putExtra(ScheduleDate.class.getName(), date);
        intent.putExtra(ScheduleExecutor.class.getName(), scheduleExecutor);

        PendingIntent pi = PendingIntent.getBroadcast(context, uniqueKey.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (!date.isRepeatable()) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTriggerAtTime(), pi);
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, date.getTriggerAtTime(), date.getInterval(), pi);
        }
    }

    /**
     * 停止计划任务
     * @param uniqueKey 计划任务标示
     */
    public void removeTask(String uniqueKey) {
        Intent intent = new Intent(action);
        alarmManager.cancel(PendingIntent.getBroadcast(context,uniqueKey.hashCode(),intent,PendingIntent.FLAG_UPDATE_CURRENT));
    }

}
