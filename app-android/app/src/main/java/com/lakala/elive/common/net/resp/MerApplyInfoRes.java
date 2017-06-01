package com.lakala.elive.common.net.resp;

import java.util.Map;

/**
 * Created by wenhaogu on 2017/1/12.
 */

public class MerApplyInfoRes {

    private String commandId;
    private ContentBean content;
    private String resultCode;
    private String message;
    public final String[] resultTypeList = {
            "accountResult",
            "idCardBlackResult",
            "accountBlackResult",
            "termResult"};

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private int resultDataType;

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

        private String applyId;

        private long id;
        private Map<String,String> validInfoMap;

        public Map<String, String> getValidInfoMap() {
            return validInfoMap;
        }

        public void setValidInfoMap(Map<String, String> validInfoMap) {
            this.validInfoMap = validInfoMap;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getApplyId() {
            return applyId;
        }

        public void setApplyId(String applyId) {
            this.applyId = applyId;
        }
    }
}
