package com.lakala.platform.bean;

import android.text.TextUtils;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.common.LKlPreferencesKey;
import com.lakala.platform.common.LklPreferences;
import com.lakala.platform.dao.UserDao;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户信息相关bean
 * <p/>
 * Created by jerry on 14-1-7.
 */
public class User {


    /**
     * 用户级别（用于钱包，理财，一块夺宝的话术）
     * 根据userTypeInfo的值设定此参数的值 默认为1 显示话术；0 隐藏话术
     */
    private int userType=1;

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    /**
     * 登录用户名
     */
    private String loginName = "";
    /**
     * 用户id
     */
    private String userId = "";

    /**
     * token
     */
    private String accessToken = "";//token

    /**
     * refresh Token
     */
    private String refreshToken = "";//刷新令牌
    /**
     * 登录下发虚拟终端号
     */
    private String terminalId = "";

    /**
     * MTS terminal Id
     */
    private String mtsTerminalId = "";

    /**
     * 手势密码
     */
    private String gesturePwd;

//    /**
//     * 刷卡器串号
//     */
//    private String swiperId = "";

    /**
     * 未读消息条目
     */
    private int msgCount = 0;

    /**
     * app 设置
     */
    private AppConfig appConfig = new AppConfig();
    /**
     * 扫码接入
     */
    private ScancodeAccess scancodeAccess = ScancodeAccess.UNKNOWN;

    /**
     * 实名认证状态
     */
    private AuthenticationStatus authenticationStatus = AuthenticationStatus.UNKNOWN;

    /**
     * 大额收款权限
     */
    private LargeAmountAccess largeAmountAccess = LargeAmountAccess.UNKNOWN;

    /**
     * 支付密码标识  1:true
     */
    private boolean TrsPasswordFlag;
    /**
     * 支付密码密保问题标识 1:true
     */
    private boolean QuestionFlag;


    private String bankNoBeijing = "";//理财中使用的

    private String largeAmountLimit;

    private String mtsPinKey;
    private String mtsWorkKey;
    private String mtsMacKey;
    private String authFlag;
    private String mtsCustomerName;
    //身份证号码
    private String identifier = "";


    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getMtsCustomerName() {
        return mtsCustomerName;
    }

    public void setMtsCustomerName(String mtsCustomerName) {
        this.mtsCustomerName = mtsCustomerName;
    }

    public String getAuthFlag() {
        return authFlag;
    }

    public void setAuthFlag(String authFlag) {
        this.authFlag = authFlag;
    }

    public String getMtsPinKey() {
        return mtsPinKey;
    }

    public void setMtsPinKey(String mtsPinKey) {
        this.mtsPinKey = mtsPinKey;
    }

    public String getMtsWorkKey() {
        return mtsWorkKey;
    }

    public void setMtsWorkKey(String mtsWorkKey) {
        this.mtsWorkKey = mtsWorkKey;
    }

    public String getMtsMacKey() {
        return mtsMacKey;
    }

    public void setMtsMacKey(String mtsMacKey) {
        this.mtsMacKey = mtsMacKey;
    }

