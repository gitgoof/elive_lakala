package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by LMQ on 2015/10/9.
 */
public class QryBusiBanksRequest extends BaseRequest {
    private String BusId = "1G0_1";

    public String getBusId() {
        return BusId;
    }

    public QryBusiBanksRequest setBusId(String busId) {
        BusId = busId;
        return this;
    }
}
