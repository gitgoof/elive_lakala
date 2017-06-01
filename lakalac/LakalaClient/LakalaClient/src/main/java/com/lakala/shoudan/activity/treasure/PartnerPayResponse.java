package com.lakala.shoudan.activity.treasure;

import com.lakala.platform.common.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LMQ on 2015/12/24.
 */
public class PartnerPayResponse {
    private String busId;
    private String subject;
    private String outOrderNo;
    private String orderAmount;
    private String callBackURL;
    private String notifyURL;
    private String merchantId;
    private String params;
    private String billId;
    private String lakalaOrderId;
    private String orderid;
    private PaymentTypes paymentTypes;

    public void updatePayType(JSONObject type){
        if(type != null){
            PaymentTypes types = PaymentTypes.parse(type);
            setPaymentTypes(types);
        }
    }
    public static PartnerPayResponse parse(JSONObject jsonObject){
        PartnerPayResponse response = new PartnerPayResponse();
        response.setBusId(jsonObject.optString("busId",""));
        response.setSubject(jsonObject.optString("subject",""));
        response.setOutOrderNo(jsonObject.optString("outOrderNo",""));
        response.setOrderAmount(Utils.fen2Yuan(jsonObject.optString("orderAmount", "")));
        response.setCallBackURL(jsonObject.optString("callBackURL",""));
        response.setNotifyURL(jsonObject.optString("notifyURL",""));
        response.setMerchantId(jsonObject.optString("merchantId",""));
        response.setParams(jsonObject.optString("params",""));
        response.setBillId(jsonObject.optString("billId",""));
        response.setLakalaOrderId(jsonObject.optString("lakalaOrderId",""));
        response.setOrderid(jsonObject.optString("orderid",""));
        JSONObject type = jsonObject.optJSONObject("paymentTypes");
        if(type != null){
            PaymentTypes types = PaymentTypes.parse(type);
            response.setPaymentTypes(types);
        }
        return response;
    }

    public String getOrderid() {
        return orderid;
    }

    public PartnerPayResponse setOrderid(String orderid) {
        this.orderid = orderid;
        return this;
    }

    public PaymentTypes getPaymentTypes() {
        return paymentTypes;
    }

    public PartnerPayResponse setPaymentTypes(PaymentTypes paymentTypes) {
        this.paymentTypes = paymentTypes;
        return this;
    }

    public String getBusId() {
        return busId;
    }

