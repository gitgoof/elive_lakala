package com.lakala.shoudan.activity.shoudan.loan.datafine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 最后一笔记录
 * @author zmy
 *
 */
public class LastApplyInfo implements Serializable{
	private String certno;
	private String username;
	private String debitbank;
	private String debitcard;
	private String contractno;
	private String loanamt;
	private String paidamt;
	private String paytime;
	private String contractstatus;
	private String failreason;
	
	private List<Unpaidamts> unpaidamts;
	
	public List<Unpaidamts> getUnpaidamts() {
		return unpaidamts;
	}

	public void setUnpaidamts(List<Unpaidamts> unpaidamts) {
		this.unpaidamts = unpaidamts;
	}

	public LastApplyInfo(){
		unpaidamts = new ArrayList<Unpaidamts>();
	}
	
	public String getCertno() {
		return certno;
	}
	public void setCertno(String certno) {
		this.certno = certno;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getDebitbank() {
		return debitbank;
	}
	public void setDebitbank(String debitbank) {
		this.debitbank = debitbank;
	}
	public String getDebitcard() {
		return debitcard;
	}
	public void setDebitcard(String debitcard) {
		this.debitcard = debitcard;
	}
	public String getContractno() {
		return contractno;
	}
	public void setContractno(String contractno) {
		this.contractno = contractno;
	}
	public String getLoanamt() {
		return loanamt;
	}
	public void setLoanamt(String loanamt) {
		this.loanamt = loanamt;
	}
	public String getPaidamt() {
		return paidamt;
	}
	public void setPaidamt(String paidamt) {
		this.paidamt = paidamt;
	}
	public String getPaytime() {
		return paytime;
	}
	public void setPaytime(String paytime) {
		this.paytime = paytime;
	}
	public String getContractstatus() {
		return contractstatus;
	}
	public void setContractstatus(String contractstatus) {
		this.contractstatus = contractstatus;
	}
	public String getFailreason() {
		return failreason;
	}
	public void setFailreason(String failreason) {
		this.failreason = failreason;
	}
	
	public void unpackLastApplyInfo(JSONObject jsonData)throws Exception{
		this.setCertno(jsonData.optString("certno"));
		this.setContractno(jsonData.optString("contractno"));
		this.setContractstatus(jsonData.optString("contractstatus"));
		this.setDebitbank(jsonData.optString("debitbank"));
		this.setDebitcard(jsonData.optString("debitcard"));
		this.setFailreason(jsonData.optString("failreason"));
		this.setLoanamt(jsonData.optString("loanamt"));
		this.setPaidamt(jsonData.optString("paidamt"));
		this.setPaytime(jsonData.optString("paytime"));
		this.setUsername(jsonData.optString("username"));
		this.getUnpaidamts().addAll(unpackUnpaidamts(jsonData.getJSONArray("unpaidamts")));
	}
	
	public List<Unpaidamts> unpackUnpaidamts(JSONArray jsonArray)throws Exception{
		List<Unpaidamts> unpaidamts = new ArrayList<Unpaidamts>();
		if(null != jsonArray){
			for(int i=0;i<jsonArray.length();i++){
				JSONObject jsonUnpaidamt = jsonArray.getJSONObject(i);
				Unpaidamts unpaidamt = new Unpaidamts();
				unpaidamt.setType(jsonUnpaidamt.optString("type"));
				unpaidamt.setCapital(jsonUnpaidamt.optString("capital"));
				unpaidamts.add(unpaidamt);
			}
		}
		
		return unpaidamts;
	}
	
	/**
	 * 未还款金额列表
	 * @author zmy
	 *
	 */
	public class Unpaidamts implements Serializable{
		private String type;
		private String capital;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getCapital() {
			return capital;
		}
		public void setCapital(String capital) {
			this.capital = capital;
		}
	}
	
}
