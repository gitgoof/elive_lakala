package com.lakala.shoudan.activity.messagecenter.messageBean;

import android.text.TextUtils;

import com.lakala.shoudan.activity.shoudan.Constants;
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
 * Created by huwei on 16/9/5.
 */
@Table(name = "BusinessMessage")
public class BusinessMessage implements Serializable {
    @Id(column = "id")
    @NotNull
    @Unique
    private String id;
    @Column(column = "readed")
    private boolean readed;
    @Column(column = "msgTime")
    private String msgTime;
    @Column(column = "msgType")
    private String msgType;
    @Column(column = "contentType")
    private String contentType;
    //extraInfo
    @Column(column = "typeName")
    private String typeName;
    @Column(column = "status")
    private String status;
    @Column(column = "desc")
    private String desc;
    @Column(column = "titleText")
    private String titleText;
    @Column(column = "contentText")
    private String contentText;
    //extInfo_bp
    @Column(column = "version")
    private String version;
    @Column(column = "titleImagURL")
    private String titleImagURL;
    @Column(column = "contentImageURL")
    private String contentImageURL;
    @Column(column = "contentImageTitle")
    private String contentImageTitle;
    @Column(column = "contentClickURL")
    private String contentClickURL;
    @Column(column = "functionText")
    private String functionText;
    @Column(column = "versionUpdateFlag")
    private String versionUpdateFlag;
    @Column(column = "serviceActionURL")
    private String serviceActionURL;
    @Column(column = "detailsClickURL")
    private String detailsClickURL;
    @Column(column = "detailsLabVal")
    private String detailsLabVal;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitleImagURL() {
        return titleImagURL;
    }

    public void setTitleImagURL(String titleImagURL) {
        this.titleImagURL = titleImagURL;
    }

    public String getContentImageURL() {
        return contentImageURL;
    }

    public void setContentImageURL(String contentImageURL) {
        this.contentImageURL = contentImageURL;
    }

    public String getContentImageTitle() {
        return contentImageTitle;
    }

    public void setContentImageTitle(String contentImageTitle) {
        this.contentImageTitle = contentImageTitle;
    }

    public String getContentClickURL() {
        return contentClickURL;
    }

    public void setContentClickURL(String contentClickURL) {
        this.contentClickURL = contentClickURL;
    }

    public String getFunctionText() {
        return functionText;
    }

    public void setFunctionText(String functionText) {
        this.functionText = functionText;
    }

    public String getVersionUpdateFlag() {
        return versionUpdateFlag;
    }

    public void setVersionUpdateFlag(String versionUpdateFlag) {
        this.versionUpdateFlag = versionUpdateFlag;
    }

    public String getServiceActionURL() {
        return serviceActionURL;
    }

    public void setServiceActionURL(String serviceActionURL) {
        this.serviceActionURL = serviceActionURL;
    }

    public String getDetailsClickURL() {
        return detailsClickURL;
    }

    public void setDetailsClickURL(String detailsClickURL) {
        this.detailsClickURL = detailsClickURL;
    }

    public String getDetailsLabVal() {
        return detailsLabVal;
    }

    public void setDetailsLabVal(String detailsLabVal) {
        this.detailsLabVal = detailsLabVal;
    }

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

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public static BusinessMessage parseObject(JSONObject jsonObject) throws JSONException {
        JSONObject metaData, titleRegionData, mainContentRegionData, functionRegionData, contentText;
        String typeName = "";
        BusinessMessage message = new BusinessMessage();
        message.setId(jsonObject.optString("id", ""));
        CommonServiceManager.getInstance().setMessageReaded(message.getId());
        message.setReaded(jsonObject.optBoolean("readed", false));
        message.setMsgTime(jsonObject.optString("msgTime", ""));
        message.setMsgType(jsonObject.optString("msgType"));
        message.setContentType(jsonObject.optString("contentType"));
        //ExtraInfo
        JSONObject exeInfo = jsonObject.getJSONObject("extInfo");
        if (exeInfo != null) {
            metaData = exeInfo.getJSONObject("metaData");
            typeName = metaData.optString("typeName");
            if (TextUtils.equals(typeName, Constants.BUSINESS_PUBLISH)) {
                titleRegionData = exeInfo.getJSONObject("titleRegionData");
                message.setTitleText(titleRegionData.optString("titleText"));
                message.setTitleImagURL(titleRegionData.optString("titleImagURL"));
                message.setTypeName(typeName);
                //消息内容
                mainContentRegionData = exeInfo.getJSONObject("mainContentRegionData");
                message.setContentText(mainContentRegionData.optString("contentText"));
                message.setContentImageURL(mainContentRegionData.optString("contentImageURL"));
                message.setContentImageTitle(mainContentRegionData.optString("contentImageTitle"));
                message.setContentClickURL(mainContentRegionData.optString("contentClickURL"));
                //消息功能区
                functionRegionData = exeInfo.getJSONObject("functionRegionData");
                message.setVersion(functionRegionData.optString("version"));
                message.setFunctionText(functionRegionData.optString("functionText"));
                message.setVersionUpdateFlag(functionRegionData.optString("versionUpdateFlag"));
                message.setServiceActionURL(functionRegionData.optString("serviceActionURL"));
                message.setDetailsClickURL(functionRegionData.optString("detailsClickURL"));
                message.setDetailsLabVal(functionRegionData.optString("detailsLabVal"));
            } else {
                message.setTypeName(typeName);
                message.setStatus(metaData.optString("status"));
                message.setDesc(metaData.optString("desc"));
                //消息标题
                titleRegionData = exeInfo.getJSONObject("titleRegionData");
                message.setTitleText(titleRegionData.optString("titleText"));
                //消息内容
                mainContentRegionData = exeInfo.getJSONObject("mainContentRegionData");
                message.setContentText(mainContentRegionData.optString("contentText"));
            }
        }
        return message;
    }
}
