package com.lakala.platform.swiper.mts;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangchao on 14-2-6.
 */
public class SwipeItem implements Parcelable {

    private static final String Q201 = "0001";
    private static final String Q202 = "0002";
    private static final String Q203 = "0003";
    private static final String Q206 = "0008";
    private static final String PAYFI = "0007";
    private static final String QV30E = "0009";
    private static final String LKLMobile = "0010";
    private static final String ME11 = "0012";

    public static List<SwipeItem> getDefaultList() {
        List<SwipeItem> list = new ArrayList<SwipeItem>();
        list.add(new SwipeItem("{\"UnitName\":\"Q2(01)\",\"UnitId\":\""+Q201+"\"}"));
        list.add(new SwipeItem("{\"UnitName\":\"Q2(02)\",\"UnitId\":\""+Q202+"\"}"));
        list.add(new SwipeItem("{\"UnitName\":\"Q2(03)\",\"UnitId\":\""+Q203+"\"}"));
        return list;
    }

    public static List<SwipeItem> getOtherList() {
        List<SwipeItem> list = new ArrayList<SwipeItem>();
        list.add(new SwipeItem("{\"UnitName\":\"lklphone\",\"UnitId\":\""+LKLMobile+"\"}"));
        list.add(new SwipeItem("{\"UnitName\":\"Q2(06)\",\"UnitId\":\""+Q206+"\"}"));
        list.add(new SwipeItem("{\"UnitName\":\"PayFi\",\"UnitId\":\""+PAYFI+"\"}"));
        list.add(new SwipeItem("{\"UnitName\":\"QV30E\",\"UnitId\":\""+QV30E+"\"}"));
        list.add(new SwipeItem("{\"UnitName\":\"QT168\",\"UnitId\":\""+Q203+"\"}"));
        list.add(new SwipeItem("{\"UnitName\":\"ME11\",\"UnitId\":\""+ME11+"\"}"));
        return list;
    }

    private String UnitName;
    private String UnitId;
    private String UnitDesc;
    private String Plat;
    private String Version;
    private boolean isSelect;
    private String jsonString;

    public SwipeItem() {
    }

    private SwipeItem(String UnitName, String UnitId) {
        this.UnitName = UnitName;
        this.UnitId = UnitId;
        isSelect=false;
    }

    public SwipeItem(String jsonString){
        this.jsonString = jsonString;
        JSONObject object;
        try {
            object = new JSONObject(jsonString);
            this.setName(object.optString("UnitName"));
            this.setType(object.optString("UnitId"));
            this.setUnitDesc(object.optString("UnitDesc"));
            this.setPlat(object.optString("Plat"));
            this.setVersion(object.optString("Version"));
            this.isSelect = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public String getUnitDesc() {
        return UnitDesc;
    }

    public void setUnitDesc(String unitDesc) {
        UnitDesc = unitDesc;
    }

    public String getPlat() {
        return Plat;
    }

    public void setPlat(String plat) {
        Plat = plat;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public String getName() {
        return UnitName;
    }

    public void setName(String UnitName) {
        this.UnitName = UnitName;
    }

    public String getType() {
        return UnitId;
    }

    public void setType(String UnitId) {
        this.UnitId = UnitId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getJsonString());
    }

    public static final Creator<SwipeItem> CREATOR = new Creator<SwipeItem>() {
        @Override
        public SwipeItem createFromParcel(Parcel source) {
            return new SwipeItem(source.readString());
        }

        @Override
        public SwipeItem[] newArray(int size) {
            return new SwipeItem[size];
        }
    };
}
