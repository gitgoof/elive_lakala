package com.lakala.elive.beans;

/**
 * Created by zhouzx on 2017/2/21.
 */
public class NoticeResp {

    /**
     * commandId : 2215915484049301117312
     * content : {"noticeCnt":0,"taskCnt":0}
     * message : 执行成功!
     * resultCode : SUCCESS
     * resultDataType : -1
     */

    private String commandId;
    private ContentBean content;
    private String message;
    private String resultCode;
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

    public int getResultDataType() {
        return resultDataType;
    }

    public void setResultDataType(int resultDataType) {
        this.resultDataType = resultDataType;
    }

    public static class ContentBean {
        /**
         * noticeCnt : 0
         * taskCnt : 0
         */

        private int noticeCnt;
        private int taskCnt;

        public int getNoticeCnt() {
            return noticeCnt;
        }

        public void setNoticeCnt(int noticeCnt) {
            this.noticeCnt = noticeCnt;
        }

        public int getTaskCnt() {
            return taskCnt;
        }

        public void setTaskCnt(int taskCnt) {
            this.taskCnt = taskCnt;
        }
    }
}
