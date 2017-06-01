package com.lakala.shoudan.activity.accountmanagement;

import android.content.Intent;
import android.widget.ImageView;

import com.lakala.core.http.HttpResponseHandler;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.request.ResetPasswordFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResponseCode;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.common.ConstValues;
import com.lakala.shoudan.component.PopupWindowFactory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 忘记密码  校验验证码
 * Created by ZhangMY on 2015/1/13.
 */
public class ForgotPasswordActivity extends InputPhoneGetSmsModuleActivity{

    @Override
    protected void resetPassword() {
        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Login_8,this);
        Intent intent = new Intent(ForgotPasswordActivity.this,ResetPasswordActivity.class);
        intent.putExtra(ConstValues.IntentKey.LoingName,mUserName);
        intent.putExtra(ConstValues.IntentKey.SMS_CODE,smsCode);
        intent.putExtra(ConstValues.IntentKey.BTOKEN,token);
        startActivity(intent);
    }

    @Override
    protected void getSmsCode() {
        getForgotPasswordCheckcode();
    }

    @Override
    protected void handerNext() {
        checkCodeVerify();
    }

    @Override
    protected void showEditTextTips(ImageView iconView, int width) {
        mPopupWindow = PopupWindowFactory.createEditPopupWindowTips(iconView,width, ForgotPasswordActivity.this, getResources().getString(R.string.check_phone_edit_tips_forgot_password));
    }

    protected void getForgotPasswordCheckcode(){
        if(validatePhoneNum()){//验证输入的手机号
            ResetPasswordFactory.createGetCheckCodeRequest(
                    ForgotPasswordActivity.this,
                    mUserName,
                    mGetForgotPasswordCheckCodeHandler).execute();
        }
    };

    private HttpResponseHandler mGetForgotPasswordCheckCodeHandler = new ResultDataResponseHandler( new ServiceResultCallback() {
        @Override
        public void onSuccess(ResultServices resultServices) {
            try{
                if(ResponseCode.SuccessCode.equals(resultServices.retCode)){
                    LogUtil.print(resultServices.retMsg);
                    JSONObject jsonObject = new JSONObject(resultServices.retData);
                    token = jsonObject.optString("btoken");
                    mCountDownInputBoxView.startCountdown();
                }else{
                    ToastUtil.toast(ForgotPasswordActivity.this, resultServices.retMsg);
                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onEvent(HttpConnectEvent connectEvent) {
            if(connectEvent != HttpConnectEvent.RESPONSE_ERROR)
                //连接异常处理
                return;
        }
    });

    private void checkCodeVerify(){

        ResetPasswordFactory.createCheckCheckcodeRequest(
                ForgotPasswordActivity.this,
                mUserName,
                token,
                smsCode,
                new ResultDataResponseHandler(new ServiceResultCallback() {
                    @Override
                    public void onSuccess(ResultServices resultServices) {
                        if (ResponseCode.SuccessCode.equals(resultServices.retCode)) {
                            try {
                                JSONObject data  = new JSONObject(resultServices.retData);

                                token = data.optString("btoken");
                                resetPassword();
                            } catch (JSONException e) {

                            }

                        } else {
                            ToastUtil.toast(ForgotPasswordActivity.this, resultServices.retMsg);
                        }
                    }

                    @Override
                    public void onEvent(HttpConnectEvent connectEvent) {
                        LogUtil.print(connectEvent.getDescribe());
                        ToastUtil.toast(ForgotPasswordActivity.this, R.string.network_fail);
                    }
                }
                )).execute();

    }
}
