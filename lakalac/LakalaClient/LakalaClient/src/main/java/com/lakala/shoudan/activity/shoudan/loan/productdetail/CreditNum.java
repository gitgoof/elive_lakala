package com.lakala.shoudan.activity.shoudan.loan.productdetail;

/**
 * Created by Administrator on 2016/12/28.
 */
public enum  CreditNum {
    isCreat;
    private boolean isList=false;
    private boolean lisDetail=false;

    public boolean isList() {
        return isList;
    }

    public void setList(boolean list) {
        isList = list;
    }

    public boolean isLisDetail() {
        return lisDetail;
    }

    public void setLisDetail(boolean lisDetail) {
        this.lisDetail = lisDetail;
    }
}
