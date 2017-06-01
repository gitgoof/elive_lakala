package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.library.encryption.Mac;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.util.Util;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by LMQ on 2015/10/13.
 */
public class NeedMacRequest {
    private String mac;
    private String tdtm = Util.dateForWallet();
    private String series = Util.createSeries();
    private String chntype = "02102";
    private String rnd = Mac.getRnd();
    private String termid;

    public NeedMacRequest() {
        rnd = Mac.getRnd();
        termid = ApplicationEx.getInstance().getUser().getTerminalId();//默认使用虚拟终端号，刷卡时需要设置为刷卡器终端号
//        if(TextUtils.isEmpty(Parameters.swiperNo)){
//            termid = ApplicationEx.getInstance().getUser().getTerminalId();
//        }else{
//            termid = Parameters.swiperNo;
//        }
    }

    public String getMac() {
        return mac;
    }

    public NeedMacRequest setMac(String mac) {
        this.mac = mac;
        return this;
    }

    public String getTdtm() {
        return tdtm;
    }

    public NeedMacRequest setTdtm(String tdtm) {
        this.tdtm = tdtm;
        return this;
    }

    public String getSeries() {
        return series;
    }

    public NeedMacRequest setSeries(String series) {
        this.series = series;
        return this;
    }

    public String getChntype() {
        return chntype;
    }

    public NeedMacRequest setChntype(String chntype) {
        this.chntype = chntype;
        return this;
    }

    public String getRnd() {
        return rnd;
    }

    public NeedMacRequest setRnd(String rnd) {
        this.rnd = rnd;
        return this;
    }

    public String getTermid() {
        return termid;
    }

    public NeedMacRequest setTermid(String termid) {
        this.termid = termid;
        return this;
    }

    public JSONObject getJSONObject() throws Exception {
        Field[] fields = getClass().getDeclaredFields();
        JSONObject jsonObject = new JSONObject();
        List<Field> fieldList = new ArrayList<Field>();
        fieldList.addAll(Arrays.asList(fields));

        for(Field field : fieldList){
            String name = field.getName();
            field.setAccessible(true);
            String key = name.replaceFirst(
                    name.substring(0, 1), name.substring(0, 1).toUpperCase()
            );
            Method m = getClass().getMethod("get"+key);
            Object obj = m.invoke(this);
            jsonObject.put(name,obj);
        }
        return jsonObject;

    }
}
