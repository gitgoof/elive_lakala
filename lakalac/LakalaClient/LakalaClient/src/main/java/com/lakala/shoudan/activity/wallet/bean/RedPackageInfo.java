package com.lakala.shoudan.activity.wallet.bean;

import android.text.Html;
import android.text.Spanned;

import com.lakala.shoudan.common.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengxuan on 2015/12/23.
 */
public class RedPackageInfo {

    private String pageSize;
    private String giftNum;
    private List<GiftListEntity> giftList = new ArrayList<GiftListEntity>();

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public void setGiftNum(String giftNum) {
        this.giftNum = giftNum;
    }

    public void setGiftList(List<GiftListEntity> giftList) {
        this.giftList = giftList;
    }

    public String getPageSize() {
        return pageSize;
    }

    public String getGiftNum() {
        return giftNum;
    }

    public List<GiftListEntity> getGiftList() {
        return giftList;
    }

    public static class GiftListEntity {
        private String giftSubAcNo;
        private String giftStartDate;
        private String giftBalance;
        private int mergeFlag;
        private String giftEndDate;
        private String giftName;
        private int giftState;

        public static GiftListEntity parseObject(JSONObject json){
            RedPackageInfo.GiftListEntity entity = new RedPackageInfo.GiftListEntity();
            entity.setGiftSubAcNo(json.optString("giftSubAcNo"));
            entity.setGiftStartDate(json.optString("giftStartDate"));
            entity.setGiftBalance(json.optString("giftBalance"));
            entity.setMergeFlag(json.optInt("mergeFlag",0));
            entity.setGiftEndDate(json.optString("giftEndDate"));
            entity.setGiftName(json.optString("giftName"));
            entity.setGiftState(json.optInt("giftState",0));
            return entity;
        }
        public Spanned getShowSpanned(){
            Double balance = Double.valueOf(getGiftBalance());
            String amount = Util.formatTwo(balance);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<font color=#ff8308>(%s元)</font>").append(getGiftName());
            String htmlText = String.format(stringBuilder.toString(), amount);
            return Html.fromHtml(htmlText);
        }
        public JSONObject getJsonObject(){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("giftSubAcNo",getGiftSubAcNo());
                jsonObject.put("giftStartDate",getGiftStartDate());
                jsonObject.put("giftBalance",getGiftBalance());
                jsonObject.put("mergeFlag",getMergeFlag());
                jsonObject.put("giftEndDate",getGiftEndDate());
                jsonObject.put("giftName",getGiftName());
                jsonObject.put("giftState",getGiftState());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        public void setGiftSubAcNo(String giftSubAcNo) {
            this.giftSubAcNo = giftSubAcNo;
        }

        public void setGiftStartDate(String giftStartDate) {
            this.giftStartDate = giftStartDate;
        }

        public void setGiftBalance(String giftBalance) {
            this.giftBalance = giftBalance;
        }

        public void setMergeFlag(int mergeFlag) {
            this.mergeFlag = mergeFlag;
        }

        public void setGiftEndDate(String giftEndDate) {
            this.giftEndDate = giftEndDate;
        }

        public void setGiftName(String giftName) {
            this.giftName = giftName;
        }

        public void setGiftState(int giftState) {
            this.giftState = giftState;
        }

        public String getGiftSubAcNo() {
            return giftSubAcNo;
        }

        public String getGiftStartDate() {
            return giftStartDate;
        }

        public String getGiftBalance() {
            return giftBalance;
        }

        public int getMergeFlag() {
            return mergeFlag;
        }

        public String getGiftEndDate() {
            return giftEndDate;
        }

        public String getGiftName() {
            return giftName;
        }

        public int getGiftState() {
            return giftState;
        }
    }

    public void paseObject(JSONObject jsonObject) throws JSONException {

        setGiftNum(jsonObject.optString("giftNum"));
        setPageSize(jsonObject.optString("pageSize"));
        List<RedPackageInfo.GiftListEntity> entities = new ArrayList<GiftListEntity>();
        JSONArray jsonArray = jsonObject.getJSONArray("giftList");
        for (int i=0;i<jsonArray.length();i++){
            JSONObject json = jsonArray.getJSONObject(i);
            if(json == null){
                continue;
            }
            entities.add(GiftListEntity.parseObject(json));
        }
        setGiftList(entities);
    }
}
