package com.lakala.platform.statistic;

/**
 * 立即提款
 * Created by huangjp on 2015/12/30.
 */
public enum DoEnum {
    Do;
    String advertId;
    boolean isHomepage;//首页
    boolean isCollectionPage;//收款一级菜单

    public String getAdvertId() {
        return advertId;
    }

    public void setAdvertId(String advertId) {
        this.advertId = advertId;
        isHomepage=false;
        isCollectionPage=false;
    }

    public boolean isHomepage() {
        return isHomepage;
    }

    public void setIsHomepage(boolean isHomepage) {
        this.isHomepage = isHomepage;
        isCollectionPage=false;
        advertId="";
    }

    public boolean isCollectionPage() {
        return isCollectionPage;
    }

    public void setIsCollectionPage(boolean isCollectionPage) {
        this.isCollectionPage = isCollectionPage;
        isHomepage=false;
        advertId="";
    }

    public void setData(String advertId,boolean isHomepage,boolean isCollectionPage){
        this.advertId=advertId;
        this.isHomepage=isHomepage;
        this.isCollectionPage=isCollectionPage;
    }
}
