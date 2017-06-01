package com.lakala.shoudan.activity.wallet.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengxuan on 2015/12/19.
 */
public class AccountInfo implements Serializable{

    private String TrsPasswordFlag;
    private String ListFlag;
    private String WalletWithdrawBalance;
    private List<ListEntity> list;

    public void setTrsPasswordFlag(String TrsPasswordFlag) {
        this.TrsPasswordFlag = TrsPasswordFlag;
    }

    public void setListFlag(String ListFlag) {
        this.ListFlag = ListFlag;
    }

    public void setWalletWithdrawBalance(String WalletWithdrawBalance) {
        this.WalletWithdrawBalance = WalletWithdrawBalance;
    }

    public void setList(List<ListEntity> list) {
        this.list = list;
    }

    public String getTrsPasswordFlag() {
        return TrsPasswordFlag;
    }

    public String getListFlag() {
        return ListFlag;
    }

    public String getWalletWithdrawBalance() {
        return WalletWithdrawBalance;
    }

    public List<ListEntity> getList() {
        return list;
    }

    public static class ListEntity {
        private String PayeeBankName;
        private String CardTypeName;
        private String PayeeAcType;
        private String LogoName;
        private String PayeeName;
        private String CardTailFour;
        private String PayeeAcNo;
        private String PayeeCoreBankId;
        private List<ArrivalTypeListEntity> arrivalTypeList;

        public void setPayeeBankName(String PayeeBankName) {
            this.PayeeBankName = PayeeBankName;
        }

        public void setCardTypeName(String CardTypeName) {
            this.CardTypeName = CardTypeName;
        }

        public void setPayeeAcType(String PayeeAcType) {
            this.PayeeAcType = PayeeAcType;
        }

        public void setLogoName(String LogoName) {
            this.LogoName = LogoName;
        }

        public void setPayeeName(String PayeeName) {
            this.PayeeName = PayeeName;
        }

        public void setCardTailFour(String CardTailFour) {
            this.CardTailFour = CardTailFour;
        }

        public void setPayeeAcNo(String PayeeAcNo) {
            this.PayeeAcNo = PayeeAcNo;
        }

        public void setPayeeCoreBankId(String PayeeCoreBankId) {
            this.PayeeCoreBankId = PayeeCoreBankId;
        }

        public void setArrivalTypeList(List<ArrivalTypeListEntity> arrivalTypeList) {
            this.arrivalTypeList = arrivalTypeList;
        }

        public String getPayeeBankName() {
            return PayeeBankName;
        }

        public String getCardTypeName() {
            return CardTypeName;
        }

        public String getPayeeAcType() {
            return PayeeAcType;
        }

        public String getLogoName() {
            return LogoName;
        }

        public String getPayeeName() {
            return PayeeName;
        }

        public String getCardTailFour() {
            return CardTailFour;
        }

        public String getPayeeAcNo() {
            return PayeeAcNo;
        }

        public String getPayeeCoreBankId() {
            return PayeeCoreBankId;
        }

        public List<ArrivalTypeListEntity> getArrivalTypeList() {
            return arrivalTypeList;
        }

        public static class ArrivalTypeListEntity {
            private String ArrivalTypeName;
            private String ArrivalType;

            public void setArrivalTypeName(String ArrivalTypeName) {
                this.ArrivalTypeName = ArrivalTypeName;
            }

            public void setArrivalType(String ArrivalType) {
                this.ArrivalType = ArrivalType;
            }

            public String getArrivalTypeName() {
                return ArrivalTypeName;
            }

            public String getArrivalType() {
                return ArrivalType;
            }
        }
    }

    public void parseObject(JSONObject jsonObject) throws JSONException {

        setTrsPasswordFlag(jsonObject.optString("TrsPasswordFlag"));
        setListFlag(jsonObject.optString("ListFlag"));
        setWalletWithdrawBalance(jsonObject.optString("WalletWithdrawBalance"));

        List<ListEntity> list = new ArrayList<ListEntity>();

        JSONArray jsonArray = jsonObject.getJSONArray("list");
        if (jsonArray != null && jsonArray.length() > 0) {

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                AccountInfo.ListEntity listEntity = new AccountInfo.ListEntity();
                listEntity.setPayeeBankName(json.optString("PayeeBankName"));
                listEntity.setCardTypeName(json.optString("CardTypeName"));
                listEntity.setPayeeAcType(json.optString("PayeeAcType"));
                listEntity.setLogoName(json.optString("LogoName"));
                listEntity.setPayeeName(json.optString("PayeeName"));
                listEntity.setCardTailFour(json.optString("CardTailFour"));
                listEntity.setPayeeAcNo(json.optString("PayeeAcNo"));
                listEntity.setPayeeCoreBankId(json.optString("PayeeCoreBankId"));

                List<ListEntity.ArrivalTypeListEntity> sonList = new ArrayList<ListEntity.ArrivalTypeListEntity>();
                JSONArray jarr = json.getJSONArray("arrivalTypeList");
                if (jarr != null && jarr.length() > 0) {
                    for (int j = 0; j < jarr.length(); j++) {
                        JSONObject jobj = jarr.getJSONObject(j);
                        AccountInfo.ListEntity.ArrivalTypeListEntity arrivalTypeListEntity = new AccountInfo.ListEntity.ArrivalTypeListEntity();
                        arrivalTypeListEntity.setArrivalTypeName(jobj.optString("ArrivalTypeName"));
                        arrivalTypeListEntity.setArrivalType(jobj.optString("ArrivalType"));
                        sonList.add(arrivalTypeListEntity);
                    }
                } else {
                    sonList = new ArrayList<ListEntity.ArrivalTypeListEntity>();
                }

                listEntity.setArrivalTypeList(sonList);
                list.add(listEntity);
            }

        } else {
            list = new ArrayList<ListEntity>() ;
        }

        setList(list);
    }

}
