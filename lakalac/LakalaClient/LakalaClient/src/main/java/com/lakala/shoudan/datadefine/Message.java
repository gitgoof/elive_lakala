package com.lakala.shoudan.datadefine;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSON;
import com.lakala.library.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LMQ on 2015/4/22.
 */
public class Message implements Parcelable {
    private static final String TAG = "ssMessage";
    private String id;
    private String title;
    private int top;
    private int readed;
    private String content;
    private long msgTime;
    private String extInfo;
    private MSG_TYPE msgType;
    private CONTENT_TYPE contentType;
    private int idx;

    public static Message obtain(JSONObject jsonObject) {
        Message message = new Message();
        message.setId(jsonObject.optString("id", ""));
        message.setTitle(jsonObject.optString("title", ""));
        message.setTop(jsonObject.optBoolean("top", false));
        message.setReaded(jsonObject.optBoolean("readed", false));
        message.setContent(jsonObject.optString("content", ""));
        message.setMsgTime(jsonObject.optString("msgTime", "0"));
        message.setExtInfo(jsonObject.optString("extInfo", ""));
        message.setMsgType(jsonObject.optString("msgType", ""));
        message.setContentType(jsonObject.optString("contentType", ""));
        message.setIdx(jsonObject.optInt("idx"));
        return message;
    }

    public static Message obtain2(JSONObject jsonObject) {
        Message message = new Message();
        JSONObject jsonObject1;
        JSONObject jsonObject2;
        try {
            jsonObject1=jsonObject.getJSONObject("extInfo");
            jsonObject2=new JSONObject(jsonObject1.optString("titleRegionData"));
//            LogUtil.print(TAG,jsonObject1.toString());
//            LogUtil.print(TAG,jsonObject2.toString());
            message.setId(jsonObject.optString("id", ""));
//            LogUtil.print(TAG,message.getId()+"");
            message.setTop(jsonObject.optBoolean("top", false));
//            LogUtil.print(TAG,message.getTop()+"");
            message.setReaded(jsonObject.optBoolean("readed", false));
//            LogUtil.print(TAG,message.getReaded()+"");
            message.setExtInfo(jsonObject.optString("extInfo", ""));
//            LogUtil.print(TAG,message.getExtInfo()+"");
            message.setIdx(Integer.parseInt(jsonObject.optString("idx")));
//            LogUtil.print(TAG,message.getIdx()+"");
            message.setTitle(jsonObject2.optString("titleText"));
//            LogUtil.print(TAG,message.getTitle()+"");
            message.setContent(jsonObject2.optString("titleClickUrl"));
//            LogUtil.print(TAG,message.getContent()+"");
        } catch (JSONException e) {
            e.printStackTrace();
//            LogUtil.print(TAG,"错误");
        }

        return message;
    }

    public int getIdx() {
        return idx;
    }

    public Message setIdx(int idx) {
        this.idx = idx;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTop() {
        return top;
    }

    public Message setTop(boolean top) {
        this.top = top ? 1 : 0;
        return this;
    }

    public int getReaded() {
        return readed;
    }

    public Message setReaded(boolean readed) {
        this.readed = readed ? 1 : 0;
        return this;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        long time = 0;
        try {
            Date date = parseFormat.parse(msgTime);
            time = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.msgTime = time;
    }

    public void setLongMsgTime(long time) {
        this.msgTime = time;
    }

    public String getMsgFormatTime() {
        return getFormatTime(this.msgTime);
    }

    private static final SimpleDateFormat parseFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final SimpleDateFormat formatFormat = new SimpleDateFormat("yyyy/MM/dd " +
            "HH:mm:ss");

    private String getFormatTime(long msgTime) {
        String time = formatFormat.format(new Date(msgTime));
        return time;
    }

    public String getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }

    public String getMsgType() {
        return msgType.name();
    }

    public String getMsgTypeChinese() {
        return msgType.getChineseValue();
    }

    public void setMsgType(String msgType) {
        this.msgType = MSG_TYPE.valueOf(msgType);
    }

    public String getContentType() {
        return contentType.name();
    }

    public void setContentType(String name) {
        try {
            this.contentType = CONTENT_TYPE.valueOf(name);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            this.contentType = CONTENT_TYPE.TEXT;
        }
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public enum CONTENT_TYPE {
        HTML, TEXT, URL, CLICK_URL;

        public static CONTENT_TYPE valueOf(int index) {
            return values()[index];
        }
    }

    public enum MSG_TYPE {
        Publish("系统公告", 0),
        Trade("交易通知", 1),
        Business("业务通知", 2),
        INDEX("首页消息"),
        NULL;
        private String chineseValue;
        private int index;

        MSG_TYPE(String chineseValue) {
            this.chineseValue = chineseValue;
        }

        MSG_TYPE() {
        }

        MSG_TYPE(String chineseValue, int index) {
            this.index = index;
            this.chineseValue = chineseValue;
        }

        public int getIndex() {
            return index;
        }

        public String getChineseValue() {
            return chineseValue;
        }

        public static MSG_TYPE valueOf(int index) {
            return values()[index];
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeInt(this.top);
        dest.writeInt(this.readed);
        dest.writeString(this.content);
        dest.writeLong(this.msgTime);
        dest.writeString(this.extInfo);
        dest.writeInt(this.msgType == null ? -1 : this.msgType.ordinal());
        dest.writeInt(this.contentType == null ? -1 : this.contentType.ordinal());
    }

    public Message() {
    }

    protected Message(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.top = in.readInt();
        this.readed = in.readInt();
        this.content = in.readString();
        this.msgTime = in.readLong();
        this.extInfo = in.readString();
        int tmpMsgType = in.readInt();
        this.msgType = tmpMsgType == -1 ? null : MSG_TYPE.values()[tmpMsgType];
        int tmpContentType = in.readInt();
        this.contentType = tmpContentType == -1 ? null : CONTENT_TYPE.values()[tmpContentType];
    }

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
