package com.lakala.core.cache;

import com.lakala.library.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bob on 30/12/13.
 */
public class CacheRule {
    public enum ECacheUpdateMode{
        CacheUpdateModeByVersion,
        CacheUpdateModeByDate,
        CacheUpdateModeByVersionAndDate,
        CacheUpdateModeEveryTime,
        CacheUpdateModeNever
    }


    public ECacheUpdateMode updateMode;
    private boolean isReturnExpire;
    private int validDays;
    private int version;
    private List<String> keyFields;


    public static CacheRule createCacheRule(String jsonString){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            return null;
        }
        return createCacheRule(jsonObject);
    }

    public static CacheRule createCacheRule(JSONObject jsonObject){
        CacheRule retCachRule = new CacheRule();

        String mode;
        try{
            mode= jsonObject.getString("updateMode");
        } catch (JSONException e) {
           return null;
        }

        if (StringUtil.isEmpty(mode)){
            return null;
        }
        //缓存数据的更新模式 (Version,Date,VersionAndDate,EveryTime,Never)
        try{
            retCachRule.isReturnExpire = jsonObject.getBoolean("isReturnExpire");

            if ("Version".equals(mode)){
                retCachRule.updateMode = ECacheUpdateMode.CacheUpdateModeByVersionAndDate;
                retCachRule.version = jsonObject.getInt("version");
            }else if ("Date".equals(mode)){
                retCachRule.updateMode = ECacheUpdateMode.CacheUpdateModeByDate;
                retCachRule.validDays = jsonObject.getInt("validDays");
            }else if ("VersionAndDate".equals(mode)){
                retCachRule.updateMode = ECacheUpdateMode.CacheUpdateModeByVersionAndDate;
                retCachRule.version = jsonObject.getInt("version");
                retCachRule.validDays = jsonObject.getInt("validDays");
            }else if ("EveryTime".equals(mode)){
                retCachRule.updateMode = ECacheUpdateMode.CacheUpdateModeEveryTime;
                retCachRule.version = jsonObject.getInt("version");
                retCachRule.validDays = jsonObject.getInt("validDays");
            }else if ("Never".equals(mode)){
                retCachRule.updateMode = ECacheUpdateMode.CacheUpdateModeNever;
                retCachRule.version = jsonObject.getInt("version");
                retCachRule.validDays = jsonObject.getInt("validDays");
            }

            JSONArray jsonarrayKeyFiles = jsonObject.getJSONArray("keyFields");
            for(int i=0; i<jsonarrayKeyFiles.length(); i++){
                String key =(String)jsonarrayKeyFiles.get(i);
                retCachRule.keyFields = new ArrayList<String>();
                retCachRule.keyFields.add(key);
            }

        } catch (JSONException e) {
           return null;
        }

        return retCachRule;
    }

    public CacheRule(){

    }

    public CacheRule(ECacheUpdateMode mode,
                     boolean isReturnExpire,
                     int validDays,
                     int version){
        this.updateMode = mode;
        this.isReturnExpire = isReturnExpire;
        this.validDays = validDays;
        this.version = version;
    }

    public CacheRule(ECacheUpdateMode mode,
                     boolean isReturnExpire,
                     int validDays){
        this.updateMode = mode;
        this.isReturnExpire = isReturnExpire;
        this.validDays = validDays;
        this.version = -1;
    }

    public boolean getIsReturnExpire(){
        return this.isReturnExpire;
    }

    public int getVersion(){
        return this.version;
    }

    public int getValidDays(){
        return this.validDays;
    }

    public List<String> getKeyFields(){
        return this.keyFields;
    }
}
