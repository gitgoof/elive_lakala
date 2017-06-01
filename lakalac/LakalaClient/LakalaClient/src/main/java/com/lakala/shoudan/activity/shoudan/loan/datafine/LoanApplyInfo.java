package com.lakala.shoudan.activity.shoudan.loan.datafine;

import java.io.Serializable;

public class LoanApplyInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5668623351507577263L;
	private String applyName;// 申请人姓名
	private String creditCardNo;// 代还信用卡卡号
	private String loanMoney;// 借款金额
	private String fee;//手续费
	
	private String staging;// 分期
	private String savingCard;// 绑定储蓄卡
	private String duetime;//到期还贷日
	public String getApplyName() {
		return applyName;
	}
	public void setApplyName(String applyName) {
		this.applyName = applyName;
	}
	public String getCreditCardNo() {
		return creditCardNo;
	}
	public void setCreditCardNo(String creditCardNo) {
		this.creditCardNo = creditCardNo;
	}
	public String getLoanMoney() {
		return loanMoney;
	}
	public void setLoanMoney(String loanMoney) {
		this.loanMoney = loanMoney;
	}
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}
	public String getStaging() {
		return staging;
	}
	public void setStaging(String staging) {
		this.staging = staging;
	}
	public String getSavingCard() {
		return savingCard;
	}
	public void setSavingCard(String savingCard) {
		this.savingCard = savingCard;
	}
	public String getDuetime() {
		return duetime;
	}
	public void setDuetime(String duetime) {
		this.duetime = duetime;
	}
}
