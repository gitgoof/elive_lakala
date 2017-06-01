package com.lakala.shoudan.activity.shoudan.webmall;

import android.net.Uri;

import com.lakala.shoudan.activity.payment.base.BaseTransInfo;
import com.lakala.shoudan.common.util.Util;

import org.json.JSONObject;


/**
 * Created by More on 14-7-25.
 */
public abstract class WebCallTransInfo extends BaseTransInfo{

    private String amount;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    protected String phoneNumber;

    protected String orderId;
    protected String lakalaOrderId;

    protected String merNo;

    public String getMerNo() {
        return merNo;
    }

    public void setMerNo(String merNo) {
        this.merNo = merNo;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    protected final String PARAMS = "p";

    protected String usrToken;

    protected String timeStamp;

    protected String remark;

    protected String merId;

    protected String ver;

    protected String expriredTime;

    protected String minCode;

    protected String productDesc;

    protected String callbackUrl;

    protected String randNum;

    protected String billNo;//订单号

    protected String productName;

    protected String userId;

    protected String fBillNo;//查询后的订单号

    protected String cnSName;//商户名称

    protected String param;

    protected String cryptType;

    protected String sign;

    protected String channelNo;

    protected String price;//查询后的金额

    protected String querySid;

    protected String billType;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuerySid() {
        return querySid;
    }

    public void setQuerySid(String querySid) {
        this.querySid = querySid;
    }

    public String getParam() {
        return param;
    }

    public String getCryptType() {
        return cryptType;
    }

    public String getSign() {
        return sign;
    }

    public String getChannelNo() {
        return channelNo;
    }

    public String getUsrToken() {
        return usrToken;
    }

    public void setUsrToken(String usrToken) {
        this.usrToken = usrToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getfBillNo() {
        return fBillNo;
    }

    public void setfBillNo(String fBillNo) {
        this.fBillNo = fBillNo;
    }

    public String getCnSName() {
        return cnSName;
    }

    public void setCnSName(String cnSName) {
        this.cnSName = cnSName;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public void setCryptType(String cryptType) {
        this.cryptType = cryptType;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        this.merId = merId;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getExpriredTime() {
        return expriredTime;
    }

    public void setExpriredTime(String expriredTime) {
        this.expriredTime = expriredTime;
    }

    public String getMinCode() {
        return minCode;
    }

    public void setMinCode(String minCode) {
        this.minCode = minCode;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getRandNum() {
        return randNum;
    }

    public void setRandNum(String randNum) {
        this.randNum = randNum;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getLakalaOrderId() {
        return lakalaOrderId;
    }

    public void setLakalaOrderId(String lakalaOrderId) {
        this.lakalaOrderId = lakalaOrderId;
    }

    public void unpackCallUri(Uri uri){

        this.param = uri.getQueryParameter(PARAMS);

    }


    public void unpackValidateResult(JSONObject jb){
        usrToken = jb.optString("userToken");
        userId = jb.optString("userID");
        fBillNo = jb.optString("f_billno");
        cnSName = jb.optString("cnSName");
        billType = jb.optString("billType");
        timeStamp = jb.optString("timeStamp");
        remark = jb.optString("remark");
        merId = jb.optString("merId");
        ver = jb.optString("ver");
        cnSName = jb.optString("cnSName");
        cryptType = jb.optString("crypType");
        expriredTime = jb.optString("expriredtime");
        minCode = jb.optString("minCode");
        setAmount(Util.formatBigDecimalAmount(jb.optString("amount")));
        sign = jb.optString("sign");
        fBillNo = jb.optString("f_billno");
        productDesc = jb.optString("productDesc","");
        callbackUrl = jb.optString("callbackUrl");
        billNo = jb.optString("billNo");
        productName = jb.optString("productName","");
        channelNo = jb.optString("channelCode", "");
        this.phoneNumber = jb.optString("phoneNumber");
        this.orderId = jb.optString("orderId");
        this.merNo = jb.optString("merNo");
        this.lakalaOrderId = jb.optString("lakalaOrderId");
    }



}
