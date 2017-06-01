package com.lakala.platform.response;

import com.lakala.core.http.HttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by ZhangMY on 2015/2/4.
 */
public abstract class JsonHttpResonseHandler extends HttpResponseHandler{

    public abstract void onSuccess(int statusCode,JSONObject jsonResponse);

    public abstract void onFailure(int statusCode,Header[] header,JSONObject jsonResponse,Throwable error);

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        super.onSuccess(statusCode, headers, responseBody);
        JSONObject jsonObject = null;
        try{
            jsonObject = new JSONObject(getResponseString(responseBody,getCharset()));
        }catch(Exception e){
            e.printStackTrace();
        }

        onSuccess(statusCode,jsonObject);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        super.onFailure(statusCode, headers, responseBody, error);
        JSONObject jsonObject = null;
        try{
            jsonObject = new JSONObject(getResponseString(responseBody,getCharset()));
        }catch(Exception e){
            e.printStackTrace();
        }
        onFailure(statusCode,headers,jsonObject,error);
    }
}
