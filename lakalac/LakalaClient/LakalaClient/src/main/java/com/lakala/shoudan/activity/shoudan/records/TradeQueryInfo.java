package com.lakala.shoudan.activity.shoudan.records;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengxuan on 2015/12/28.
 */
public class TradeQueryInfo {
    private int successCount;
    private double successAmount;
    private int pageSize;
    private int pageStart;
    private int pageNo;
    private int totalCount;
    private int totalPage;
    private List<DealDetails> dealDetails;

    public List<DealDetails> getDealDetails() {
        return dealDetails;
    }

    public void setDealDetails(List<DealDetails> dealDetails) {
        this.dealDetails = dealDetails;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public double getSuccessAmount() {
        return successAmount;
    }

    public void setSuccessAmount(double successAmount) {
        this.successAmount = successAmount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageStart() {
        return pageStart;
    }

    public void setPageStart(int pageStart) {
        this.pageStart = pageStart;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public TradeQueryInfo parseObject(JSONObject jsonObject) throws JSONException {

        TradeQueryInfo tradeQueryInfo = new TradeQueryInfo();
        tradeQueryInfo.setSuccessAmount(jsonObject.optDouble("successAmount"));
        tradeQueryInfo.setSuccessCount(jsonObject.optInt("successCount"));
        JSONObject pageResult = jsonObject.getJSONObject("pageResult");
        tradeQueryInfo.setPageNo(pageResult.optInt("pageNo"));
        tradeQueryInfo.setPageSize(pageResult.optInt("pageSize"));
        tradeQueryInfo.setPageStart(pageResult.optInt("pageStart"));
        tradeQueryInfo.setTotalCount(pageResult.optInt("totalCount"));
        tradeQueryInfo.setTotalPage(pageResult.optInt("totalPage"));

        List<DealDetails> dealDetails = new ArrayList<DealDetails>();
        JSONArray jsonArray = jsonObject.getJSONArray("dealDetails");
        for (int i=0;i<jsonArray.length();i++){
            JSONObject jobj = jsonArray.getJSONObject(i);
            DealDetails details = new DealDetails();
            details = details.parseObject(jobj);
            dealDetails.add(details);
        }
        tradeQueryInfo.setDealDetails(dealDetails);

        return tradeQueryInfo;
    }
}
