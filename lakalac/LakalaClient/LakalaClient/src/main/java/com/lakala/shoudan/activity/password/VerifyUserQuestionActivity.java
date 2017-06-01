package com.lakala.shoudan.activity.password;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.finance.bean.Question;
import com.lakala.shoudan.activity.shoudan.finance.question.CaptchaPayActivity;
import com.lakala.shoudan.activity.shoudan.finance.question.VerifyQuestionActivity;
import com.lakala.shoudan.activity.wallet.request.VerifyUserQuestionRequest;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;

/**
 * 重置支付密码（用户密保页面）
 * Created by huangjp on 2015/12/16.
 */
public class VerifyUserQuestionActivity extends VerifyQuestionActivity{
    public static void open(Context context,Question question){
        Intent intent = new Intent(context,VerifyUserQuestionActivity.class);
        intent.putExtra(Constants.IntentKey.TRANS_INFO, JSON.toJSONString(question));
        context.startActivity(intent);
    }

    @Override
    protected void verifyQuestion(Question question, String answer) {
        showProgressWithNoMsg();
        BusinessRequest businessRequest= RequestFactory.getRequest(this, RequestFactory.Type.VERIFY_USER_QUESTION);
        VerifyUserQuestionRequest params=new VerifyUserQuestionRequest(context);
        params.setAnswer(answer);
        params.setQuestionId(question.getQuestionId());
        params.setMobile(ApplicationEx.getInstance().getUser().getLoginName());
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()){
                    CaptchaPayPwdActivity.open(context);
                    finish();
                }else {
                    toast(resultServices.retMsg);
                }

            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        WalletServiceManager.getInstance().start(params,businessRequest);
    }
}
