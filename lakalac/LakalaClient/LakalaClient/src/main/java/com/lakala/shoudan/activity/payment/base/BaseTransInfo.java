package com.lakala.shoudan.activity.payment.base;

import android.text.TextUtils;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by More on 15/1/22.
 *
 * 基础交易信息
 */
public abstract class BaseTransInfo implements TransInfoInterface,Serializable {

    /**
     * 交易结果
     */
    protected TransResult transResult;

    protected SwiperInfo.CardType cardType;

    /**
     * 提示信息,主要用于失败,
     */
    protected String msg = "";

    protected String payCardNo = "";

    protected String sid = "";

    protected String sysRef = "";

    protected String syTm = ""; //系统交易时间

    public String getPayCardNo() {
        return payCardNo;
    }

    public void setPayCardNo(String payCardNo) {
        this.payCardNo = payCardNo;
    }

    public TransResult getTransResult() {
        return transResult;
    }

    public void setTransResult(TransResult transResult) {
        this.transResult = transResult;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSysRef() {
        return sysRef;
    }

    public void setSysRef(String sysRef) {
        this.sysRef = sysRef;
    }

    public String getSyTm() {
        return syTm;
    }

    public void setSyTm(String syTm) {
        this.syTm = syTm;
    }

    public SwiperInfo.CardType getCardType() {
        return cardType;
    }

    public void setCardType(SwiperInfo.CardType cardType) {
        this.cardType = cardType;
    }

    private String icc55;
    private int tcAsyFlag;

    private String acinstcode;

    public String getAcinstcode() {
        return acinstcode;
    }

    public void setAcinstcode(String acinstcode) {
        this.acinstcode = acinstcode;
    }

    public String getIcc55() {
        return icc55;
    }

    public void setIcc55(String icc55) {
        this.icc55 = icc55;
    }

    public int getTcAsyFlag() {
        return tcAsyFlag;
    }

    public void setTcAsyFlag(int tcAsyFlag) {
        this.tcAsyFlag = tcAsyFlag;
    }

    public void optBaseData(String jsonStr){

        try {

            JSONObject data = new JSONObject(jsonStr);
            LogUtil.print("<><><>",data.toString());
            this.syTm = data.optString("sytm");
            this.sysRef = data.optString("sysref");
            icc55 = data.optString("icc55");
            tcAsyFlag = data.optInt("tc_asyflag");
            sid = data.optString("sid");
            authcode= data.optString("authcode");
            acinstcode = data.optString("acinstcode");
            if(!TextUtils.isEmpty(data.optString("pan",""))){
                payCardNo = data.optString("pan");
            }
        }catch (JSONException e){
            LogUtil.print(e);
            LogUtil.print("<AS>","解析异常");
        }


    }
    private String authcode;

    public abstract TransactionType getType();


    public String getAuthcode() {
        return authcode;
    }

    public void setAuthcode(String authcode) {
        this.authcode = authcode;
    }

    private int resultCode;//交易结果

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    @Deprecated
    public void unpackValidateResult(JSONObject jsonObject) {

    }

    /**
     * suppose to be abstract
     * @return
     */
    public abstract String getAdditionalMsg();

}


