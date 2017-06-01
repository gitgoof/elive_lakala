package com.lakala.shoudan.common.net.volley;

import android.os.Build;
import android.text.TextUtils;

import com.lakala.library.DebugConfig;
import com.lakala.library.encryption.Mac;
import com.lakala.library.jni.LakalaNative;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.CommonEncrypt;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.NeedMacRequest;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.util.AppUtil;
import com.lakala.shoudan.common.util.Util;
import com.loopj.android.http.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by More on 15/8/26.
 */
public class BaseRequest implements Serializable{


    private String _Platform = "android";
    private String _TimeStamp = new Date().getTime() + "";
    private String _GesturePwd= "0";
    private String _Guid = AppUtil.createGuid();
    private String _DeviceId = AppUtil.createDeviceID(ApplicationEx.getInstance());
    private String _DeviceModel = Build.MODEL;
    private String _SubChannelId = "10000027";
    private String telecode = TerminalKey.getTelecode();
    private final String _IsNewMPOS = "1";

    private String _AccessToken = ApplicationEx.getInstance().getSession().getUser().getAccessToken();//createFinanceToken();
    private String _RefreshToken = ApplicationEx.getInstance().getSession().getUser().getRefreshToken();
    public String getTelecode() {
        return telecode;
    }

    public BaseRequest setTelecode(String telecode) {
        this.telecode = telecode;
        return this;
    }


    public String get_RefreshToken() {
        return _RefreshToken;
    }

    public void set_RefreshToken(String _RefreshToken) {
        this._RefreshToken = _RefreshToken;
    }

    public String get_AccessToken() {
        return _AccessToken;
    }

    public void set_AccessToken(String _AccessToken) {
        this._AccessToken = _AccessToken;
    }

    private String Mobile =  ApplicationEx.getInstance().getUser().getLoginName(); //"15101103548";//"15280593993";//

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String get_Platform() {
        return _Platform;
    }

    public void set_Platform(String _Platform) {
        this._Platform = _Platform;
    }

    public String get_TimeStamp() {
        return _TimeStamp;
    }

    public void set_TimeStamp(String _TimeStamp) {
        this._TimeStamp = _TimeStamp;
    }

    public String get_GesturePwd() {
        return _GesturePwd;
    }

    public void set_GesturePwd(String _GesturePwd) {
        this._GesturePwd = _GesturePwd;
    }

    public String get_Guid() {
        return _Guid;
    }

    public void set_Guid(String _Guid) {
        this._Guid = _Guid;
    }

    public String get_DeviceId() {
        return _DeviceId;
    }

    public void set_DeviceId(String _DeviceId) {
        this._DeviceId = _DeviceId;
    }

    public String get_DeviceModel() {
        return _DeviceModel;
    }

    public void set_DeviceModel(String _DeviceModel) {
        this._DeviceModel = _DeviceModel;
    }

    public String get_SubChannelId() {
        return _SubChannelId;
    }

    public void set_SubChannelId(String _SubChannelId) {
        this._SubChannelId = _SubChannelId;
    }

    public NeedMacRequest createMacRequest(){
        JSONObject requestParams = new JSONObject();
        NeedMacRequest macRequest = new NeedMacRequest();
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
        try {
            addToJSON(macRequest.getJSONObject(), requestParams);
            JSONObject obj = getJSONObject();
            addToJSON(obj,requestParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String macString = Mac.resolveMacData(requestParams)[0];
        LogUtil.print(getClass().getName(),"mab:"+macString);
        String ksn = macRequest.getTermid();
        String mac = CommonEncrypt.generateMac(ksn, macString);
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

    /**
     *
     * @return {uid}_{vercode}_{plattype}_{token}_{mobileNum}_{pv}_{platform}_{userId}
     */
    private String createFinanceToken() {

        final String underline = "_";
        StringBuffer financeToken = new StringBuffer();
        financeToken.append(ShoudanService.getInstance().getUid()).append(underline);
        financeToken.append(ShoudanService.getInstance().getVercode()).append(underline);
        financeToken.append("7").append(underline);//7 aposMackey
        financeToken.append(ApplicationEx.getInstance().getUser().getAccessToken()).append(underline);
        financeToken.append(ApplicationEx.getInstance().getUser().getLoginName()).append(underline);
        financeToken.append(Util.getAppVersionCode()).append(underline);
        financeToken.append("ANDROID").append(underline);//upcase
        financeToken.append(ApplicationEx.getInstance().getUser().getUserId());

        return new String(Base64.encode(financeToken.toString().getBytes(), Base64.NO_WRAP));


    }

    public String get_IsNewMPOS() {
        return _IsNewMPOS;
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
}
