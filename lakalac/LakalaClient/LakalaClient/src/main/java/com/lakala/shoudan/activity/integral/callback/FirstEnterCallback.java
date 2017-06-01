package com.lakala.shoudan.activity.integral.callback;

import android.content.Context;
import android.support.annotation.Nullable;

import com.lakala.library.util.ToastUtil;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by linmq on 2016/6/13.
 */
public abstract class FirstEnterCallback implements ServiceResultCallback {
    private Context context;

    public FirstEnterCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onSuccess(ResultServices resultServices) {
        Boolean isFirst = null;
        if(resultServices.isRetCodeSuccess()){
            try {
                JSONObject jsonObject = new JSONObject(resultServices.retData);
                String name = "firstEnter";
                if(jsonObject.has(name)){
                    isFirst = jsonObject.optBoolean(name);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            ToastUtil.toast(context, resultServices.retMsg);
        }
        firstEnterCallback(isFirst);
    }

    /**
     *
     * @param firstEnter true:首页进入，false:非首次进入，null:网络异常
     */
    public abstract void firstEnterCallback(@Nullable Boolean firstEnter);
}
