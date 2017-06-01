package com.lakala.core.schedule;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

/**
 * Created by LL on 13-12-23.
 */
public final class ScheduleDate implements Parcelable{

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeSerializable(unit);
        out.writeSerializable(triggerTime);
        out.writeInt(num);
        out.writeLong(expiredTime);
    }

    public static final Creator<ScheduleDate> CREATOR = new Creator<ScheduleDate>() {

        @Override
        public ScheduleDate createFromParcel(Parcel in) {
            return new ScheduleDate(in);
        }

        @Override
        public ScheduleDate[] newArray(int i) {
            return new ScheduleDate[i];
        }
    };

    /**
     * 单位基数  e.g : unit = HOUR && num = 5; 表示每5个小时触发一次
     */
    private int num;
    /**
     * 间隔单位
     */
    private IntervalUnit unit = IntervalUnit.NULL;
    /**
     * 触发时间
      */
    private Calendar triggerTime;
    /**
     * 过期时间
     */
    private long expiredTime = Long.MAX_VALUE;

    /**
     * 序列化构造函数
     */
    private ScheduleDate(Parcel in){
        unit           = (IntervalUnit) in.readSerializable();
        triggerTime    = (Calendar) in.readSerializable();
        num            = in.readInt();
        expiredTime    = in.readLong();
    }

    /**
     * 设置一次性的计划
     *
     * @param triggerTime 发生时间
     */
    public ScheduleDate(Calendar triggerTime) {
        super();
        init(triggerTime, IntervalUnit.NULL, 0);
    }

    /**
     * 重复计划
     * <p>e.g 一个计划任务要求每隔两个月执行一次,则传递 unit = EVERY_DAY_OF_MONTH ,num = 2 ;
     * @param triggerTime   发生时间
     * @param unit          间隔时间单位
     * @param num           间隔基数
     */
    public ScheduleDate(Calendar triggerTime, IntervalUnit unit, int num) {
        super();
        init(triggerTime, unit, num);
    }

    /**
     * 重复计划
     * <p>e.g 一个计划任务要求每隔两个月执行一次,则传递 unit = EVERY_DAY_OF_MONTH ,num = 2 ;
     * @param triggerTime   发生时间
     * @param unit          间隔时间单位
     * @param num           间隔基数
     * @param executeTimes  执行次数
     */
    public ScheduleDate(Calendar triggerTime,IntervalUnit unit,int num,int executeTimes){
        super();
        init(triggerTime, unit, num);
        setExpiredTime(executeTimes);
    }

    /**
     * 设置重复计划
     * <p>e.g 一个计划任务要求每隔两个月执行一次,则传递 unit = EVERY_DAY_OF_MONTH ,num = 2 ;
     * @param triggerTime   发生时间
     * @param unit          间隔时间单位
     * @param num           间隔基数
     * @param expiredTime   任务过期时间
     */
    public ScheduleDate(Calendar triggerTime,IntervalUnit unit,int num,Calendar expiredTime){
        super();
        init(triggerTime, unit, num);
        this.expiredTime = expiredTime.getTimeInMillis();
    }

    /**
     * 初始化参数
     * @param triggerTime       发生时间
     * @param unit              间隔时间单位
     * @param num               间隔基数
     */
    private void init(Calendar triggerTime, IntervalUnit unit, int num) {
        this.triggerTime = triggerTime;
        this.unit = unit;
        this.num = num;
        this.checkCalendar();
    }

    /**
     *  <p>如果传入的时间是过去的时间,系统会马上触发一次Alarm事件,影响体验,所以需要对触发时间做一下处理;
     *  <p>检查triggerTime设置:
     *  <p>1.判断触发时间是否是将来的时间;
     *  <p>2.如果是过去的时间 并且不是重复任务,设置为5分钟后触发(系统闹钟服务 不同的机型有不同的最小触发间隔,小米2s测试为5分钟唤醒一次);
     *  <p>3.如果是重复任务,将首次触发时间推迟到下一次;
     */
    private void checkCalendar(){
        if (triggerTime.getTimeInMillis() > System.currentTimeMillis())
            return;

        if (!isRepeatable()){
            triggerTime = Calendar.getInstance();
            triggerTime.add(Calendar.MINUTE, 5);
        } else {
            long next   = triggerTime.getTimeInMillis() + this.getInterval();
            triggerTime.setTimeInMillis(next);
            checkCalendar();
        }
    }

    /**
     * 根据任务的重复次数确定任务过期时间
     * @param executeTimes  任务重复次数
     */
    private void setExpiredTime(int executeTimes){
        if (executeTimes <= 0){
            throw new IllegalArgumentException("参数错误,执行次数最小为1");
        }

        if (unit == IntervalUnit.EVERY_DAY_OF_MONTH){
            Calendar e  = (Calendar) triggerTime.clone();
            e.add(Calendar.MONTH,executeTimes * num);
            expiredTime = e.getTimeInMillis();
        } else {
            long times  = executeTimes;
            expiredTime = triggerTime.getTimeInMillis() + this.getInterval() * times;
        }
    }

    /**
     * 是否是重复计划
     *
     * @return boolean
     */
    protected boolean isRepeatable() {
        return unit != IntervalUnit.NULL;
    }

    /**
     * 获取触发时间
     *
     * @return 触发时间
     */
    protected long getTriggerAtTime() {
        return triggerTime.getTimeInMillis();
    }

    /**
     * 获取间隔时间
     *
     * @return 间隔时间
     */
    protected long getInterval() {
        switch (unit) {
            case NULL:
                return 0;
            case EVERY_DAY_OF_MONTH:
                Calendar nextMonth = (Calendar) triggerTime.clone();
                nextMonth.add(Calendar.MONTH, num);
                return nextMonth.getTimeInMillis() - triggerTime.getTimeInMillis();
            default:
                long num = this.num;
                return num * unit.unit;
        }
    }

    /**
     * 检查是否过期(包括每月定期执行的任务间隔时间的纠错)
     * @param context   应用上下文,用来获取  ScheduleManager
     * @param uniqueKey    任务标示
     * @param executor  任务执行器
     */
    protected void checkState(Context context,String uniqueKey, ScheduleExecutor executor){
        ScheduleManager manager = new ScheduleManager(context);

        //系统时间大于过期时间,已过期
        if (System.currentTimeMillis() > expiredTime){
            manager.removeTask(uniqueKey);

        //每隔月份执行 每个月的天数不一样,需要纠正
        } else if (unit == IntervalUnit.EVERY_DAY_OF_MONTH){
            manager.removeTask(uniqueKey);
            checkCalendar();
            manager.addTask(uniqueKey,this,executor);
        }
    }


    /**
     * <p>定时单位
     * <p>包括 以分钟为单位,以小时为单位,以天为单位,每个月的当天为单位;
     * <p>e.g 一个计划任务要求每隔5小时执行一次,则传递 unit = HOUR ,num = 5 ;
     * @see java.util.concurrent.TimeUnit
     */
    public static enum IntervalUnit {
        NULL                (0),
        SECOND              (1000L),
        MINUTE              (60L * SECOND.unit),
        HOUR                (60L * MINUTE.unit),
        DAY                 (24L * HOUR.unit),
        EVERY_DAY_OF_MONTH  (30L * DAY.unit);

        private long unit;

        private IntervalUnit(long unit) {
            this.unit = unit;
        }
    }
}
