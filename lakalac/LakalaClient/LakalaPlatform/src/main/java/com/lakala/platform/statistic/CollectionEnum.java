package com.lakala.platform.statistic;

/**
 * 刷卡收款-成功-入口
 * Created by huangjp on 2015/12/30.
 */
public enum CollectionEnum {
    Colletcion;
    String advertId;
    boolean isHomePage;

    public String getAdvertId() {
        return advertId;
    }

    public void setAdvertId(String advertId) {
        this.advertId = advertId;
    }

    public boolean isHomePage() {
        return isHomePage;
    }

    public void setIsHomePage(boolean isHomePage) {
        this.isHomePage = isHomePage;
    }
    public void setData(String advertId,boolean isHomePage){
        this.advertId = advertId;
        this.isHomePage = isHomePage;
    }
}
