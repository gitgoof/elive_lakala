package com.lakala.platform.statistic;

/**
 * 替你还
 * Created by huangjp on 2015/12/30.
 */
public enum  PayForYouEnum {
    PayForYou;
    String advertId;
    boolean isForYou = false;
    boolean isLoan = false;

    public boolean isPayCredit() {
        return isPayCredit;
    }

    public void setPayCredit(boolean payCredit) {
        isLoan = false;
        isForYou = false;
        PublicEnum.Business.setDirection(false);
        isPayCredit = payCredit;
    }
    public void close() {
        isPayCredit = false;
        isLoan = false;
        isForYou = false;
    }

    boolean isPayCredit = false;

    public boolean isForYou() {
        return isForYou;
    }

    public void setForYou(boolean forYou) {
        isForYou = forYou;
        isLoan = false;
        isPayCredit=false;
    }

    public boolean isLoan() {
        return isLoan;
    }

    public void setLoan(boolean loan) {
        isLoan = loan;
        isForYou = false;
        isPayCredit=false;
        PublicEnum.Business.setDirection(false);
    }

    public String getAdvertId() {
        return advertId;
    }

    public void setAdvertId(String advertId) {
        this.advertId = advertId;
    }
}
