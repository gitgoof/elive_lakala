package com.lakala.platform.bean;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LMQ on 2015/12/7.
 */
public class MerchantInfo {


    private String shopNo;
    /**升级状态*/
    private UpgradeStatus upgradeStatus = UpgradeStatus.NONE;
    /**
     * 用户个人信息
     * 包含真实姓名,身份证信息(身份照,身份证号)
     */
    private UserEntity user;
    /**
     * 商户地址信息
     * 提供包括详细分类信息,以及封装了用来展示整体信息的方法
     */
    private BusinessAddressEntity businessAddress = new BusinessAddressEntity();
    /**
     * 邮件
     */
    private String email = "";
    /**
     * 商户名
     */
    private String businessName = "";
    /**
     * 商户类型:  对公(public,企业)对私(个人,private)
     */
    private AccountType accountType = AccountType.PRIVATE;
    /**
     * 收款银行卡开户行号
     */
    private String bankNo = "";
    /**
     * 收款银行卡开户行
     */
    private String bankName = "";
    /*
    * 收款银行卡卡号
     */
    private String accountNo = "";
    /**
     * 收款银行卡开户名
     */
    private String accountName = "";
    /**
     * 商户审核状态
     */
    private MerchantStatus merchantStatus = MerchantStatus.NONE;
    /**
     * 商户号
     */
    private String merNo = "";
    /**
     * T0 开通状态
     */
    private T0Status t0  = T0Status.UNKNOWN;
    /**
     * 呵呵哒
     */
    private Map<String, String> errField = new HashMap<String, String>();

    public Map<String, String> getErrField() {

        return errField;
    }


    public boolean has(String name){
        if(errField == null){
            return false;
        }

        if(errField.containsKey(name)){
            return true;
        }
        return false;
    }

    public void setErrField(Map<String, String> errField) {
        this.errField = errField;
    }

    public MerchantInfo(String jsonObjectString) throws JSONException {

        this.jsonMerchantInfo = jsonObjectString;
        if(TextUtils.isEmpty(jsonObjectString)){
            return;
        }
        JSONObject jsonObject = new JSONObject(jsonObjectString);

        user = new UserEntity(jsonObject.optJSONObject("user"));
        businessAddress = new BusinessAddressEntity(jsonObject.optJSONObject("businessAddress"));
        email = jsonObject.optString("email");
        businessName = jsonObject.optString("businessName");
        accountType = AccountType.fromString(jsonObject.optString("accountType", "0"));
        bankNo = jsonObject.optString("bankNo");
        bankName = jsonObject.optString("bankName");
        accountNo = jsonObject.optString("accountNo");
        accountName = jsonObject.optString("accountName");
        merchantStatus = MerchantStatus.fromString(jsonObject.optString("merchantStatus", "NONE"));
        upgradeStatus = UpgradeStatus.fromString(jsonObject.optString("upgradeStatus", "NONE"));
        merNo = jsonObject.optString("merNo");
        shopNo = jsonObject.optString("shopNo");
        t0 = T0Status.valueOf(jsonObject.optString("t0","UNKNOWN"));
        parseError(jsonObject.optJSONArray("error"));
    }

    public String getShopNo() {
        return shopNo;
    }

    public MerchantInfo setShopNo(String shopNo) {
        this.shopNo = shopNo;
        return this;
    }

    public UpgradeStatus getUpgradeStatus() {
        return upgradeStatus;
    }

    public MerchantInfo setUpgradeStatus(UpgradeStatus upgradeStatus) {
        this.upgradeStatus = upgradeStatus;
        return this;
    }

