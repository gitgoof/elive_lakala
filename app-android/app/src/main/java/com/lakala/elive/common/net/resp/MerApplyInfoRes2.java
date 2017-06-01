package com.lakala.elive.common.net.resp;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by wenhaogu on 2017/1/12.
 */

public class MerApplyInfoRes2 {

    private String commandId;
    private ContentBean content;
    private String resultCode;
    private String message;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class ContentBean {
        private boolean success;
        private String code;
        private String message;
        private String no;
        private String name;
        private String corporator;
        private String address;
        private String expire;
        private String id;
        private boolean verifyResult;

        private Map<String,String> validInfoMap;
        public Map<String, String> getValidInfoMap() {
            return validInfoMap;
        }
        public void setValidInfoMap(Map<String, String> validInfoMap) {
            this.validInfoMap = validInfoMap;
        }

//        private ValidInfoMap  validInfoMap;
//
//        public ValidInfoMap getValidInfoMap() {
//            return validInfoMap;
//        }
//
//        public void setValidInfoMap(ValidInfoMap validInfoMap) {
//            this.validInfoMap = validInfoMap;
//        }
//
//        public  static   class ValidInfoMap  implements Serializable{
//
//            private String  verifyResultInfo;
//
//            public String getVerifyResultInfo() {
//                return verifyResultInfo;
//            }
//
//            public void setVerifyResultInfo(String verifyResultInfo) {
//                this.verifyResultInfo = verifyResultInfo;
//            }
//        };

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

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

        public String getNo() {
            return no;
        }

        public void setNo(String no) {
            this.no = no;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCorporator() {
            return corporator;
        }

        public void setCorporator(String corporator) {
            this.corporator = corporator;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getExpire() {
            return expire;
        }

        public void setExpire(String expire) {
            this.expire = expire;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isVerifyResult() {
            return verifyResult;
        }

        public void setVerifyResult(boolean verifyResult) {
            this.verifyResult = verifyResult;
        }
    }
}
