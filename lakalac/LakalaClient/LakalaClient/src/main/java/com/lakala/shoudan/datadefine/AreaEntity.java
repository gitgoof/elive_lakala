package com.lakala.shoudan.datadefine;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LMQ on 2015/6/18.
 */
public class AreaEntity implements Parcelable {
    private String name;
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }

    @Override
    public boolean equals(Object o) {
        AreaEntity entity = (AreaEntity)o;
        return TextUtils.equals(code,entity.getCode());
    }
    public static List<AreaEntity> parseList(JSONArray jsonArray){
        List<AreaEntity> areas = new ArrayList<AreaEntity>();
        if(jsonArray == null){
            return areas;
        }
        int length = jsonArray.length();
        AreaEntity area = null;
        for(int i = 0;i<length;i++){
            area = new AreaEntity();
            areas.add(area);

            JSONObject jsonObject = jsonArray.optJSONObject(i);
            String name = jsonObject.optString("name");
            if(name.contains("\"")){
                name = name.split("\"")[0];
            }
            area.setName(name);
            area.setCode(jsonObject.optString("code"));
        }
        return areas;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.code);
    }

    public AreaEntity() {
    }

    protected AreaEntity(Parcel in) {
        this.name = in.readString();
        this.code = in.readString();
    }

    public static final Parcelable.Creator<AreaEntity> CREATOR = new Parcelable.Creator<AreaEntity>() {
        @Override
        public AreaEntity createFromParcel(Parcel source) {
            return new AreaEntity(source);
        }

        @Override
        public AreaEntity[] newArray(int size) {
            return new AreaEntity[size];
        }
    };
}
