package com.lakala.shoudan.activity.shoudan.loan.datafine;

import java.io.Serializable;

public class LoanBackShowInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6258430856862979022L;
	private String orderno="";//订单号
	
	private int applyamt=0;//		申请金额	//单位 分
	private int period=0;//	申请周期	
	private int rate=0;//		手续费率	
	private String ratefee="";//费用   //单位元
	private String curcode="";//		币种	
	private String termid="";//		终端号码	
	private String telecode	="";//	线路号码	
	private String applicant="";//		申请人姓名	
	private String certno="";//		身份证号码	
	private String apcrcodes="";//		所在住宅省市区编码串	
	private String apcrnames="";//		所在住宅省市区名称串	
	private String address="";//		详细住宅地址	(
	private String highestlevel="";//		最高学历	
	private String email="";//		电子邮箱	
	private String certfirstimg="";//		身份证正面照	
	private String certsecondimg="";	//	身份证反面照	
	private String nowphoto="";//		上半身照
	private String companyname="";	//	单位全称	
	private String position	="";	//申请人职位	
	private String cpcrcodes="";	//	所在单位省市区编码串	
	private String cpcrnames="";	//	所在单位省市区名称串	
	private String companyaddress="";	//	单位详细地址	
	private String companytel="";	//	单位电话	
	private String income="";		//月收入	
	private String contactname="";	//	直系亲属联系人姓名	
	private String contactmobile="";//		直系亲属联系人手机	
	private String creditcard="";//		信用卡卡号	
	private String creditbank="";//		信用卡开户行	
	private String creditbankName="";//		信用卡开户行名
	private String debitcard="";	//	借记卡卡号	
	private String debitbank="";//		借记卡开户行
	private String debitbankName="";//		借记卡开户行名
	
	private String errMsg="";
	private boolean pass=false;
	private boolean isApplyErr=false;//申请失败 是否是额度校验失败
	private String dueTimeFormat1="";//2014-11-19
	private String dueTimeFormat2="";//2014年12月8日
	
	private String loanLimits;//额度值 单位元
	
	
	public String getLoanLimits() {
		return loanLimits;
	}
	public void setLoanLimits(String loanLimits) {
		this.loanLimits = loanLimits;
	}
	public String getCreditbankName() {
		return creditbankName;
	}
	public void setCreditbankName(String creditbankName) {
		this.creditbankName = creditbankName;
	}
	public String getDebitbankName() {
		return debitbankName;
	}
	public void setDebitbankName(String debitbankName) {
		this.debitbankName = debitbankName;
	}
	public String getDueTimeFormat1() {
		return dueTimeFormat1;
	}
	public void setDueTimeFormat1(String dueTimeFormat1) {
		this.dueTimeFormat1 = dueTimeFormat1;
	}
	public String getDueTimeFormat2() {
		return dueTimeFormat2;
	}
	public void setDueTimeFormat2(String dueTimeFormat2) {
		this.dueTimeFormat2 = dueTimeFormat2;
	}
	public String getRatefee() {
		return ratefee;
	}
	public void setRatefee(String ratefee) {
		this.ratefee = ratefee;
	}
	public String getOrderno() {
		return orderno;
	}
	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}
	public int getApplyamt() {
		return applyamt;
	}
	public void setApplyamt(int applyamt) {
		this.applyamt = applyamt;
	}
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	public int getRate() {
		return rate;
	}
	public void setRate(int rate) {
		this.rate = rate;
	}
	public String getCurcode() {
		return curcode;
	}
	public void setCurcode(String curcode) {
		this.curcode = curcode;
	}
	public String getTermid() {
		return termid;
	}
	public void setTermid(String termid) {
		this.termid = termid;
	}
	public String getTelecode() {
		return telecode;
	}
	public void setTelecode(String telecode) {
		this.telecode = telecode;
	}
	public String getApplicant() {
		return applicant;
	}
	public void setApplicant(String applicant) {
		this.applicant = applicant;
	}
	public String getCertno() {
		return certno;
	}
	public void setCertno(String certno) {
		this.certno = certno;
	}
	public String getApcrcodes() {
		return apcrcodes;
	}
	public void setApcrcodes(String apcrcodes) {
		this.apcrcodes = apcrcodes;
	}
	public String getApcrnames() {
		return apcrnames;
	}
	public void setApcrnames(String apcrnames) {
		this.apcrnames = apcrnames;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getHighestlevel() {
		return highestlevel;
	}
	public void setHighestlevel(String highestlevel) {
		this.highestlevel = highestlevel;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCertfirstimg() {
		return certfirstimg;
	}
	public void setCertfirstimg(String certfirstimg) {
		this.certfirstimg = certfirstimg;
	}
	public String getCertsecondimg() {
		return certsecondimg;
	}
	public void setCertsecondimg(String certsecondimg) {
		this.certsecondimg = certsecondimg;
	}
	public String getNowphoto() {
		return nowphoto;
	}
	public void setNowphoto(String nowphoto) {
		this.nowphoto = nowphoto;
	}
	public String getCompanyname() {
		return companyname;
	}
	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getCpcrcodes() {
		return cpcrcodes;
	}
	public void setCpcrcodes(String cpcrcodes) {
		this.cpcrcodes = cpcrcodes;
	}
	public String getCpcrnames() {
		return cpcrnames;
	}
	public void setCpcrnames(String cpcrnames) {
		this.cpcrnames = cpcrnames;
	}
	public String getCompanyaddress() {
		return companyaddress;
	}
	public void setCompanyaddress(String companyaddress) {
		this.companyaddress = companyaddress;
	}
	public String getCompanytel() {
		return companytel;
	}
	public void setCompanytel(String companytel) {
		this.companytel = companytel;
	}
	public String getIncome() {
		return income;
	}
	public void setIncome(String income) {
		this.income = income;
	}
	public String getContactname() {
		return contactname;
	}
	public void setContactname(String contactname) {
		this.contactname = contactname;
	}
	public String getContactmobile() {
		return contactmobile;
	}
	public void setContactmobile(String contactmobile) {
		this.contactmobile = contactmobile;
	}
	public String getCreditcard() {
		return creditcard;
	}
	public void setCreditcard(String creditcard) {
		this.creditcard = creditcard;
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
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	public boolean isPass() {
		return pass;
	}
	public void setPass(boolean pass) {
		this.pass = pass;
	}
	public boolean isApplyErr() {
		return isApplyErr;
	}
	public void setApplyErr(boolean isApplyErr) {
		this.isApplyErr = isApplyErr;
	}
	

}
