package com.lakala.shoudan.activity.accountmanagement;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.lakala.core.http.HttpResponseHandler;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.request.LoginRequestFactory;
import com.lakala.platform.request.RegisterRequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResponseCode;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.login.LoginActivity;
import com.lakala.shoudan.common.ConstValues;
import com.lakala.shoudan.component.PopupWindowFactory;
import com.lakala.ui.dialog.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 注册 校验验证码
 * Created by ZhangMY on 2015/3/11.
 */
public class UserRegisterActivity extends InputPhoneGetSmsModuleActivity {

    @Override
    protected void getSmsCode() {
        getReigsterCheckCode();
    }

    @Override
    protected void handerNext() {
        // 注册时下一步校验验证码
        checkCodeVerify();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationBar.setTitle("注册");
        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Login_11, this);
    }

    @Override
    protected void showEditTextTips(ImageView iconView, int width) {
        mPopupWindow = PopupWindowFactory.createEditPopupWindowTips(iconView, width, UserRegisterActivity.this, getResources().getString(R.string.check_phone_edit_tips));
    }

    protected void getReigsterCheckCode() {
        if (validatePhoneNum()) {//验证输入的手机号
            RegisterRequestFactory.createGetVerticationCode(this, mUserName, mGetDeviceCheckCodeHandler).execute();
        }
    }

    ;

    private void checkCodeVerify() {

        LoginRequestFactory.createCheckCodeVerifyRequest(token, mUserName, smsCode, new ResultDataResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {

                if (ResponseCode.SuccessCode.equals(resultServices.retCode)) {
                    try {
                        JSONObject data = new JSONObject(resultServices.retData);

                        token = data.optString("btoken");
                        resetPassword();
                    } catch (JSONException e) {

                    }

                } else {
                    ToastUtil.toast(UserRegisterActivity.this, resultServices.retMsg);
                }

            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {

                ToastUtil.toast(UserRegisterActivity.this, R.string.socket_fail);
            }
        }), this).execute();
    }

    @Override
    protected void resetPassword() {
        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Login_6, this);
        Intent intent = new Intent(UserRegisterActivity.this, RegisterSetPasswordActivity.class);
        intent.putExtra(ConstValues.IntentKey.LoingName, mUserName);
        intent.putExtra(ConstValues.IntentKey.SMS_CODE, smsCode);
        intent.putExtra(ConstValues.IntentKey.BTOKEN, token);
        startActivity(intent);
    }

    private HttpResponseHandler mGetDeviceCheckCodeHandler = new ResultDataResponseHandler(new ServiceResultCallback() {
        @Override
        public void onSuccess(ResultServices resultServices) {
            try {
                if (ResponseCode.SuccessCode.equals(resultServices.retCode)) {
                    LogUtil.print(resultServices.retMsg);
                    JSONObject jsonObject = new JSONObject(resultServices.retData);
                    token = jsonObject.optString("btoken");
                    mCountDownInputBoxView.startCountdown();
                } else if ("050401".equals(resultServices.retCode)) {

                    //该手机号已注册过或已与拉卡拉账号绑定过，请直接登录
                    showRegisteredDialog();

                } else {
                    ToastUtil.toast(UserRegisterActivity.this, resultServices.retMsg);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onEvent(HttpConnectEvent connectEvent) {
            if (connectEvent != HttpConnectEvent.RESPONSE_ERROR)
                //连接异常处理
                return;
        }
    });

    private void showRegisteredDialog() {

        //该手机号已注册过或已与拉卡拉账号绑定过，请直接登录
        AlertDialog alertDialog = new AlertDialog();
        alertDialog.setTitle(getString(R.string.alert));
        alertDialog.setMessage(getString(R.string.already_registered));
        alertDialog.setButtons(new String[]{getString(R.string.cancel), getString(R.string.login)});
        alertDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                switch (index) {
                    case 0:
                        dialog.dismiss();
                        //Reset Page
                        break;
                    case 1:
                        startActivity(new Intent(UserRegisterActivity.this, LoginActivity.class).putExtra(ConstValues.IntentKey.LoingName, mUserName));
                        finish();
                        break;
                }
            }
        });
        alertDialog.show(this.getSupportFragmentManager());
    }
}
