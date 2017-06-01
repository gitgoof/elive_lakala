package com.lakala.platform.bean;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangCheng on 2016/8/25.
 */
public class StlmRem {
    private String remStatus;
    private String remAmt;
    private int count;
    private boolean isOpen=false;//记录是否打开二级列表
    private boolean isHave=false;//记录是否已经联网加载数据
    private String title;
    public List<StlmRem2> list=new ArrayList<>();
    private JSONObject jsonObject;

    public void setStlmRem(JSONObject jsonObject){
        remStatus=jsonObject.optString("remStatus");
        remAmt=jsonObject.optString("remAmt");
        count=jsonObject.optInt("count");
    }

    public StlmRem(String title,int no,String num){
        this.title=title;
        this.count=no;
        this.remAmt=num;
    }

    public String getRemStatus() {
        return remStatus;
    }

    public void setRemStatus(String remStatus) {
        this.remStatus = remStatus;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getRemAmt() {
        return remAmt;
    }

    public void setRemAmt(String remAmt) {
        this.remAmt = remAmt;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public boolean isHave() {
        return isHave;
    }

    public void setHave(boolean have) {
        isHave = have;
    }
    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
