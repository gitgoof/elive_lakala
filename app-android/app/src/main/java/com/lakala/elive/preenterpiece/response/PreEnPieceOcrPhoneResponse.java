package com.lakala.elive.preenterpiece.response;


import java.io.Serializable;

/**
 * Created by ousachisan on 2017/3/23.
 * <p>
 * 合作方预进件的Ocr和Phone接口的Response
 */

public class PreEnPieceOcrPhoneResponse implements Serializable {

    private String message;
    private String resultCode;
    private RespOcr content;

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

    public RespOcr getContent() {
        return content;
    }

    public void setContent(RespOcr content) {
        this.content = content;
    }

    public static class RespOcr implements Serializable {

        //申请ID
        public String applyId;
        //身份证信息(背面)
        public IdCardInfoBack idCardInfoBack;
        //银行卡信息
        public BankCardInfo bankCardInfo;
        //组织机构证信息
        public OrganCardInfo organCardInfo;
        //营业执照信息
        public LicenseInfo licenseInfo;
        //税务证信息
        public TaxCardInfo taxCardInfo;

        //身份证信息
        public String getApplyId() {
            return applyId;
        }

        public void setApplyId(String applyId) {
            this.applyId = applyId;
        }

        public IdCardInfo getIdCardInfo() {
            return idCardInfo;
        }

        public void setIdCardInfo(IdCardInfo idCardInfo) {
            this.idCardInfo = idCardInfo;
        }

        public IdCardInfoBack getIdCardInfoBack() {
            return idCardInfoBack;
        }

        public void setIdCardInfoBack(IdCardInfoBack idCardInfoBack) {
            this.idCardInfoBack = idCardInfoBack;
        }

        public BankCardInfo getBankCardInfo() {
            return bankCardInfo;
        }

        public void setBankCardInfo(BankCardInfo bankCardInfo) {
            this.bankCardInfo = bankCardInfo;
        }

        public OrganCardInfo getOrganCardInfo() {
            return organCardInfo;
        }

        public void setOrganCardInfo(OrganCardInfo organCardInfo) {
            this.organCardInfo = organCardInfo;
        }

        public LicenseInfo getLicenseInfo() {
            return licenseInfo;
        }

        public void setLicenseInfo(LicenseInfo licenseInfo) {
            this.licenseInfo = licenseInfo;
        }

        public TaxCardInfo getTaxCardInfo() {
            return taxCardInfo;
        }

        public void setTaxCardInfo(TaxCardInfo taxCardInfo) {
            this.taxCardInfo = taxCardInfo;
        }

        public IdCardInfo idCardInfo;
    }

    public static class IdCardInfo implements Serializable {
        //类型
        public String type;
        //身份证ID
        public String id_number;
        // 姓名
        public String name;
        // 性别
        public String sex;
        // 民族
        public String people;
        //  生日
        public String birthday;
        // 地址
        public String address;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
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

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getPeople() {
            return people;
        }

        public void setPeople(String people) {
            this.people = people;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

    }

    public static class IdCardInfoBack implements Serializable {

        //类型
        public String type;
        // 发行机构
        public String issue_authority;
        //  有效期
        public String validity;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getIssue_authority() {
            return issue_authority;
        }

        public void setIssue_authority(String issue_authority) {
            this.issue_authority = issue_authority;
        }

        public String getValidity() {
            return validity;
        }

        public void setValidity(String validity) {
            this.validity = validity;
        }

    }

    public static class BankCardInfo implements Serializable {
        //   类型
        public String type;
        //   卡号
        public String card_number;
        //   发行者
        public String issuer;
        //   生效日期
        public String validate;
        //   持卡者姓名
        public String holder_name;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCard_number() {
            return card_number;
        }

        public void setCard_number(String card_number) {
            this.card_number = card_number;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public String getValidate() {
            return validate;
        }

        public void setValidate(String validate) {
            this.validate = validate;
        }

        public String getHolder_name() {
            return holder_name;
        }

        public void setHolder_name(String holder_name) {
            this.holder_name = holder_name;
        }

    }

