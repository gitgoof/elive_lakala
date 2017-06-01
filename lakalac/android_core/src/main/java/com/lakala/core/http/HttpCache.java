package com.lakala.core.http;

import com.lakala.core.cache.CacheKey;
import com.lakala.core.cache.CacheRule;
import com.lakala.core.cache.ExpireDate;
import com.lakala.core.cache.FileCache;
import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.StringUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Michael on 14-10-15.
 */
public class HttpCache {

    private HttpRequest             httpRequest;
    private HttpResponseHandler     responseHandler;
    private String                  requestURL;
    private String                  cacheRuleName;
    private CacheRule               cacheRule;
    private CacheKey                cacheKey;
    private String                  cacheFilePath;

    //缓存的数据是否已经过期
    private boolean                 isExpired      = false;

    public HttpCache(HttpRequest httpRequest){
        initParams(httpRequest);
    }

    public String getCacheFilePath(){
        return cacheFilePath;
    }

    /**
     * 缓存的数据是否过期
     * @return 过期返回 true。
     */
    public boolean isExpired() {
        return isExpired;
    }

    /**
     * 初始化参数
     *
     * @param httpRequest
     */
    private void initParams(HttpRequest httpRequest){
        this.httpRequest     = httpRequest;
        this.responseHandler = httpRequest.getResponseHandler();
        this.requestURL      = httpRequest.getRequestURL();
        this.cacheRuleName   = httpRequest.getCacheRuleName();
    }

    /**
     * 初始化缓存，如果该请求设置了缓存，则生成缓存key，根据缓存 key 读取缓存数据。
     */
    public byte[] readCache(String cacheRuleName){
        if(StringUtil.isEmpty(cacheRuleName)){
            return null;
        }

        cacheRule = getCacheRule();

        //如果获取规则失败则直接发请求
        if(cacheRule == null){
            return null;
        }

        cacheKey = getCacheKey();

        return handleCache();
    }

    /**
     * 若该请求设置了缓存，则缓存该请求的缓存数据
     */
    protected void cache(){
        if (cacheRule != null && cacheKey != null){
            byte[] responseBody = responseHandler.getResponseBody();

            FileCache fileCache = new FileCache(httpRequest.getContext().getApplicationContext());
            fileCache.setCategory("response");

            switch (cacheRule.updateMode){
                case CacheUpdateModeByDate:
                    ExpireDate expireDate = ExpireDate.expiredAfterDays(cacheRule.getValidDays());
                    fileCache.put(cacheKey, expireDate, responseBody);
                    break;
                case CacheUpdateModeByVersion:
                case CacheUpdateModeEveryTime:
                    fileCache.put(cacheKey, cacheRule.getVersion(), responseBody);
                    break;
                case CacheUpdateModeByVersionAndDate:
                    ExpireDate expireDate1 = ExpireDate.expiredAfterDays(cacheRule.getValidDays());
                    fileCache.put(cacheKey,expireDate1,cacheRule.getVersion(), responseBody);
                    break;
                default:
                    fileCache.put(cacheKey, cacheRule.getVersion(), responseBody);
                    break;
            }
        }
    }

    /**
     * 处理缓存
     * 如果没有缓存数据，则执行网络请求
     * 如果有缓存数据，且缓存没有过期，则直接返回缓存数据
     * 如果缓存数据已经过期，isReturnExpire 为true 则先使用缓存数据，再发送网络请求，否则直接发送网络请求
     */
    private byte[] handleCache(){
        FileCache fileCache = new FileCache(httpRequest.getContext().getApplicationContext());
        fileCache.setCategory("response");

        InputStream data = fileCache.getExpiredData(cacheKey);

        //没有缓存数据，则执行网络请求
        if (data == null){
            return null;
        }

        isExpired = false;

        switch (cacheRule.updateMode){
            case CacheUpdateModeByDate:
                isExpired = fileCache.isExpire(cacheKey);
                break;
            case CacheUpdateModeByVersion:
                isExpired = fileCache.isExpire(cacheKey,cacheRule.getVersion());
                break;
            case CacheUpdateModeByVersionAndDate:
                isExpired = fileCache.isExpireByDateAndVersion(cacheKey, cacheRule.getVersion());
                break;
            case CacheUpdateModeEveryTime:
                isExpired = true;
                break;
            case CacheUpdateModeNever:
                isExpired = false;
                break;
        }

        //有缓存数据，且缓存没有过期，则直接返回缓存数据
        if (!isExpired){
            cacheFilePath = fileCache.getFilePath(cacheKey);
            return toBytes(data);
        }

        //数据已经过期，但不允许使用过期数据，则放弃缓存数据，从网络获取新数据。
        if (!cacheRule.getIsReturnExpire()){
            return null;
        }

        //数据已经过期，但允许使用过期数据，先返回缓存的数据，再执继续行网络请求获取新数据
        cacheFilePath = fileCache.getFilePath(cacheKey);
        return toBytes(data);
    }

    /**
     * 如果有该请求设置了缓存，则将缓存数据传给解析器{@link com.lakala.core.http.HttpResponseHandler}
     */
    private byte[] toBytes(InputStream data){
        int length = 0;
        try {
            length = data.available();
        } catch (IOException e) {}

        byte[] responseBody = new byte[length];

        try {
            data.read(responseBody);
        } catch (IOException e) { }

        return responseBody;
    }

    /**
     * 获取CackeKey
     *
     * @return
     */
    private CacheKey getCacheKey(){
        List<String> keys = cacheRule.getKeyFields();
        HttpRequestParams requestParams = httpRequest.getRequestParams();

        StringBuilder stringBuilder = new StringBuilder();
        for(String key : keys){
            Object value = requestParams.get(key);

            if (value != null && !"".equals(value) && value instanceof String){
                stringBuilder.append((String)value);
                stringBuilder.append(".");
            }
        }

        return CacheKey.generate(requestURL, stringBuilder.toString());
    }

    /**
     * 获取缓存规则
     * @return
     */
    private CacheRule getCacheRule(){
        if (StringUtil.isNotEmpty(cacheRuleName)){
            try {
                InputStream inputStream = httpRequest.getContext()
                        .getAssets().open("cacherule.json");
                int length = inputStream.available();
                byte[] buffer = new byte[length];
                inputStream.read(buffer);
                inputStream.close();

                String jsonString = new String(buffer);

                JSONObject jsonObject = new JSONObject(jsonString);
                JSONObject jsonObject1 = jsonObject.getJSONObject("rules");
                return CacheRule.createCacheRule(jsonObject1.optJSONObject(cacheRuleName));

            } catch (Exception e) {
                return null;
            }
        }else {
            return null;
        }
    }

}
