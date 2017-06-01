package com.lakala.shoudan.activity.password;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.CommonEncrypt;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.finance.InputPayPwdDialogActivity;
import com.lakala.shoudan.activity.shoudan.finance.bean.Question;
import com.lakala.shoudan.activity.wallet.request.VerifyPayPwdRequest;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by huangjp on 2015/12/16.
 */
public class PayPwdDialogActivity extends InputPayPwdDialogActivity {
    private Question question=new Question();
    private String questionContent;//密保问题
    private String answerNote;//密保问题答案说明
    private String questionId;//密保问题Id
    private String encryptPwd;

    @Override
    protected void initAction() {
        super.initAction();
        tvOk.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String pwd = getString(inputStack);
                        if (TextUtils.isEmpty(pwd) || pwd.length() < 6) {
                            LogUtil.print("密码长度不为6");
                            return;
                        }
                        showProgressWithNoMsg();
                        encryptPwd= CommonEncrypt.pinKeyDesRsaEncrypt(
                                ApplicationEx.getInstance().getUser().getTerminalId(), pwd);
                        verifyPayPwd(encryptPwd);
                    }
                }
        );
    }
    /**
     * 校验支付密码
     * @param trsPassword
     */
    private void verifyPayPwd(String trsPassword){
        BusinessRequest businessRequest=RequestFactory.getRequest(this, RequestFactory.Type.VERIFY_PAY_PWD);
        VerifyPayPwdRequest params=new VerifyPayPwdRequest(context);
        params.setTrsPassword(trsPassword);
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                Intent data = new Intent();
                if (resultServices.isRetCodeSuccess()) {
                    data.putExtra(Constants.IntentKey.PASSWORD, encryptPwd);
                    setResult(ConstKey.RESULT_PWD_BACK, data);
                    finish();
                }else {
                    toast(resultServices.retMsg);
                    finish();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
                LogUtil.print(connectEvent.getDescribe());
                finish();
            }
        });
        WalletServiceManager.getInstance().start(params, businessRequest);
    }



    /**
     * 查询用户密保问题
     */
    @Override
    protected void userQuestionQry() {
        showProgressWithNoMsg();
        BusinessRequest businessRequest= RequestFactory.getRequest(this, RequestFactory.Type.QRY_USER_SECURITY_QUESTION);
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                        questionContent=jsonObject.optString("questionContent");
                        answerNote=jsonObject.optString("answerNote");
                        questionId=jsonObject.optString("questionId");
                        question.setQuestionContent(questionContent);
                        question.setAnswerNote(answerNote);
                        question.setQuestionId(questionId);
                        VerifyUserQuestionActivity.open(context,question);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
        WalletServiceManager.getInstance().start(context, businessRequest);
    }
}
