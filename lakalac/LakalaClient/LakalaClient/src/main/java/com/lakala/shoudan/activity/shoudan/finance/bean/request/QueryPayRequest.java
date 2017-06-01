package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by LMQ on 2015/10/16.
 */
public class QueryPayRequest  extends BaseRequest{
    private String BillId;
    private String Title = "理财";

    public String getBillId() {
        return BillId;
    }

    public QueryPayRequest setBillId(String billId) {
        BillId = billId;
        return this;
    }

    public String getTitle() {
        return Title;
    }

    public QueryPayRequest setTitle(String title) {
        Title = title;
        return this;
    }
}
