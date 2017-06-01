package com.lakala.shoudan.activity.messagecenter.messageBean;

/**
 * Created by huwei on 16/9/2.
 * <p/>
 * <p/>
 * 消息总数和各个消息类型的未读数量存储
 */
public class MessageCount {
    private int allCount;
    private int publishCount;
    private int tradeCount;
    private int businessCount;

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    public int getPublishCount() {
        return publishCount;
    }

    public void setPublishCount(int publishCount) {
        this.publishCount = publishCount;
    }

    public int getTradeCount() {
        return tradeCount;
    }

    public void setTradeCount(int tradeCount) {
        this.tradeCount = tradeCount;
    }

    public int getBusinessCount() {
        return businessCount;
    }

    public void setBusinessCount(int businessCount) {
        this.businessCount = businessCount;
    }
}
