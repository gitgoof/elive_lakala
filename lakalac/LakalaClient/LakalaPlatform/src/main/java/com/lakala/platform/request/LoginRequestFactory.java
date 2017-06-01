package com.lakala.platform.request;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.lakala.core.http.HttpRequest;
import com.lakala.core.http.HttpResponseHandler;
import com.lakala.platform.http.BusinessRequest;
import com.loopj.lakala.http.RequestParams;

/**
 * Created by ZhangMY on 2015/2/3.
 */
public class LoginRequestFactory {

    /**
     * 登录
     * @param mUserName
     * @param mPassword
     * @param checkCode 绑定设备时的验证码
     * @Param token 绑定设备时的token
     * @param mHttpResponseHandler
     * @return
     */
    public static BusinessRequest createLoginRequest(FragmentActivity activity, String mUserName,String mPassword,String checkCode,String token,HttpResponseHandler mHttpResponseHandler){
        BusinessRequest businessRequest = BusinessRequest.obtainRequest(activity, "v1.0/auth/login", HttpRequest.RequestMethod.POST,true);
        businessRequest.setResponseHandler(mHttpResponseHandler);
        RequestParams requestParams =  businessRequest.getRequestParams();
        requestParams.put("loginName", mUserName);
        requestParams.put("password", mPassword);

        if(!TextUtils.isEmpty(checkCode)){
            requestParams.put("checkCode",checkCode);
        }
        if(!TextUtils.isEmpty(token)){
            requestParams.put("btoken",token);
        }

       return businessRequest;

    }

    /**
     * 获取绑定设备的授权码
     * @param mobileNum
     * @param mHttpResponseHandler
     * @return
     */
    public static BusinessRequest createDeviceCheckCode(String mobileNum,HttpResponseHandler mHttpResponseHandler, FragmentActivity activity){
        BusinessRequest businessRequest = BusinessRequest.obtainRequest(activity, "v1.0/device/checkcode", HttpRequest.RequestMethod.POST, true);
        businessRequest.setResponseHandler(mHttpResponseHandler);
        RequestParams requestParams =  businessRequest.getRequestParams();
        requestParams.put("mobileNum", mobileNum);

        return businessRequest;

    }

    /**
     * 刷新令牌
     * @return
     */
    public static BusinessRequest createRefreshTokenRequest(String refreshtoken,HttpResponseHandler mHttpResponseHandler, FragmentActivity activity){

        BusinessRequest businessRequest = BusinessRequest.obtainRequest( activity,"v1.0/auth/refresh", HttpRequest.RequestMethod.POST, activity != null && !activity.isFinishing());
        if(activity == null){
            businessRequest = BusinessRequest.obtainRequest("v1.0/auth/refresh", HttpRequest.RequestMethod.POST);
        }
        businessRequest.setResponseHandler(mHttpResponseHandler);
        RequestParams requestParams =  businessRequest.getRequestParams();
        requestParams.put("refreshtoken", refreshtoken);

        return businessRequest;
    }

    public static BusinessRequest createCheckCodeVerifyRequest(String btToken, String loginName, String checkCode, HttpResponseHandler mHttpResponseHandler, FragmentActivity activity){
        BusinessRequest businessRequest = BusinessRequest.obtainRequest( activity,"v1.0/user/checkcode", HttpRequest.RequestMethod.GET, true);
        businessRequest.setResponseHandler(mHttpResponseHandler);
        RequestParams requestParams =  businessRequest.getRequestParams();
        requestParams.put("btoken", btToken);
        requestParams.put("mobileNum", loginName);
        requestParams.put("checkCode", checkCode);

        return businessRequest;
    }

    public static BusinessRequest createBusinessInfoRequest(){
        BusinessRequest businessRequest = BusinessRequest.obtainRequest("v1.0/getMerchantInfo", HttpRequest.RequestMethod.GET);
        businessRequest.setAutoShowToast(true);
        return businessRequest;
    }

    public static BusinessRequest createMerchantStateRequest(){
        return BusinessRequest.obtainRequest("v1.0/merchant/bind", HttpRequest.RequestMethod.GET);
    }

    /**
     * 缴费业务开关
     * @return
     */
    public static BusinessRequest createMerchantStatusRequest(){
        return BusinessRequest.obtainRequest("v1.0/merchantStatus", HttpRequest.RequestMethod.GET);
    }


}
