package com.lakala.elive.qcodeenter.response;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ousachisan on 2017/3/23.
 * <p>
 * Q码的列表也查询接口的Response(ELIVE_PARTNER_APPLY_001)
 */

public class QCodeListResponse implements Serializable {

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

        public String shopNo;
        public String success;
        public String code;
        public String message;
        public List<Qcodes> qcodes;

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

        public List<Qcodes> getQcodes() {
            return qcodes;
        }

        public void setQcodes(List<Qcodes> qcodes) {
            this.qcodes = qcodes;
        }

        public static class Qcodes implements Serializable {
            public String qcodeUrl;
            public String qCode;
            public String createTime;
            public String status;
            public String codeId;

            public String getQcodeUrl() {
                return qcodeUrl;
            }

            public void setQcodeUrl(String qcodeUrl) {
                this.qcodeUrl = qcodeUrl;
            }

            public String getQCode() {
                return qCode;
            }

            public void setQCode(String qCode) {
                this.qCode = qCode;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getCodeId() {
                return codeId;
            }

            public void setCodeId(String codeId) {
                this.codeId = codeId;
            }
        }
    }

}
