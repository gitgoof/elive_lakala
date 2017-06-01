package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by HJP on 2015/10/19.
 */
public class FundWeekIncomeQryRequest extends BaseRequest {
    private String TradeType;
    private String PageSize;
    private String PageNo="1";

    public FundWeekIncomeQryRequest(String TradeType,String PageSize){
        this.TradeType=TradeType;
        this.PageSize=PageSize;
    }

    public String getTradeType() {
        return TradeType;
    }

    public void setTradeType(String tradeType) {
        TradeType = tradeType;
    }

    public String getPageSize() {
        return PageSize;
    }

    public void setPageSize(String pageSize) {
        PageSize = pageSize;
    }

    public String getPageNo() {
        return PageNo;
    }

    public void setPageNo(String pageNo) {
        PageNo = pageNo;
    }
}
