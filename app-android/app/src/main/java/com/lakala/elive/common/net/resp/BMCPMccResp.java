package com.lakala.elive.common.net.resp;

import java.util.List;

/**
 * Created by wenhaogu on 2017/1/12.
 */

public class BMCPMccResp {

    private boolean success;
    private String code;
    private String message;
    private List<MccsBean> mccs;

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

    public List<MccsBean> getMccs() {
        return mccs;
    }

    public void setMccs(List<MccsBean> mccs) {
        this.mccs = mccs;
    }

    public static class MccsBean {


        private String id;
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
