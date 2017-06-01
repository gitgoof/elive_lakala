package com.lakala.library.net;

import com.loopj.lakala.http.RequestParams;
import com.loopj.lakala.http.ResponseHandlerInterface;

import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Michael on 14-10-14.
 */
public class HttpRequestParams extends RequestParams {

    public final static String APPLICATION_IMAGE =
            "image/jpeg";

    private boolean useJsonString;

    public HttpRequestParams(){
        this(true);
    }

    public HttpRequestParams(boolean useJsonString){
        super();
        this.useJsonString = useJsonString;
    }

    public HttpRequestParams(boolean useJsonString, Object... keysAndValues){
        super(keysAndValues);
        this.useJsonString = useJsonString;
    }

    @Override
    public HttpEntity getEntity(ResponseHandlerInterface progressHandler) throws IOException {
        if(useJsonString && streamParams.isEmpty() && fileParams.isEmpty()){
            return new JsonStringEntity(getParamsList(), HTTP.UTF_8);
        }else {
            return super.getEntity(progressHandler);
        }
    }

    /**
     * 设置是否使用JsonString
     *
     * @param useJsonString
     */
    public void setUseJsonString(boolean useJsonString){
        this.useJsonString = useJsonString;
    }

    /**
     * 根据key获取请求参数中的value
     *
     * @param key 请求key
     * @return
     */
    public Object get(String key){
        if(urlParams.containsKey(key)){
            return urlParams.get(key);
        }else if(streamParams.containsKey(key)){
            return streamParams.get(key);
        }else if(fileParams.containsKey(key)){
            return fileParams.get(key);
        }else if(urlParamsWithObjects.containsKey(key)){
            return urlParamsWithObjects.get(key);
        }

        return null;
    }

    /**
     * 获取请求参数列表，这个列表仅包含字符串类型参数。
     * @return 参数列表
     */
    public List<BasicNameValuePair> getUrlParams(){
        List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();

        for (ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
            lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        return lparams;
    }
}
