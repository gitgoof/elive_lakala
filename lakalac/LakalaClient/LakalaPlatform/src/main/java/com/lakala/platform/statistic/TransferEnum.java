package com.lakala.platform.statistic;

/**
 * Created by Administrator on 2016/11/22.
 */
public enum TransferEnum {
    transfer;
    boolean phone = false;
    boolean isSmsOpen = false;
    boolean contanct = false;
    boolean reachTime = false;


    public boolean isPhone() {
        return phone;
    }

    public void setPhone(boolean phone) {
        this.phone = phone;
    }

    public boolean isContanct() {
        return contanct;
    }

    public void setContanct(boolean contanct) {
        this.contanct = contanct;
    }

    public boolean isReachTime() {
        return reachTime;
    }

    public void setReachTime(boolean reachTime) {
        this.reachTime = reachTime;
    }

    public boolean isSmsOpen() {
        return isSmsOpen;
    }

    public void setSmsOpen(boolean smsOpen) {
        isSmsOpen = smsOpen;
    }
}
