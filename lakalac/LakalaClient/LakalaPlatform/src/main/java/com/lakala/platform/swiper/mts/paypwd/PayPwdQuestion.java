package com.lakala.platform.swiper.mts.paypwd;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lianglong on 14-6-6.
 */
public class PayPwdQuestion implements Parcelable {
    String QuestionContent, QuestionType, AnswerNote, QuestionId, AnswerType,QuestionFlag,source;

    @Override
    public boolean equals(Object o) {
        return o != null && this.toString().equals(o.toString());
    }

    @Override
    public String toString() {
        return source == null ? "{}" : source;
    }

    public PayPwdQuestion(JSONObject data){
        this.source     = data.toString();
        QuestionContent = data.optString("QuestionContent");
        QuestionType    = data.optString("QuestionType");
        AnswerNote      = data.optString("AnswerNote");
        QuestionId      = data.optString("QuestionId");
        AnswerType      = data.optString("AnswerType");
        QuestionFlag    = data.optString("QuestionFlag");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(toString());
    }

    public static final Parcelable.Creator<PayPwdQuestion> CREATOR = new Parcelable.Creator<PayPwdQuestion>() {
        public PayPwdQuestion createFromParcel(Parcel source) {
            try {
                return new PayPwdQuestion(new JSONObject(source.readString()));
            } catch (JSONException e) {
                return new PayPwdQuestion(new JSONObject());
            }
        }

        public PayPwdQuestion[] newArray(int size) {
            return new PayPwdQuestion[size];
        }
    };
}