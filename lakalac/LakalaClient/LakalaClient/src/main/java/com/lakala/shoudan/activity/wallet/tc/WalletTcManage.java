package com.lakala.shoudan.activity.wallet.tc;

import android.app.Activity;
import android.database.Cursor;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.TcRequest;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceTcDao;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.activity.wallet.request.WalletTcRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fengxuan on 2015/12/25.
 * TC管理类，负责保存上送失败的TC信息等
 */
public class WalletTcManage {

    private WalletTcDao dbManager;
    private Map<String, String> tcContents;
    private Activity context;

    public WalletTcManage(Activity context) {
        this.context = context;
        initManage();
    }

    private void initManage(){
        dbManager = WalletTcDao.getInstance();
        Cursor cursor = dbManager.queryWalletTcTable();
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
            dbManager = WalletTcDao.getInstance();
        }
        dbManager.deleteWalletTcContent(id);
    }

    public void tcSyncService(){

        if(tcContents.size() == 0){
            return;
        }

        try {

            for (final String key : tcContents.keySet()) {
                String tcContent = tcContents.get(key);
                TcRequest tcRequest = TcRequest.parse(tcContent);
                BusinessRequest request = RequestFactory.getRequest(context, RequestFactory.Type.UPLOAD_TC);
                WalletTcRequest params = new WalletTcRequest(ApplicationEx.getInstance());

                params.setTcicc55(tcRequest.getTcicc55());
                params.setScpicc55(tcRequest.getScpicc55());
                params.setSrcSid(tcRequest.getSrcSid());
                params.setTcvalue(tcRequest.getTcvalue());

                request.setResponseHandler(new ServiceResultCallback() {
                    @Override
                    public void onSuccess(ResultServices resultServices) {
                        deleteFundTcContent(key);
                    }

                    @Override
                    public void onEvent(HttpConnectEvent connectEvent) {

                    }
                });
                WalletServiceManager.getInstance().start(params,request);
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
}
