package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by Administrator on 2015/10/14.
 */
public class FundValidateProdListRequest extends BaseRequest{

    private String Mobile;

    @Override
    public String getMobile() {
        return Mobile;
    }

    @Override
    public void setMobile(String mobile) {
        Mobile = mobile;
    }
}
