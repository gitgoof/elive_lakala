package com.lakala.shoudan.activity.shoudan.finance.bean;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by LMQ on 2015/10/10.
 */
public class QryBusiBankReturnData {
    public static QryBusiBankReturnData parse(JSONObject jsonObject){
        return JSON.parseObject(jsonObject.toString(),QryBusiBankReturnData.class);
    }
    private List<Bank> List;

    public java.util.List<Bank> getList() {
        return List;
    }

    public QryBusiBankReturnData setList(java.util.List<Bank> list) {
        List = list;
        return this;
    }
}
