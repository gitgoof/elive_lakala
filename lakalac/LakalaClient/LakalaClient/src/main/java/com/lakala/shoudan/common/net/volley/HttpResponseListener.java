package com.lakala.shoudan.common.net.volley;


import android.support.annotation.Nullable;

import com.lakala.platform.response.HttpConnectEvent;

import org.json.JSONObject;

/**
 * Created by More on 15/9/2.
 */
public interface HttpResponseListener {

    abstract void onStart();

    abstract void onFinished(ReturnHeader returnHeader,@Nullable JSONObject responseData);

    abstract void onErrorResponse();

}