    public static class OrganCardInfo implements Serializable {
        //   类型
        public String type;
        //   证件类型
        public String biz_license_type;
        //   id
        public String id_number;
        //   机构名称
        public String biz_license_company_name;
        //    机构类型
        public String biz_license_company_type;
        //    组织机构地址
        public String biz_license_address;
        //    有效期
        public String biz_license_validity;
        //    颁发机构
        public String biz_license_issued_org;
        //    注册号
        public String biz_license_registration_code;

        //   组织机构号
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getBiz_license_type() {
            return biz_license_type;
        }

        public void setBiz_license_type(String biz_license_type) {
            this.biz_license_type = biz_license_type;
        }

        public String getBiz_license_org_number() {
            return biz_license_org_number;
        }

        public void setBiz_license_org_number(String biz_license_org_number) {
            this.biz_license_org_number = biz_license_org_number;
        }

        public String getId_number() {
            return id_number;
        }

        public void setId_number(String id_number) {
            this.id_number = id_number;
        }

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

        public String getBiz_license_address() {
            return biz_license_address;
        }

        public void setBiz_license_address(String biz_license_address) {
            this.biz_license_address = biz_license_address;
        }

        public String getBiz_license_validity() {
            return biz_license_validity;
        }

        public void setBiz_license_validity(String biz_license_validity) {
            this.biz_license_validity = biz_license_validity;
        }

        public String getBiz_license_issued_org() {
            return biz_license_issued_org;
        }

        public void setBiz_license_issued_org(String biz_license_issued_org) {
            this.biz_license_issued_org = biz_license_issued_org;
        }

        public String getBiz_license_registration_code() {
            return biz_license_registration_code;
        }

        public void setBiz_license_registration_code(String biz_license_registration_code) {
            this.biz_license_registration_code = biz_license_registration_code;
        }

        public String biz_license_org_number;
    }

    public static class LicenseInfo implements Serializable {
        //    类型
        public String type;
        //    卡BIN
        public String biz_license_type;
        //    开户行编号
        public String biz_license_registration_code;
        //     清算银行编号
        public String biz_license_company_name;
        //     银行名称
        public String biz_license_company_type;
        //   卡类型
        public String biz_license_composing_form;
        //   地址
        public String biz_license_address;
        //    经营范围
        public String biz_license_scope;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getBiz_license_type() {
            return biz_license_type;
        }

        public void setBiz_license_type(String biz_license_type) {
            this.biz_license_type = biz_license_type;
        }

        public String getBiz_license_registration_code() {
            return biz_license_registration_code;
        }

        public void setBiz_license_registration_code(String biz_license_registration_code) {
            this.biz_license_registration_code = biz_license_registration_code;
        }

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

        public String getBiz_license_address() {
            return biz_license_address;
        }

        public void setBiz_license_address(String biz_license_address) {
            this.biz_license_address = biz_license_address;
        }

        public String getBiz_license_scope() {
            return biz_license_scope;
        }

        public void setBiz_license_scope(String biz_license_scope) {
            this.biz_license_scope = biz_license_scope;
        }

    }


    public static class TaxCardInfo implements Serializable {

        //    类型
        public String type;
        //     证件类型
        public String biz_license_type;
        //    公司名称
        public String biz_license_company_name;
        //    法人
        public String biz_license_owner_name;
        //     地址
        public String biz_license_address;
        //     注册机构
        public String biz_license_registration_institution;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getBiz_license_type() {
            return biz_license_type;
        }

        public void setBiz_license_type(String biz_license_type) {
            this.biz_license_type = biz_license_type;
        }

        public String getBiz_license_company_name() {
            return biz_license_company_name;
        }

        public void setBiz_license_company_name(String biz_license_company_name) {
            this.biz_license_company_name = biz_license_company_name;
        }

        public String getBiz_license_owner_name() {
            return biz_license_owner_name;
        }

        public void setBiz_license_owner_name(String biz_license_owner_name) {
            this.biz_license_owner_name = biz_license_owner_name;
        }

        public String getBiz_license_address() {
            return biz_license_address;
        }

        public void setBiz_license_address(String biz_license_address) {
            this.biz_license_address = biz_license_address;
        }

        public String getBiz_license_registration_institution() {
            return biz_license_registration_institution;
        }

        public void setBiz_license_registration_institution(String biz_license_registration_institution) {
            this.biz_license_registration_institution = biz_license_registration_institution;
        }

    }
}
