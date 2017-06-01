package com.lakala.shoudan.activity.shoudan.finance.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LMQ on 2015/10/23.
 */
public class SwipePaymentType {
    private String CardType;
    private String Cardsn;
    private String Icc55;
    private String PayerAcNo;
    private String PaymentAmount;
    private String PaymentType;
    private String Pinkey;
    private String Posemc;
    private String ProductId;
    private String Random;
    private String Track2;
    private String Otrack;
    private String Pan;
    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("CardType",getCardType());
            jsonObject.put("Cardsn",getCardsn());
            jsonObject.put("Icc55",getIcc55());
            jsonObject.put("PayerAcNo",getPayerAcNo());
            jsonObject.put("PaymentAmount",getPaymentAmount());
            jsonObject.put("PaymentType",getPaymentType());
            jsonObject.put("Pinkey",getPinkey());
            jsonObject.put("Posemc",getPosemc());
            jsonObject.put("ProductId",getProductId());
            jsonObject.put("Random",getRandom());
            jsonObject.put("Track2",getTrack2());
            jsonObject.put("Otrack",getOtrack());
            jsonObject.put("Pan",getPan());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String getPan() {
        return Pan;
    }

    public SwipePaymentType setPan(String pan) {
        Pan = pan;
        return this;
    }

    public String getOtrack() {
        return Otrack;
    }

    public SwipePaymentType setOtrack(String otrack) {
        Otrack = otrack;
        return this;
    }

    public String getCardType() {
        return CardType;
    }

    public SwipePaymentType setCardType(String cardType) {
        CardType = cardType;
        return this;
    }

    public String getCardsn() {
        return Cardsn;
    }

    public SwipePaymentType setCardsn(String cardsn) {
        Cardsn = cardsn;
        return this;
    }

    public String getIcc55() {
        return Icc55;
    }

    public SwipePaymentType setIcc55(String icc55) {
        Icc55 = icc55;
        return this;
    }

    public String getPayerAcNo() {
        return PayerAcNo;
    }

    public SwipePaymentType setPayerAcNo(String payerAcNo) {
        PayerAcNo = payerAcNo;
        return this;
    }

    public String getPaymentAmount() {
        return PaymentAmount;
    }

    public SwipePaymentType setPaymentAmount(String paymentAmount) {
        PaymentAmount = paymentAmount;
        return this;
    }

    public String getPaymentType() {
        return PaymentType;
    }

    public SwipePaymentType setPaymentType(String paymentType) {
        PaymentType = paymentType;
        return this;
    }

    public String getPinkey() {
        return Pinkey;
    }

    public SwipePaymentType setPinkey(String pinkey) {
        Pinkey = pinkey;
        return this;
    }

    public String getPosemc() {
        return Posemc;
    }

    public SwipePaymentType setPosemc(String posemc) {
        Posemc = posemc;
        return this;
    }

    public String getProductId() {
        return ProductId;
    }

    public SwipePaymentType setProductId(String productId) {
        ProductId = productId;
        return this;
    }

    public String getRandom() {
        return Random;
    }

    public SwipePaymentType setRandom(String random) {
        Random = random;
        return this;
    }

    public String getTrack2() {
        return Track2;
    }

    public SwipePaymentType setTrack2(String track2) {
        Track2 = track2;
        return this;
    }
}
