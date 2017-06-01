package com.lakala.platform.response;

import com.lakala.core.http.HttpResponseHandler;

import org.apache.http.Header;

/**
 * Created by ZhangMY on 2015/2/4.
 * 将请求而返回的byte数组转成string
 */
public abstract class TextHttpResponseHandler extends HttpResponseHandler{

    public abstract void onSuccess(String response);

    public abstract void onFailure(int statusCodce,Header[] headers,String response,Throwable error);

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        super.onSuccess(statusCode, headers, responseBody);
        onSuccess(getResponseString(responseBody,getCharset()));
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        super.onFailure(statusCode, headers, responseBody, error);
    }

    @Override
    public void onStart() {
        super.onStart();
        //TODO dialog

    }

    @Override
    public void onFinish() {
        super.onFinish();
        //TODO dialog
    }
}
