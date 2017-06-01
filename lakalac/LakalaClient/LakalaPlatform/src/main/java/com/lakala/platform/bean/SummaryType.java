package com.lakala.platform.bean;

/**
 * Created by LMQ on 2015/3/27.
 */
public enum SummaryType {
    /**HOME_INFO_TYPE最近收款？*/
    METATYPE("最近划款"),
    /**打款*/
    PAYMENT("打款"),
    /**今日收款*/
    COLLECTION_T("今日收款"),
    /**昨日收款*/
    COLLECTION_Y("昨日收款");
    private String showName;
    SummaryType(String showName){
        this.showName = showName;
    }
    public String getShowName() {
        return showName;
    }
    public static boolean equals(SummaryType one,SummaryType two){
        if(one == two){
            return true;
        }
        if(one != null && two != null){
            return one.name().equals(two.name());
        }
        return false;
    }
}
