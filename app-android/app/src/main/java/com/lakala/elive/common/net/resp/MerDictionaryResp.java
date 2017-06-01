package com.lakala.elive.common.net.resp;


import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wenhaogu on 2017/1/17.
 */

public class MerDictionaryResp {


    private String commandId;
    private ContentBean content;
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

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

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

    public static class ContentBean {

        private String code;
        private String message;
        private boolean success;
        @SerializedName(value = "items", alternate = {"mccs", "regions"})
        private List<ItemsBean> items;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public List<ItemsBean> getItems() {
            return items;
        }

        public void setItems(List<ItemsBean> items) {
            this.items = items;
        }

        public static class ItemsBean {

            private String id;
            @SerializedName(value = "value", alternate = {"name"})
            private String value;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }
}
