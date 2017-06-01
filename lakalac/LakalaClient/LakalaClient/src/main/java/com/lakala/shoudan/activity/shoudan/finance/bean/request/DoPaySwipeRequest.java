package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.net.volley.BaseRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.SwipePaymentType;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by LMQ on 2015/10/23.
 */
public class DoPaySwipeRequest extends BaseRequest {

    private String busid = "1G2";
    private String BillId;
    private String TrsPassword;
    private String LastPaymentType;
    private String ProductId;
    private JSONArray PaymentTypes;
    private String pan;
    private String inpan;
    private String amount;
    private String Period;
    private String fee = "0";
    private String _TerminalId;
    private int OutMode;

    public int getOutMode() {
        return OutMode;
    }

    public DoPaySwipeRequest setOutMode(int outMode) {
        OutMode = outMode;
        return this;
    }

    public String get_TerminalId() {
        return _TerminalId;
    }

    public DoPaySwipeRequest set_TerminalId(String _TerminalId) {
        this._TerminalId = _TerminalId;
        return this;
    }

    public String getBusid() {
        return busid;
    }

    public DoPaySwipeRequest setBusid(String busid) {
        this.busid = busid;
        return this;
    }

    public String getBillId() {
        return BillId;
    }

    public DoPaySwipeRequest setBillId(String billId) {
        BillId = billId;
        return this;
    }

    public String getTrsPassword() {
        return TrsPassword;
    }

    public DoPaySwipeRequest setTrsPassword(String trsPassword) {
        TrsPassword = trsPassword;
        return this;
    }

    public String getLastPaymentType() {
        return LastPaymentType;
    }

    public DoPaySwipeRequest setLastPaymentType(String lastPaymentType) {
        LastPaymentType = lastPaymentType;
        return this;
    }

    public String getProductId() {
        return ProductId;
    }

    public DoPaySwipeRequest setProductId(String productId) {
        ProductId = productId;
        return this;
    }

    public JSONArray getPaymentTypes() {
        return PaymentTypes;
    }

    public DoPaySwipeRequest setPaymentTypes(List<SwipePaymentType> paymentTypes) {
        JSONArray jsonArray = new JSONArray();
        for(SwipePaymentType type:paymentTypes){
            jsonArray.put(type.getJSONObject());
        }
        this.PaymentTypes = jsonArray;
        return this;
    }

    public String getPan() {
        return pan;
    }

    public DoPaySwipeRequest setPan(String pan) {
        this.pan = pan;
        return this;
    }

    public String getInpan() {
        return inpan;
    }

    public DoPaySwipeRequest setInpan(String inpan) {
        this.inpan = inpan;
        return this;
    }

    public String getAmount() {
        return amount;
    }

    public DoPaySwipeRequest setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    public String getPeriod() {
        return Period;
    }

    public DoPaySwipeRequest setPeriod(String period) {
        Period = period;
        return this;
    }

    public String getFee() {
        return fee;
    }

    public DoPaySwipeRequest setFee(String fee) {
        this.fee = fee;
        return this;
    }

    @Override
    protected String getChntype() {
        return "02101";
    }

    @Override
    protected String getTermId() {
        return  ApplicationEx.getInstance().getSession().getCurrentKSN();
    }
}
