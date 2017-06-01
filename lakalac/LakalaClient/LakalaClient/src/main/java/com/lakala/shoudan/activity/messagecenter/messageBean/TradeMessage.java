package com.lakala.shoudan.activity.messagecenter.messageBean;

import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NotNull;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Unique;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by huwei on 16/9/2.
 */
@Table(name = "TradeMessage")
public class TradeMessage implements Serializable {
    @Id(column = "id")
    @NotNull
    @Unique
    String id;
    @Column(column = "readed")
    private boolean readed;
    @Column(column = "msgTime")
    private String msgTime;
    @Column(column = "msgType")
    private String msgType;
    @Column(column = "contentType")
    private String contentType;
    //extrainfo
    @Column(column = "typeName")
    private String typeName;
    @Column(column = "status")
    private String status;
    @Column(column = "desc")
    private String desc;
    @Column(column = "titleText")
    private String titleText;
    @Column(column = "mobileNum")
    private String mobileNum;
    @Column(column = "amount")
    private String amount;
    @Column(column = "payCardNum")
    private String payCardNum;
    @Column(column = "receiveCardNum")
    private String receiveCardNum;
    @Column(column = "tradeTime")
    private String tradeTime;
    @Column(column = "sid")
    private String sid;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPayCardNum() {
        return payCardNum;
    }

    public void setPayCardNum(String payCardNum) {
        this.payCardNum = payCardNum;
    }

    public String getReceiveCardNum() {
        return receiveCardNum;
    }

    public void setReceiveCardNum(String receiveCardNum) {
        this.receiveCardNum = receiveCardNum;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public static TradeMessage parseObject(JSONObject jsonObject) throws JSONException {
        JSONObject metaData, titleRegionData, mainContentRegionData, functionRegionData, contentText;
        TradeMessage message = new TradeMessage();
        message.setId(jsonObject.optString("id", ""));
        CommonServiceManager.getInstance().setMessageReaded(message.getId());
        message.setReaded(jsonObject.optBoolean("readed", false));
        message.setMsgTime(jsonObject.optString("msgTime", ""));
        message.setMsgType(jsonObject.optString("msgType"));
//        message.setVersion(jsonObject.optString("version"));
        message.setContentType(jsonObject.optString("contentType"));
        //ExtraInfo
        JSONObject exeInfo = jsonObject.getJSONObject("extInfo");
        if (exeInfo != null) {
            //消息说明
            metaData = exeInfo.getJSONObject("metaData");
            message.setTypeName(metaData.optString("typeName"));
            message.setStatus(metaData.optString("status"));
            message.setDesc(metaData.optString("desc"));
            //消息标题
            titleRegionData = exeInfo.getJSONObject("titleRegionData");
            message.setTitleText(titleRegionData.optString("titleText"));
            //消息内容
            mainContentRegionData = exeInfo.getJSONObject("mainContentRegionData");
            contentText = mainContentRegionData.getJSONObject("contentText");
            message.setMobileNum(contentText.optString("mobileNum"));
            message.setAmount(contentText.optString("amount"));
            message.setPayCardNum(contentText.optString("payCardNum"));
            message.setReceiveCardNum(contentText.optString("receiveCardNum"));
            message.setTradeTime(contentText.optString("tradeTime"));
            //消息功能区
            try {
                functionRegionData = exeInfo.getJSONObject("functionRegionData");
                if (functionRegionData != null) {
                    message.setSid(functionRegionData.optString("sid"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return message;
    }
}
