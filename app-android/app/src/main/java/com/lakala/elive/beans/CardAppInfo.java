package com.lakala.elive.beans;


import java.io.Serializable;

/**
 *  卡应用类型
 *  业务终端号
 */
public class CardAppInfo implements Serializable {

    private String cardAppType;

    private String cardAppTypeName;

    private String termNo;

    private String termStatus;//终端状态


    public String getTermStatus() {
        return termStatus;
    }

    public void setTermStatus(String termStatus) {
        this.termStatus = termStatus;
    }

    public String getCardAppTypeName() {
        return cardAppTypeName;
    }

    public void setCardAppTypeName(String cardAppTypeName) {
        this.cardAppTypeName = cardAppTypeName;
    }

    public String getCardAppType() {
        return cardAppType;
    }

    public void setCardAppType(String cardAppType) {
        this.cardAppType = cardAppType;
    }

    public String getTermNo() {
        return termNo;
    }

    public void setTermNo(String termNo) {
        this.termNo = termNo;
    }

    @Override
    public String toString() {
        return termNo + ",";
    }

}
