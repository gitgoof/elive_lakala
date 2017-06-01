package com.lakala.shoudan.activity.shoudan.records;

import java.io.Serializable;
/**
 * 今日划款记录
 * 
 *
 */
public class TransferMoney implements Serializable{

	/**
	 * 今日划款数额
	 */
   private String money;
   /**
    * 交易类型
    */
   private String transType;
   /**
    * 商户名称
    */
   private String merchantName;
   /**
    * 商户编号
    */
   private String merchantNum;
   /**
    * 结算账号
    */
   private String bankNo;
   
   /**
    * 交易时间(2014/04/27 12:01)
    */
   private String date;
   
	public String getMoney() {
		return money;
	}

	public void setMoney(String d) {
		this.money = d;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getMerchantNum() {
		return merchantNum;
	}

	public void setMerchantNum(String merchantNum) {
		this.merchantNum = merchantNum;
	}

	public String getBankNo() {
		return bankNo;
	}

	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
   
   
}
