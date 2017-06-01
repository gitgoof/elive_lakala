package com.lakala.platform.statistic;

/**
 * 二维码-成功-入口
 * Created by huwei on 2016/7/18.
 */
public enum ErweimaCollectionEnum {
    ERWEIMA_COLLECTION;
    boolean isHomePage;
    String advertId;

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

    public void setData(String advertId, boolean isHomePage) {
        this.advertId = advertId;
        this.isHomePage = isHomePage;
    }
}