    private void parseError(JSONArray jsonArray){

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
            LogUtil.print(e);
        }

    }



    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setBusinessAddress(BusinessAddressEntity businessAddress) {
        this.businessAddress = businessAddress;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setMerchantStatus(MerchantStatus merchantStatus) {
        this.merchantStatus = merchantStatus;
    }

    public void setMerNo(String merNo) {
        this.merNo = merNo;
    }

    public void setT0(T0Status t0) {
        this.t0 = t0;
    }

    public UserEntity getUser() {
        return user;
    }

    public BusinessAddressEntity getBusinessAddress() {
        return businessAddress;
    }

    public String getEmail() {
        return email;
    }

    public String getBusinessName() {
        return businessName;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public String getBankNo() {
        return bankNo;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public String getAccountName() {
        return accountName;
    }

    public MerchantStatus getMerchantStatus() {
        return merchantStatus;
    }

    public String getMerNo() {
        return merNo;
    }


    public T0Status getT0() {
        return t0;
    }

    public static class UserEntity {
        private int id;
        /**
         * 用户的登陆账号
         */
        private String loginName;
        /**
         * 密码安全级别
         */
        private String passwordSecurityLevel;
        /**
         * 性别
         */
        private String gender;
        /**
         * 身份证信息
         */
        private IdCardInfoEntity idCardInfo;
        /**
         * 手机号
         */
        private String mobileNum;
        /**
         * 真实姓名
         */
        private String realName;

        private boolean enabled;

        private String vipLevelCode;
        private String vipLevelName;
        private String customerType;

        public UserEntity(JSONObject jsonObject) {

            id = jsonObject.optInt("id");
            loginName = jsonObject.optString("loginName");
            passwordSecurityLevel = jsonObject.optString("passwordSecurityLevel");
            gender = jsonObject.optString("gender");
            idCardInfo= new IdCardInfoEntity(jsonObject.optJSONObject("idCardInfo"));
            mobileNum = jsonObject.optString("mobileNum");
            realName = jsonObject.optString("realName");
            enabled = jsonObject.optBoolean("enabled");
            vipLevelCode = jsonObject.optString("vipLevelCode");
            vipLevelName = jsonObject.optString("vipLevelName");
            customerType = jsonObject.optString("customerType");
        }


        public void setId(int id) {
            this.id = id;
        }

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }

        public void setPasswordSecurityLevel(String passwordSecurityLevel) {
            this.passwordSecurityLevel = passwordSecurityLevel;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public void setIdCardInfo(IdCardInfoEntity idCardInfo) {
            this.idCardInfo = idCardInfo;
        }

        public void setMobileNum(String mobileNum) {
            this.mobileNum = mobileNum;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public void setVipLevelCode(String vipLevelCode) {
            this.vipLevelCode = vipLevelCode;
        }

        public void setVipLevelName(String vipLevelName) {
            this.vipLevelName = vipLevelName;
        }

        public void setCustomerType(String customerType) {
            this.customerType = customerType;
        }


        public int getId() {
            return id;
        }

        public String getLoginName() {
            return loginName;
        }

        public String getPasswordSecurityLevel() {
            return passwordSecurityLevel;
        }

        public String getGender() {
            return gender;
        }

        public IdCardInfoEntity getIdCardInfo() {
            return idCardInfo;
        }


        public String getMobileNum() {
            return mobileNum;
        }

        public String getRealName() {
            return realName;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public String getVipLevelCode() {
            return vipLevelCode;
        }

        public String getVipLevelName() {
            return vipLevelName;
        }

        public boolean isIdInfoValid(){
            return TextUtils.equals("REALNAME_AUTHD",customerType);
        }


        public static class IdCardInfoEntity {
            /**
             * 证件类型
             */
            private String idCardType = "";
            /**
             * 证件号
             */
            private String idCardId = "";

            private boolean isAvailable;
            /**
             * 用户实名认证状态
             */
            private UserAuthStatus authStatus = UserAuthStatus.NONE;
            /**
             *
             */
            private boolean hasIdCardPic;
            private String authRemark = "";
            private boolean available;
            /**
             * 身份证正面
             */
            private String picPath1 = "";
            /**
             * 身份证反面
             */
            private String picPath2 = "";

            public IdCardInfoEntity() {
            }

            public IdCardInfoEntity(JSONObject jsonObject) {
                if(jsonObject == null){
                    return;
                }
                idCardType = jsonObject.optString("idCardType");
                idCardId = jsonObject.optString("idCardId");
                authStatus = UserAuthStatus.valueOf(jsonObject.optString("authStatus", "NONE"));
                JSONArray jsonArray = jsonObject.optJSONArray("picPaths");
                try {
                    if(jsonArray != null && jsonArray.length() >=2){
                        picPath1 = jsonArray.getString(0);
                        picPath2 = jsonArray.getString(1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void setAuthRemark(String authRemark) {
                this.authRemark = authRemark;
            }

            public String getPicPath1() {
                return picPath1;
            }

            public void setPicPath1(String picPath1) {
                this.picPath1 = picPath1;
            }

            public String getPicPath2() {
                return picPath2;
            }

            public void setPicPath2(String picPath2) {
                this.picPath2 = picPath2;
            }

            public void setIdCardType(String idCardType) {
                this.idCardType = idCardType;
            }

            public void setIdCardId(String idCardId) {
                this.idCardId = idCardId;
            }

            public void setIsAvailable(boolean isAvailable) {
                this.isAvailable = isAvailable;
            }

            public void setAuthStatus(UserAuthStatus authStatus) {
                this.authStatus = authStatus;
            }


            public void setHasIdCardPic(boolean hasIdCardPic) {
                this.hasIdCardPic = hasIdCardPic;
            }

            public void setAvailable(boolean available) {
                this.available = available;
            }


            public String getIdCardType() {
                return idCardType;
            }

            public String getIdCardId() {
                return idCardId;
            }

            public boolean isIsAvailable() {
                return isAvailable;
            }
//
//            public UserAuthStatus getAuthStatus() {
//                return authStatus;
//            }

            public boolean isHasIdCardPic() {
                return hasIdCardPic;
            }

            public Object getAuthRemark() {
                return authRemark;
            }

            public boolean isAvailable() {
                return available;
            }

            @Override
            public String toString() {
                return JSON.toJSONString(this);
            }
        }

        @Override
        public String toString() {
            return "UserEntity{" +
                    "id=" + id +
                    ", loginName='" + loginName + '\'' +
                    ", passwordSecurityLevel='" + passwordSecurityLevel + '\'' +
                    ", gender='" + gender + '\'' +
                    ", idCardInfo=" + idCardInfo +
                    ", mobileNum='" + mobileNum + '\'' +
                    ", realName='" + realName + '\'' +
                    ", enabled=" + enabled +
                    ", vipLevelCode='" + vipLevelCode + '\'' +
                    ", vipLevelName='" + vipLevelName + '\'' +
                    ", customerType='" + customerType + '\'' +
                    '}';
        }
    }



    public static class BusinessAddressEntity {

        /**
         *
         */
        private String country = "";
        private String countryName = "";
        /**
         * 省号
         */
        private String province = "";
        /**
         * 省名
         */
        private String provinceName = "";
        /**
         * 市号
         */
        private String city = "";
        /**
         * 市名
         */
        private String cityName = "";
        /**
         * 区号
         */
        private String district = "";
        /**
         * 区名
         */
        private String districtName = "";
        /**
         * 详细地址
         */
        private String homeAddr = "";
        /**
         * 邮编
         */
        private String zipCode = "";
        public String getAd(){
            return provinceName+cityName+districtName;
        }

        public BusinessAddressEntity() {
        }

        public BusinessAddressEntity(JSONObject jsonObject) {

            if(jsonObject == null){
                return;
            }
            country = jsonObject.optString("country");
            countryName = jsonObject.optString("countryName");
            province = jsonObject.optString("province");
            provinceName = jsonObject.optString("provinceName");
            city = jsonObject.optString("city");
            cityName = jsonObject.optString("cityName");
            district = jsonObject.optString("district");
            districtName = jsonObject.optString("districtName");
            homeAddr = jsonObject.optString("homeAddr");
            zipCode = jsonObject.optString("zipCode");

        }

        public void setCountry(String country) {
            this.country = country;
        }

        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public void setProvinceName(String provinceName) {
            this.provinceName = provinceName;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public void setDistrictName(String districtName) {
            this.districtName = districtName;
        }

        public void setHomeAddr(String homeAddr) {
            this.homeAddr = homeAddr;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        public String getCountry() {
            return country;
        }

        public String getCountryName() {
            return countryName;
        }

        public String getProvince() {
            return province;
        }

        public String getProvinceName() {
            return provinceName;
        }

        public String getCity() {
            return city;
        }

        public String getCityName() {
            return cityName;
        }

        public String getDistrict() {
            return district;
        }

        public String getDistrictName() {
            return districtName;
        }

        public String getHomeAddr() {
            return homeAddr;
        }

        public String getZipCode() {
            return zipCode;
        }

        public String getFullDisplayAddress(){
            StringBuilder full = new StringBuilder();
            append(full,provinceName);
            append(full,cityName);
            append(full,districtName);
            append(full,homeAddr);
            return full.toString();

        }
        private void append(StringBuilder stringBuilder,String str){
            if(stringBuilder == null){
                return;
            }
            if(TextUtils.isEmpty(str) || "null".equals(str)){
                return;
            }
            stringBuilder.append(str);
        }

        public String getDistDetail(){
            StringBuilder stringBuilder = new StringBuilder();
            append(stringBuilder,provinceName);
            append(stringBuilder,cityName);
            append(stringBuilder,districtName);
            return stringBuilder.toString();
        }

        @Override
        public String toString() {
            return "BusinessAddressEntity{" +
                    "country='" + country + '\'' +
                    ", countryName='" + countryName + '\'' +
                    ", province='" + province + '\'' +
                    ", provinceName='" + provinceName + '\'' +
                    ", city='" + city + '\'' +
                    ", cityName='" + cityName + '\'' +
                    ", district='" + district + '\'' +
                    ", districtName='" + districtName + '\'' +
                    ", homeAddr='" + homeAddr + '\'' +
                    ", zipCode='" + zipCode + '\'' +
                    '}';
        }
    }


    public String getDisplasyAccountNo(){

        return accountType == AccountType.PUBLIC? StringUtil.formatCompanyAccount(
                getAccountNo()):
                StringUtil.formatCardNumberN6S4N4(getAccountNo());

    }

    private String jsonMerchantInfo;

    public String getJsonMerchantInfo() {
        return jsonMerchantInfo;
    }

    public void setJsonMerchantInfo(String jsonMerchantInfo) {
        this.jsonMerchantInfo = jsonMerchantInfo;
    }

    @Override
    public String toString() {
        return "MerchantInfo{" +
                "user=" + user +
                ", businessAddress=" + businessAddress +
                ", email='" + email + '\'' +
                ", businessName='" + businessName + '\'' +
                ", accountType='" + accountType + '\'' +
                ", bankNo='" + bankNo + '\'' +
                ", bankName='" + bankName + '\'' +
                ", accountNo='" + accountNo + '\'' +
                ", accountName='" + accountName + '\'' +
                ", merchantStatus='" + merchantStatus + '\'' +
                ", merNo='" + merNo + '\'' +
                ", t0='" + t0 + '\'' +
                ", errField=" + errField +
                '}';
    }

}
