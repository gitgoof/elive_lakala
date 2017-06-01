package com.lakala.shoudan.activity.quickArrive;

import org.json.JSONObject;

/**
 * 绑定银行卡类
 * Created by WangCheng on 2016/8/24.
 */
public class BankCards {
    private String bindsid;
    private String d0bindcardno;
    private String userid;
    private String bindstatus;
    private String bankname;
    private String merid;
    private String cardkind;
    private String unbindsid;
    private String bankcode;
    private String accountname;
    private String cardtype;

    public BankCards(JSONObject jsonObject){
        bindsid=jsonObject.optString("bindsid");
        d0bindcardno=jsonObject.optString("d0bindcardno");
        userid=jsonObject.optString("userid");
        bindstatus=jsonObject.optString("bindstatus");
        bankname=jsonObject.optString("bankname");
        merid=jsonObject.optString("merid");
        cardkind=jsonObject.optString("cardkind");
        unbindsid=jsonObject.optString("unbindsid");
        bankcode=jsonObject.optString("bankcode");
        accountname=jsonObject.optString("accountname");
        cardtype=jsonObject.optString("cardtype");
    }

    public String getBindsid() {
        return bindsid;
    }

    public void setBindsid(String bindsid) {
        this.bindsid = bindsid;
    }

    public String getD0bindcardno() {
        return d0bindcardno;
    }

    public void setD0bindcardno(String d0bindcardno) {
        this.d0bindcardno = d0bindcardno;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getBindstatus() {
        return bindstatus;
    }

    public void setBindstatus(String bindstatus) {
        this.bindstatus = bindstatus;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getMerid() {
        return merid;
    }

    public void setMerid(String merid) {
        this.merid = merid;
    }

    public String getCardkind() {
        return cardkind;
    }

    public void setCardkind(String cardkind) {
        this.cardkind = cardkind;
    }

    public String getUnbindsid() {
        return unbindsid;
    }

    public void setUnbindsid(String unbindsid) {
        this.unbindsid = unbindsid;
    }

    public String getBankcode() {
        return bankcode;
    }

    public void setBankcode(String bankcode) {
        this.bankcode = bankcode;
    }

    public String getAccountname() {
        return accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }

    public String getCardtype() {
        return cardtype;
    }

    public void setCardtype(String cardtype) {
        this.cardtype = cardtype;
    }
}
