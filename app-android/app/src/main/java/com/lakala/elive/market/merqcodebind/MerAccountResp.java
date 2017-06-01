package com.lakala.elive.market.merqcodebind;


import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wenhaogu on 2017/1/17.
 */

public class MerAccountResp {


    private String commandId;

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    private List<String> content;
    private String resultCode;
    private int resultDataType;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

//    public ContentBean getContent() {
//        return content;
//    }
//
//    public void setContent(ContentBean content) {
//        this.content = content;
//    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public int getResultDataType() {
        return resultDataType;
    }

    public void setResultDataType(int resultDataType) {
        this.resultDataType = resultDataType;
    }

//    public static class ContentBean {
//
//        private String code;
//
//
//        public List<ItemsBean> getItems() {
//            return items;
//        }
//
//
//    }
}
