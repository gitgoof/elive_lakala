package com.lakala.shoudan.activity.shoudan.finance.bean;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * 理财产品列表数据
 * Created by HJP on 2015/10/9.
 */
public class TotalProductInfo {
    private List<ProductInfo> ProdList;
    private String AccountNo;
    private String FoundFlag;
    private String _Guid;
    private String TransAcctId;
    private String FinAcctId;

    public static TotalProductInfo parserStringToTotalProductInfo(String str){
        return JSON.parseObject(str, TotalProductInfo.class);

    }

    public List<ProductInfo> getProdList() {
        return ProdList;
    }

    public void setProdList(List<ProductInfo> prodList) {
        ProdList = prodList;
    }

    public String getAccountNo() {
        return AccountNo;
    }

    public void setAccountNo(String accountNo) {
        AccountNo = accountNo;
    }

    public String getFoundFlag() {
        return FoundFlag;
    }

    public void setFoundFlag(String foundFlag) {
        FoundFlag = foundFlag;
    }

    public String get_Guid() {
        return _Guid;
    }

    public void set_Guid(String _Guid) {
        this._Guid = _Guid;
    }

    public String getTransAcctId() {
        return TransAcctId;
    }

    public void setTransAcctId(String transAcctId) {
        TransAcctId = transAcctId;
    }

    public String getFinAcctId() {
        return FinAcctId;
    }

    public void setFinAcctId(String finAcctId) {
        FinAcctId = finAcctId;
    }
}
