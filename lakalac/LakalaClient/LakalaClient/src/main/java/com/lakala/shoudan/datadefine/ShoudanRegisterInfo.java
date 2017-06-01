package com.lakala.shoudan.datadefine;

import com.lakala.platform.bean.AuthenticationStatus;
import com.lakala.platform.bean.LargeAmountAccess;
import com.lakala.platform.bean.ScancodeAccess;
import com.lakala.shoudan.bll.service.shoudan.BaseServiceShoudanResponse;


import org.json.JSONArray;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 注册信息
 * 
 * @author More
 *
 */
public class ShoudanRegisterInfo extends BaseServiceShoudanResponse implements Serializable{

	private String realName = ""; //用户真实姓名
	private String idCardType = "";//卡类型
	private String idCardNo = "";//卡号
	private String email = "";//邮箱
	private String province = "";//省份
    private String provinceCode = "";
	private String city = "";//城市
    private String cityCode = "";
    private String district = "";//县区
    private String districtCode = "";
    private String homeAddr = "";//家庭住址
    private String zipCode = "";//邮编
    private String businessAddr = "";//营业地址
    private String businessName = "";//商户名称
    private String businessNo = "";
    private String accountType = "";//账户类型
    private String bankNo = "";//开户行
    private String bankNoBeijing = "";//理财中使用的
    private String bankName = "";//开户行名称
    private String accountNo = "";//账户号
    private String accountName = "";//账户名
    private String mobileNo = "";//电话号码
    private String picPath1 = "";//身份证照片路径1
    private String picPath2 = "";//身份证照片路径2
    private boolean isUsrInfoEditable = false;//用户信息是否可以被修改
    private String authRemark = "";//用户信息未通过原因
    private T0Status t0Status = T0Status.UNKNOWN;
    private AuthenticationStatus authenticationStatus = AuthenticationStatus.UNKNOWN;
    private LargeAmountAccess largeAmountAccess = LargeAmountAccess.UNKNOWN;
    private ScancodeAccess scancodeAccess = ScancodeAccess.UNKNOWN;
    private String largeAmountLimit = "";
    private boolean isDownEnable = false;

    public String getBankNoBeijing() {
        return bankNoBeijing;
    }

    public ShoudanRegisterInfo setBankNoBeijing(String bankNoBeijing) {
        this.bankNoBeijing = bankNoBeijing;
        return this;
    }

    public boolean isDownEnable() {
        return isDownEnable;
    }

    public void setIsDownEnable(boolean isDownEnable) {
        this.isDownEnable = isDownEnable;
    }

    public String getLargeAmountLimit() {
        return largeAmountLimit;
    }

    public void setLargeAmountLimit(String largeAmountLimit) {
        this.largeAmountLimit = largeAmountLimit;
    }

    public LargeAmountAccess getLargeAmountAccess() {
        return largeAmountAccess;
    }

    public void setLargeAmountAccess(LargeAmountAccess largeAmountAccess) {
        this.largeAmountAccess = largeAmountAccess;
    }

    public ScancodeAccess getScancodeAccess() {
        return scancodeAccess;
    }

    public void setScancodeAccess(ScancodeAccess scancodeAccess) {
        this.scancodeAccess = scancodeAccess;
    }

    public T0Status getT0Status() {
        return t0Status;
    }

