package com.lakala.platform.bean;

import android.text.TextUtils;

import com.lakala.core.cordova.cordova.App;
import com.lakala.library.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by More on 15/3/23.
 */
public class AppConfig {

    private static final String MOBILE ="CUSTOMER_SERVICE_MOBILE";//客服电话
    private static final String IC_DOWN_ENABLED = "IC_DOWN_ENABLED";//是否允许降级
    //Config
    private static final String LOAN_ENABLED ="LOAN_ENABLED";//信贷服务
    private static final String ADVANCE_DRAW_ENABLED = "ADVANCE_DRAW_ENABLED";//提前划款
    private static final String DROW_APPLY_ENABLED = "DROW_APPLY_ENABLED";//贷款申请
    private static final String WHOLESALE_ENABLED = "WHOLESALE_ENABLED";//超级批发
    private static final String WEALTH_ENABLED = "WEALTH_ENABLED";


    private boolean rentCollectionEnabled = false;

    private boolean contributePaymentEnabled = false;


    /**
     * app 设置 作为json string 保存本地
     */
    private String config = "";

    private String lakalServantTel = "4007666666";
    private boolean icDownEnabled = false;

    private boolean loanEnabled = false;
    private boolean advanceDrawEnabled = false;
    private boolean drowApplyEnabled = false;
    private boolean wholeSaleEnabled = false;
    private boolean wealthEnabled = true;

    public AppConfig() {
    }

    public AppConfig(String appconfig) {
        if(!TextUtils.isEmpty(appconfig)){
            try{

                initFromJson(new JSONObject(appconfig));

            }catch (Exception e){
                LogUtil.print(e);
            }
        }
    }

    private void initFromJson(JSONObject jsonObject){
        if(jsonObject == null){
            return;
        }
        config = jsonObject.toString();
        lakalServantTel = jsonObject.optString(MOBILE, "4007666666");
        icDownEnabled = jsonObject.optBoolean(IC_DOWN_ENABLED, false);
        loanEnabled = jsonObject.optBoolean(LOAN_ENABLED,false);
        advanceDrawEnabled = jsonObject.optBoolean(ADVANCE_DRAW_ENABLED,false);
        drowApplyEnabled = jsonObject.optBoolean(DROW_APPLY_ENABLED, false);
        wholeSaleEnabled = jsonObject.optBoolean(WHOLESALE_ENABLED,false);
        wealthEnabled = jsonObject.optBoolean(WEALTH_ENABLED,false);
    }

    public AppConfig(JSONObject jsonObject) {

        initFromJson(jsonObject);

    }

//    public String getConfig() {
//        return config;
//    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getLakalServantTel() {
        return lakalServantTel;
    }

    public void setLakalServantTel(String lakalServantTel) {
        this.lakalServantTel = lakalServantTel;
    }

    public boolean isWealthEnabled() {
        return wealthEnabled;
    }

    public AppConfig setWealthEnabled(boolean wealthEnabled) {
        this.wealthEnabled = wealthEnabled;
        return this;
    }

    public boolean isIcDownEnabled() {
        return icDownEnabled;
    }

    public void setIcDownEnabled(boolean icDownEnabled) {
        this.icDownEnabled = icDownEnabled;
    }

    public boolean isLoanEnabled() {
        return loanEnabled;
    }

    public void setLoanEnabled(boolean loanEnabled) {
        this.loanEnabled = loanEnabled;
    }

    public boolean isAdvanceDrawEnabled() {
        return advanceDrawEnabled;
    }

    public void setAdvanceDrawEnabled(boolean advanceDrawEnabled) {
        this.advanceDrawEnabled = advanceDrawEnabled;
    }

    public boolean isDrowApplyEnabled() {
        return drowApplyEnabled;
    }

    public void setDrowApplyEnabled(boolean drowApplyEnabled) {
        this.drowApplyEnabled = drowApplyEnabled;
    }

    public boolean isWholeSaleEnabled() {
        return wholeSaleEnabled;
    }

    public void setWholeSaleEnabled(boolean wholeSaleEnabled) {
        this.wholeSaleEnabled = wholeSaleEnabled;
    }

    public boolean isRentCollectionEnabled() {
        return rentCollectionEnabled;
    }

    public void setRentCollectionEnabled(boolean rentCollectionEnabled) {
        this.rentCollectionEnabled = rentCollectionEnabled;
    }

    public boolean isContributePaymentEnabled() {
        return contributePaymentEnabled;
    }

    public void setContributePaymentEnabled(boolean contributePaymentEnabled) {
        this.contributePaymentEnabled = contributePaymentEnabled;
    }

    public String getConfigStr(){


        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("rentCollectionEnabled", rentCollectionEnabled);
            jsonObject.put("contributePaymentEnabled", contributePaymentEnabled);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.print(jsonObject.toString());

        return jsonObject.toString();
    }

    public AppConfig updateConfig(String str){

        if(!TextUtils.isEmpty(str)){

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(str);

                rentCollectionEnabled = jsonObject.optBoolean("rentCollectionEnabled", false);
                contributePaymentEnabled = jsonObject.optBoolean("contributePaymentEnabled", false);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        return this;
    }


    @Override
    public String toString() {
        return "AppConfig{" +
                "rentCollectionEnabled=" + rentCollectionEnabled +
                ", contributePaymentEnabled=" + contributePaymentEnabled +
                ", config='" + config + '\'' +
                ", lakalServantTel='" + lakalServantTel + '\'' +
                ", icDownEnabled=" + icDownEnabled +
                ", loanEnabled=" + loanEnabled +
                ", advanceDrawEnabled=" + advanceDrawEnabled +
                ", drowApplyEnabled=" + drowApplyEnabled +
                ", wholeSaleEnabled=" + wholeSaleEnabled +
                ", wealthEnabled=" + wealthEnabled +
                '}';
    }
}
