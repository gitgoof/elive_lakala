package com.lakala.elive.common.net.req;

import java.io.Serializable;

/**
 * 进件--终端开通基本信息
 * Created by wenhaogu on 2017/1/9.
 */

public class CardAppInfo implements Serializable {
    private String applyId;
    private String terminalId;
    private String cardAppCode;
    private String cardAppName;

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getCardAppCode() {
        return cardAppCode;
    }

    public void setCardAppCode(String cardAppCode) {
        this.cardAppCode = cardAppCode;
    }

    public String getCardAppName() {
        return cardAppName;
    }

    public void setCardAppName(String cardAppName) {
        this.cardAppName = cardAppName;
    }
}
