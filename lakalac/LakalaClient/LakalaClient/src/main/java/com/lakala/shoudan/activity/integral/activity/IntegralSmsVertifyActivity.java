package com.lakala.shoudan.activity.integral.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.accountmanagement.GetSmsModuleActivity;
import com.lakala.shoudan.activity.accountmanagement.InputPhoneGetSmsModuleActivity;
import com.lakala.ui.common.NextStepTextWatcherControl;

import java.util.HashMap;

/**
 * Created by fengxuan on 2016/5/18.
 */
public class IntegralSmsVertifyActivity extends InputPhoneGetSmsModuleActivity{

    private TextView hint;

    @Override
    protected void initSpecialView() {

        btnNext.setEnabled(false);
        editPhone.setVisibility(View.GONE);

        mUserName = ApplicationEx.getInstance().getUser().getLoginName();

        hint = (TextView) findViewById(R.id.textIndicator);
        hint.setText(getString(R.string.integral_vertify));

        navigationBar.setTitle("短信验证");

        mCountDownInputBoxView.getVerifycodeEdit().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4){
                    btnNext.setEnabled(true);
                }else {
                    btnNext.setEnabled(false);
                }
            }
        });
    }

    @Override
    protected boolean isInputValidate() {
        if(!validateSmscodeAndToken()){
            return false;
        }
        return true;
    }

    @Override
    protected boolean validateSmscodeAndToken() {
        smsCode = mCountDownInputBoxView.getVerifycodeEdit().getText().toString().trim();
        if (StringUtil.isEmpty(smsCode)) {
            ToastUtil.toast(this, getString(R.string.please_input_mobile_vercode), Toast.LENGTH_SHORT);
            return false;
        }

        if(TextUtils.isEmpty(token)){
            ToastUtil.toast(this, getString(R.string.please_get_checkcode_first));
            return false;
        }
        int len = smsCode.length();
        if (len != 4) {
            ToastUtil.toast(this, getString(R.string.input_code_4_null), Toast.LENGTH_SHORT);
            return false;
        }
        return true;
    }

    @Override
    protected void resetPassword() {
    }

    @Override
    protected void getSmsCode() {
        showProgressWithNoMsg();
        BusinessRequest request = RequestFactory
                .getRequest(context, RequestFactory.Type.POINTS_GET_CODE);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    token = "token";
                    mCountDownInputBoxView.startCountdown();
                }else {
                    ToastUtil.toast(context,resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context,getString(R.string.socket_fail));
            }
        });
        request.execute();
    }

    @Override
    protected void handerNext() {
        checkCode(smsCode);
    }

    private void checkCode(String smsCode){
        showProgressWithNoMsg();
        final BusinessRequest request = RequestFactory
                .getRequest(context, RequestFactory.Type.POINTS_CHECK_CODE);
        HttpRequestParams params = request.getRequestParams();
        params.put("code",smsCode);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    Intent intent = new Intent(context,IntegralMainActivity.class);
                    startActivity(intent);
                }else {
                    ToastUtil.toast(context,resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context,getString(R.string.socket_fail));
            }
        });
        request.execute();
    }

    @Override
    protected void showEditTextTips(ImageView iconView, int width) {

    }
}
