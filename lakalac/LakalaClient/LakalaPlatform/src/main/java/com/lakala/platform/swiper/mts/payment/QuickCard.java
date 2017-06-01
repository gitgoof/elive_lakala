package com.lakala.platform.swiper.mts.payment;

/**
 * 快捷支付银行卡
 * Created by xyz on 14-1-7.
 */
public class QuickCard {

    //银行名称
    private String bankName;

    //银行code
    private String bankCode;

    //银行卡号
    private String cardNo;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }
}
