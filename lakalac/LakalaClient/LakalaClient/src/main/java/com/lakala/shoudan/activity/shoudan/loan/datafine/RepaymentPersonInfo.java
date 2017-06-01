package com.lakala.shoudan.activity.shoudan.loan.datafine;

/**
 * 替你还个人信息
 * @author Zhangmy
 *
 */
public class RepaymentPersonInfo {
	
	private String applicantName;//姓名
	private String certno;
	private String apcrcodes;//所在住宅省市区编码串
	private String apcrnames; //所在住宅省市区名称
	private String address;
	private String highestlevel;
	private String email;
	private String certfirstimg;//身份证正面照
	private String certsecondimg;//背面照
	private String nowphoto;//上班身照
	public String getApplicantName() {
		return applicantName;
	}
	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
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
}
