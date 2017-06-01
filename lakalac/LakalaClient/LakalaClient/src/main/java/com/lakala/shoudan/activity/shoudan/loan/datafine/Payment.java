package com.lakala.shoudan.activity.shoudan.loan.datafine;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
/**
 * 还款记录
 * @author zmy
 *
 */
public class Payment {
	private String payamt;
	private String paytime;
	private String paytype;
	private String payacct;
	public String getPayamt() {
		return payamt;
	}
	public void setPayamt(String payamt) {
		this.payamt = payamt;
	}
	public String getPaytime() {
		return paytime;
	}
	public void setPaytime(String paytime) {
		this.paytime = paytime;
	}
	public String getPaytype() {
		return paytype;
	}
	public void setPaytype(String paytype) {
		this.paytype = paytype;
	}
	public String getPayacct() {
		return payacct;
	}
	public void setPayacct(String payacct) {
		this.payacct = payacct;
	}
	
	public static List<Payment> unpackPayments(JSONArray jsonArray)throws Exception{
		List<Payment> payments = new ArrayList<Payment>();
		for(int i=0;i<jsonArray.length();i++){
			JSONObject jsonPayment = jsonArray.getJSONObject(i);
			Payment payment = new Payment();
			payment.setPayamt(jsonPayment.optString("payamt"));
			payment.setPaytime(jsonPayment.optString("paytime"));
			payment.setPaytype(jsonPayment.optString("paytype"));
			payment.setPayacct(jsonPayment.optString("payacct"));
			payments.add(payment);
		}
		
		return payments;
	}
}
