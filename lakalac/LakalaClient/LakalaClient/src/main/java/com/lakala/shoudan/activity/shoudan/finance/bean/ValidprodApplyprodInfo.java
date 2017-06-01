package com.lakala.shoudan.activity.shoudan.finance.bean;

import java.io.Serializable;

/**
 * Created by HJP on 2015/9/21.
 */
public class ValidprodApplyprodInfo implements Serializable {
    /**
     *发行期数
     */
    private String Period;
    /**
     *产品名称
     */
    private String ProdName;
    private String SuccessTime;
    /**
     *预计收益
     */
    private double PredictIncome;
    /**
     *本金
     */
    private double Principal;

    /**
     *合同号
     */
    private String ContractId;
    private String SubmitTime;
    private String IsRedeemDate;
    private String IntrsPeriod;
    private String UnitMin;
    /**
     *APP上使用的显示模版
     */
    private String AppModel;
    /**
     *期数名称
     */
    private String PeriodName;
    /**
     *产品编号
     */
    private String ProductId;
    private String Income;
    private double IncomeW;

    public double getIncomeW() {
        return IncomeW;
    }

    public void setIncomeW(double incomeW) {
        IncomeW = incomeW;
    }

    private String LiquiEnd;
    private String EndDate;

    private String Url;
    /**
     *昨日收益
     */
    private double DayIncome;
    private String LiquiStart;
    private String UnitMax;
    /**
     *产品状态 0-未开放,1-可(认)申购,2-已封闭,9-已失效
     */
    private int ProdState;
    /**
     *期限
     */
    private String LimitTime;
    /**
     *产品类型 0：活期  1：定期
     */
    private int ProdType;
    /**
     *收益率
     */
    private double GrowthRate;
    private String Remark;
    /**
     *是否可变现/赎回类型 0-不可变现，其他为可变现
     */
    private int RedeemType;

    private double YearOrofit;
    /**
     *累计收益
     */
    private double TotalIncome;
    /**
     *总资产
     */
    private double TotalAsset;

    private String EstimateIncome;

    public String getPeriod() {
        return Period;
    }

    public void setPeriod(String period) {
        Period = period;
    }

    public String getProdName() {
        return ProdName;
    }

    public void setProdName(String prodName) {
        ProdName = prodName;
    }

    public double getPredictIncome() {
        return PredictIncome;
    }

    public void setPredictIncome(double predictIncome) {
        PredictIncome = predictIncome;
    }

    public double getPrincipal() {
        return Principal;
    }

    public void setPrincipal(double principal) {
        Principal = principal;
    }

    public String getContractId() {
        return ContractId;
    }

    public void setContractId(String contractId) {
        ContractId = contractId;
    }

    public String getIsRedeemDate() {
        return IsRedeemDate;
    }

    public void setIsRedeemDate(String isRedeemDate) {
        IsRedeemDate = isRedeemDate;
    }

    public String getIntrsPeriod() {
        return IntrsPeriod;
    }

    public void setIntrsPeriod(String intrsPeriod) {
        IntrsPeriod = intrsPeriod;
    }

    public String getUnitMin() {
        return UnitMin;
    }

    public void setUnitMin(String unitMin) {
        UnitMin = unitMin;
    }

    public String getAppModel() {
        return AppModel;
    }

    public void setAppModel(String appModel) {
        AppModel = appModel;
    }

    public String getPeriodName() {
        return PeriodName;
    }

    public void setPeriodName(String periodName) {
        PeriodName = periodName;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getIncome() {
        return Income;
    }

    public void setIncome(String income) {
        Income = income;
    }

    public String getLiquiEnd() {
        return LiquiEnd;
    }

    public void setLiquiEnd(String liquiEnd) {
        LiquiEnd = liquiEnd;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public double getDayIncome() {
        return DayIncome;
    }

    public void setDayIncome(double dayIncome) {
        DayIncome = dayIncome;
    }

    public String getLiquiStart() {
        return LiquiStart;
    }

    public void setLiquiStart(String liquiStart) {
        LiquiStart = liquiStart;
    }

    public String getUnitMax() {
        return UnitMax;
    }

    public void setUnitMax(String unitMax) {
        UnitMax = unitMax;
    }

    public int getProdState() {
        return ProdState;
    }

    public void setProdState(int prodState) {
        ProdState = prodState;
    }

    public String getLimitTime() {
        return LimitTime;
    }

    public void setLimitTime(String limitTime) {
        LimitTime = limitTime;
    }

    public int getProdType() {
        return ProdType;
    }

    public void setProdType(int prodType) {
        ProdType = prodType;
    }

    public double getGrowthRate() {
        return GrowthRate;
    }

    public void setGrowthRate(double growthRate) {
        GrowthRate = growthRate;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public int getRedeemType() {
        return RedeemType;
    }

    public void setRedeemType(int redeemType) {
        RedeemType = redeemType;
    }

    public double getYearOrofit() {
        return YearOrofit;
    }

    public void setYearOrofit(double yearOrofit) {
        YearOrofit = yearOrofit;
    }

    public double getTotalIncome() {
        return TotalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        TotalIncome = totalIncome;
    }

    public double getTotalAsset() {
        return TotalAsset;
    }

    public void setTotalAsset(double totalAsset) {
        TotalAsset = totalAsset;
    }

    public String getEstimateIncome() {
        return EstimateIncome;
    }

    public void setEstimateIncome(String estimateIncome) {
        EstimateIncome = estimateIncome;
    }

    public String getSuccessTime() {
        return SuccessTime;
    }

    public void setSuccessTime(String successTime) {
        SuccessTime = successTime;
    }

    public String getSubmitTime() {
        return SubmitTime;
    }

    public void setSubmitTime(String submitTime) {
        SubmitTime = submitTime;
    }
}
