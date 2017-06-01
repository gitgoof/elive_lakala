package com.lakala.shoudan.activity.integral.callback;

import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.datadefine.CashRecordList;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by linmq on 2016/6/14.
 */
public abstract class CashRecordListCallback implements ServiceResultCallback {
    @Override
    public void onSuccess(ResultServices resultServices) {
        CashRecordList cashRecordList = null;
        if(resultServices.isRetCodeSuccess()){
            try {
                JSONObject jsonObject = new JSONObject(resultServices.retData);
                if(jsonObject != null){
                    cashRecordList = CashRecordList.obtain(jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        onResult(resultServices,cashRecordList);
    }
    public abstract void onResult(ResultServices resultServices, CashRecordList cashRecordList);
}
