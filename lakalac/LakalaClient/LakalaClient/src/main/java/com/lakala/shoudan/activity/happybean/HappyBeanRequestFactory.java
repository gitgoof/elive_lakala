package com.lakala.shoudan.activity.happybean;

import android.app.Activity;

import com.lakala.core.http.HttpRequest;
import com.lakala.platform.http.BusinessRequest;

/**
 * Created by huangjp on 2016/5/18.
 */
public class HappyBeanRequestFactory {
    public static BusinessRequest  getRequest(Activity activity,HappyBeanType type){
        BusinessRequest request=new BusinessRequest(activity);
        switch (type){
            case VERIFY_PAY_PWD:
                request.setRequestURL("");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
        }
        return request;
    }
    public enum HappyBeanType{
        /**校验支付密码*/
        VERIFY_PAY_PWD,
        /**校验支付密码*/
        CHECK_PAY_PWD,
    }
}
