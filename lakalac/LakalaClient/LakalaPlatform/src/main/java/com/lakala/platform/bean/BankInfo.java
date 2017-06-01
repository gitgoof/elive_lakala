package com.lakala.platform.bean;

import java.io.Serializable;

/**
 * Created by LMQ on 2015/3/10.
 */
public class BankInfo implements Serializable {
    private String bankCode;
    private boolean paymentflag;
    private boolean commonflag;
    private String icon;
    private String bankName;
    private String bankNameAlais;
    private int idx;

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public void setPaymentflag(boolean paymentflag) {
        this.paymentflag = paymentflag;
    }

    public void setCommonflag(boolean commonflag) {
        this.commonflag = commonflag;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setBankNameAlais(String bankNameAlais) {
        this.bankNameAlais = bankNameAlais;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getBankCode() {
        return bankCode;
    }

    public boolean isPaymentflag() {
        return paymentflag;
    }

    public boolean isCommonflag() {
        return commonflag;
    }

    public String getIcon() {
        return icon;
    }

    public String getBankName() {
        return bankName;
    }

    public String getBankNameAlais() {
        return bankNameAlais;
    }

    public int getIdx() {
        return idx;
    }
}
