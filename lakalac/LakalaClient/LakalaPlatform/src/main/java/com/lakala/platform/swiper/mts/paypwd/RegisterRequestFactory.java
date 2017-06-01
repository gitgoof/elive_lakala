package com.lakala.platform.swiper.mts.paypwd;

import android.content.Context;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.http.BusinessRequest;

/**
 * 用户注册请求工程
 * <p/>
 * Created by LL on 14-1-4.
 */
public class RegisterRequestFactory {

    /**
     * 设置支付密码
     *
     * @param TrsPassword        支付密码
     * @param NewTrsPassword     新支付密码(修改支付密码时必输)
     * @param ConfirmTrsPassword 确认支付密码
     */
    public static BusinessRequest createSetPayPwd(Context context, String TrsPassword, String NewTrsPassword, String ConfirmTrsPassword) {

        BusinessRequest request = BusinessRequest.obtainRequest(context, BusinessRequest.SCHEME_MTS, "setting/setPayPwd.do", HttpRequest.RequestMethod.POST, true);
        request.getRequestParams().put("TrsPassword", PayPwdProcess.encryptPayPassword(TrsPassword));
        if (StringUtil.isNotEmpty(NewTrsPassword)) {
            request.getRequestParams().put("NewTrsPassword", PayPwdProcess.encryptPayPassword(NewTrsPassword));
        }
        request.getRequestParams().put("ConfirmTrsPassword", PayPwdProcess.encryptPayPassword(ConfirmTrsPassword));

        return request;
    }

    /**
     * 设置支付密码 by fengx
     * @param context
     * @param loginName
     * @param payPassword
     * @param Flag
     * @return
     */
    public static BusinessRequest setPaymentPwd(Context context,String loginName,String payPassword,String Flag){

        BusinessRequest request = BusinessRequest.obtainRequest(context,BusinessRequest.SCHEME_MTS,"v1.0/commons/securityService/pwd", HttpRequest.RequestMethod.POST,true);
        request.getRequestParams().put("loginName",loginName);
        request.getRequestParams().put("payPassword",payPassword);
        request.getRequestParams().put("Flag",Flag);

        return request;
    }
    /**
     * 查询密保提示问题列表
     */
    public static BusinessRequest queryQuestionList(Context context) {
        return BusinessRequest.obtainRequest(context, BusinessRequest.SCHEME_MTS, "common/questionListQry.do", HttpRequest.RequestMethod.POST, true);
    }

    /**
     * 提交密保问题
     *
     * @param Mobile
     * @param QuestionId
     * @param QuestionContent
     * @param QuestionType
     * @param Answer
     * @return
     */
    public static BusinessRequest addUserQuestion(Context context, String Mobile, String QuestionId, String QuestionContent, String QuestionType, String Answer) {
        BusinessRequest request = BusinessRequest.obtainRequest(context, BusinessRequest.SCHEME_MTS, "common/addUserQuestion.do", HttpRequest.RequestMethod.POST, true);

        request.getRequestParams().put("Mobile", Mobile);
        request.getRequestParams().put("QuestionId", QuestionId);
        request.getRequestParams().put("QuestionContent", QuestionContent);
        request.getRequestParams().put("QuestionType", QuestionType);
        request.getRequestParams().put("Answer", Answer);
        return request;
    }

    /**
     * 提交密保问题(新)
     *
     * @param Mobile
     * @param QuestionId
     * @param QuestionContent
     * @param QuestionType
     * @param Answer
     * @return
     */
    public static BusinessRequest addSecurityQuestion(Context context, String Mobile, String QuestionId, String QuestionContent, String QuestionType, String Answer) {
        BusinessRequest request = BusinessRequest.obtainRequest(context, BusinessRequest.SCHEME_MTS, "v1.0/common/securityService/addUserQuestion", HttpRequest.RequestMethod.POST, true);

        request.getRequestParams().put("Mobile", Mobile);
        request.getRequestParams().put("QuestionId", QuestionId);
        request.getRequestParams().put("QuestionContent", QuestionContent);
        request.getRequestParams().put("QuestionType", QuestionType);
        request.getRequestParams().put("Answer", Answer);
        return request;
    }

    /**
     * 验证密保
     */
    public static BusinessRequest checkPayPwd(Context context, String TrsPassword) {
        BusinessRequest request = BusinessRequest.obtainRequest(context, BusinessRequest.SCHEME_MTS, "setting/checkPayPwd.do", HttpRequest.RequestMethod.POST, true);
        request.getRequestParams().put("TrsPassword", PayPwdProcess.encryptPayPassword(TrsPassword));
        return request;
    }

    /**
     * 查询用户设置的密保问题
     */
    public static BusinessRequest userQuestionQry(Context context, String Mobile) {
        BusinessRequest request = BusinessRequest.obtainRequest(context, BusinessRequest.SCHEME_MTS, "common/userQuestionQry.do", HttpRequest.RequestMethod.POST, true);
        request.getRequestParams().put("Mobile", Mobile);
        return request;
    }

    /**
     * 验证密保答案
     */
    public static BusinessRequest verifyQuestion(Context context, String Mobile, String Answer, String QuestionId) {
        BusinessRequest request = BusinessRequest.obtainRequest(context, BusinessRequest.SCHEME_MTS, "common/verifyQuestion.do", HttpRequest.RequestMethod.POST, true);
        request.setAutoShowToast(true);
        request.getRequestParams().put("Mobile", Mobile);
        request.getRequestParams().put("Answer", Answer);
        request.getRequestParams().put("QuestionId", QuestionId);
        return request;
    }

    /**
     * 免密支付开关
     *
     * @param toOpen
     * @param TrsPassword
     * @return
     */
    public static BusinessRequest noPasswordPay(Context context, boolean toOpen, String TrsPassword) {
        BusinessRequest request = BusinessRequest.obtainRequest(context, BusinessRequest.SCHEME_MTS, "setting/noPasswordPay.do", HttpRequest.RequestMethod.POST, true);

        request.getRequestParams().put("State", toOpen ? "1" : "0");
        request.getRequestParams().put("Amount", "1000");
        request.getRequestParams().put("TrsPassword", PayPwdProcess.encryptPayPassword(TrsPassword));
        return request;
    }

}
