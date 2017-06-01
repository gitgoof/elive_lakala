package com.lakala.library.net;

import com.lakala.library.util.LogUtil;
import com.loopj.lakala.http.AsyncHttpClient;
import com.loopj.lakala.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Michael on 14-10-14.
 */
public class JsonStringEntity extends StringEntity {

    //JSON CONTENT TYPE
    private static final Header HEADER_JSON_CONTENT =
            new BasicHeader(
                    AsyncHttpClient.HEADER_CONTENT_TYPE,
                    RequestParams.APPLICATION_JSON);

    public JsonStringEntity(String s, String charset) throws UnsupportedEncodingException {
        super(s, charset);
    }

    public JsonStringEntity(String s) throws UnsupportedEncodingException {
        super(s);
    }

    public JsonStringEntity(List<BasicNameValuePair> paramsList, String charset) throws UnsupportedEncodingException {
        this(JsonStringEntity.getJsonStringWithParams(paramsList), charset);
    }

    @Override
    public Header getContentType() {
        return HEADER_JSON_CONTENT;
    }

    @Override
    public Header getContentEncoding() {
        return super.getContentEncoding();
    }

    /**
     * 获取当前参数->JSON String
     *
     * @return
     */
    private static String getJsonStringWithParams(List<BasicNameValuePair> paramsList){

        JSONObject jsonParams = new JSONObject();

        for(BasicNameValuePair nameValuePair : paramsList){

            String key   = nameValuePair.getName();
            String value = nameValuePair.getValue();

            try {
                //to JSONObject
                JSONObject jsonObjectValue = new JSONObject(value);
                jsonParams.put(key,jsonObjectValue);
            } catch (JSONException e) {
                //to JSONArray
                try {
                    JSONArray jsonArray = new JSONArray(value);
                    jsonParams.put(key,jsonArray);
                } catch (JSONException e1) {
                    //没有转换成JSONObject或JSONArray,则传入原始数据
                    try {
                        jsonParams.put(key,value);
                    } catch (JSONException e2) {
                        LogUtil.print("HttpRequest 参数转换为JSON时出错");
                    }
                }
            }
        }

        return jsonParams.toString();
    }
}
