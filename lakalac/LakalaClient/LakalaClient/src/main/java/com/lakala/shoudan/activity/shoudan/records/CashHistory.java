package com.lakala.shoudan.activity.shoudan.records;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by LMQ on 2015/5/23.
 */
public class CashHistory implements Parcelable {
    private String sid;
    private String tradeDate;

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    private String tradeTime;
    private double amount;
    private Status status;
    private String statusName;
    private double feeAmount;
    private String shopNo;
    private OperatorType operatorType;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CashHistory that = (CashHistory) o;

        if (Double.compare(that.amount, amount) != 0) return false;
        if (Double.compare(that.feeAmount, feeAmount) != 0) return false;
        if (!sid.equals(that.sid)) return false;
        if (!tradeDate.equals(that.tradeDate)) return false;
        if (!tradeTime.equals(that.tradeTime)) return false;
        if (status != that.status) return false;
        if (!statusName.equals(that.statusName)) return false;
        if (!shopNo.equals(that.shopNo)) return false;
        return operatorType == that.operatorType;

    }
    public String getShopNo() {
        return shopNo;
    }

    public CashHistory setShopNo(String shopNo) {
        this.shopNo = shopNo;
        return this;
    }

    public double getFeeAmount() {
        return feeAmount;
    }

    public CashHistory setFeeAmount(double feeAmount) {
        this.feeAmount = feeAmount;
        return this;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = Status.valuesOf(status);
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public OperatorType getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(OperatorType operatorType) {
        this.operatorType = operatorType;
    }

    public enum Status{
        NULL(null,""),
        STATUS_00("00","划款成功"),
        STATUS_01("01","划款中"),
        STATUS_FF("FF","划款失败");
        private String key;
        private String statusName;

        Status(String key, String statusName) {
            this.key = key;
            this.statusName = statusName;
        }

        public String getStatusName() {
            return statusName;
        }

        public static Status valuesOf(String key){
            Status status = Status.NULL;
            for(Status s:values()){
                if(TextUtils.equals(s.key,key)){
                    status = s;
                    break;
                }
            }
            return status;
        }
    }

    public enum OperatorType{
        NULL,D0("D+0","提前划款"),T0("T+0","提前划款"),D1("D+1","正常划款"),T1("T+1","正常划款");
        private String typeKey;
        private String chineseDesc;
        OperatorType(String typeKey,String chineseDesc) {
            this.typeKey = typeKey;
            this.chineseDesc = chineseDesc;
        }
        OperatorType() {
        }

        public String getTypeKey() {
            return typeKey;
        }

        public String getChineseDesc() {
            return chineseDesc;
        }

        public static OperatorType valueOfByKey(String typeKey){
            OperatorType operatorType = OperatorType.NULL;
            for(OperatorType type:values()){
                if(TextUtils.equals(typeKey,type.getTypeKey())){
                    operatorType = type;
                    break;
                }
            }
            return operatorType;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sid);
        dest.writeString(this.tradeDate);
        dest.writeString(this.tradeTime);
        dest.writeDouble(this.amount);
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
        dest.writeString(this.statusName);
        dest.writeDouble(this.feeAmount);
        dest.writeInt(this.operatorType == null ? -1 : this.operatorType.ordinal());
    }

    public CashHistory() {
    }

    protected CashHistory(Parcel in) {
        this.sid = in.readString();
        this.tradeDate = in.readString();
        this.tradeTime = in.readString();
        this.amount = in.readDouble();
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : Status.values()[tmpStatus];
        this.statusName = in.readString();
        this.feeAmount = in.readDouble();
        int tmpOperatorType = in.readInt();
        this.operatorType = tmpOperatorType == -1 ? null : OperatorType.values()[tmpOperatorType];
    }

    public static final Creator<CashHistory> CREATOR = new Creator<CashHistory>() {
        public CashHistory createFromParcel(Parcel source) {
            return new CashHistory(source);
        }

        public CashHistory[] newArray(int size) {
            return new CashHistory[size];
        }
    };
}
