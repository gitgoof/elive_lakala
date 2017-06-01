package com.lakala.platform.request;

import android.support.v4.app.FragmentActivity;

import com.lakala.core.http.HttpRequest;
import com.lakala.core.http.HttpResponseHandler;
import com.lakala.platform.http.BusinessRequest;
import com.loopj.lakala.http.RequestParams;

/**
 * Created by ZhangMY on 2015/2/4.
 *
 * 注册
 */
public class RegisterRequestFactory {


    /**
     * 注册获取验证
     * @param mobileNum
     * @param mHttpResponseHandler
     * @return
     */
    public static BusinessRequest createGetVerticationCode(FragmentActivity activity, String mobileNum,HttpResponseHandler mHttpResponseHandler){
        BusinessRequest businessRequest = BusinessRequest.obtainRequest(activity, "v1.0/user/checkcode", HttpRequest.RequestMethod.POST, true);
        businessRequest.setResponseHandler(mHttpResponseHandler);
        RequestParams requestParams =  businessRequest.getRequestParams();
        requestParams.put("mobileNum", mobileNum);

        return businessRequest;

    }

    /**
     * 用户注册
     * @param loginName
     * @param password
     * @param btoken
     * @param checkCode
     * @param mHttpResponseHandler
     * @return
     */
    public static BusinessRequest createRegisterRequest(FragmentActivity activity, String loginName,String password,String btoken,String checkCode,HttpResponseHandler mHttpResponseHandler){
        BusinessRequest businessRequest = BusinessRequest.obtainRequest(activity,"v1.0/user/regist", HttpRequest.RequestMethod.POST, true);
        businessRequest.setResponseHandler(mHttpResponseHandler);
        RequestParams requestParams =  businessRequest.getRequestParams();
        requestParams.put("loginName", loginName);
        requestParams.put("password", password);
        requestParams.put("btoken", btoken);
        requestParams.put("checkCode", checkCode);

        return businessRequest;

    }


}
