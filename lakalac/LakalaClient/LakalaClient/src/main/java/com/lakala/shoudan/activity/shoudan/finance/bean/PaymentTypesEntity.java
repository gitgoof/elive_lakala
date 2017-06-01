package com.lakala.shoudan.activity.shoudan.finance.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fengx on 2015/10/19.
 */
public class PaymentTypesEntity {

    private String PaymentType;
    private String PaymentAmount;
    private long CardId;
    private String AccountNo;
    private int AccountType;
    private String ProductId;
    private String Random;

    @Override
    public String toString() {
        return getJSONObject().toString();
    }
    public JSONObject getJSONObject(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("PaymentType",PaymentType);
            jsonObject.put("PaymentAmount",PaymentAmount);
            jsonObject.put("CardId",CardId);
            jsonObject.put("AccountNo",AccountNo);
            jsonObject.put("AccountType",AccountType);
            jsonObject.put("ProductId",ProductId);
            jsonObject.put("Random",Random);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public long getCardId() {
        return CardId;
    }

    public PaymentTypesEntity setCardId(long cardId) {
        CardId = cardId;
        return this;
    }

    public String getAccountNo() {
        return AccountNo;
    }

    public PaymentTypesEntity setAccountNo(String accountNo) {
        AccountNo = accountNo;
        return this;
    }

    public int getAccountType() {
        return AccountType;
    }

    public PaymentTypesEntity setAccountType(int accountType) {
        AccountType = accountType;
        return this;
    }

    public String getProductId() {
        return ProductId;
    }

    public PaymentTypesEntity setProductId(String productId) {
        ProductId = productId;
        return this;
    }

    public String getRandom() {
        return Random;
    }

    public PaymentTypesEntity setRandom(String random) {
        Random = random;
        return this;
    }

    public void setPaymentType(String PaymentType) {
        this.PaymentType = PaymentType;
    }


    public String getPaymentType() {
        return PaymentType;
    }

    public String getPaymentAmount() {
        return PaymentAmount;
    }

    public PaymentTypesEntity setPaymentAmount(String paymentAmount) {
        PaymentAmount = paymentAmount;
        return this;
    }
}
