package com.lakala.shoudan.common;

import android.support.annotation.Nullable;

import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.datadefine.OpenBankInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LMQ on 2015/12/1.
 */
public abstract class OpenBankInfoCallback implements ServiceResultCallback {

    @Override
    public void onSuccess(ResultServices resultServices) {
        if(resultServices.isRetCodeSuccess()){
            OpenBankInfo openBankInfo = null;
            String errMsg = null;
            try {
                JSONObject jsonObject = new JSONObject(resultServices.retData);
                if(jsonObject != null){
                    openBankInfo = OpenBankInfo.construct(jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(openBankInfo == null){
                errMsg = "暂不支持此银行，请更换银行卡";
            }
            onSuccess(openBankInfo,errMsg);
        }else{
            onSuccess(null,resultServices.retMsg);
        }
    }
    public abstract void onSuccess(@Nullable OpenBankInfo openBankInfo,@Nullable String errMsg);
}
