package com.lakala.shoudan.activity.shoudan.loan.datafine;

import java.io.Serializable;

public class BankInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5038791031368675971L;
	private String creditCard;
	private String creditbank;
	private String debitcard;
	private String debitbank;
	public String getCreditCard() {
		return creditCard;
	}
	public void setCreditCard(String creditCard) {
		this.creditCard = creditCard;
	}
	public String getCreditbank() {
		return creditbank;
	}
	public void setCreditbank(String creditbank) {
		this.creditbank = creditbank;
	}
	public String getDebitcard() {
		return debitcard;
	}
	public void setDebitcard(String debitcard) {
		this.debitcard = debitcard;
	}
	public String getDebitbank() {
		return debitbank;
	}
	public void setDebitbank(String debitbank) {
		this.debitbank = debitbank;
	}
	
}
