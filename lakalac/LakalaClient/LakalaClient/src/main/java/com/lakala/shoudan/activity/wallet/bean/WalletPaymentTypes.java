package com.lakala.shoudan.activity.wallet.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by fengxuan on 2015/12/18.
 */
public class WalletPaymentTypes {

    private JSONArray paymentTypes = new JSONArray();
    private String paymentType;
    private String random;
    private String icc55;
    private String posemc;
    private String payerAcNo;
    private String cardsn;
    private String pan;
    private String track1;
    private String track2;
    private double paymentAmount;
    private String cardtype;
    private String pinkey;
    private String otrack;
    private String sMSCode;

    public String getSMSCode() {
        return sMSCode;
    }

    public WalletPaymentTypes setSMSCode(String sMSCode) {
        this.sMSCode = sMSCode;
        return this;
    }

    public JSONArray getPaymentTypes() {
        return paymentTypes;
    }

    public void setPaymentTypes(JSONArray paymentTypes) {
        this.paymentTypes = paymentTypes;
    }

    public static JSONArray getJsonArray(List<WalletPaymentTypes> types){
        if(types == null){
            return null;
        }
        JSONArray jsonArray = new JSONArray();
        for(WalletPaymentTypes type:types){
            jsonArray.put(type.getJsonObject());
        }
        return jsonArray;
    }
    public JSONObject getJsonObject(){
        JSONObject jobj = new JSONObject();
        try {
            jobj.put("paymentType",getPaymentType());
            jobj.put("random",getRandom());
            jobj.put("icc55",getIcc55());
            jobj.put("posemc",getPosemc());
            jobj.put("payerAcNo",getPayerAcNo());
            jobj.put("cardsn",getCardsn());
            jobj.put("pan",getPan());
            if (track2 != null){
                jobj.put("track2",getTrack2());
            }
            jobj.put("paymentAmount",getPaymentAmount());
            jobj.put("cardtype",getCardtype());
            jobj.put("pinkey",getPinkey());
            if (otrack != null){
                jobj.put("otrack",otrack);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jobj;
    }

    public JSONArray getJsonArray(){
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(getJsonObject());
        return jsonArray;
    }



    public String getOtrack() {
        return otrack;
    }

    public void setOtrack(String encTracks) {
        this.otrack = encTracks;
    }

    public String getTrack1() {
        return track1;
    }

    public void setTrack1(String track1) {
        this.track1 = track1;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }

    public String getIcc55() {
        return icc55;
    }

    public void setIcc55(String icc55) {
        this.icc55 = icc55;
    }

    public String getPosemc() {
        return posemc;
    }

    public void setPosemc(String posemc) {
        this.posemc = posemc;
    }

    public String getPayerAcNo() {
        return payerAcNo;
    }

    public void setPayerAcNo(String payerAcNo) {
        this.payerAcNo = payerAcNo;
    }

    public String getCardsn() {
        return cardsn;
    }

    public void setCardsn(String cardsn) {
        this.cardsn = cardsn;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getTrack2() {
        return track2;
    }

    public void setTrack2(String track2) {
        this.track2 = track2;
    }

    public double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getCardtype() {
        return cardtype;
    }

    public void setCardtype(String cardtype) {
        this.cardtype = cardtype;
    }

    public String getPinkey() {
        return pinkey;
    }

    public void setPinkey(String pinkey) {
        this.pinkey = pinkey;
    }

}
