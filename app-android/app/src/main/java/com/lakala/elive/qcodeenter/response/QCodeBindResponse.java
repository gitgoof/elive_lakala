package com.lakala.elive.qcodeenter.response;

import java.io.Serializable;
import java.util.List;

/**
 * Q码的绑定
 */
public class QCodeBindResponse implements Serializable {

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

    public static class ContentBean implements Serializable {

        public String   code;
        public String  message;
//        public String  qcodes;
        public String  shopNo;
        public String  success;

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

//        public String getQcodes() {
//            return qcodes;
//        }
//
//        public void setQcodes(String qcodes) {
//            this.qcodes = qcodes;
//        }

        public String getShopNo() {
            return shopNo;
        }

        public void setShopNo(String shopNo) {
            this.shopNo = shopNo;
        }

        public String getSuccess() {
            return success;
        }

        public void setSuccess(String success) {
            this.success = success;
        }

    }

}
