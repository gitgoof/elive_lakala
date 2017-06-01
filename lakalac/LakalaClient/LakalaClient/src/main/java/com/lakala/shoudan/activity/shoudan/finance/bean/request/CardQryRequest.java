package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by LMQ on 2015/10/28.
 */
public class CardQryRequest extends BaseRequest {
    private String CreditcardNo;

    public String getCreditcardNo() {
        return CreditcardNo;
    }

    public CardQryRequest setCreditcardNo(String creditcardNo) {
        CreditcardNo = creditcardNo;
        return this;
    }
}
