package com.lakala.elive.common.net.resp;

/**
 * Created by wenhaogu on 2017/3/2.
 */

public class GetPhotoResq {

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

        private int enabled;
        private String fileContent;
        private String fileId;
        private String fileName;
        private String locate;
        private long operateDate;
        private String operator;
        private String refObject;
        private String saveName;
        private String segment;
        public String fileType;
        public String comments;

        public int getEnabled() {
            return enabled;
        }

        public void setEnabled(int enabled) {
            this.enabled = enabled;
        }

        public String getFileContent() {
            return fileContent;
        }

        public void setFileContent(String fileContent) {
            this.fileContent = fileContent;
        }

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public String getLocate() {
            return locate;
        }

        public void setLocate(String locate) {
            this.locate = locate;
        }

        public long getOperateDate() {
            return operateDate;
        }

        public void setOperateDate(long operateDate) {
            this.operateDate = operateDate;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getRefObject() {
            return refObject;
        }

        public void setRefObject(String refObject) {
            this.refObject = refObject;
        }

        public String getSaveName() {
            return saveName;
        }

        public void setSaveName(String saveName) {
            this.saveName = saveName;
        }

        public String getSegment() {
            return segment;
        }

        public void setSegment(String segment) {
            this.segment = segment;
        }
    }
}
