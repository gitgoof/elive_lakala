package com.lakala.elive.common.net.resp;

import java.io.Serializable;

/**
 * Created by wenhaogu on 2017/1/20.
 */

public class PhotoDiscernResp implements Serializable {


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

    public static class ContentBean implements Serializable {

        private String applyId;
        private BankCardInfoBean bankCardInfo;
        private IdCardInfoBean idCardInfo;
        private IdCardInfoBackBean idCardInfoBack;
        private LicenseInfoBean licenseInfo;

        public String getApplyId() {
            return applyId;
        }

        public void setApplyId(String applyId) {
            this.applyId = applyId;
        }

        public BankCardInfoBean getBankCardInfo() {
            return bankCardInfo;
        }

        public void setBankCardInfo(BankCardInfoBean bankCardInfo) {
            this.bankCardInfo = bankCardInfo;
        }

        public IdCardInfoBean getIdCardInfo() {
            return idCardInfo;
        }

        public void setIdCardInfo(IdCardInfoBean idCardInfo) {
            this.idCardInfo = idCardInfo;
        }

        public IdCardInfoBackBean getIdCardInfoBack() {
            return idCardInfoBack;
        }

        public void setIdCardInfoBack(IdCardInfoBackBean idCardInfoBack) {
            this.idCardInfoBack = idCardInfoBack;
        }

        public LicenseInfoBean getLicenseInfo() {
            return licenseInfo;
        }

        public void setLicenseInfo(LicenseInfoBean licenseInfo) {
            this.licenseInfo = licenseInfo;
        }

        public static class BankCardInfoBean implements Serializable {


            private String card_number;
            private String holder_name;
            private String issuer;
            private String type;
            private String validate;

            public String getCard_number() {
                return card_number;
            }

            public void setCard_number(String card_number) {
                this.card_number = card_number;
            }

            public String getHolder_name() {
                return holder_name;
            }

            public void setHolder_name(String holder_name) {
                this.holder_name = holder_name;
            }

            public String getIssuer() {
                return issuer;
            }

            public void setIssuer(String issuer) {
                this.issuer = issuer;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getValidate() {
                return validate;
            }

            public void setValidate(String validate) {
                this.validate = validate;
            }
        }

        public static class IdCardInfoBean implements Serializable {

            private String address;
            private String birthday;
            private String id_number;
            private String name;
            private String people;
            private String sex;
            private String type;

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getBirthday() {
                return birthday;
            }

            public void setBirthday(String birthday) {
                this.birthday = birthday;
            }

            public String getId_number() {
                return id_number;
            }

            public void setId_number(String id_number) {
                this.id_number = id_number;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPeople() {
                return people;
            }

            public void setPeople(String people) {
                this.people = people;
            }

            public String getSex() {
                return sex;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }

        public static class IdCardInfoBackBean implements Serializable {


            private String issue_authority;
            private String type;
            private String validity;

            public String getIssue_authority() {
                return issue_authority;
            }

            public void setIssue_authority(String issue_authority) {
                this.issue_authority = issue_authority;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getValidity() {
                return validity;
            }

            public void setValidity(String validity) {
                this.validity = validity;
            }
        }

        public static class LicenseInfoBean implements Serializable {

            private String biz_license_company_name;
            private String biz_license_company_type;
            private String biz_license_composing_form;
            private String biz_license_registration_code;
            private String biz_license_scope;
            private String biz_license_type;
            private String type;

            public String getBiz_license_company_name() {
                return biz_license_company_name;
            }

            public void setBiz_license_company_name(String biz_license_company_name) {
                this.biz_license_company_name = biz_license_company_name;
            }

            public String getBiz_license_company_type() {
                return biz_license_company_type;
            }

            public void setBiz_license_company_type(String biz_license_company_type) {
                this.biz_license_company_type = biz_license_company_type;
            }

            public String getBiz_license_composing_form() {
                return biz_license_composing_form;
            }

            public void setBiz_license_composing_form(String biz_license_composing_form) {
                this.biz_license_composing_form = biz_license_composing_form;
            }

            public String getBiz_license_registration_code() {
                return biz_license_registration_code;
            }

            public void setBiz_license_registration_code(String biz_license_registration_code) {
                this.biz_license_registration_code = biz_license_registration_code;
            }

            public String getBiz_license_scope() {
                return biz_license_scope;
            }

            public void setBiz_license_scope(String biz_license_scope) {
                this.biz_license_scope = biz_license_scope;
            }

            public String getBiz_license_type() {
                return biz_license_type;
            }

            public void setBiz_license_type(String biz_license_type) {
                this.biz_license_type = biz_license_type;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }
}
