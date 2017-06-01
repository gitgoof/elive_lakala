package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by LMQ on 2015/10/19.
 */
public class QueryLineNoRequest extends BaseRequest {

    public String getTerminalId() {
        return TerminalId;
    }

    public QueryLineNoRequest setTerminalId(String terminalId) {
        TerminalId = terminalId;
        return this;
    }

    private String TerminalId;
}
