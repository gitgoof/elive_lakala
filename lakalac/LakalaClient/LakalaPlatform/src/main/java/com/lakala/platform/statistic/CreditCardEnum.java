package com.lakala.platform.statistic;

/**
 * Created by Administrator on 2016/11/22.
 */
public enum CreditCardEnum {
    PayBack;
    boolean isPayOpen = false;
    boolean isSmsOpen = false;


    public boolean isPayOpen() {
        return isPayOpen;
    }

    public void setPayOpen(boolean payOpen) {
        isPayOpen = payOpen;
    }

    public boolean isSmsOpen() {
        return isSmsOpen;
    }

    public void setSmsOpen(boolean smsOpen) {
        isSmsOpen = smsOpen;
    }
}
