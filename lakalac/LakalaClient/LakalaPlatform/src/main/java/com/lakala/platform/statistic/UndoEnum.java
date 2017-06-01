package com.lakala.platform.statistic;

/**
 * 撤销交易-成功-入口
 * Created by huangjp on 2015/12/30.
 */
public enum UndoEnum {
    Undo;
    String advertId;

    public String getAdvertId() {
        return advertId;
    }

    public void setAdvertId(String advertId) {
        this.advertId = advertId;
    }
}
