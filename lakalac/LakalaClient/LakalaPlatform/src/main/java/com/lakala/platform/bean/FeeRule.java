package com.lakala.platform.bean;

import android.text.TextUtils;

/**
 * Created by HUASHO on 2015/1/21.
 * 转账汇款手续费规则类
 */
public class FeeRule {
    /**	手续费规则id	*/
    public String feeId = "";

    /**	手续费种类名	*/
    public String transName = "";

    /**	最低手续费	*/
    private String minFee = "";

    /**	最高手续费	*/
    private String maxFee = "";

    /**	手续费比率	*/
    private String chargeRate = "";

    public String getMinFee() {

        if(minFee == null || TextUtils.isEmpty(minFee)){
            return "0";
        }

        return minFee;
    }

    public void setMinFee(String minFee) {
        this.minFee = minFee;
    }

    public String getMaxFee() {

        if(maxFee == null || TextUtils.isEmpty(maxFee)){
            return "0";
        }

        return maxFee;
    }

    public void setMaxFee(String maxFee) {
        this.maxFee = maxFee;
    }

    public String getChargeRate() {

        if(chargeRate == null || TextUtils.isEmpty(chargeRate)){
            return "0";
        }


        return chargeRate;
    }

    public void setChargeRate(String chargeRate) {
        this.chargeRate = chargeRate;
    }
}
