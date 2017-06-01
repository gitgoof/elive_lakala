package com.lakala.core.http;

import com.lakala.library.exception.HttpException;
import com.loopj.lakala.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.client.HttpResponseException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLPeerUnverifiedException;

/**
 * Created by Michael on 14-10-14.
 */
public abstract class HttpResponseHandler extends AsyncHttpResponseHandler {
    public static final int RESPONSE_DATA_TYPE_DEFAULT = 0;

    protected String resultCode    = "";
    protected String resultMessage = "";
    protected Object resultData    = null;

    protected int responseDataType = RESPONSE_DATA_TYPE_DEFAULT;
    protected HttpRequest request  = null;

    protected Map<String,Header> headers     = new HashMap<String,Header>();
    protected int statusCode       = 0;
    protected byte[] responseBody  = null;

    //Response 数据是否来自缓存。
    protected boolean isFromCache  = false;

    /**
     * 获取交易状态码
     * @return 交易状态码
     */
    public String getResultCode() { return resultCode; }

    /**
     * 获取错误消息文本
     * @return 消息文本
     */
    public String getResultMessage() { return resultMessage; }

    /**
     * 获取数据
     * @return
     */
    public Object getResultData() { return resultData; }

    /**
     * 设置 Response 数据类型
     * @param type
     */
    public void setResponseDataType(int type) {
        responseDataType = type;
    }
    /**
     * 获取 Response 数据类型
     * @return
     */
    public int  getResponseDataType() {
        return responseDataType;
    }

    public void setHttpRequest(HttpRequest request){
        this.request = request;
    }

    public int getStatusCode(){
        return statusCode;
    }

    public Map<String,Header> getHeaders(){
        return headers;
    }

    public byte[] getResponseBody(){
        return responseBody;
    }

    /**
     * 判断响应数据是否是来自缓存。
     * @return 如果数据来自缓存则返回 true。
     */
    public boolean isFromCache() {
        return isFromCache;
    }

    /**
     * 返回 Resonse 可以接受的数据类型
     * @return
     */
    public String getAccept() {
        return "application/json";
    }

    //-----------------------------------------------------------------------
    @Override
    public void onProgress(int bytesWritten, int totalSize) {
        if (request != null){
            request.onProgress(bytesWritten, totalSize);
        }
    }

    @Override
    public void onStart() {
        if (request != null){
            request.onStart();
        }
    }

    /**
     * 复盖此方法需要调用超类方法，如果不调用超类方法需要在你的重写方法中调用 setCookie() 方法;
     * @param statusCode   the status code of the response
     * @param headers      return headers, if any
     * @param responseBody the body of the HTTP response from the server
     */
    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
        setHeaders(headers);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.isFromCache = false;

        setCookie();

        if (request != null){
            request.onSuccess();
        }
    }

    public void onReadCacheSuccess(int statusCode, Header[] headers, byte[] responseBody){
        setHeaders(headers);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.isFromCache = true;

        if (request != null){
            request.onSuccess();
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
        setHeaders(headers);
        this.statusCode = statusCode;
        this.responseBody = responseBody;

        HttpException exception;
        String message;

        if (error instanceof HttpResponseException){
            //http 应答错误，如 404，505
            this.statusCode = statusCode;
            message = error.getLocalizedMessage();
        }
        else if (error instanceof HttpHostConnectException){
            //连接主机失败
            this.statusCode = HttpException.ERRCODE_REQUEST_CANNOT_CONNECT_TO_HOST;
            message = error.getLocalizedMessage();
        }
        else if (error instanceof ConnectTimeoutException){
            //连接超时（此时并未发送数据）
            this.statusCode = HttpException.ERRCODE_REQUEST_TIMEOUT;
            message = error.getLocalizedMessage();
        }
        else if (error instanceof SocketTimeoutException){
            //Socket 超时，此时数据已经发送但服务器未应答。
            this.statusCode = HttpException.ERRCODE_REQUEST_TIMEOUT;
            message = error.getLocalizedMessage();
        }
        else if (error instanceof SSLPeerUnverifiedException){
            //证书错误。
            this.statusCode = HttpException.ERRCODE_REQUEST_CERTIFICATE_INVAILD;
            message = error.getLocalizedMessage();
        }
        else{
            //其它错误
            this.statusCode = HttpException.ERRCODE_REQUEST_UNKNOWN_ERROR;
            if (error != null){
                message = error.getLocalizedMessage();
            }
            else{
                message = "unknown error";
            }
        }

        if (request != null){
            exception = new HttpException(this.statusCode,"",message,error);
            request.onFailure(exception);
        }
    }

    @Override
    public void onRetry(int retryNo) {
        if (request != null){
            request.onRetry(retryNo);
        }
    }

    @Override
    public void onFinish() {
        if(request != null){
            request.onFinish();
        }
    }

    /**
     * Attempts to encode response bytes as string of set encoding
     *
     * @param charset     charset to create string with
     * @param stringBytes response bytes
     * @return String of set encoding or null
     */
    public static String getResponseString(byte[] stringBytes, String charset) {
        try {
            return stringBytes == null ? null : new String(stringBytes, charset);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    protected void setCookie(){
        Header header = headers.get("Set-Cookie");
        if (header != null){
            CookieManager.getInstance().setCookie(request.getRequestURL(),header.getValue());
        }
    }

    protected void setHeaders(Header[] headers){
        if (headers != null){
            for (Header header : headers){
                this.headers.put(header.getName(),header);
            }
        }
    }
}