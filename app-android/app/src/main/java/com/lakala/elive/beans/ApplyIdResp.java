package com.lakala.elive.beans;

import java.util.List;

/**
 * Created by zhouzx on 2017/2/28.
 */
public class ApplyIdResp {
    private String resultCode;
    private String message;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<MerApplyInfo> getContent() {
        return content;
    }

    public void setContent(List<MerApplyInfo> content) {
        this.content = content;
    }

    private List<MerApplyInfo> content;

    public class MerApplyInfo {

        private String applyId;
        private String resultId;
        private String applyType;
        private String process;
        private String accountKind;

        public String getApplyChannel() {
            return applyChannel;
        }

        public void setApplyChannel(String applyChannel) {
            this.applyChannel = applyChannel;
        }

        private String  applyChannel;

        public String getAccountKind() {
            return accountKind;
        }

        public void setAccountKind(String accountKind) {
            this.accountKind = accountKind;
        }

        public String getApplyId() {
            return applyId;
        }

        public void setApplyId(String applyId) {
            this.applyId = applyId;
        }

        public String getResultId() {
            return resultId;
        }

        public void setResultId(String resultId) {
            this.resultId = resultId;
        }

        public String getApplyType() {
            return applyType;
        }

        public void setApplyType(String applyType) {
            this.applyType = applyType;
        }

        public String getProcess() {
            return process;
        }

        public void setProcess(String process) {
            this.process = process;
        }
    }
}
