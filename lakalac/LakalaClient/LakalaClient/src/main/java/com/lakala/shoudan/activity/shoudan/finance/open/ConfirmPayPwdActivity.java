package com.lakala.shoudan.activity.shoudan.finance.open;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.CommonEncrypt;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.activity.BaseSafeManageActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.finance.InputPayPwdDialogActivity;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.FundSignUpRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.SetPayPwdRequest;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.activity.shoudan.finance.question.AddUserQuestionActivity;
import com.lakala.shoudan.common.net.volley.HttpResponseListener;
import com.lakala.shoudan.common.net.volley.ReturnHeader;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Stack;

/**
 * Created by LMQ on 2015/10/9.
 */
public class ConfirmPayPwdActivity extends BaseSafeManageActivity {

    private SetPayPwdRequest request;
    private String pwd;
    private String confirmPwd;
    private FundSignUpRequest signUpRequest = null;

    public static void start(Context context, SetPayPwdRequest request,
                             FundSignUpRequest signUpRequest) {
        Intent intent = new Intent(context, ConfirmPayPwdActivity.class);
        if (request != null) {
            intent.putExtra(Constants.IntentKey.PASSWORD, JSON.toJSONString(request));
        }
        if (signUpRequest != null) {
            intent.putExtra(Constants.IntentKey.TRANS_INFO, JSON.toJSONString
                    (signUpRequest));
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String json = getIntent().getStringExtra(Constants.IntentKey.PASSWORD);
        String signJson = getIntent().getStringExtra(Constants.IntentKey.TRANS_INFO);
        signUpRequest = JSON.parseObject(signJson, FundSignUpRequest.class);
        request = JSON.parseObject(json, SetPayPwdRequest.class);
        pwd = request.getTrsPassword();
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Modify_Login_czLoginPwd_xiugaipwd, this);
        navigationBar.setBackBtnVisibility(View.GONE);
        navigationBar.setTitle("设置支付密码");
        btnNext.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmPwd = getString(inputStack);
                        if (!TextUtils.equals(pwd, confirmPwd)) {
                            ToastUtil.toast(context, "2次密码输入不一致，请重新输入");
                            inputStack.clear();
                            clear();
//                            setIvPwd(false, ids);
                            return;
                        }
                        toSetPayPwd();
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
    }

    private void toSetPayPwd() {

        request.setTrsPassword(encrypt(pwd).toUpperCase());
        request.setConfirmTrsPassword(encrypt(confirmPwd).toUpperCase());
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader,
                                   JSONObject responseData) {
                hideProgressDialog();
                if (returnHeader.isSuccess()) {
                    if (signUpRequest == null) {
                        //找回密码成功，跳转到购买页面继续购买
                        ToastUtil.toast(context, "密码设置成功");
                        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Modify_Login_czLoginPwd_confirmxiugai, context);
                        BusinessLauncher.getInstance().clearTop(InputPayPwdDialogActivity.class);
                    } else {//来自于理财开户页面
                        signUpRequest.setTrsPassword(request.getTrsPassword());
                        AddUserQuestionActivity.open(context, signUpRequest);
                    }
                } else {
                    ToastUtil.toast(context, returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();

            }
        };
        FinanceRequestManager.getInstance().setPayPwd(request, listener);
    }

    private String encrypt(String data) {
        String retData = CommonEncrypt
                .pinKeyDesRsaEncrypt(ApplicationEx.getInstance().getUser().getTerminalId(), data);
        return retData;
    }

    public String getString(Stack<String> stack) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> iterator = stack.iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next());
        }
        return stringBuilder.toString();
    }

}
