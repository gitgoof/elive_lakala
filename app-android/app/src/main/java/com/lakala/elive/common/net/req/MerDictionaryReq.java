package com.lakala.elive.common.net.req;

/**
 * 字典查询bean
 * Created by wenhaogu on 2017/1/17.
 */

public class MerDictionaryReq {
    private String authToken;
    private String dicType;
    private String id;
    private String type;
    private String code;
    private String parentMcc;
    private String level;


    public MerDictionaryReq(String authToken,String dicType) {
        this.authToken = authToken;
        this.dicType = dicType;
    }

    /**
     * 银行查询
     *
     * @param authToken
     * @param dicType
     * @param type
     */
    public MerDictionaryReq(String authToken, String dicType, String type) {
        this.authToken = authToken;
        this.dicType = dicType;
        this.type = type;
    }

    public MerDictionaryReq(String authToken, String dicType, String type, String code) {
        this.authToken = authToken;
        this.dicType = dicType;
        this.type = type;
        this.code = code;
    }


    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getDicType() {
        return dicType;
    }

    public void setDicType(String dicType) {
        this.dicType = dicType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParentMcc() {
        return parentMcc;
    }

    public void setParentMcc(String parentMcc) {
        this.parentMcc = parentMcc;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
