package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LMQ on 2015/10/26.
 */
public class TcRequest extends BaseRequest {
    private String BillId;
    private String busid = "TCCHK";
    private String SrcSid;
    private String Tcicc55;
    private String Tcvalue;
    private String Scpicc55;
    private String macRnd;
    private String tcAsyFlag;

    public String getTcAsyFlag() {
        return tcAsyFlag;
    }

    public TcRequest setTcAsyFlag(String tcAsyFlag) {
        this.tcAsyFlag = tcAsyFlag;
        return this;
    }

    @Override
    public String getMacRnd() {
        return macRnd;
    }

    public TcRequest setMacRnd(String macRnd) {
        this.macRnd = macRnd;
        return this;
    }

    public String getScpicc55() {
        return Scpicc55;
    }

    public TcRequest setScpicc55(String scpicc55) {
        Scpicc55 = scpicc55;
        return this;
    }

    public String getBillId() {
        return BillId;
    }

    public TcRequest setBillId(String billId) {
        BillId = billId;
        return this;
    }

    public String getBusid() {
        return busid;
    }

    public TcRequest setBusid(String busid) {
        this.busid = busid;
        return this;
    }

    public String getSrcSid() {
        return SrcSid;
    }

    public TcRequest setSrcSid(String srcSid) {
        SrcSid = srcSid;
        return this;
    }

    public String getTcicc55() {
        return Tcicc55;
    }

    public TcRequest setTcicc55(String tcicc55) {
        Tcicc55 = tcicc55;
        return this;
    }

    public String getTcvalue() {
        return Tcvalue;
    }

    public TcRequest setTcvalue(String tcvalue) {
        Tcvalue = tcvalue;
        return this;
    }

    public static TcRequest parse(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            TcRequest tcRequest = new TcRequest();
            tcRequest.setBillId(jsonObject.optString("BillId",""));
            tcRequest.setBusid(jsonObject.optString("busid", ""));
            tcRequest.setSrcSid(jsonObject.optString("SrcSid", ""));
            tcRequest.setTcicc55(jsonObject.optString("Tcicc55", ""));
            tcRequest.setTcvalue(jsonObject.optString("Tcvalue", ""));
            tcRequest.setScpicc55(jsonObject.optString("Scpicc55",""));
            tcRequest.setTelecode(jsonObject.optString("telecode",""));
            return tcRequest;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String getChntype() {
        return "02101";
    }
}
