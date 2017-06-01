package com.lakala.shoudan.activity.shoudan.loan.datafine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 借款信息
 * @author zmy
 *
 */
public class Loan implements Serializable{
	private String contractno;
	private String applytime;
	private String contractstatus;
	private String effecttime;
	private String loanamt;
	private String fee;
	private String period;
	private String returndate;
	private String debitcard;
	private String latestpaytime;
	public String getContractno() {
		return contractno;
	}
	public void setContractno(String contractno) {
		this.contractno = contractno;
	}
	public String getApplytime() {
		return applytime;
	}
	public void setApplytime(String applytime) {
		this.applytime = applytime;
	}
	public String getContractstatus() {
		return contractstatus;
	}
	public void setContractstatus(String contractstatus) {
		this.contractstatus = contractstatus;
	}
	public String getEffecttime() {
		return effecttime;
	}
	public void setEffecttime(String effecttime) {
		this.effecttime = effecttime;
	}
	public String getLoanamt() {
		return loanamt;
	}
	public void setLoanamt(String loanamt) {
		this.loanamt = loanamt;
	}
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getReturndate() {
		return returndate;
	}
	public void setReturndate(String returndate) {
		this.returndate = returndate;
	}
	public String getDebitcard() {
		return debitcard;
	}
	public void setDebitcard(String debitcard) {
		this.debitcard = debitcard;
	}
	public String getLatestpaytime() {
		return latestpaytime;
	}
	public void setLatestpaytime(String latestpaytime) {
		this.latestpaytime = latestpaytime;
	}
	
	public static List<Loan> unpackLoan(JSONArray jsonArray)throws Exception{
		List<Loan> loans = new ArrayList<Loan>();
		for(int i=0;i<jsonArray.length();i++){
			JSONObject jsonLoan = jsonArray.getJSONObject(i);
			Loan loan = new Loan();
			loan.setApplytime(jsonLoan.optString("applytime"));
			loan.setContractno(jsonLoan.optString("contractno"));
			loan.setContractstatus(jsonLoan.optString("contractstatus"));
			loan.setEffecttime(jsonLoan.optString("effecttime"));
			loan.setLoanamt(jsonLoan.optString("loanamt"));
			loan.setFee(jsonLoan.optString("fee"));
			loan.setPeriod(jsonLoan.optString("period"));
			loan.setReturndate(jsonLoan.optString("returndate"));
			loan.setDebitcard(jsonLoan.optString("debitcard"));
			loan.setLatestpaytime(jsonLoan.optString("latestpaytime"));
			
			loans.add(loan);
		}
		return loans;
	}
}