    public void setT0Status(String t0Status) {
        try {
            this.t0Status = T0Status.valueOf(t0Status);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public AuthenticationStatus getAuthenticationStatus() {
        return authenticationStatus;
    }

    public void setAuthenticationStatus(AuthenticationStatus authenticationStatus) {
        this.authenticationStatus = authenticationStatus;
    }

    public void setAuthenticationStatus(String authenticationStatus){
        try {
            this.authenticationStatus = AuthenticationStatus.valueOf(authenticationStatus);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getPicPath1(){
        return  picPath1;
    }
    public void setPicPath1(String picPath1){
        this.picPath1 = picPath1;
    }

    public String getPicPath2(){
        return picPath2;
    }

    public void setPicPath2(String picPath2){
        this.picPath2 = picPath2;
    }

    public String getRealName(){
        return realName;
    }

    public void setRealName(String realName){
        this.realName = realName;
    }

    public String getIdCardType(){
        return idCardType;
    }

    public void setIdCardType(String idCardType){
        this.idCardType = idCardType;
    }

    public String getIdCardNo(){
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo){
        this.idCardNo = idCardNo;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getProvince(){
        return province;
    }

    public void setProvince(String province){
        this.province = province;
    }

    public void setCity(String city){
        this.city = city;
    }

    public String getCity(){
        return city;
    }

    public String getDistrict(){
        return district;
    }

    public void setDistrict(String district){
        this.district = district;
    }

    public String getAccountName(){
        return  accountName;
    }

    public void setAccountName(String accountName){
        this.accountName = accountName;
    }

    public String getAccountNo(){
        return accountNo;
    }

    public void setAccountNo(String accountNo){
        this.accountNo = accountNo;
    }

    public String getBankName(){
        return bankName;
    }

    public void setBankName(String bankName){
        this.bankName = bankName;
    }

    public String getBankNo(){
        return bankNo;
    }

    public void setBankNo(String bankNo){
        this.bankNo = bankNo;
    }

    public void setAccountType(String accountType){
        this.accountType = accountType;
    }

    public String getAccountType(){
        return accountType;
    }

    public void setBusinessName(String businessName){
        this.businessName = businessName;
    }

    public String getBusinessName(){
        return businessName;
    }

    public String getBusinessNo(){
        return businessNo;
    }

    public void setBusinessNo(String businessNo){
        this.businessNo = businessNo;
    }

    public String getBusinessAddr(){
        return businessAddr;
    }

    public void setBusinessAddr(String businessName){
        this.businessAddr = businessName;
    }

    public String getZipCode(){
        return zipCode;
    }

    public void setZipCode(String zipCode){
        this.zipCode = zipCode;
    }

    public String getHomeAddr(){
        return homeAddr;
    }

    public String getMerchantAddress(){

        return (province== null? "" : province)
                + (city==null ? "" : city)
                + (district==null?"" : district )
                + (homeAddr==null? "" : homeAddr);

    }

    public String getDistDetail(){
        return ("".equals(province)? "" : province)
                + ("".equals(city) ? "" : ("-" +city))
                + ("".equals(district)?"" : ("-" +district) );
    }

    public void setHomeAddr(String homeAddr){
        this.homeAddr = homeAddr;
    }


    public void setMobileNo(String mobileNo){
        this.mobileNo  = mobileNo;
    }

    public String getMobileNo(){
        return mobileNo;
    }

    public void setUsrInfoEditable(boolean editable){
        this.isUsrInfoEditable = editable;
    }

    public boolean isUsrInfoEditable(){
        return isUsrInfoEditable;
    }

    /**
     * null，“REJECT”，“NONE" 可以提交
     * PASS，APPLY不能提交
     * @param authStatus
     * @return
     */
    public static boolean handleAuthStatus(String authStatus){
        if(authStatus.equals("PASS") || authStatus.equals("APPLY")){
            return false;
        }
        return true;
    }

    public void setAuthRemark(String authRemark){
    	this.authRemark = authRemark;
    }
    
    public String getAuthRemark(){
    	return authRemark;
    }

    public void unpackError(JSONArray jsonArray){


        if(jsonArray == null){
            return ;
        }
        try {

            int index = 0;
            for(int i=0; i<jsonArray.length(); i++){
                String msg = jsonArray.getJSONObject(i).optString("msg");
                String code = jsonArray.getJSONObject(i).getString("code");

                errField.put(code, msg);
            }

        }catch (Exception e){

        }

    }

    public boolean has(String name){

        if(errField == null){
            return false;
        }

        if(errField.containsKey(name)){
            return true;
        }
        return false;
//        for(int i=0; i<errFields.length ;i++){
//            if(errFields[i] == null){
//                return false;
//            }
//            if(name.equals(errFields[i])){
//                return true;
//            }
//        }
//        return false;
    }


    private  Map<String, String> errField = new HashMap<String, String>();
//    static{
//        errField.put("10001", "商户名称");
//        errField.put("10002", "经营地址");
//        errField.put("10003", "负责人姓名");
//        errField.put("10004", "身份证号");
//        errField.put("10005", "联系电话");
//        errField.put("10006", "Email");
//        errField.put("10007", "收款银行");
//        errField.put("10008", "收款卡号");
//        errField.put("10009", "收款人姓名");
//    }

    @Override
    public String toString() {
        return "ShoudanRegisterInfo{" +
                "realName='" + realName + '\'' +
                ", idCardType='" + idCardType + '\'' +
                ", idCardNo='" + idCardNo + '\'' +
                ", email='" + email + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", homeAddr='" + homeAddr + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", businessAddr='" + businessAddr + '\'' +
                ", businessName='" + businessName + '\'' +
                ", businessNo='" + businessNo + '\'' +
                ", accountType='" + accountType + '\'' +
                ", bankNo='" + bankNo + '\'' +
                ", bankName='" + bankName + '\'' +
                ", accountNo='" + accountNo + '\'' +
                ", accountName='" + accountName + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", picPath1='" + picPath1 + '\'' +
                ", picPath2='" + picPath2 + '\'' +
                ", isUsrInfoEditable=" + isUsrInfoEditable +
                ", authRemark=" + authRemark +
                '}';
    }

    public enum T0Status {
        UNKNOWN("未知状态"),
        NOTSUPPORT("不支持"),
        SUPPORT("支持/未申请"),
        PROCESSING("处理中"),
        COMPLETED("完成"),
        FAILURE("失败");
        private String errMsg;

        /**
         *
         * @param describe 状态描述
         */
        T0Status(String describe) {
        }

        public String getErrMsg() {
            return errMsg;
        }

        public void setErrMsg(String errMsg) {
            this.errMsg = errMsg;
        }
    }
}
