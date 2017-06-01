package com.lakala.shoudan.common.net.volley;


import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.lakala.core.http.HttpRequest;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.PropertiesUtil;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.RequestUrlEnum;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.NeedMacRequest;
import com.loopj.lakala.http.RequestParams;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ServiceManager<T extends BaseRequest>{

    private final int TIMEOUT = 30; //超时时间
    private static ServiceManager ourInstance = new ServiceManager();

    public static ServiceManager getInstance() {
        return ourInstance;
    }

    private RequestQueue requestQueue;

    private ServiceManager() {

        if(requestQueue == null)
            requestQueue = Volley.newRequestQueue(ApplicationEx.getInstance());

    }

    public static final String BASE_URL = PropertiesUtil.getUrl() + "v1.0/product/financial/";

    public void sendRequest(final T request, RequestUrlEnum requestUrlEnum, final HttpResponseListener
            httpResponseListener){

        StringBuffer stringBuffer = new StringBuffer(BASE_URL);
        stringBuffer.append(requestUrlEnum.value());
        stringBuffer.append("?_t=json");

        String fullUrl = stringBuffer.toString();

        List<Field> fields = new ArrayList<>();
        getClassFields(request.getClass(), fields);


        BusinessRequest businessRequest = BusinessRequest.obtainRequest(fullUrl, HttpRequest.RequestMethod.POST);

        RequestParams requestParams = businessRequest.getRequestParams();

//        requestParams.setUseJsonStreamer();

        for(Field field : fields){

            setJson(request, field, requestParams);

        }

        if(requestUrlEnum.isNeedMac()){
            NeedMacRequest macRequest = request.createMacRequest();
            try {
                addToJSON(macRequest.getJSONObject(),requestParams);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        LogUtil.print("Request Url = " + fullUrl + "\nRequest JSON = " + requestParams.toString());

        if(httpResponseListener != null)
            httpResponseListener.onStart();

        businessRequest.setResponseHandler(new FinanceResponseHandler(httpResponseListener));
        businessRequest.execute();

    }

    private void getClassFields(Class clazz,List<Field> arrays){
        if(arrays == null){
            arrays = new ArrayList<>();
        }
        Field[] fields = clazz.getDeclaredFields();
        arrays.addAll(Arrays.asList(fields));
        Class superClazz = clazz.getSuperclass();
        if(TextUtils.equals(Object.class.getName(),superClazz.getName())){
            return;
        }
        getClassFields(superClazz, arrays);
    }

    private void setJson(T request, Field field, RequestParams jsonObject){

        field.setAccessible(true);

        Object value = null;
        try {
            value = field.get(request);
            jsonObject.put(field.getName(), value);
        } catch (Exception e) {
            LogUtil.print(e);
        }

    }
    private void addToJSON(JSONObject jsonObject,RequestParams retJSONObject){

        if(retJSONObject == null || jsonObject == null){
            return;
        }

        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            String value = jsonObject.optString(key, "");
            retJSONObject.put(key, value);
        }

    }


}
