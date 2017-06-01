package com.lakala.platform.request;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.lakala.core.http.HttpRequest;
import com.lakala.core.http.HttpResponseHandler;
import com.lakala.platform.http.BusinessRequest;
import com.loopj.lakala.http.RequestParams;

/**
 * Created by ZhangMY on 2015/3/12.
 * 修改密码，重置密码
 */
public class ResetPasswordFactory {


    /**
     * 获取验证码
     * @param context
     * @param mobile
     * @param mHttpResponseHandler
     * @return
     */
    public static BusinessRequest createGetCheckCodeRequest(FragmentActivity context,String mobile,HttpResponseHandler mHttpResponseHandler){
        BusinessRequest businessRequest = BusinessRequest.obtainRequest(context,"v1.0/user/password/forget/checkcode", HttpRequest.RequestMethod.POST,true);
        businessRequest.setResponseHandler(mHttpResponseHandler);
        RequestParams requestParams = businessRequest.getRequestParams();

        requestParams.put("mobileNum",mobile);

        return businessRequest;
    }

    /**
     * 校验验证码
     * @param context
     * @param mobile
     * @param btoken
     * @param checkCode
     * @param mHttpRrHttpResponseHandler
     * @return
     */
    public static BusinessRequest createCheckCheckcodeRequest(FragmentActivity context,String mobile,String btoken,String checkCode,HttpResponseHandler mHttpRrHttpResponseHandler){
        BusinessRequest businessRequest = BusinessRequest.obtainRequest(context,"v1.0/user/password/forget/checkcode", HttpRequest.RequestMethod.GET,true);
        businessRequest.setResponseHandler(mHttpRrHttpResponseHandler);
        RequestParams requestParams = businessRequest.getRequestParams();

        requestParams.put("mobileNum",mobile);
        if(!TextUtils.isEmpty(btoken)){
            requestParams.put("btoken",btoken);
        }
        if(!TextUtils.isEmpty(checkCode)){
            requestParams.put("checkCode",checkCode);
        }
        return businessRequest;
    }

    /**
     * 重置密码
     * @param context
     * @param mobile
     * @param password
     * @param btoken
     * @param mHttpResponseHandler
     * @return
     */
    public static BusinessRequest createResetPasswordRequest(FragmentActivity context,String mobile,String password,String btoken,HttpResponseHandler mHttpResponseHandler){
        BusinessRequest businessRequest = BusinessRequest.obtainRequest(context,"v1.0/user/password/forget", HttpRequest.RequestMethod.POST,true);
        businessRequest.setResponseHandler(mHttpResponseHandler);
        RequestParams requestParams = businessRequest.getRequestParams();
        requestParams.put("mobileNum",mobile);
        requestParams.put("password",password);
        requestParams.put("btoken",btoken);
//        requestParams.put("checkCode",smsCode);

        return businessRequest;
    }

    /**
     * 登录后修改密码
     * @param context
     * @param password  旧密码
     * @param newPassword 新密码
     * @param mHttpResponseHandler
     * @return
     */
    public static BusinessRequest createUpdatePasswordRequest(FragmentActivity context,String password,String newPassword,HttpResponseHandler mHttpResponseHandler){
        BusinessRequest businessRequest = BusinessRequest.obtainRequest(context,"v1.0/user/password", HttpRequest.RequestMethod.PUT,true);
        businessRequest.setResponseHandler(mHttpResponseHandler);
        RequestParams requestParams = businessRequest.getRequestParams();
        requestParams.put("password",password);
        requestParams.put("newPassword",newPassword);

        return businessRequest;
    }
}
