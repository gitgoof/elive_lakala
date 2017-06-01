package com.lakala.platform.swiper.mts.paypwd;

import android.content.Context;

import com.lakala.core.http.HttpRequest;
import com.lakala.platform.http.BusinessRequest;

/**
 * 用户公共请求
 * <p/>
 * Created by LL on 14-1-4.
 */
public class CommonRequestFactory {

    /**
     * 500W
     */
    public static final String TOKEN_TYPE_500W = "20002";
    /**
     * 商城
     */
    public static final String TOKEN_TYPE_STORE = "20001";

    /**
     * 获取短信验证码
     *
     * @param Mobile       手机号码
     * @param BusinessType 业务类型
     * @param BusinessCode 业务编码
     */
    public static BusinessRequest createGetSMSCode(Context context,
                                                   String Mobile,
                                                   String BusinessType,
                                                   String BusinessCode) {
        BusinessRequest request = BusinessRequest.obtainRequest(context, BusinessRequest.SCHEME_MTS, "common/getSMSCode.do", HttpRequest.RequestMethod.POST, true);

        request.getRequestParams().put("Mobile", Mobile);
        request.getRequestParams().put("BusinessType", BusinessType);
        request.getRequestParams().put("BusinessCode", BusinessCode);

        return request;
    }

    /**
     * 效验短信验证码
     *
     * @param Mobile       手机号码
     * @param SMSCode      短信验证码
     * @param BusinessType 业务类型 0:移动客户端 1:PC端 9：第三方
     * @param BusinessCode 业务编码:
     *                     注册	228001
     *                     绑定授信设备 	228102
     *                     实名认证	228103
     *                     快捷签约	228104
     *                     找回密码	228201
     *                     找回支付密码	228202
     */
    public static BusinessRequest createVerifySMSCode(Context context,
                                                      String Mobile,
                                                      String SMSCode,
                                                      String BusinessType,
                                                      String BusinessCode) {
        BusinessRequest request = BusinessRequest.obtainRequest(context, BusinessRequest.SCHEME_MTS, "common/verifySMSCode.do", HttpRequest.RequestMethod.POST, true);

        request.getRequestParams().put("Mobile", Mobile);
        request.getRequestParams().put("SMSCode", SMSCode);
        request.getRequestParams().put("BusinessType", BusinessType);
        request.getRequestParams().put("BusinessCode", BusinessCode);

        return request;
    }

}
