package com.lakala.shoudan.datadefine;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class HistoryCreditCardInfo implements Serializable,Parcelable{
	/**
	 * 信用卡分期还款历史页信息
	 */
	private static final long serialVersionUID = 5601139617518243086L;
	public String bankcode;//银行Code
	public String bankname;//银行name
	public String cardno;//信用卡卡号
	public String desc;//分期描述信息
	public String pageSize;//每页显示数
	public String totallPage;//总页数
	public String startPage;//开始页
	public String totalRecord;//总记录数
	public String recordId; //分期记录id
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(bankcode);
		dest.writeString(bankname);
		dest.writeString(cardno);
		dest.writeString(desc);
		dest.writeString(pageSize);
		dest.writeString(totallPage);
		dest.writeString(startPage);
		dest.writeString(totalRecord);
		dest.writeString(recordId);
	}
	
	// 声明一个常量
	public static final Creator<HistoryCreditCardInfo> CREATOR = new Creator<HistoryCreditCardInfo>() {
			public HistoryCreditCardInfo createFromParcel(Parcel source) {
				HistoryCreditCardInfo cardInfo = new HistoryCreditCardInfo();
				 cardInfo.bankcode = source.readString();
				 cardInfo.bankname = source.readString();
				 cardInfo.cardno = source.readString();
				 cardInfo.desc = source.readString();
				 cardInfo.pageSize = source.readString();
				 cardInfo.totallPage = source.readString();
				 cardInfo.startPage = source.readString();
				 cardInfo.totalRecord = source.readString();
				 cardInfo.recordId = source.readString();
				return cardInfo;
			}
			public HistoryCreditCardInfo[] newArray(int size) {
				return new HistoryCreditCardInfo[size];
			}
	};
}
