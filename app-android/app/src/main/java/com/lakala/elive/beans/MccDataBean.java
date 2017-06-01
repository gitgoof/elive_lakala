package com.lakala.elive.beans;

import java.io.Serializable;
import java.util.List;

/**
 * MCC数据存储
 * Created by gaofeng on 2017/4/18.
 * "message":"执行成功!","resultCode":"SUCCESS",
 * {"mccCode":"5","mccLevel":"0","mccName":"县乡优惠","parentMcc":"-1","status":"VALID"
 *{"mccCode":"5993","mccLevel":"2","mccName":"香烟 , 雪茄专卖店","parentMcc":"404","status":"VALID"}
 * {"mccCode":"101","mccLevel":"1","mccName":"宾馆类","parentMcc":"1","status":"VALID"}
 */

public class MccDataBean implements Serializable {
    private String message;
    private String resultCode;
    private ContentBean content;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public static class ContentBean{
        private List<MccInfoItem> bigMccList;
        private List<MccInfoItem> mccInfoList;
        private List<MccInfoItem> smallMccList;

        public List<MccInfoItem> getBigMccList() {
            return bigMccList;
        }

        public void setBigMccList(List<MccInfoItem> bigMccList) {
            this.bigMccList = bigMccList;
        }

        public List<MccInfoItem> getMccInfoList() {
            return mccInfoList;
        }

        public void setMccInfoList(List<MccInfoItem> mccInfoList) {
            this.mccInfoList = mccInfoList;
        }

        public List<MccInfoItem> getSmallMccList() {
            return smallMccList;
        }

        public void setSmallMccList(List<MccInfoItem> smallMccList) {
            this.smallMccList = smallMccList;
        }
    }
    // {"mccCode":"5993","mccLevel":"2","mccName":"香烟 , 雪茄专卖店","parentMcc":"404","status":"VALID"}
    /**
     * mcc详情
     */
    public static class MccInfoItem{
        private String mccCode;
        private String mccLevel;
        private String mccName;
        private String parentMcc;
        private String status;

        public String getMccCode() {
            return mccCode;
        }

        public void setMccCode(String mccCode) {
            this.mccCode = mccCode;
        }

        public String getMccLevel() {
            return mccLevel;
        }

        public void setMccLevel(String mccLevel) {
            this.mccLevel = mccLevel;
        }

        public String getMccName() {
            return mccName;
        }

        public void setMccName(String mccName) {
            this.mccName = mccName;
        }

        public String getParentMcc() {
            return parentMcc;
        }

        public void setParentMcc(String parentMcc) {
            this.parentMcc = parentMcc;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
