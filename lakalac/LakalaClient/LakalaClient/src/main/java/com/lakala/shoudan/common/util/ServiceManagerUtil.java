package com.lakala.shoudan.common.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.activity.ErrorDialogActivity;
import com.lakala.platform.bean.Session;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.CrashlyticsUtil;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.LoginRequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.activity.wallet.request.WalletMacRequest;
import com.lakala.shoudan.common.CommonBaseRequest;
import com.lakala.shoudan.util.LoginUtil;
import com.lakala.ui.dialog.AlertDialog;
import com.loopj.lakala.http.RequestParams;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by huangjp on 2016/5/31.
 */
public class ServiceManagerUtil<T extends CommonBaseRequest>{
    private static final ServiceManagerUtil instance=new ServiceManagerUtil();
    public static ServiceManagerUtil getInstance(){
        return instance;
    }
    public void start(Context context,BusinessRequest request){
        CommonBaseRequest params=new CommonBaseRequest(context);
        start((T)params, request, null);
    }
    public void start(T params,BusinessRequest request){
        start(params, request, null);
    }
    public void start(T params, final BusinessRequest request, final ServiceResultCallback callback){
        if(request == null){
            return;
        }
        List<Field> fields = new ArrayList<>();
        getClassFields(params.getClass(), fields);

        HttpRequestParams requestParams = request.getRequestParams();

        for(Field field : fields){

            setJson(params, field, requestParams);

        }
        if(params.isNeedMac()){
            WalletMacRequest macRequest = params.createMacRequest();
            try {
                addToJSON(macRequest.getJSONObject(),requestParams);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(callback != null){
            request.setResponseHandler(callback);
        }
        LogUtil.print("requestParams == "+requestParams.toString());
        request.execute();
    }
    private void getClassFields(Class clazz,List<Field> arrays){
        if(arrays == null){
            arrays = new ArrayList<>();
        }
        Field[] fields = clazz.getDeclaredFields();
        arrays.addAll(Arrays.asList(fields));
        Class superClazz = clazz.getSuperclass();
        if(TextUtils.equals(Object.class.getName(), superClazz.getName())){
            return;
        }
        getClassFields(superClazz, arrays);
    }
    private void addToJSON(JSONObject jsonObject, RequestParams retJSONObject){

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
    private void setJson(T request, Field field, RequestParams jsonObject){

        field.setAccessible(true);

        Object value = null;
        try {
//            value = getValue(request,field.getName());
            value = field.get(request);
            LogUtil.print("msg",field.getName()+":"+value);
            jsonObject.put(field.getName(), value);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * 刷新令牌  令牌调用
     */
    public static void refreshToken(){

        final Session session = ApplicationEx.getInstance().getSession();
        if(session.isUserLogin()){

            String refreshToken = session.getUser().getRefreshToken();

            LoginRequestFactory.createRefreshTokenRequest(refreshToken, new ResultDataResponseHandler(new ServiceResultCallback() {
                @Override
                public void onSuccess(ResultServices resultServices) {
                    try {

                        if (resultServices.isRetCodeSuccess()) {
                            session.getUser().updateUserToken(new JSONObject(resultServices.retData));
                        }

                    } catch (Exception e) {
                        LogUtil.print(e);
                    }
                }

                @Override
                public void onEvent(HttpConnectEvent connectEvent) {

                }
            }), null).execute();
        }

    }
    public static void toLoginDialog(String msg){
        final Context context = ApplicationEx.getInstance();
        AlertDialog alertDialog = new AlertDialog();
        alertDialog.setMessage(msg);
        alertDialog.setButtons(new String[]{context.getString(com.lakala.platform.R.string.ui_certain)});
        alertDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                try {
                    dialog.dismiss();
                    LoginUtil.loginOut(context);

                } catch (Exception e) {
                    LogUtil.print(e);
                    CrashlyticsUtil.logException(e);
                }

            }
        });
        LogUtil.print("handler ", "Result data response handler");

        ErrorDialogActivity.startSelf(alertDialog, "toLoginDialog");
    }





    private <RETOBJ extends Object> RETOBJ getValue(T model,String name)
            throws Exception {
        name = name.replaceFirst(name.substring(0, 1), name.substring(0, 1)
                .toUpperCase());
        Method m = model.getClass().getMethod("get" + name);
        Object obj = m.invoke(model);
        if(obj != null){
            return (RETOBJ)obj;
        }
        return null;
    }
}
