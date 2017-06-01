package com.lakala.shoudan.activity.revocation;

import com.lakala.platform.bean.TradeStatus;
import com.lakala.shoudan.datadefine.RecordDetail;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by More on 15/3/15.
 */
public class RevocationRecord implements Serializable{

//            "sid": "9F00738D7531480CB77018C4E75290DE",
//            "tradeDate": "20150324133014",
//            "amount": 33,
//            "merNo": "310000000015401",
//            "shopNo": "822100050210001",
//            "sysRef": "455331799896",
//            "authCode": null,
//            "batchNo": null,
//            "payCardNo": "6216911500911792",
//            "isSign": null,
//            "terminalNo": null,
//            "psamNo": "CBC4A4BF00000116",
//            "voucherNo": null,
//            "acq": null,
//            "merName": "asdfgj",
//            "tradeType": "POS",
//            "fee": 0,
//            "listAmount": 33,
//            "busid": "18X",
//            "busName": "手机收单消费",
//            "payCardBankCode": "03050000",
//            "payCardBankName": "民生银行(03050000)",
//            "payCardAccountType": "D",
//            "payCardName": "借记卡普卡",
//            "series": "622024",
//            "srcsid": null,
//            "cancelTradeInfo": [],
//            "billNo": null,
//            "canceled": false,
//            "status": "00"

    private String payCardBankName;//银行卡发卡行

    private String payCardName;//卡类型,借记卡 or 贷记卡

    private String payCardAccountType;

    private String srcSysref;

    private String srcsid;

    private String srcseries;

    private String payMobileNo;//支付人手机号

    private String srcamount;//单位元

    private String payCardNo;//支付卡号

    private String lpmercd;

    private String tradeDate;

    private TradeStatus status;//交易状态

    private boolean canceled;//是否被撤销了

    public RevocationRecord() {
    }

    public RevocationRecord(RecordDetail recordDetail) {

//        payCardBankName = recordDetail.getPaymentAccount();
//        payCardName = recordD
        srcSysref = recordDetail.getSysSeq();
        srcsid = recordDetail.getSid();
        srcseries = recordDetail.getSeries();
        payMobileNo = recordDetail.getPaymentMobile();
        srcamount = recordDetail.getDealAmount();
        payCardNo = recordDetail.getPaymentAccount();
        lpmercd = recordDetail.getMerChantCode();
        tradeDate = recordDetail.getDealDateTime();
        status = TradeStatus.fromString(recordDetail.getStatus());
        canceled = false;
//        payCardAccountType = recordDetail.getP

    }


    public RevocationRecord(JSONObject jsonObject) {
        if(jsonObject == null){
            return;
        }

        payCardBankName = jsonObject.optString("payCardBankName");
        payCardName = jsonObject.optString("payCardName");
        srcSysref = jsonObject.optString("sysRef");
        srcsid = jsonObject.optString("sid");
        srcseries = jsonObject.optString("series");
        payMobileNo = jsonObject.optString("payMobileNum");
        srcamount = jsonObject.optString("amount");
        payCardNo = jsonObject.optString("payCardNo");
        lpmercd = jsonObject.optString("merNo");
        tradeDate = jsonObject.optString("tradeDate");
        status = TradeStatus.fromString(jsonObject.optString("status"));
        canceled = jsonObject.optBoolean("canceled");
        payCardAccountType = jsonObject.optString("payCardAccountType");
    }

    public String getPayCardAccountType() {
        return payCardAccountType;
    }

    public void setPayCardAccountType(String payCardAccountType) {
        this.payCardAccountType = payCardAccountType;
    }

    public String getPayCardBankName() {
        return payCardBankName;
    }

    public void setPayCardBankName(String payCardBankName) {
        this.payCardBankName = payCardBankName;
    }

    public String getPayCardName() {
        return payCardName;
    }

    public void setPayCardName(String payCardName) {
        this.payCardName = payCardName;
    }

    public String getSrcSysref() {
        return srcSysref;
    }

    public void setSrcSysref(String srcSysref) {
        this.srcSysref = srcSysref;
    }

    public String getPayMobileNo() {
        return payMobileNo;
    }

    public void setPayMobileNo(String payMobileNo) {
        this.payMobileNo = payMobileNo;
    }

    public String getPayCardNo() {
        return payCardNo;
    }

    public void setPayCardNo(String payCardNo) {
        this.payCardNo = payCardNo;
    }

    public String getLpmercd() {
        return lpmercd;
    }

    public void setLpmercd(String lpmercd) {
        this.lpmercd = lpmercd;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getSrcsid() {
        return srcsid;
    }

    public void setSrcsid(String srcsid) {
        this.srcsid = srcsid;
    }

    public String getSrcseries() {
        return srcseries;
    }

    public void setSrcseries(String srcseries) {
        this.srcseries = srcseries;
    }


    public String getSrcamount() {
        return srcamount;
    }

    public void setSrcamount(String srcamount) {
        this.srcamount = srcamount;
    }

    public TradeStatus getStatus() {
        return status;
    }

    public void setStatus(TradeStatus status) {
        this.status = status;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
