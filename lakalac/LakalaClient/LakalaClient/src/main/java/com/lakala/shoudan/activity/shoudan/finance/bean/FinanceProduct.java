package com.lakala.shoudan.activity.shoudan.finance.bean;

import com.alibaba.fastjson.JSON;

/**
 * Created by More on 15/9/2.
 */
public class FinanceProduct {
    /**
     *
     * Period : 20150901_001
     * ProdName : 考拉1号-定期理财
     * ProdTypeName : 定期理财
     * CountDownFlag : 0
     * ToCash : 0
     * PeriodName : 00011
     * IncomeType : 0
     * ProductOwner : 掌柜出品
     * OpenTime : 2015-09-01 00:00:00
     * IsDesign : 0
     * Url : http://download.lakala.com.cn/LCHT/20150901_001
     * ProdState : 1
     * StartTime : 2015-09-05
     * GrowthRate : 8.88
     * SurplusAmount : 1.998272044E7
     * IsRecom : 1
     * Ident : 1
     * UnitMin : 0.2
     * ProductMargin : 1998.2万元
     * AppModel : 50020101
     * StartAmout : 0.2
     * ProductId : KL1H00001
     * LiquiEnd : null
     * RyRate : 6.66
     * LiquiStart : null
     * UnitMax : 20000000
     * EndTime : 2015-09-17
     * LimitTime : 12
     * ProdType : 1
     * Remark : 1
     */

    /**
     * 发型期数
     */
    private String Period;
    /**
     * 产品名称
     */
    private String ProdName;
    private String ProdTypeName;
    private String CountDownFlag;
    /**
     * 中途可变现
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
    private int IsDesign;
    private String Url;
    /**
     * 产品状态  0-未开放,1-可(认)申购,2-已封闭,9-已失效
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
     * 1是 0 否
     */
    private int IsRecom;
    private int Ident;
    private double UnitMin;
    /**
     * 产品余量
     */
    private String ProductMargin;
    /**
     * app上使用的现实模板
     * 50010201：考拉理财
     * 50010202：基金理财
     */
    private String AppModel;
    /**
     * 起购金额
     */
    private double StartAmout;
    private String ProductId;
    private Object LiquiEnd;
    private double RyRate;
    private Object LiquiStart;
    private int UnitMax;
    /**
     * 到息日
     */
    private String EndTime;
    /**
     * 期限
     */
    private String LimitTime;
    /**
     * 产品类型 0-基金产品；1-拉卡拉自有理财产品
     */
    private int ProdType;
    /**
     * 备注
     */
    private String Remark;

    public void setPeriod(String Period) {
        this.Period = Period;
    }

    public void setProdName(String ProdName) {
        this.ProdName = ProdName;
    }

    public void setProdTypeName(String ProdTypeName) {
        this.ProdTypeName = ProdTypeName;
    }

    public void setCountDownFlag(String CountDownFlag) {
        this.CountDownFlag = CountDownFlag;
    }

    public void setToCash(int ToCash) {
        this.ToCash = ToCash;
    }

    public void setPeriodName(String PeriodName) {
        this.PeriodName = PeriodName;
    }

    public void setIncomeType(String IncomeType) {
        this.IncomeType = IncomeType;
    }

    public void setProductOwner(String ProductOwner) {
        this.ProductOwner = ProductOwner;
    }

    public void setOpenTime(String OpenTime) {
        this.OpenTime = OpenTime;
    }

    public void setIsDesign(int IsDesign) {
        this.IsDesign = IsDesign;
    }

    public void setUrl(String Url) {
        this.Url = Url;
    }

    public void setProdState(int ProdState) {
        this.ProdState = ProdState;
    }

    public void setStartTime(String StartTime) {
        this.StartTime = StartTime;
    }

    public void setGrowthRate(double GrowthRate) {
        this.GrowthRate = GrowthRate;
    }

    public void setSurplusAmount(double SurplusAmount) {
        this.SurplusAmount = SurplusAmount;
    }

    public void setIsRecom(int IsRecom) {
        this.IsRecom = IsRecom;
    }

    public void setIdent(int Ident) {
        this.Ident = Ident;
    }

    public void setUnitMin(double UnitMin) {
        this.UnitMin = UnitMin;
    }

    public void setProductMargin(String ProductMargin) {
        this.ProductMargin = ProductMargin;
    }

    public void setAppModel(String AppModel) {
        this.AppModel = AppModel;
    }

    public void setStartAmout(double StartAmout) {
        this.StartAmout = StartAmout;
    }

    public void setProductId(String ProductId) {
        this.ProductId = ProductId;
    }

    public void setLiquiEnd(Object LiquiEnd) {
        this.LiquiEnd = LiquiEnd;
    }

    public void setRyRate(double RyRate) {
        this.RyRate = RyRate;
    }

    public void setLiquiStart(Object LiquiStart) {
        this.LiquiStart = LiquiStart;
    }

    public void setUnitMax(int UnitMax) {
        this.UnitMax = UnitMax;
    }

    public void setEndTime(String EndTime) {
        this.EndTime = EndTime;
    }

    public void setLimitTime(String LimitTime) {
        this.LimitTime = LimitTime;
    }

    public void setProdType(int ProdType) {
        this.ProdType = ProdType;
    }

    public void setRemark(String Remark) {
        this.Remark = Remark;
    }

    public String getPeriod() {
        return Period;
    }

    public String getProdName() {
        return ProdName;
    }

    public String getProdTypeName() {
        return ProdTypeName;
    }

    public String getCountDownFlag() {
        return CountDownFlag;
    }

    public int getToCash() {
        return ToCash;
    }

    public String getPeriodName() {
        return PeriodName;
    }

    public String getIncomeType() {
        return IncomeType;
    }

    public String getProductOwner() {
        return ProductOwner;
    }

    public String getOpenTime() {
        return OpenTime;
    }

    public int getIsDesign() {
        return IsDesign;
    }

    public String getUrl() {
        return Url;
    }

    public int getProdState() {
        return ProdState;
    }

    public String getStartTime() {
        return StartTime;
    }

    public double getGrowthRate() {
        return GrowthRate;
    }

    public double getSurplusAmount() {
        return SurplusAmount;
    }

    public int getIsRecom() {
        return IsRecom;
    }

    public int getIdent() {
        return Ident;
    }

    public double getUnitMin() {
        return UnitMin;
    }

    public String getProductMargin() {
        return ProductMargin;
    }

    public String getAppModel() {
        return AppModel;
    }

    public double getStartAmout() {
        return StartAmout;
    }

    public String getProductId() {
        return ProductId;
    }

    public Object getLiquiEnd() {
        return LiquiEnd;
    }

    public double getRyRate() {
        return RyRate;
    }

    public Object getLiquiStart() {
        return LiquiStart;
    }

    public int getUnitMax() {
        return UnitMax;
    }

    public String getEndTime() {
        return EndTime;
    }

    public String getLimitTime() {
        return LimitTime;
    }

    public int getProdType() {
        return ProdType;
    }

    public String getRemark() {
        return Remark;
    }

    public static FinanceProduct parser(String str){
        return JSON.parseObject(str, FinanceProduct.class);
    }
}
