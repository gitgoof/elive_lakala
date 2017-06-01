package com.lakala.shoudan.common;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.lakala.library.encryption.Digest;
import com.lakala.library.encryption.Mac;
import com.lakala.library.util.DeviceUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.CommonEncrypt;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.shoudan.activity.wallet.request.WalletMacRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by huangjp on 2016/5/18.
 */
public class CommonBaseRequest implements Serializable {
    private String platform = "android";
    private String timeStamp = new Date().getTime() + "";
    private String gesturePwd = ApplicationEx.getInstance().getUser().isExistGesturePassword() ?
            "1" : "0";
    private String guid;
    private String deviceId;
    private String deviceModel;
    private String subChannelId = "10000027";
    private String accessToken = ApplicationEx.getInstance().getSession().getUser().getAccessToken();
    private String refreshToken = ApplicationEx.getInstance().getSession().getUser()
            .getRefreshToken();
    private String telecode = TerminalKey.getTelecode();

    public String getTelecode() {
        return telecode;
    }

    public CommonBaseRequest setTelecode(String telecode) {
        this.telecode = telecode;
        return this;
    }

    public CommonBaseRequest(Context context) {
        String deviceId = DeviceUtil.getDeviceId(context);
        Calendar calendar = Calendar.getInstance();

        //deviceid 年月日时分秒 5位随机数
        String guid = String.format("%s%tY%tm%td%tH%tM%tS%s",
                deviceId,
                calendar,
                calendar,
                calendar,
                calendar,
                calendar,
                calendar,
                StringUtil.getRandom(5)
        );
        String md5Value = Digest.md5(guid);
        this.guid = md5Value;
        this.deviceId = deviceId;
        this.deviceModel = Build.MODEL;
    }
    public WalletMacRequest createMacRequest(){
        JSONObject requestParams = new JSONObject();
        WalletMacRequest macRequest = new WalletMacRequest();
        String rnd = getMacRnd();
        String chntype = getChntype();
        String termid = getTermId();
        if(!TextUtils.isEmpty(rnd)){
            macRequest.setRnd(rnd);
        }
        if(!TextUtils.isEmpty(chntype)){
            macRequest.setChntype(chntype);
        }
        if(!TextUtils.isEmpty(termid)){
            macRequest.setTermid(termid);
        }
        if (!isNeedRnd()){
            macRequest.setRnd(null);
        }
        try {
            addToJSON(macRequest.getJSONObject(), requestParams);
            JSONObject obj = getJSONObject();
            addToJSON(obj,requestParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String macString = Mac.resolveMacData(requestParams)[0];
        LogUtil.print("mab is " + macString);
        String ksn = macRequest.getTermid();
        String mac = CommonEncrypt.generateMac(ksn, macString);
        LogUtil.print("tpkKey is " + TerminalKey.getMasterKey(ksn) + " and macKey is " + TerminalKey.getMacKey(ksn));

        if (StringUtil.isNotEmpty(mac) && mac.length() > 8) {
            mac = mac.substring(0, 8);
            macRequest.setMac(mac);
        }

        return macRequest;
    }
    private void addToJSON(JSONObject inputObject,JSONObject retJSONObject) throws JSONException {
        Iterator<String> keys = inputObject.keys();
        while(keys.hasNext()){
            String key = keys.next();
            Object value = inputObject.opt(key);
            retJSONObject.put(key,value);
        }
    }

    public JSONObject getJSONObject(){
        Field[] fields = getClass().getDeclaredFields();
        Field[] superFields = getClass().getSuperclass().getDeclaredFields();
        JSONObject jsonObject = new JSONObject();
        List<Field> fieldList = new ArrayList<Field>();
        fieldList.addAll(Arrays.asList(fields));
        fieldList.addAll(Arrays.asList(superFields));

        for(Field field : fieldList){
            String name = field.getName();
            field.setAccessible(true);
            try {
                Object obj = field.get(this);
                jsonObject.put(name,obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonObject;

    }

    public String getPlatform() {
        return platform;
    }

    public CommonBaseRequest setPlatform(String platform) {
        this.platform = platform;
        return this;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public CommonBaseRequest setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public String getGesturePwd() {
        return gesturePwd;
    }

    public CommonBaseRequest setGesturePwd(String gesturePwd) {
        this.gesturePwd = gesturePwd;
        return this;
    }

    public String getGuid() {
        return guid;
    }

    public CommonBaseRequest setGuid(String guid) {
        this.guid = guid;
        return this;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public CommonBaseRequest setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public CommonBaseRequest setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
        return this;
    }

    public String getSubChannelId() {
        return subChannelId;
    }

    public CommonBaseRequest setSubChannelId(String subChannelId) {
        this.subChannelId = subChannelId;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public CommonBaseRequest setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public CommonBaseRequest setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    protected String getChntype(){
        return null;
    }
    protected String getMacRnd(){
        return null;
    }

    protected String getTermId(){
        return null;
    }
    public boolean isNeedMac(){
        return false;
    }
    public boolean isNeedRnd(){
        return true;
    }
}
