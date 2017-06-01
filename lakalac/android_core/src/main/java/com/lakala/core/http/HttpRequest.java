package com.lakala.core.http;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.lakala.library.exception.BaseException;
import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.StringUtil;
import com.loopj.lakala.http.AsyncHttpClient;
import com.loopj.lakala.http.PersistentCookieStore;
import com.loopj.lakala.http.RequestHandle;

import org.apache.http.Header;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.protocol.HttpContext;

import java.util.Map;

/**
 * Created by Michael on 14-10-14.
 */
public class HttpRequest {

    public static enum RequestMethod{
        GET,
        POST,
        PUT,
        DELETE
    }

    public static class HttpClient extends AsyncHttpClient {

        private static HttpClient httpClient;

        public static HttpClient getInstance(Context context){
            if(httpClient==null){
                httpClient=new HttpClient(context);
            }
            return httpClient;
        }
        public HttpClient(Context context) {
            super(context);
        }
    }

    //请求URL
    protected String              requestURL         = null;
    //异步请求代理
    protected HttpClient          httpClient         = null;
    //请求参数
    protected HttpRequestParams   requestParams      = null;
    //请求解析器
    protected HttpResponseHandler responseHandler    = null;
    //请求handle
    protected RequestHandle       requestHandle      = null;
    //events
    protected IHttpRequestEvents  events             = null;
    //request method
    protected RequestMethod       requestMethod      = RequestMethod.GET;
    //缓存规则名称
    protected String              cacheRuleName      = null;
    //http cache
    protected HttpCache           httpCache          = null;
    //Tag 值
    protected String              tag                = null;
    //是否强制发
    protected boolean             isForceSendRequest = false;
    //context
    protected Context             context            = null;

    private ERequestDataType requestDataType = ERequestDataType.JSON;//设置默认请求类型是json

    public HttpRequest(Context context){
        this(new DefaultHttpResponseHandler(), context);
    }

    public HttpRequest(HttpResponseHandler responseHandler, Context context){
        this.requestParams   = new HttpRequestParams();
        this.httpClient      = HttpClient.getInstance(context);
        this.responseHandler = responseHandler;
        this.context         = context;
    }

    public void setRequestDataType(ERequestDataType eRequestDataType){
        this.requestDataType = eRequestDataType;
    }

    ERequestDataType getRequestDataType(){
        return this.requestDataType;
    }

    /**
     * 设置请求方法
     *
     * @param requestMethod
     */
    public HttpRequest setRequestMethod(RequestMethod requestMethod){
        this.requestMethod = requestMethod;
        return this;
    }

    /**
     * 设置请求URL
     *
     * @param requestURL
     */
    public HttpRequest setRequestURL(String requestURL){
        this.requestURL = requestURL;
        return this;
    }

    /**
     * 获取请求URL
     *
     * @return
     */
    public String getRequestURL(){
        return this.requestURL;
    }

    /**
     * 设置请求events
     *
     * @param events
     */
    public HttpRequest setIHttpRequestEvents(IHttpRequestEvents events){
        this.events = events;
        return this;
    }

    /**
     * 设置超时时间
     *
     * @param timeout
     * @return
     */
    public HttpRequest setTimeout(int timeout){
        if(httpClient == null){
            return this;
        }
        httpClient.setTimeout(timeout);
        return this;
    }

    /**
     * 设置缓存规则名字
     *
     * @param cacheRuleName
     */
    public HttpRequest setCacheRuleName(String cacheRuleName){
        this.cacheRuleName = cacheRuleName;
        return this;
    }

    /**
     * 获取缓存规则名字
     *
     * @return
     */
    protected String getCacheRuleName(){
        return this.cacheRuleName;
    }

    /**
     * 设置ResponseHandler
     *
     * @param responseHandler
     */
    public HttpRequest setResponseHandler(HttpResponseHandler responseHandler){
        this.responseHandler = responseHandler;
        return this;
    }

    /**
     * get current RequestParams
     *
     * @return
     */
    public HttpRequestParams getRequestParams (){return requestParams;}

    /**
     * get current ResponseHandler
     *
     * @return
     */
    public HttpResponseHandler getResponseHandler (){return responseHandler;}


    /**
     * get current context
     *
     * @return
     */
    public Context getContext(){
        return context;
    }
    /**
     * 获取 Tag
     * @return Tag
     */
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * 是否强制发送 http 请求。当启用缓存时有效，如果该属性为 true，则无论缓存数据是否过期都发送 Http 请求。
     * @return boolean
     */
    public boolean isForceSendRequest() {
        return isForceSendRequest;
    }

