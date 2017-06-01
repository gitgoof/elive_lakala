package com.lakala.shoudan.datadefine;

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
 * Created by huwei on 2017/3/1.
 */
@Table(name = "AdBottomMessage")
public class AdBottomMessage implements Serializable {

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
    @Column(column = "version")
    private String version;
    @Column(column = "titleText")
    private String titleText;
    @Column(column = "titleImagURL")
    private String titleImagURL;
    @Column(column = "titleIconImagURL")
    private String titleIconImagURL;
    @Column(column = "contentText")
    private String contentText;
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
    @Column(column = "experImmediateflag")
    private String experImmediateflag;
    @Column(column = "busiList_id")
    private String busiList_id;
    @Column(column = "busiList_version")
    private String busiList_version;

    public String getTitleIconImagURL() {
        return titleIconImagURL;
    }

    public void setTitleIconImagURL(String titleIconImagURL) {
        this.titleIconImagURL = titleIconImagURL;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getTitleImagURL() {
        return titleImagURL;
    }

    public void setTitleImagURL(String titleImagURL) {
        this.titleImagURL = titleImagURL;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
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

    public String getExperImmediateflag() {
        return experImmediateflag;
    }

    public void setExperImmediateflag(String experImmediateflag) {
        this.experImmediateflag = experImmediateflag;
    }

    public String getBusiList_id() {
        return busiList_id;
    }

    public void setBusiList_id(String busiList_id) {
        this.busiList_id = busiList_id;
    }

    public String getBusiList_version() {
        return busiList_version;
    }

    public void setBusiList_version(String busiList_version) {
        this.busiList_version = busiList_version;
    }

    public static AdBottomMessage parseObject(JSONObject jsonObject) throws JSONException {
        JSONObject titleRegionData, mainContentRegionData, functionRegionData;
        AdBottomMessage message = new AdBottomMessage();
        message.setId(jsonObject.optString("id", ""));
        CommonServiceManager.getInstance().setMessageReaded(message.getId());
        message.setReaded(jsonObject.optBoolean("readed", false));
        message.setMsgTime(jsonObject.optString("msgTime", ""));
        message.setMsgType(jsonObject.optString("msgType"));
//        message.setVersion(jsonObject.optString("version"));
        message.setContentType(jsonObject.optString("contentType"));
        //ExtraInfo
        JSONObject exeInfo = jsonObject.getJSONObject("extInfo");
        //消息标题
        if (exeInfo != null) {
            titleRegionData = exeInfo.getJSONObject("titleRegionData");
            message.setTitleText(titleRegionData.optString("titleText"));
            message.setTitleImagURL(titleRegionData.optString("titleImagURL"));
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
            message.setExperImmediateflag(functionRegionData.optString("experImmediateflag"));
            message.setBusiList_id(functionRegionData.optString("busList_id"));
            message.setBusiList_version(functionRegionData.optString("busList_version"));
        }
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdBottomMessage message = (AdBottomMessage) o;

        if (readed != message.readed) return false;
        if (id != null ? !id.equals(message.id) : message.id != null) return false;
        if (msgTime != null ? !msgTime.equals(message.msgTime) : message.msgTime != null)
            return false;
        if (msgType != null ? !msgType.equals(message.msgType) : message.msgType != null)
            return false;
        if (contentType != null ? !contentType.equals(message.contentType) : message.contentType != null)
            return false;
        if (version != null ? !version.equals(message.version) : message.version != null)
            return false;
        if (titleText != null ? !titleText.equals(message.titleText) : message.titleText != null)
            return false;
        if (titleImagURL != null ? !titleImagURL.equals(message.titleImagURL) : message.titleImagURL != null)
            return false;
        if (titleIconImagURL != null ? !titleIconImagURL.equals(message.titleIconImagURL) : message.titleIconImagURL != null)
            return false;
        if (contentText != null ? !contentText.equals(message.contentText) : message.contentText != null)
            return false;
        if (contentImageURL != null ? !contentImageURL.equals(message.contentImageURL) : message.contentImageURL != null)
            return false;
        if (contentImageTitle != null ? !contentImageTitle.equals(message.contentImageTitle) : message.contentImageTitle != null)
            return false;
        if (contentClickURL != null ? !contentClickURL.equals(message.contentClickURL) : message.contentClickURL != null)
            return false;
        if (functionText != null ? !functionText.equals(message.functionText) : message.functionText != null)
            return false;
        if (versionUpdateFlag != null ? !versionUpdateFlag.equals(message.versionUpdateFlag) : message.versionUpdateFlag != null)
            return false;
        if (serviceActionURL != null ? !serviceActionURL.equals(message.serviceActionURL) : message.serviceActionURL != null)
            return false;
        if (detailsClickURL != null ? !detailsClickURL.equals(message.detailsClickURL) : message.detailsClickURL != null)
            return false;
        if (detailsLabVal != null ? !detailsLabVal.equals(message.detailsLabVal) : message.detailsLabVal != null)
            return false;
        if (experImmediateflag != null ? !experImmediateflag.equals(message.experImmediateflag) : message.experImmediateflag != null)
            return false;
        if (busiList_id != null ? !busiList_id.equals(message.busiList_id) : message.busiList_id != null)
            return false;
        return busiList_version != null ? busiList_version.equals(message.busiList_version) : message.busiList_version == null;

    }
}
