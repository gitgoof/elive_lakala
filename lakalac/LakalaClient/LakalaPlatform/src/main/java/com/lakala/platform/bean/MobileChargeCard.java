package com.lakala.platform.bean;

import java.io.Serializable;

/**
 * Created by More on 15/2/5.
 */
public class MobileChargeCard implements Serializable{

    /**
     * cardName : 300
     * cardCode : 05
     * cardType : SC
     * cardDesc : 手机充值
     * cardOrder : 5
     */
    private String cardName;
    private String cardCode;
    private String cardType;
    private String cardDesc;
    private int cardOrder;

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public void setCardDesc(String cardDesc) {
        this.cardDesc = cardDesc;
    }

    public void setCardOrder(int cardOrder) {
        this.cardOrder = cardOrder;
    }

    public String getCardName() {
        return cardName;
    }

    public String getCardCode() {
        return cardCode;
    }

    public String getCardType() {
        return cardType;
    }

    public String getCardDesc() {
        return cardDesc;
    }

    public int getCardOrder() {
        return cardOrder;
    }
}
