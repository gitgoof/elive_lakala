package com.lakala.platform.cordovaplugin;

import android.support.v4.app.FragmentActivity;
import android.util.Base64;

import com.lakala.core.cordova.cordova.CallbackContext;
import com.lakala.core.cordova.cordova.CordovaArgs;
import com.lakala.core.cordova.cordova.CordovaInterface;
import com.lakala.core.cordova.cordova.CordovaPlugin;
import com.lakala.core.cordova.cordova.CordovaWebView;
import com.lakala.core.cordova.cordova.PluginResult;
import com.lakala.core.http.DefaultHttpResponseHandler;
import com.lakala.core.http.IHttpRequestEvents;
import com.lakala.library.exception.BaseException;
import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.FileUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.PackageFileManager;
import com.lakala.platform.http.BusinessRequest;
import com.loopj.lakala.http.AsyncHttpClient;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by wangchao on 14/10/21.
 * HttpRequest插件
 * <p/>
 * config:{
 * url:url,
 * method:请求方法类型,get/post/..,
 * forceReqest:强制请求,
 * cacheRuleName:cache规则名,
 * queueIndex:request在queue中的索引,
 * maskMsg:是否显示progressDialog,不为空时显示
 * dataType:type不
 * 传 或 ''，  表示请求的是服务端的接口数据，JSON 数据保存在 resultData 中。
 * 'RemoteFile' 表示从服务器下载一个文本类型的文件，文本数据保存在 responseText中。
 * 'LocalFile'  表示从本地获取一个文本类型的文件，文本数据保存在 responseText中。
 */
public class HttpPlugin extends CordovaPlugin {

    //ACTION
    private static final String ACTION_SEND                 = "request";
    private static final String ACTION_CANCEL               = "cancel";

    //request type
    private static final String METHOD_POST                 = "POST";
    private static final String METHOD_GET                  = "GET";
    private static final String METHOD_PUT                  = "PUT";
    private static final String METHOD_DELETE                  = "DELETE";

    private static final String TYPE_REMOTE                 = "RemoteFile";//请求remote文件
    private static final String TYPE_LOCAL                  = "LocalFile";//请求本地文件
    private static final String TYPE_JSON                   = "RemoteJSON";//请求json
    //URL
    private static final String URL_TYPE_ABS                = "://";
    private static final String URL_TYPE_REL_ROOT           = "/";
    private static final String URL_TYPE_REL_CURRENT        = "";

    //config key
    private static final String CONFIG_METHOD               = "method";
    private static final String CONFIG_URL                  = "url";
    private static final String CONFIG_DATA_TYPE            = "dataType";
    private static final String CONFIG_QUEUE_INDEX          = "queueIndex";
    private static final String CONFIG_AUTO_TOAST           = "autoToast";
    private static final String CONFIG_IS_FULL_SCREEN_MASK  = "isFullScreenMask";
    private static final String CONFIG_MASK_MSG             = "maskMsg";
    private static final String CONFIG_CACHE_RULE_NAME      = "cacheRuleName";
    private static final String CONFIG_FORCE_REQUEST        = "forceRequest";
    private static final String CONFIG_AUTO_PARSE           = "autoParse";

    //context
    private FragmentActivity me;

    //current url
    private String currentUrl;

    private HashMap<String,HttpRequest> httpRequestQueue;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        me = (FragmentActivity) cordova.getActivity();

        httpRequestQueue = new HashMap<String,HttpRequest>();
    }

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        if (action.equalsIgnoreCase(ACTION_SEND)) {
            return sendHttpRequest(args, callbackContext);
        }
        if (action.equalsIgnoreCase(ACTION_CANCEL)) {
            return cancelHttpRequest(args);
        }

        return super.execute(action, args, callbackContext);
    }

    @Override
    public Object onMessage(String id, Object data) {
        if(id.equals("onPageStarted")){
            currentUrl = data.toString();
        }

        return super.onMessage(id, data);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onReset(){
        super.onReset();
    }

    /**
     * 发送HttpRequest
     *
     * @param args
     * @param callbackContext
     * @return
     */
    private boolean sendHttpRequest(CordovaArgs args, CallbackContext callbackContext){
        if(args.isNull(0)){
            return true;
        }

        //Http 请求配置参数
        JSONObject config = args.optJSONObject(0);

        if (config == null){
            return false;
        }

        //业务参数
        Object parameter = args.opt(1);
        HttpRequest httpInstance;

        if (parameter instanceof String) {
            try {
                parameter = new JSONObject(args.optString(1));
            }
            catch (JSONException e) {
                //类型转换失败
                parameter = new JSONObject();
            }
        }
        else if (!(parameter instanceof JSONObject)){
            parameter = new JSONObject();
        }

        String index = config.optString("queueIndex","");
        if ("".equals(index)){
            //队列索引值错误
            return false;
        }

        httpInstance = new HttpRequest(config, (JSONObject)parameter, callbackContext);
        httpRequestQueue.put(index,httpInstance);
        return httpInstance.execute();
    }

    /**
     * 取消当前HttpRequest
     *
     * @param args 预留该字段
     * @return
     */
    private boolean cancelHttpRequest(CordovaArgs args){
        String index = args.optString(0);

        if (index == null || "".equals(index)){
            //队列索引值错误
            return false;
        }

        if (!httpRequestQueue.containsKey(index)){
            //索引值不存在
            return false;
        }

        HttpRequest httpInstance = httpRequestQueue.remove(index);
        if (httpInstance != null){
            httpInstance.cancel();
        }

        return true;
    }

    /**
     * inner http request
     */
    private class HttpRequest extends IHttpRequestEvents {
        private JSONObject config;
        private JSONObject data;
        private CallbackContext callbackContext;

        private BusinessRequest httpRequest;

        public HttpRequest(JSONObject config, JSONObject data, CallbackContext callbackContext){
            this.config            = config;
            this.data              = data;
            this.callbackContext   = callbackContext;
            String scheme = getScheme();
            this.httpRequest       = BusinessRequest.obtainRequest(me,
                    scheme,
                    getUrl(),
                    getRequestMethod(),
                    autoShowProgressDialog());
        }

        public CallbackContext getCallbackContext(){
            return this.callbackContext;
        }

        /**
         * 执行 http 请求
         * @return 请求成功发送（成功发送不等于请求成功）返回 true，否则返回false。
         */
        public boolean execute(){
            String dataType = getDataTypeInConfig(config);
            String fileType = getFileType(config);

            if (TYPE_LOCAL.equalsIgnoreCase(dataType) &&
                    "path".equalsIgnoreCase(fileType)){
                //如果获取本地文件路径，则接返回不需要发送 http 请求
                sendPluginResult(PluginResult.Status.OK,
                        this.httpRequest.getRequestURL(),
                        200,
                        true,
                        true);

                sendPluginResult(PluginResult.Status.OK,
                        "invokeFinish",
                        200,
                        false,
                        false);

                return true;
            }
            else if (TYPE_JSON.equalsIgnoreCase(dataType)){
                //要获取的是远程服务器上的JSON数据
                httpRequest.getResponseHandler().setResponseDataType(DefaultHttpResponseHandler.RESPONSE_DATA_TYPE_JSON);
            }
            else if ("text".equalsIgnoreCase(fileType)){
                //要获取的是远程服务器上的文本文件数据
                httpRequest.getResponseHandler().setResponseDataType(DefaultHttpResponseHandler.RESPONSE_DATA_TYPE_TEXT);
            }
            else {
                //要获取的是远程服务器上的二制文件数据
                httpRequest.getResponseHandler().setResponseDataType(DefaultHttpResponseHandler.RESPONSE_DATA_TYPE_DATA);
            }

            //设置请求参数
            setRequestParams(httpRequest, data);

            //添加上传文件
            JSONObject files = config.optJSONObject("files");
            if (files != null){
                HttpRequestParams requestParams = httpRequest.getRequestParams();
                for(Iterator iterator = files.keys(); iterator.hasNext();){
                    String key   = iterator.next().toString();
                    String value = data.optString(key, "");
                    File file = new File(value);

                    try {
                        requestParams.put(key, file);
                    } catch (FileNotFoundException e) {}
                }
            }

            // web 业务版本号
            httpRequest.setHeader(AsyncHttpClient.X_Client_Bus_Ver,config.optString("_bus_ver", ""));

            //执行请求
            httpRequest.setIHttpRequestEvents(this);
            httpRequest.setAutoShowToast(getAutoToastInConfig(config));
            httpRequest.setProgressMessage(getMaskMsgInConfig(config))
                    .setCacheRuleName(getCacheRuleName(config))
                    .setForceSendRequest(getForceRequest(config))
                    .execute();

            return true;
        }

        /**
         * 取消请求
         */
        public void cancel(){
            if(httpRequest != null){
                httpRequest.cancel(true);
            }
        }

        /**
         * auto show progress dialog
         *
         * @return
         */
        private boolean autoShowProgressDialog(){
            String maskMsg = getMaskMsgInConfig(this.config);
            if (maskMsg.equals("null")){
                return false;
            }
            return true;
        }

        /**
         * 获取请求方法类型
         * 默认为POST
         *
         * @return
         */
        private com.lakala.core.http.HttpRequest.RequestMethod getRequestMethod(){
            String method = getMethodInConfig(config);
            if(method.equals(METHOD_GET)){
                return com.lakala.core.http.HttpRequest.RequestMethod.GET;
            }
            if(method.equals(METHOD_POST)){
                return com.lakala.core.http.HttpRequest.RequestMethod.POST;
            }
            if(METHOD_DELETE.equals(method)){
                return com.lakala.core.http.HttpRequest.RequestMethod.DELETE;
            }
            if(METHOD_PUT.equals(method)){
                return com.lakala.core.http.HttpRequest.RequestMethod.PUT;
            }
            return com.lakala.core.http.HttpRequest.RequestMethod.POST;
        }

        /**
         * 设置请求参数
         *
         * @param request
         * @param data
         */
        private void setRequestParams(BusinessRequest request, JSONObject data){
            HttpRequestParams requestParams = request.getRequestParams();

            for(Iterator iterator = data.keys(); iterator.hasNext();){
                String key   = iterator.next().toString();
                String value = data.optString(key, "");
                requestParams.put(key, value);
            }
        }

        /**
         * 获取 url scheme
         *
         * @return String
         */
        private String getScheme(){
            String url = getUrlInConfig(config);
            //绝对路径
            if (url.contains(BusinessRequest.SCHEME_MSVR)) {
                return BusinessRequest.SCHEME_MSVR;
            }
            else if (url.contains(BusinessRequest.SCHEME_LOCAL)) {
                return BusinessRequest.SCHEME_LOCAL;
            } else if (url.contains(BusinessRequest.SCHEME_MTS)) {
                return BusinessRequest.SCHEME_MTS;
            }

            return "";
        }

        /**
         * 获取绝对URL
         */
        private String getUrl(){
            String url = getUrlInConfig(config);
            String dataType = getDataTypeInConfig(config);

            if (TYPE_LOCAL.equalsIgnoreCase(dataType)){
                //要获取的是本地文件
                if (url.startsWith("/")){
                    //相对于webapp 根
                    url = "file://" + PackageFileManager.getInstance().getPmobilePath() + url;
                }
                else if (!url.contains("://")){
                    //相对于当前位置
                    url = currentUrl.subSequence(0, currentUrl.lastIndexOf("/") + 1) + url;
                }
            }
            else{
                //获取的是远程资源
                url = BusinessRequest.replaceHostByUrlSecheme(url);

                //当 URL 不是绝对路径时将它转换成绝对路径
                if (!url.contains(URL_TYPE_ABS)) {
                    if (currentUrl.contains("file://")) {
                        //当前页面在是本地，使用服务器地址做为web根路径。
                        //usinessRequest 会自动添加主机头，所以不用修改url。
                    }
                    else {
                        //当前Web内容是一个选程页面
                        if (url.startsWith("/")){
                            //URL 相对于web页面根
                            try {
                                URI uri = new URI(currentUrl);
                                url = String.format("%s://%s:%s%s",uri.getScheme(),uri.getHost(),uri.getPort(),url);
                            } catch (URISyntaxException e) {}
                        }
                        else{
                            //URL 相对于当前页面
                            url = currentUrl.subSequence(0, currentUrl.lastIndexOf("/") + 1) + url;
                        }
                    }
                }
            }

            return url;
        }

        /**
         * 获取返回对象
         * response 中有以下成员：
         * index : 请求队列的索引值。
         * data  : 服务器返回的数据文本格式。
         * isFromCache : 数据是否来自缓存。
         * isFinished  : 请求是否完成。
         * status:  服务器状态码。
         */
        private JSONObject getCallbackResponse(String response,int status, boolean canShowDialog) throws JSONException {
            JSONObject object = new JSONObject();
            object.put("index",         getQueueIndexInConfig(config));
            object.put("data",          response);
            object.put("isFromCache",   httpRequest.responseFromCache());
            object.put("isFinished",    httpRequest.isFinished());
            object.put("status",        status);
            object.put("canShowDialog", canShowDialog);
            return object;
        }

        /**
         * 发送result
         */
        private void sendPluginResult(PluginResult.Status result,String response, int status, boolean keepCallback, boolean canShowDialog){
            try {
                if (response.equals("invokeFinish")){
                    callbackContext.success(response);
                    return;
                }
                response = structureJsonFormat(response);
                PluginResult cordovaResult = keepCallback ? new PluginResult(result, getCallbackResponse(response, status, canShowDialog))
                        : new PluginResult(result, response);

                cordovaResult.setKeepCallback(keepCallback);
                callbackContext.sendPluginResult(cordovaResult);
            } catch (JSONException e) {
                LogUtil.print(e);
            }
        }

        /**
         * 获取Config属性
         */
        private String getMethodInConfig(JSONObject config) {
            return config.optString(CONFIG_METHOD, "");
        }

        private String getUrlInConfig(JSONObject config) {
            return config.optString(CONFIG_URL, "");
        }

        private String getDataTypeInConfig(JSONObject config) {
            return config.optString(CONFIG_DATA_TYPE, "remotejson");
        }

        private String getQueueIndexInConfig(JSONObject config) {
            return config.optString(CONFIG_QUEUE_INDEX, "");
        }

        private boolean getAutoToastInConfig(JSONObject config) {
            return config.optBoolean(CONFIG_AUTO_TOAST, false);
        }

        private boolean getIsFullScreenMaskInConfig(JSONObject config) {
            return config.optBoolean(CONFIG_IS_FULL_SCREEN_MASK, false);
        }

        private String getMaskMsgInConfig(JSONObject config) {
            return config.optString(CONFIG_MASK_MSG, "");
        }

        private String getCacheRuleName(JSONObject config) {
            return config.optString(CONFIG_CACHE_RULE_NAME, "");
        }

        private boolean getForceRequest(JSONObject config) {
            return config.optBoolean(CONFIG_FORCE_REQUEST, false);
        }

        private boolean getAutoParse(JSONObject config) {
            return config.optBoolean(CONFIG_AUTO_PARSE, true);
        }

        private String getFileType(JSONObject config){
            return config.optString("fileType","text");
        }

        private String generateResponseText(com.lakala.core.http.HttpRequest request){
            byte[] response = request.getResponseHandler().getResponseBody();
            String responseText = "";
            String dataType = getDataTypeInConfig(config);
            String fileType = getFileType(config);
            Header header = request.getResponseHandler().getHeaders().get("Content-Type");

            /**
             * 当请求的数据类型为 JSON 时返回文本数据。
             * 当请求的数据类型为文件时，根据 fileType 解析 ResponseText
             */
            if (TYPE_JSON.equalsIgnoreCase(dataType)){
                try {
                    responseText = new String(response, "utf-8");
                } catch (UnsupportedEncodingException e) {}
            }
            else if ("path".equalsIgnoreCase(fileType)){
                /**
                 * 需要返回文件路径，如果数据来自文件缓存则直接返回缓存文件的路径，
                 * 否则将数据保存到临时目录下，然后返回该临时文件路径。
                 */
                if (request.responseFromCache()){
                    responseText = request.getCacheFilePath();
                }
                else{
                    responseText = FileUtil.writeDataToCacheDir(ApplicationEx.getInstance(),response,"rtf");
                }
            }
            else if ("text".equalsIgnoreCase(fileType)){
                try {
                    responseText = new String(response, "utf-8");
                } catch (UnsupportedEncodingException e) {}
            }
            else if ("image".equalsIgnoreCase(fileType)){
                String imagetype = header != null ? header.getValue() : "image/png";
                responseText = String.format("data:%s;base64,%s",
                        imagetype,
                        Base64.encodeToString(response,Base64.DEFAULT));
            }
            else{
                //binary 或 其它未知模式
                responseText = Base64.encodeToString(response,Base64.DEFAULT);
            }

            return responseText;
        }

        /**
         * request 代理事件
         */
        @Override
        public void onStart(com.lakala.core.http.HttpRequest request) {
            //todo 处理 progress dialog
        }

        @Override
        public void onSuccess(com.lakala.core.http.HttpRequest request) {
            String responseText = generateResponseText(request);
            int     status        = request.getResponseHandler().getStatusCode();
            boolean canShowDialog = true;//todo 需要判断逻辑修改改值
            sendPluginResult(PluginResult.Status.OK, responseText, status, true, canShowDialog);
        }

        @Override
        public void onFailure(com.lakala.core.http.HttpRequest request, BaseException exception) {
            byte[] response = request.getResponseHandler().getResponseBody();
            String responseText = "";

            if (response != null){
                try {
                    responseText = new String(response, "utf-8");
                } catch (UnsupportedEncodingException e) {}
            }

            int     status        = request.getResponseHandler().getStatusCode();
            boolean canShowDialog = true;//todo 需要判断逻辑修改改值

            sendPluginResult(PluginResult.Status.ERROR, responseText, status, true, canShowDialog);
        }

        @Override
        public void onCancel(com.lakala.core.http.HttpRequest request) {
        }

        @Override
        public void onFinish(com.lakala.core.http.HttpRequest request) {
            String index = getQueueIndexInConfig(config);
            httpRequestQueue.remove(index);
            request.setIHttpRequestEvents(null);

            String response = "invokeFinish";
            sendPluginResult(PluginResult.Status.OK, response, 200, false, false);
        }

        private String structureJsonFormat(final String origenalStr){

            return origenalStr == null? origenalStr :origenalStr.replace("retCode", "_ReturnCode").replace("retMsg", "_ReturnMsg").replace("retData","_ReturnData");
        }

    }
}
