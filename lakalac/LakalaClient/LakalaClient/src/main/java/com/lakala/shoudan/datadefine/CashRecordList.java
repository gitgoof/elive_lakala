package com.lakala.shoudan.datadefine;

import com.lakala.shoudan.activity.shoudan.loan.LoanDetailNotLastActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by linmq on 2016/6/14.
 */
public class CashRecordList {
    private int num;
    private int total;
    private int nowp;
    private List<Detail> detailList;
    public static CashRecordList obtain(JSONObject jsonObject){
        CashRecordList cashRecordList = new CashRecordList();
        cashRecordList.num = jsonObject.optInt("num");
        cashRecordList.total = jsonObject.optInt("total");
        cashRecordList.nowp = jsonObject.optInt("nowp");
        JSONArray array = jsonObject.optJSONArray("detailList");
        cashRecordList.detailList = new ArrayList<Detail>();
        if(array != null){
            int length = array.length();
            for(int i = 0;i<length;i++){
                JSONObject detailJson = array.optJSONObject(i);
                if(detailJson == null){
                    continue;
                }
                Detail detail = Detail.obtain(detailJson);
                cashRecordList.detailList.add(detail);
            }
        }
        return cashRecordList;
    }

    public int getNum() {
        return num;
    }

    public CashRecordList setNum(int num) {
        this.num = num;
        return this;
    }

    public int getTotal() {
        return total;
    }

    public CashRecordList setTotal(int total) {
        this.total = total;
        return this;
    }

    public int getNowp() {
        return nowp;
    }

    public CashRecordList setNowp(int nowp) {
        this.nowp = nowp;
        return this;
    }

    public List<Detail> getDetailList() {
        return detailList;
    }

    public CashRecordList setDetailList(
            List<Detail> detailList) {
        this.detailList = detailList;
        return this;
    }

    public static class Detail{
        private String id;
        private String amount;
        private String behaviorTypeId;
        private String createTime;
        private Type type;
        public static Detail obtain(JSONObject jsonObject){
            Detail detail = new Detail();
            detail.id = jsonObject.optString("id");
            detail.amount = jsonObject.optString("amount");
            detail.behaviorTypeId = jsonObject.optString("behaviorTypeId");
            detail.createTime = jsonObject.optString("createTime");
            String value = jsonObject.optString("type");
            detail.type = Type.obatainByValue(value);
            return detail;
        }

        public String getId() {
            return id;
        }

        public Detail setId(String id) {
            this.id = id;
            return this;
        }

        public String getAmount() {
            return amount;
        }

        public Detail setAmount(String amount) {
            this.amount = amount;
            return this;
        }

        public String getBehaviorTypeId() {
            return behaviorTypeId;
        }

        public Detail setBehaviorTypeId(String behaviorTypeId) {
            this.behaviorTypeId = behaviorTypeId;
            return this;
        }

        public String getCreateTime() {
            return createTime;
        }

        public Detail setCreateTime(String createTime) {
            this.createTime = createTime;
            return this;
        }

        public String getType() {
            return type == null?null:type.getValue();
        }

        public Type getTypeEnum(){
            return type;
        }
        public Detail setTypeEnum(Type type){
            this.type = type;
            return this;
        }

        public Detail setType(String type) {
            this.type = Type.obatainByValue(type);
            return this;
        }
    }

    public enum Type{
        TYPE1("0","全部"),
        TYPE2("1","收入"),
        TYPE3("2","支出");
        private static HashMap<String,Type> TYPE_MAP = new HashMap<String, Type>();
        static {
            for(Type type:values()){
                TYPE_MAP.put(type.getValue(),type);
            }
        }
        public static Type obatainByValue(String value){
            return TYPE_MAP.containsKey(value)?TYPE_MAP.get(value):null;
        }
        private String desc;
        private String value;

        Type(String value, String desc) {
            this.desc = desc;
            this.value = value;
        }

        public String getDesc() {
            return desc;
        }

        public String getValue() {
            return value;
        }
    }
}
