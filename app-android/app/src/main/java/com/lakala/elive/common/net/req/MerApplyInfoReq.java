package com.lakala.elive.common.net.req;

import com.lakala.elive.beans.MerApplyInfo;
import com.lakala.elive.beans.MerAttachFile;
import com.lakala.elive.beans.MerOpenInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 进件申请 结算信息
 * Created by wenhaogu on 2017/1/9.
 */

public class MerApplyInfoReq implements Serializable {
    private String authToken;
    private MerApplyInfo merApplyInfo;
    private MerOpenInfo merOpenInfo;
    private List<MerAttachFile> merAttachFileList;
    //private List<ShopOpenInfo> shopOpenInfoList;
    //private List<TerminalInfo> terminalInfoList;
    //private List<CardAppInfo> cardAppInfoList;


    public MerApplyInfoReq(String authToken) {
        this.authToken = authToken;
    }
    public MerApplyInfoReq() {
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public MerApplyInfo getMerApplyInfo() {
        return merApplyInfo;
    }

    public void setMerApplyInfo(MerApplyInfo merApplyInfo) {
        this.merApplyInfo = merApplyInfo;
    }

    public MerOpenInfo getMerOpenInfo() {
        return merOpenInfo;
    }

    public void setMerOpenInfo(MerOpenInfo merOpenInfo) {
        this.merOpenInfo = merOpenInfo;
    }

    public List<MerAttachFile> getMerAttachFileList() {
        return merAttachFileList;
    }

    public void setMerAttachFileList(List<MerAttachFile> merAttachFileList) {
        this.merAttachFileList = merAttachFileList;
    }

//    public List<ShopOpenInfo> getShopOpenInfoList() {
//        return shopOpenInfoList;
//    }
//
//    public void setShopOpenInfoList(List<ShopOpenInfo> shopOpenInfoList) {
//        this.shopOpenInfoList = shopOpenInfoList;
//    }
//
//    public List<TerminalInfo> getTerminalInfoList() {
//        return terminalInfoList;
//    }
//
//    public void setTerminalInfoList(List<TerminalInfo> terminalInfoList) {
//        this.terminalInfoList = terminalInfoList;
//    }
//
//    public List<CardAppInfo> getCardAppInfoList() {
//        return cardAppInfoList;
//    }
//
//    public void setCardAppInfoList(List<CardAppInfo> cardAppInfoList) {
//        this.cardAppInfoList = cardAppInfoList;
//    }
}