    public int getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }

    /**
     * 有效期
     */
    private String expirein;//有效期


    public User() {

    }

    /**
     * 通过接口返回的参数，构造一个user对象
     *
     * @param jsonObject    数据
     */
    public User(JSONObject jsonObject) throws JSONException {
        this.initMerchantAttrWithJson(jsonObject);
    }

    public String getMtsTerminalId() {
        return mtsTerminalId;
    }

    public void setMtsTerminalId(String mtsTerminalId) {
        this.mtsTerminalId = mtsTerminalId;
    }

    public boolean isTrsPasswordFlag() {
        return TrsPasswordFlag;
    }

    public void setTrsPasswordFlag(boolean trsPasswordFlag) {
        TrsPasswordFlag = trsPasswordFlag;
    }

    public boolean isQuestionFlag() {
        return QuestionFlag;
    }

    public void setQuestionFlag(boolean questionFlag) {
        QuestionFlag = questionFlag;
    }

    public void setExpirein(String expirein){
        this.expirein = expirein;
    }

    public String getExpirein(){
        return this.expirein;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getGesturePwd() {
        return gesturePwd;
    }

    public void updateGesturePwd(String gesturePwd){
        this.gesturePwd = gesturePwd;
        if(TextUtils.isEmpty(gesturePwd)){
            LklPreferences.getInstance().putBoolean(LKlPreferencesKey.KEY_IGNORE_SET_GESTURE + getLoginName(), true);
        }else{
            LklPreferences.getInstance().putBoolean(LKlPreferencesKey.KEY_IGNORE_SET_GESTURE + getLoginName(), false);
        }
        save();
    }

    public void setGesturePwd(String gesturePwd) {
        this.gesturePwd = gesturePwd;
    }

    /**
     * 是否存在手势密码信息
     */
    public boolean isExistGesturePassword() {
        return StringUtil.isNotEmpty(gesturePwd);
    }

    /**
     * 保存用户信息到数据库
     */
    public void save() {
        if(TextUtils.isEmpty(loginName)){
            return;
        }

        try {
            UserDao.getInstance().saveUser(this);
        } catch (Exception e) {
            LogUtil.print(e);
        }
    }

    public boolean ifSkipGesture(){
        return !isExistGesturePassword();
    }

    public void updateUserToken(JSONObject jsonObject){

        String token = jsonObject.optString("token");
        String refreshToken = jsonObject.optString("refreshtoken");
        String expirein = jsonObject.optString("expirein");

        setAccessToken(token);
        setRefreshToken(refreshToken);
        setExpirein(expirein);
        //需要将最新的token写入数据库,防止应用被杀导致token丢失
        save();

    }

    public void setGestureSkip(){
        updateGesturePwd("");
        save();
    }

    public void clear(){
        loginName = "";
        terminalId = "";
        userId = "";
        accessToken = "";
        refreshToken = "";
        expirein = "";

    }

    private MerchantInfo merchantInfo;

    public MerchantInfo getMerchantInfo() {
        return merchantInfo;
    }


    public void setMerchantInfo(MerchantInfo merchantInfo) {
        this.merchantInfo = merchantInfo;
    }

    public void initMerchantAttrWithJson(JSONObject data) throws JSONException {

        mtsInitWithJson(data);

        merchantInfo = new MerchantInfo(data.toString());
        LogUtil.print(merchantInfo.toString());
        /**
         * 用户个人信息
         */
        setUserId(data.optString("id"));

        /**
         * 商户信息
         */
        JSONObject merchant = data.optJSONObject("merchant");

        if(merchant == null){
            return;
        }

        /**
         * 系统开关
         */
        JSONObject config = data.optJSONObject("config");

        this.appConfig = new AppConfig(config);
    }




    private void mtsInitWithJson(JSONObject data){
        if (data.has("CustomerName")){
            this.setMtsCustomerName(data.optString("CustomerName"));
        }
        if (data.has("AuthFlag")){
            this.setAuthFlag(data.optString("AuthFlag"));
        }
        if (data.has("QuestionFlag")){
            this.setQuestionFlag("1".equals(data.optString("QuestionFlag")));
        }
        if (data.has("TrsPasswordFlag")){
            this.setTrsPasswordFlag("1".equals(data.optString("TrsPasswordFlag")));
        }
        if (data.has("Identifier")){
            this.setIdentifier(data.optString("Identifier"));
        }
    }

    public AppConfig getAppConfig() {
        return appConfig;
    }

    public void setAppConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public boolean skipGesture(){
        return  LklPreferences.getInstance().getBoolean(LKlPreferencesKey.KEY_IGNORE_SET_GESTURE + getLoginName(), false);
    }

    public void retSkipGesture(boolean b){
        LklPreferences.getInstance().putBoolean(LKlPreferencesKey.KEY_IGNORE_SET_GESTURE + getLoginName(), b);
    }

    public ScancodeAccess getScancodeAccess() {

        return scancodeAccess;
    }

    public void setScancodeAccess(ScancodeAccess scancodeAccess) {
        this.scancodeAccess = scancodeAccess;
    }

    public AuthenticationStatus getAuthenticationStatus() {
        return authenticationStatus;
    }

    public void setAuthenticationStatus(AuthenticationStatus authenticationStatus) {
        this.authenticationStatus = authenticationStatus;
    }

    public LargeAmountAccess getLargeAmountAccess() {
        return largeAmountAccess;
    }

    public void setLargeAmountAccess(LargeAmountAccess largeAmountAccess) {
        this.largeAmountAccess = largeAmountAccess;
    }

    public String getBankNoBeijing() {
        return bankNoBeijing;
    }

    public void setBankNoBeijing(String bankNoBeijing) {
        this.bankNoBeijing = bankNoBeijing;
    }

    public String getLargeAmountLimit() {
        return largeAmountLimit;
    }

    public void setLargeAmountLimit(String largeAmountLimit) {
        this.largeAmountLimit = largeAmountLimit;
    }



    @Override
    public String toString() {
        return "User{" +
                "loginName='" + loginName + '\'' +
                ", userId='" + userId + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", terminalId='" + terminalId + '\'' +
                ", mtsTerminalId='" + mtsTerminalId + '\'' +
                ", gesturePwd='" + gesturePwd + '\'' +
                ", msgCount=" + msgCount +
                ", appConfig=" + appConfig +
                ", TrsPasswordFlag=" + TrsPasswordFlag +
                ", QuestionFlag=" + QuestionFlag +
                ", mtsPinKey='" + mtsPinKey + '\'' +
                ", mtsWorkKey='" + mtsWorkKey + '\'' +
                ", mtsMacKey='" + mtsMacKey + '\'' +
                ", authFlag='" + authFlag + '\'' +
                ", mtsCustomerName='" + mtsCustomerName + '\'' +
                ", identifier='" + identifier + '\'' +
                ", expirein='" + expirein + '\'' +
                ", merchantInfo=" + merchantInfo +
                '}';
    }
}