    /**
     * 是否强制发送 http 请求。当启用缓存时有效，如果该属性为 true，则无论缓存数据是否过期都发送 Http 请求。
     * @param isForceSendRequest 是否强制发送
     */
    public HttpRequest setForceSendRequest(boolean isForceSendRequest) {
        this.isForceSendRequest = isForceSendRequest;
        return this;
    }

    /**
     * execute request
     */
    public HttpRequest execute(){
        if(requestMethod == null){
            requestMethod = RequestMethod.GET;
        }

        //初始化 ResponseHandler
        responseHandler.setHttpRequest(this);

        //调用 onExecuteBefore 内部事件
        onExecuteBefore();

        if (requestURL == null || requestURL.isEmpty()){
            LogUtil.e("HttpRequest","url can not empty!");
            return this;
        }

        //如果设置了缓存规则名称，则从配置文件中读取缓存规则，然后尝试从缓存中读取数据。
        if(!canReadCache()){
            executeRealRequest();
        }
        else{
            //读取缓存
            readCache();
        }

        return this;
    }

    /**
     * cancel request
     */
    public HttpRequest cancel(final boolean mayInterruptIfRunning){

        if (requestHandle == null ||
                requestHandle.isCancelled() ||
                requestHandle.isFinished()) {
            return this;
        }

        Runnable r = new Runnable() {
            @Override
            public void run() {
                requestHandle.cancel(mayInterruptIfRunning);
            }
        };

        if (Looper.myLooper() == Looper.getMainLooper()) {
            new Thread(r).start();
        } else {
            r.run();
        }

        return this;
    }

    /**
     * 当前request是否完成
     * @return
     */
    public boolean isFinished(){
        return requestHandle == null || requestHandle.isFinished();
    }

    /**
     * 当前request是否取消
     * @return
     */
    public boolean isCancel(){
        return requestHandle == null || requestHandle.isCancelled();
    }

    /**
     * 判断缓存的数据是否已经过期，该属性只有在应答数据来自缓存时有效。
     * @return 如果缓存的数据已经过期则返回 true，如果没有启动缓存则返回 true，否则返回 false。
     */
    public boolean cacheExpired(){
        if (httpCache != null){
            return httpCache.isExpired();
        }
        else{
            return true;
        }
    }

    /**
     * 判断应答数据是否来自缓存
     * @return 如果应答数据来自缓存则返回 true。
     */
    public boolean responseFromCache(){
        if (responseHandler != null){
            return responseHandler.isFromCache();
        }
        else{
            return false;
        }
    }

    /**
     * 如果数据来自文件缓存，则用该方法获取缓存文件的路径（注意不保证缓存文件真实存在），否则返回 null;
     * @return 缓存文件路径
     */
    public String getCacheFilePath(){
        if (httpCache != null){
            return httpCache.getCacheFilePath();
        }
        else{
            return null;
        }
    }


    /**
     * Sets headers that will be added to all requests this client makes (before sending).
     *
     * @param header the name of the header
     * @param value  the contents of the header
     */
    public HttpRequest setHeader(String header, String value){
        httpClient.addHeader(header, value);
        return this;
    }

    public HttpRequest removeHeader(String header) {
        httpClient.removeHeader(header);
        return this;
    }


    /**
     * 获取CookieStore
     *
     * @return
     */
    public PersistentCookieStore getCookieStore(Context context) {

        HttpContext httpContext = httpClient.getHttpContext();
        PersistentCookieStore cookieStore = (PersistentCookieStore) httpContext.getAttribute(ClientContext.COOKIE_STORE);

        if (cookieStore == null) {
            cookieStore = new PersistentCookieStore(context);
            setCookieStore(cookieStore);
        }

        return cookieStore;
    }

    /**
     * 设置cookiestore
     *
     * @param cookieStore
     */
    public void setCookieStore(PersistentCookieStore cookieStore) {

        HttpContext httpContext = httpClient.getHttpContext();
        PersistentCookieStore persistentCookieStore = (PersistentCookieStore) httpContext.getAttribute(ClientContext.COOKIE_STORE);
        if (persistentCookieStore == null) {
            httpClient.setCookieStore(cookieStore);
        }
    }

