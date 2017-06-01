package com.lakala.shoudan.bll.service.shoudan;

import java.io.Serializable;

/**
 * 查询卡类型
 * Created by More on 14-1-15.
 */
public class RspQueryCardType extends BaseServiceShoudanResponse implements Serializable{

    private String cardType;//卡类型
    private String bankName;//银行
    private String cardNum;//卡号

    public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
        if(bankName == null){
            bankName = "";
        }
        if (bankName.contains("\\(")) {
            int index = bankName.indexOf("\\(");
            this.bankName = bankName.substring(0, index);
        } else {
            this.bankName = bankName;
        }

	}

	public String getCardNum() {
		return cardNum;
	}

	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}

	public void setCardType(String cardType) {
        if("C".equals(cardType)){
            this.cardType = "信用卡";
        }else if("D".equals(cardType)){
            this.cardType = "借记卡";
        }
    }

    public String getCardType() {
        return cardType;
    }


}
