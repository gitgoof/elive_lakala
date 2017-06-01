package com.lakala.shoudan.datadefine;

import android.os.Parcel;
import android.os.Parcelable;


import org.json.JSONObject;

/**
 * 信用卡类
 */
public class CreditCard implements Parcelable {

	public String creditCardNo; //卡号
	public String bankName ; //银行名称
	public String bankNo ;//银行编号
	public String bankimg = "";
	public String cardHolder ;//持卡人姓名
	//TODO
//	public ManageActivity.DepositCardStatus depositCardStatus ;// 卡状态(成功，失败，验证中)

	/**
	 * 根据json创建实例
	 * @param data
	 * @return
	 */
	public static CreditCard construct(JSONObject data){

		CreditCard depositCard = null;
		try{
		 	depositCard = new CreditCard();
		}catch(Exception e){

		}

		return depositCard;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(creditCardNo);
		dest.writeString(bankName);
		dest.writeString(bankNo);
		dest.writeString(bankimg);
		dest.writeString(cardHolder);
//		TODO
//		dest.writeValue(depositCardStatus);
	}

	public static Creator<CreditCard> CREATOR = new Creator<CreditCard>() {
		
		@Override
		public CreditCard[] newArray(int size) {
			return new CreditCard[size];
		}
		
		@Override
		public CreditCard createFromParcel(Parcel source) {
			CreditCard depositCard = new CreditCard();
			depositCard.creditCardNo = source.readString();
			depositCard.bankName = source.readString();
			depositCard.bankNo = source.readString();
			depositCard.bankimg = source.readString();
			depositCard.cardHolder = source.readString();
			//TODO
//			depositCard.depositCardStatus = (ManageActivity.DepositCardStatus) source.readValue(null);
			return depositCard;
		}
	};

}
