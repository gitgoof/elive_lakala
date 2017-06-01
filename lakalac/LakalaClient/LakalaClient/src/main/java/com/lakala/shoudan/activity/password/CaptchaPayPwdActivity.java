package com.lakala.shoudan.activity.password;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.shoudan.activity.shoudan.finance.question.CaptchaPayActivity;
import com.lakala.shoudan.activity.wallet.request.MessageCaptchaRequest;
import com.lakala.shoudan.activity.wallet.request.VerifyCaptchaPayPwdRequest;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;

/**
 * 重置支付密码短信验证
 * Created by huangjp on 2015/12/19.
 */
public class CaptchaPayPwdActivity extends CaptchaPayActivity {
    public static void open(Activity activity){
        Intent intent = new Intent(activity,CaptchaPayPwdActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void initUI() {
        super.initUI();
        String mobile=ApplicationEx.getInstance().getUser().getLoginName();
        if (!TextUtils.isEmpty(mobile)){
            tvTips.setText("请输入手机尾号"+mobile.substring(7,11)+"收到的短信验证码：");
        }

    }

    /**
     * 获取验证码
     */
    @Override
    protected void getSMSCode() {
        showProgressWithNoMsg();
        MessageCaptchaRequest params=new MessageCaptchaRequest(context);
        params.setMobile(ApplicationEx.getInstance().getUser().getLoginName());
        BusinessRequest businessRequest=RequestFactory.getRequest(this, RequestFactory.Type.MESSAGE_RESET_PAY_PWD);
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    tvGetCaptcha.startCaptchaDown(59);

                }else {
                    tvGetCaptcha.resetCaptchaDown();
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

    /**
     * 验证验证码
     */
    @Override
    protected void verifySMSCode() {
        showProgressWithNoMsg();
        VerifyCaptchaPayPwdRequest params=new VerifyCaptchaPayPwdRequest(context);
        params.setMobile(ApplicationEx.getInstance().getUser().getLoginName());
        params.setSMSCode(etInputCaptcha.getText().toString().trim());
        BusinessRequest businessRequest=RequestFactory.getRequest(this, RequestFactory.Type.VERIFY_MESSAGE);
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    Intent intent=new Intent(CaptchaPayPwdActivity.this,SetPaymentPasswordActivity.class);
                    intent.putExtra("reset_pwd","reset_pwd");
                    startActivity(intent);
                    finish();
                }else{
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
