package com.lakala.shoudan.activity.shoudan.finance.bean;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by More on 15/9/2.
 *
 */
public class TotalAssets {
    private List<ValidprodApplyprodInfo> ValidprodList;
    private Object ApplyinsuprodList;
    /**
     *累计收益
     */
    private double TotalIncome;
    /**
     *总资产
     */
    private double TotalAsset;

    private List<ValidprodApplyprodInfo> ApplyprodList;
    /**
     *客户端guid  跟踪号
     */
    private String _Guid;

    private String EstimateIncome;

    public static TotalAssets parserStringToTotalAssets(String str){
        TotalAssets assets = JSON.parseObject(str, TotalAssets.class);
        return assets;

    }

    public Object getApplyinsuprodList() {
        return ApplyinsuprodList;
    }

    public void setApplyinsuprodList(Object applyinsuprodList) {
        ApplyinsuprodList = applyinsuprodList;
    }

    public List<ValidprodApplyprodInfo> getValidprodList() {
        return ValidprodList;
    }

    public void setValidprodList(List<ValidprodApplyprodInfo> validprodList) {
        ValidprodList = validprodList;
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

    public List<ValidprodApplyprodInfo> getApplyprodList() {
        return ApplyprodList;
    }

    public void setApplyprodList(List<ValidprodApplyprodInfo> applyprodList) {
        ApplyprodList = applyprodList;
    }

    public String get_Guid() {
        return _Guid;
    }

    public void set_Guid(String _Guid) {
        this._Guid = _Guid;
    }

    public String getEstimateIncome() {
        return EstimateIncome;
    }

    public void setEstimateIncome(String estimateIncome) {
        EstimateIncome = estimateIncome;
    }
}
