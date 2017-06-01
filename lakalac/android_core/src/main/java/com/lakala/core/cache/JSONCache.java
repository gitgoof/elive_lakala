package com.lakala.core.cache;

import android.content.Context;

import com.lakala.core.dao.DBCache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * JSON缓存类,DBCache的基础上做了封装,支持缓存json对象,并支持从缓存获取json对象
 * Created by bob on 26/12/13.
 */
public class JSONCache extends DBCache {
    public JSONCache(Context context) {
        super(context);
        this.setCategory("json");
    }

    /**
     * 缓存json对象数据,如果数据存在则更新
     * @param key     数据key,不能为空
     * @param date    数据过期日期,不填表示永不过期
     * @param json    数据,JSONObject 或者是 JSONArray
     * @return  缓存成功返回true,失败返回false
     */
    public boolean putJSON(CacheKey key, ExpireDate date, Object json){
        if (json == null ||
                (!(json instanceof JSONObject) && !(json instanceof JSONArray))){
            return false;
        }

        return put(key,date,json.toString());
    }

    /**
     * 缓存json对象数据,如果数据存在则更新
     * @param key     数据key,不能为空
     * @param version 数据版本
     * @param json    数据,JSONObject 或者是 JSONArray
     * @return  缓存成功返回true,失败返回false
     */
    public boolean putJSON(CacheKey key,int version, Object json){
        if (json == null ||
                (!(json instanceof JSONObject) && !(json instanceof JSONArray))){
            return false;
        }

        return put(key,version,json.toString());
    }

    /**
     * 缓存json对象数据,如果数据存在则更新
     * @param key     数据key,不能为空
     * @param date    数据过期日期,不填表示永不过期
     * @param version 数据版本
     * @param json    数据,JSONObject 或者是 JSONArray
     * @return  缓存成功返回true,失败返回false
     */
    public boolean putJSON(CacheKey key,ExpireDate date, int version, Object json){
        if (json == null ||
                (!(json instanceof JSONObject) && !(json instanceof JSONArray))){
            return false;
        }

        return put(key, date, version, json.toString());
    }

    /**
     * 从数据库中获取缓存json字符串并转换为json对象
     * 由于JSONObject ,JSONArray是同级关系,返回的对象请自行判定
     * @param key
     * @return JSONObject 或者 JSONArray
     */
    public Object getJSONObject(CacheKey key){
        String jsonString = this.get(key);
        if (jsonString == null){
            return null;
        }

        Object ret = null;
        try{
            ret = new JSONObject(jsonString);
        }catch(JSONException e){
            try{
                ret = new JSONArray(jsonString);
            }catch(JSONException e2){}
        }

        return ret;
    }

}
