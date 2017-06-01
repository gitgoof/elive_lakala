package com.lakala.platform.bean;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by LMQ on 2015/7/8.
 */
public class Advert implements Comparable<Advert>{
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private JSONObject advertJson;
    private String advertisementId;
    private String webviewTitle;
    private String content;
    private String clickUrl;
    private int idx;
    private String remark;
    private Long expire;
    private Calendar expireCalendar;

    public Advert() {
        webviewTitle = "电子现金，你刷了吗？";
        content = "银联电子现金，刷卡分分钟搞定！";
        clickUrl = "http://mp.weixin.qq.com/s?__biz=MjM5NDcxMzQ4NQ==&mid=294571988&idx=1&sn=25f19a0a2a592e6fb2cf594f0f85a5ab#rd";
    }
    public void parse(JSONObject jsonObject){
        advertJson = jsonObject;
        String tmp;
        this.advertisementId = jsonObject.optString("advertisementId","");
        this.webviewTitle = jsonObject.optString("webviewTitle", "");
        this.content = jsonObject.optString("content", "");
        this.clickUrl = jsonObject.optString("clickUrl");
        this.expire = jsonObject.optLong("expire");
        tmp = jsonObject.optString("idx");
        try {
            idx = Integer.parseInt(tmp);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        this.remark = jsonObject.optString("remark");
        if(expire != null && expire != 0){
            expireCalendar = Calendar.getInstance();
            expireCalendar.setTimeInMillis(expire);
        }
    }

    public String getWebviewTitle() {
        return webviewTitle;
    }

    public String getContent() {
        return content;
    }

    public String getClickUrl() {
        return clickUrl;
    }

    public Calendar getExpireCalendar() {
        return expireCalendar;
    }

    public String getAdvertisementId() {
        return advertisementId;
    }

    public int getIdx() {
        return idx;
    }

    public String getRemark() {
        return remark;
    }

    public JSONObject getAdvertJson() {
        return advertJson;
    }

    @Override
    public int compareTo(Advert another) {
        return another == null?-1:this.idx-another.getIdx();
    }
}