    /**
     * 读取缓存
     */
    protected void readCache(){
        //读取缓存数据
        byte[] data = onReadCache();

        if (data == null || httpCache == null){
            //没有缓存数据或读取失败，则继续发送 http 请求。
            executeRealRequest();
        }
        else{
            //从缓存中成功的读取到了数据，现在发送成功事件
            responseHandler.onSuccess(200, new Header[0], data);

            //现在检查返回的是否是过期数据或者是否要强更新缓存，如果是则继续发送 http 请求，获取新数据。
            //否则发送完成事件
            if (httpCache.isExpired() || isForceSendRequest()){
                executeRealRequest();
            }
            else{
                responseHandler.onFinish();
            }
        }
    }

    /**
     * 是否可以读取缓存
     *
     * @return
     */
    private boolean canReadCache(){
        return !StringUtil.isEmpty(cacheRuleName) && context != null;
    }

    /**
     * 执行真实的request
     */
    private void executeRealRequest(){

        if (httpClient != null && responseHandler != null){
            httpClient.addHeader("Accept",responseHandler.getAccept());
        }

        //添加 cookie  去掉 by 雷佳健
//        String cookie = CookieManager.getInstance().getCookieString(requestURL);
//        if (cookie != null){
//            httpClient.addHeader("Cookie",cookie);
//        }

        switch (requestMethod){
            case GET:
                requestHandle = get();
                break;
            case POST:
                requestHandle = post();
                break;
            case PUT:
                requestHandle = put();
                break;
            case DELETE:
                requestHandle =  delete();
                break;
            default:
                requestHandle = null;
                break;
        }
    }

    /**
     * post method
     *
     * @return RequestHandle is used to cancel this request
     */
    private RequestHandle post(){

        if(httpClient == null){
            return null;
        }

        return httpClient.post(requestURL, requestParams, responseHandler);
    }

    /**
     * get method
     *
     * @return RequestHandle is used to cancel this request
     */
    private RequestHandle get(){

        if(httpClient == null){
            return null;
        }

        return httpClient.get(requestURL, requestParams, responseHandler);
    }

    /**
     * put method
     * @return
     */
    private RequestHandle put(){
        if(httpClient == null){
            return null;
        }
        return httpClient.put(requestURL, requestParams, responseHandler);
    }


    /**
     * put method
     * @return
     */
    private RequestHandle delete(){
        if(httpClient == null){
            return null;
        }
        return httpClient.delete(null, requestURL, null, requestParams, responseHandler);
    }

    //---------------内部事件---------------
    protected void onExecuteBefore(){
        //供子类重写生
    }

    protected byte[] onReadCache(){
        httpCache = new HttpCache(this);
        return httpCache.readCache(cacheRuleName);
    }

    protected void onWriteCache(){
        if(httpCache != null){
            httpCache.cache();
        }
    }

    //---------------代理事件---------------
    /**
     * Fired when the request progress, override to handle in your own code
     *
     * @param bytesWritten offset from start of file
     * @param totalSize    total size of file
     */
    public void onProgress(int bytesWritten, int totalSize) {
        if(events == null) {
            return;
        }
        events.onProgress(this,bytesWritten, totalSize);
    }

    /**
     * Fired when the request is started, override to handle in your own code
     */
    public void onStart() {
        if(events == null) {
            return;
        }
        events.onStart(this);
    }

    /**
     * Fired in all cases when the request is finished, after both success and failure, override to
     * handle in your own code
     */
    public void onFinish() {
        if(events == null) {
            return;
        }
        events.onFinish(this);
    }

    /**
     * Fired when a request returns successfully, override to handle in your own code
     */
    public void onSuccess() {
        if(events == null) {
            return;
        }
        //如果设置了缓存规则，则根据相关规则进行缓存
        if(responseHandler != null && !responseHandler.isFromCache()){
            onWriteCache();
        }

        events.onSuccess(this);
    }

    /**
     * Fired when a request fails to complete, override to handle in your own code
     */
    public void onFailure(BaseException error) {
        if(events == null) {
            return;
        }
        events.onFailure(this, error);
    }

    /**
     * Fired when a retry occurs, override to handle in your own code
     *
     * @param retryNo number of retry
     */
    public void onRetry(int retryNo) {

    }

    /**
     * Fired when this request is cancel, override to handle in your own code
     */
    public void onCancel() {
        if(events == null) {
            return;
        }
        events.onCancel(this);
    }
}
