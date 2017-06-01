package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by Administrator on 2015/10/19.
 */
public class QueryCurrentCardInfoRequest extends BaseRequest{

    private String BusId = "1GB";

    public String getBusId() {
        return BusId;
    }
}
