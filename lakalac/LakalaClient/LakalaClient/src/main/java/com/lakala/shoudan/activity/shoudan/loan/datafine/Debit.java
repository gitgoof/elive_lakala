package com.lakala.shoudan.activity.shoudan.loan.datafine;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 签约卡管理中的借记卡
 * @author zmy
 *
 */
public class Debit {
	private String contractstatus;
	private String debitcard;
	private String debitbank;
	public String getContractstatus() {
		return contractstatus;
	}
	public void setContractstatus(String contractstatus) {
		this.contractstatus = contractstatus;
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
	
	public static List<Debit> unpackDebits(JSONArray jsonArray)throws Exception{
		List<Debit> debits = new ArrayList<Debit>();
		for(int i=0;i<jsonArray.length();i++){
			Debit debit = new Debit();
			JSONObject jsonDebit = (JSONObject) jsonArray.get(i);
			debit.setContractstatus(jsonDebit.optString("contractstatus"));
			debit.setDebitcard(jsonDebit.optString("debitcard"));
			debit.setDebitbank(jsonDebit.optString("debitbank"));
			debits.add(debit);
		}
		
		return debits;
	}
}
