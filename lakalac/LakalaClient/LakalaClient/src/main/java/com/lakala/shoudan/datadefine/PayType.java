package com.lakala.shoudan.datadefine;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LMQ on 2015/11/19.
 */
public class PayType {
    public String PaymentTypeName;
    public String PaymentType;
    public String PaymentTypeFlag;
    public ShortCard shortCard;

    public String getShowText() {
        String showText = this.PaymentTypeName;
        if("D".equals(this.PaymentType) || "S".equals(this.PaymentType)){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.shortCard.getBankName());
            stringBuilder.append(" ");
            String accountTypeStr = "";
            if("1".equals(this.shortCard.getAccountType())){
                accountTypeStr = "储蓄卡";
            }else if("2".equals(this.shortCard.getAccountType())){
                accountTypeStr = "信用卡";
            }
            stringBuilder.append(accountTypeStr).append("(")
                         .append(this.shortCard.getCardTailFour()).append(")");
            showText = stringBuilder.toString();
        }
        return showText;
    }
    public static class ShortCard{
        private String cardId;
        private String mobileInBank;
        private String accountType;//1借记卡 2 信用卡
        private String bankName;
        private String accountNo;
        private String cardTailFour;


        public static ShortCard parseFinance(JSONObject jsonObject){
            ShortCard shortCard = new ShortCard();
            shortCard.setBankName(jsonObject.optString("BankName",""));
            shortCard.setAccountType(jsonObject.optString("AccountType","0"));
            shortCard.setCardId(jsonObject.optString("CardId","0"));
            shortCard.setCardTailFour(jsonObject.optString("CardTailFour","0"));
            shortCard.setAccountNo(jsonObject.optString("AccountNo","0"));
            shortCard.setMobileInBank(jsonObject.optString("MobileInBank",""));
            return shortCard;
        }
        public static ShortCard parseTreasure(JSONObject jsonObject){
            ShortCard shortCard = new ShortCard();
            shortCard.setBankName(jsonObject.optString("bankName",""));
            shortCard.setAccountType(jsonObject.optString("accountType","0"));
            shortCard.setCardId(jsonObject.optString("cardId","0"));
            shortCard.setCardTailFour(jsonObject.optString("cardTailFour","0"));
            shortCard.setAccountNo(jsonObject.optString("accountNo","0"));
            shortCard.setMobileInBank(jsonObject.optString("mobileInBank",""));
            return shortCard;
        }
        public JSONObject getTreasureJSONObject(){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("bankName",getBankName());
                jsonObject.put("accountType",getAccountType());
                jsonObject.put("cardId",getCardId());
                jsonObject.put("mobileInBank",getMobileInBank());
                jsonObject.put("accountNo",getAccountNo());
                jsonObject.put("cardTailFour",getCardTailFour());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        public String getCardId() {
            return cardId;
        }

        public ShortCard setCardId(String cardId) {
            this.cardId = cardId;
            return this;
        }

        public String getMobileInBank() {
            return mobileInBank;
        }

        public ShortCard setMobileInBank(String mobileInBank) {
            this.mobileInBank = mobileInBank;
            return this;
        }

        public String getAccountType() {
            return accountType;
        }

        public ShortCard setAccountType(String accountType) {
            this.accountType = accountType;
            return this;
        }

        public String getBankName() {
            return bankName;
        }

        public ShortCard setBankName(String bankName) {
            this.bankName = bankName;
            return this;
        }

        public String getAccountNo() {
            return accountNo;
        }

        public ShortCard setAccountNo(String accountNo) {
            this.accountNo = accountNo;
            return this;
        }

        public String getCardTailFour() {
            return cardTailFour;
        }

        public ShortCard setCardTailFour(String cardTailFour) {
            this.cardTailFour = cardTailFour;
            return this;
        }
    }
}
