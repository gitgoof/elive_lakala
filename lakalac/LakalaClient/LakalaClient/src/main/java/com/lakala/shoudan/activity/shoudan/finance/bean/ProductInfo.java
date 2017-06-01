package com.lakala.shoudan.activity.shoudan.finance.bean;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * Created by HJP on 2015/9/15.
 */
public class ProductInfo implements Serializable{
    /**
     * 是否随存随取 返回为1显示随存随取
     */
    private int IsAnytime;

    private int IsAllow;
    /**
     * 发行期数
     */
    private String Period;
    /**
     * 产品名称
     */
    private String ProdName;
    private String ProdTypeName;
    private String CountDownFlag;
    /**
     * 可变现与否 返回为1显示可变现，返回为0显示不可变现
     */
    private int ToCash;
    /**
     * 期数名称
     */
    private String PeriodName;
    /**
     * 收益方式
     */
    private String IncomeType;
    /**
     * 产品归属
     */
    private String ProductOwner;
    /**
     * 开放时间
     */
    private String OpenTime;
    private String IsDesign;
    private String Url;
    /**
     * 产品状态 0-未开放,1-可(认)申购,2-已封闭,9-已失效
     */
    private int ProdState;
    /**
     * 起息日
     */
    private String StartTime;
    /**
     * 收益率
     */
    private double GrowthRate;
    /**
     * 剩余可投资金额
     */
    private double SurplusAmount;
    /**
     * 是否推荐产品
     */
    private int IsRecom;
    private String Ident;
    private double YearProfit;


    private String UnitMin;
    /**
     * 产品余量,即库存
     */
    private String ProductMargin;
    /**
     * APP上使用的显示模版
     */
    private String AppModel;
    /**
     * 起购金额
     */
    private String StartAmout;
    /**
     * 产品编号
     */
    private String ProductId;
    private String LiquiEnd;
    private String RyRate;
    private String LiquiStart;
    private String UnitMax;
    /**
     * 到息日
     */
    private String EndTime;
    /**
     * 期限
     */
    private String LimitTime;
    /**
     * 产品类型
     */
    private int ProdType;
    /**
     * 备注
     */
    private String Remark;
    /**
     * 万份收益
     */
    private double IncomeW;
    /**
     * 高低风险   0低，1高
     * @return
     */
    private int RiskLevel;

    public static ProductInfo parser(String str){
        return JSON.parseObject(str, ProductInfo.class);
    }

    public int getIsAllow() {
        return IsAllow;
    }

    public void setIsAllow(int isAllow) {
        IsAllow = isAllow;
    }

    public int getRiskLevel() {
        return RiskLevel;
    }

    public void setRiskLevel(int riskLevel) {
        RiskLevel = riskLevel;
    }

    public int getIsAnytime() {
        return IsAnytime;
    }

    public void setIsAnytime(int isAnytime) {
        IsAnytime = isAnytime;
    }

    public String getPeriod() {
        return Period;
    }

    public void setPeriod(String period) {
        Period = period;
    }

    public String getProdName() {
        return ProdName;
    }

    public double getYearProfit() {
        return YearProfit;
    }

    public void setYearProfit(double yearProfit) {
        YearProfit = yearProfit;
    }

    public void setProdName(String prodName) {
        ProdName = prodName;
    }

    public String getProdTypeName() {
        return ProdTypeName;
    }

    public void setProdTypeName(String prodTypeName) {
        ProdTypeName = prodTypeName;
    }

    public String getCountDownFlag() {
        return CountDownFlag;
    }

    public void setCountDownFlag(String countDownFlag) {
        CountDownFlag = countDownFlag;
    }

    public int getToCash() {
        return ToCash;
    }

    public void setToCash(int toCash) {
        ToCash = toCash;
    }

    public String getPeriodName() {
        return PeriodName;
    }

    public void setPeriodName(String periodName) {
        PeriodName = periodName;
    }

    public String getIncomeType() {
        return IncomeType;
    }

    public void setIncomeType(String incomeType) {
        IncomeType = incomeType;
    }

    public String getProductOwner() {
        return ProductOwner;
    }

    public void setProductOwner(String productOwner) {
        ProductOwner = productOwner;
    }

    public String getOpenTime() {
        return OpenTime;
    }

    public void setOpenTime(String openTime) {
        OpenTime = openTime;
    }

    public String getIsDesign() {
        return IsDesign;
    }

    public void setIsDesign(String isDesign) {
        IsDesign = isDesign;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public int getProdState() {
        return ProdState;
    }

    public void setProdState(int prodState) {
        ProdState = prodState;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public double getGrowthRate() {
        return GrowthRate;
    }

    public void setGrowthRate(double growthRate) {
        GrowthRate = growthRate;
    }

    public double getSurplusAmount() {
        return SurplusAmount;
    }

    public void setSurplusAmount(double surplusAmount) {
        SurplusAmount = surplusAmount;
    }

    public int getIsRecom() {
        return IsRecom;
    }

    public void setIsRecom(int isRecom) {
        IsRecom = isRecom;
    }

    public String getIdent() {
        return Ident;
    }

    public void setIdent(String ident) {
        Ident = ident;
    }

    public String getUnitMin() {
        return UnitMin;
    }

    public void setUnitMin(String unitMin) {
        UnitMin = unitMin;
    }

    public String getProductMargin() {
        return ProductMargin;
    }

    public void setProductMargin(String productMargin) {
        ProductMargin = productMargin;
    }

    public String getAppModel() {
        return AppModel;
    }

    public void setAppModel(String appModel) {
        AppModel = appModel;
    }

    public String getStartAmout() {
        return StartAmout;
    }

    public void setStartAmout(String startAmout) {
        StartAmout = startAmout;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getLiquiEnd() {
        return LiquiEnd;
    }

    public void setLiquiEnd(String liquiEnd) {
        LiquiEnd = liquiEnd;
    }

    public String getRyRate() {
        return RyRate;
    }

    public void setRyRate(String ryRate) {
        RyRate = ryRate;
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

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
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

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public double getIncomeW() {
        return IncomeW;
    }

    public void setIncomeW(double incomeW) {
        IncomeW = incomeW;
    }
}