    public PartnerPayResponse setBusId(String busId) {
        this.busId = busId;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public PartnerPayResponse setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getOutOrderNo() {
        return outOrderNo;
    }

    public PartnerPayResponse setOutOrderNo(String outOrderNo) {
        this.outOrderNo = outOrderNo;
        return this;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public PartnerPayResponse setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
        return this;
    }

    public String getCallBackURL() {
        return callBackURL;
    }

    public PartnerPayResponse setCallBackURL(String callBackURL) {
        this.callBackURL = callBackURL;
        return this;
    }

    public String getNotifyURL() {
        return notifyURL;
    }

    public PartnerPayResponse setNotifyURL(String notifyURL) {
        this.notifyURL = notifyURL;
        return this;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public PartnerPayResponse setMerchantId(String merchantId) {
        this.merchantId = merchantId;
        return this;
    }

    public String getParams() {
        return params;
    }

    public PartnerPayResponse setParams(String params) {
        this.params = params;
        return this;
    }

    public String getBillId() {
        return billId;
    }

    public PartnerPayResponse setBillId(String billId) {
        this.billId = billId;
        return this;
    }

    public String getLakalaOrderId() {
        return lakalaOrderId;
    }

    public PartnerPayResponse setLakalaOrderId(String lakalaOrderId) {
        this.lakalaOrderId = lakalaOrderId;
        return this;
    }

    public static class PaymentTypes{
        private String amount;
        private String safetyToolFlag;//1需要拉卡拉密码 0 不需要拉卡拉密码 2 未设置支付密码
        private String billAmount;
        private String billFee;
        private List<PaymentType> PaymentTypes;

        public static PaymentTypes parse(JSONObject jsonObject){
            PaymentTypes types = new PaymentTypes();
            types.setAmount(jsonObject.optString("amount",""));
            types.setSafetyToolFlag(jsonObject.optString("safetyToolFlag","2"));
            types.setBillAmount(jsonObject.optString("billAmount",""));
            types.setBillFee(jsonObject.optString("billFee",""));
            JSONArray array = jsonObject.optJSONArray("PaymentTypes");
            if(array == null){
                return types;
            }
            List<PaymentType> paymentTypes = new ArrayList<>();
            for(int i = 0;i<array.length();i++){
                JSONObject obj = array.optJSONObject(i);
                if(obj == null){
                    continue;
                }
                PaymentType type = PaymentType.parse(obj);
                paymentTypes.add(type);
            }
            types.setPaymentTypes(paymentTypes);
            return types;
        }

        public String getAmount() {
            return amount;
        }

        public PaymentTypes setAmount(String amount) {
            this.amount = amount;
            return this;
        }

        public String getSafetyToolFlag() {
            return safetyToolFlag;
        }

        public PaymentTypes setSafetyToolFlag(String safetyToolFlag) {
            this.safetyToolFlag = safetyToolFlag;
            return this;
        }

        public String getBillAmount() {
            return billAmount;
        }

        public PaymentTypes setBillAmount(String billAmount) {
            this.billAmount = billAmount;
            return this;
        }

        public String getBillFee() {
            return billFee;
        }

        public PaymentTypes setBillFee(String billFee) {
            this.billFee = billFee;
            return this;
        }

        public List<PaymentType> getPaymentTypes() {
            return PaymentTypes;
        }

        public PaymentTypes setPaymentTypes(List<PaymentType> paymentTypes) {
            PaymentTypes = paymentTypes;
            return this;
        }
    }

    public static class PaymentType{
        private String sMSFlag;
        private String paymentTypeFlag;
        private String paymentType;
        private String paymentTypeName;
        private String walletWithdrawBalance;
        private JSONArray List;
        public static PaymentType parse(JSONObject jsonObject){
            PaymentType type = new PaymentType();
            type.setSMSFlag(jsonObject.optString("sMSFlag",""));
            type.setPaymentTypeFlag(jsonObject.optString("paymentTypeFlag",""));
            type.setPaymentType(jsonObject.optString("paymentType",""));
            type.setPaymentTypeName(jsonObject.optString("paymentTypeName",""));
            type.setWalletWithdrawBalance(jsonObject.optString("walletWithdrawBalance",""));
            type.setList(jsonObject.optJSONArray("List"));
            return type;
        }

        public String getPaymentTypeName() {
            return paymentTypeName;
        }

        public PaymentType setPaymentTypeName(String paymentTypeName) {
            this.paymentTypeName = paymentTypeName;
            return this;
        }

        public String getSMSFlag() {
            return sMSFlag;
        }

        public PaymentType setSMSFlag(String sMSFlag) {
            this.sMSFlag = sMSFlag;
            return this;
        }

        public String getPaymentTypeFlag() {
            return paymentTypeFlag;
        }

        public PaymentType setPaymentTypeFlag(String paymentTypeFlag) {
            this.paymentTypeFlag = paymentTypeFlag;
            return this;
        }

        public String getPaymentType() {
            return paymentType;
        }

        public PaymentType setPaymentType(String paymentType) {
            this.paymentType = paymentType;
            return this;
        }

        public String getWalletWithdrawBalance() {
            return walletWithdrawBalance;
        }

        public PaymentType setWalletWithdrawBalance(String walletWithdrawBalance) {
            this.walletWithdrawBalance = walletWithdrawBalance;
            return this;
        }

        public JSONArray getList() {
            return List;
        }

        public PaymentType setList(JSONArray list) {
            List = list;
            return this;
        }
    }
}
