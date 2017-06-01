package com.lakala.shoudan.activity.shoudan.finance.manager;

import android.database.Cursor;

import com.android.volley.VolleyError;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.TcRequest;
import com.lakala.shoudan.common.net.volley.HttpResponseListener;
import com.lakala.shoudan.common.net.volley.ReturnHeader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LMQ on 2015/10/26.
 */
public class TcSyncManager {

    private FinanceTcDao dbManager;
    private Map<String, String> tcContents;

    public TcSyncManager() {
        initManger();
    }

    /**
     * 获取上送失败的tc相关信息
     */
    public void initManger(){
        dbManager  = FinanceTcDao.getInstance();
        Cursor cursor = dbManager.queryFundTcTable();
        tcContents = new HashMap<>();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex(FinanceTcDao.TableEntity.ID));
            String tcContent = cursor.getString(cursor.getColumnIndex(FinanceTcDao.TableEntity.TC_CONTENT));
            tcContents.put(id, tcContent);
        }
        cursor.close();
    }

    private void deleteFundTcContent(String id){
        if(dbManager == null){
            dbManager = FinanceTcDao.getInstance();
        }
        dbManager.deleteFundTcContent(id);
    }


    public void tcSyncService(){

        if(tcContents.size() == 0){
            return;
        }

        try {

            for (final String key : tcContents.keySet()) {
                String tcContent = tcContents.get(key);
                TcRequest tcRequest = TcRequest.parse(tcContent);
                HttpResponseListener listener = new HttpResponseListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                        deleteFundTcContent(key);
                    }

                    @Override
                    public void onErrorResponse() {

                    }
                };
                FinanceRequestManager.getInstance().asyTransTc(tcRequest,listener);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

    /**
     *
     * tc需要用的数据保存 至数据库中
     *
     */
    public void saveTcInfo(TcRequest tcRequest,String telecode){
        if(tcRequest == null){
            return;
        }
        JSONObject jsonObject = tcRequest.getJSONObject();
        try {
            jsonObject.put("telecode",telecode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dbManager.insertFundTcTable(jsonObject.toString());
    }

    private String checkValue(String value){
        return "".equals(value)? "*" : value;
    }

}
