package com.lakala.shoudan.activity.shoudan.finance.bean;

import com.alibaba.fastjson.JSON;

/**
 * Created by HJP on 2015/9/17.
 */
public class Income {
    /**
     * 状态
     */
    private int State;
    /**
     * 用户总资产
     */
    private double TotalAsset;
    /**
     * 昨日收益
     */
    private double DayIncome;
    /**
     * 近一周收益
     */
    private double WeekIncome;
    /**
     * 近一月收益
     */
    private double MonthIncome;
    /**
     * 累计收益
     */
    private double TotalIncome;
    /**
     * 万份收益
     */
    private double IncomeW;
    /**
     * 七日年化收益率
     */
    private double GrowthRate;
    /**
     * 当前日期
     */
    private String Sysdate;

    public static Income parserStringToIncome(String str){
        return JSON.parseObject(str, Income.class);
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public double getTotalAsset() {
        return TotalAsset;
    }

    public void setTotalAsset(double totalAsset) {
        TotalAsset = totalAsset;
    }

    public double getDayIncome() {
        return DayIncome;
    }

    public void setDayIncome(double dayIncome) {
        DayIncome = dayIncome;
    }

    public double getWeekIncome() {
        return WeekIncome;
    }

    public void setWeekIncome(double weekIncome) {
        WeekIncome = weekIncome;
    }

    public double getMonthIncome() {
        return MonthIncome;
    }

    public void setMonthIncome(double monthIncome) {
        MonthIncome = monthIncome;
    }

    public double getTotalIncome() {
        return TotalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        TotalIncome = totalIncome;
    }

    public double getIncomeW() {
        return IncomeW;
    }

    public void setIncomeW(double incomeW) {
        IncomeW = incomeW;
    }

    public double getGrowthRate() {
        return GrowthRate;
    }

    public void setGrowthRate(double growthRate) {
        GrowthRate = growthRate;
    }

    public String getSysdate() {
        return Sysdate;
    }

    public void setSysdate(String sysdate) {
        Sysdate = sysdate;
    }
}
