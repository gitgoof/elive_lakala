package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.PaymentTypesEntity;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by fengx on 2015/10/19.
 */
public class DoPayRequest extends BaseRequest{

    private String busid;
    private String BillId;
    private String TrsPassword;
    private String LastPaymentType;
    private String ProductId;
    private JSONArray PaymentTypes;
    private String pan;
    private String inpan;
    private String amount;
    private String Period;
    private int fee = 0;
    private int OutMode;

    public int getOutMode() {
        return OutMode;
    }

    public DoPayRequest setOutMode(int outMode) {
        OutMode = outMode;
        return this;
    }

    public String getPeriod() {
        return Period;
    }

    public DoPayRequest setPeriod(String period) {
        Period = period;
        return this;
    }

    public int getFee() {
        return fee;
    }

    public DoPayRequest setFee(int fee) {
        this.fee = fee;
        return this;
    }

    public JSONArray getPaymentTypes() {
        return PaymentTypes;
    }

    public DoPayRequest setPaymentTypes(List<PaymentTypesEntity> paymentTypes) {
        JSONArray jsonArray = new JSONArray();
        for(PaymentTypesEntity type:paymentTypes){
            jsonArray.put(type.getJSONObject());
        }
        this.PaymentTypes = jsonArray;
        return this;
    }

    public String getTrsPassword() {
        return TrsPassword;
    }

    public DoPayRequest setTrsPassword(String trsPassword) {
        TrsPassword = trsPassword;
        return this;
    }

    public String getBusid() {
        return busid;
    }

    public void setBusid(BusIdType type) {
        busid = type.getBusId();
    }

    public String getBillId() {
        return BillId;
    }

    public void setBillId(String billId) {
        BillId = billId;
    }

    public String getLastPaymentType() {
        return LastPaymentType;
    }

    public void setLastPaymentType(String lastPaymentType) {
        LastPaymentType = lastPaymentType;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getInpan() {
        return inpan;
    }

    public void setInpan(String inpan) {
        this.inpan = inpan;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public enum BusIdType{
        申购("1G2"),赎回("1G3");
        String BusId;

        BusIdType(String busId) {
            BusId = busId;
        }

        public String getBusId() {
            return BusId;
        }
    }

}
