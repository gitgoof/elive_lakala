package com.lakala.platform.bean;

import android.util.Log;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.config.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by LMQ on 2015/7/14.
 */
public class AdvertInfo {
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private String sheet;
    private int layout;
    private Type type = Type.TEXT;
    private List<Advert> advData;
    private String channelNo;
    private Long expire;
    private Calendar expireCalendar;
    private int runTime;

    public AdvertInfo() {
        this.advData = new ArrayList<Advert>();
    }

    public String getSheet() {
        return sheet;
    }

    public int getLayout() {
        return layout;
    }

    public Type getType() {
        return type;
    }

    public List<Advert> getAdvData() {
        return advData;
    }

    public String getChannelNo() {
        return channelNo;
    }

    public Calendar getExpireCalendar() {
        return expireCalendar;
    }

    public int getRunTime() {
        return runTime;
    }

    enum Type{
        PIC("单张图片"),
        PICS("多种图片"),
        FLASH("FLASH"),
        TEXT("文本");
        String describe;

        Type(String describe) {
            this.describe = describe;
        }
    }
    public void parse(JSONObject jsonObject){
        this.sheet = jsonObject.optString("sheet");
        this.layout = jsonObject.optInt("layout");

        String tmpType = jsonObject.optString("type");
        try {
            this.type = Type.valueOf(tmpType);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        this.expire = jsonObject.optLong("expire");
        if(expire != null && expire != 0){
            expireCalendar = Calendar.getInstance();
            expireCalendar.setTimeInMillis(expire);
            if(Config.isDebug()){
                LogUtil.i("expireCalendar",timeFormat.format(expireCalendar.getTime()));
            }
        }

        Calendar currentTime = Calendar.getInstance();
        currentTime.setTimeInMillis(System.currentTimeMillis());
        if(currentTime.after(expireCalendar)){
            //若广告过期，则不获取列表
            LogUtil.i("test","广告已过期");
            return;
        }

        try {
            JSONArray jsonArray = jsonObject.getJSONArray("advData");
            int length = jsonArray == null?0:jsonArray.length();
            Advert advert;
            for(int i = 0;i<length;i++){
                advert = new Advert();
                JSONObject advJSON = jsonArray.getJSONObject(i);
                advert.parse(advJSON);

                //判断广告未过期才加入显示列表
                currentTime.setTimeInMillis(System.currentTimeMillis());
                Calendar advertExpire = advert.getExpireCalendar();
                if(currentTime.before(advertExpire) || advertExpire == null){
                    this.advData.add(advert);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //如果没有获取到广告，设备默认广告
        if(this.advData.size() == 0){
            this.advData.add(new Advert());
        }

        this.channelNo = jsonObject.optString("channelNo");
        this.runTime = jsonObject.optInt("runTime");
    }
}
