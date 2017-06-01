package com.lakala.shoudan.common;

import android.util.Log;

import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.bll.service.shoudan.BaseServiceShoudanResponse;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.datadefine.ShoudanRegisterInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LMQ on 2015/11/26.
 */
public abstract class RegisterCallback implements ServiceResultCallback {
    @Override
    public void onSuccess(ResultServices resultServices) {
        BaseServiceShoudanResponse baseServiceShoudanResponse = new BaseServiceShoudanResponse();
        if (resultServices.isRetCodeSuccess()) {

            try {
                // 成功
                JSONObject retData = new JSONObject(resultServices.retData);

                baseServiceShoudanResponse.setPass(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // 失败
            baseServiceShoudanResponse.setPass(false);
            baseServiceShoudanResponse.setErrMsg(resultServices.retMsg);
        }
        onSuccess(baseServiceShoudanResponse);
    }
    public abstract void onSuccess(BaseServiceShoudanResponse response);
    private ShoudanRegisterInfo unpackMechantInfo(JSONObject retData)
            throws JSONException {
        ShoudanRegisterInfo merchantInfo = new ShoudanRegisterInfo();



        return merchantInfo;
    }
}
