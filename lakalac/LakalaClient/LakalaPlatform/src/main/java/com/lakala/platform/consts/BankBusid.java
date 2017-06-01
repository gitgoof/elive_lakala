package com.lakala.platform.consts;

/**
 * Created by LMQ on 2015/3/3.
 */
public enum BankBusid {
    /**开户*/
    MPOS_ACCT("MPOS_ACCT"),
    /**账转*/
    TRANSFER("TRANSFER"),
    /**钱包转账*/
    WALLET_TRANSFER("300007_1"),
    /**替你还*/
    LOAN_FOR_YOU("1EH");

    String value;

    public String getValue() {
        return value;
    }

    BankBusid(String value) {
        this.value = value;
    }
}
