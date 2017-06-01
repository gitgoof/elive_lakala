package com.lakala.shoudan.activity.login;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.core.http.HttpResponseHandler;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.request.LoginRequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResponseCode;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.accountmanagement.GetSmsModuleActivity;
import com.lakala.shoudan.common.ConstValues;
import com.lakala.shoudan.component.BackLinearLayout;
import com.lakala.shoudan.util.IMEUtil;
import com.lakala.shoudan.util.LoginUtil;
import com.lakala.ui.common.NextStepTextWatcherControl;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 登录成功之后，需要绑定当前的手机设备
 * Created by ZhangMY on 2015/3/11.
 */
public class LoginSuccessNeedBindDeviceActivity extends GetSmsModuleActivity {
    protected BackLinearLayout mBackRelativeLayout;
    protected int layoutY;

    @Override
    protected void initSpecialView() {
        //登录成功 需要手机验证

//        ((TextView)findViewById(R.id.main_title)).setText(getResources().getString(R.string.phone_verifaction));
        editPhone.setVisibility(View.GONE);

        initData();//获取验证码

//        ((TextView)findViewById(R.id.id_common_guide_button)).setText(getResources().getString(R.string.sure));
        HashMap<EditText,Integer> editMap = new HashMap<EditText,Integer>();
        editMap.put(mCountDownInputBoxView.getVerifycodeEdit(),6);
        new NextStepTextWatcherControl(editMap,btnNext);

        int dimenTwenty = (int)getResources().getDimension(R.dimen.dimen_20);
        mCountDownInputBoxView.getVerifycodeEdit().setPadding((int) getResources().getDimension(R.dimen.dimen_20), dimenTwenty, 0, dimenTwenty);
        mCountDownInputBoxView.getVerifycodeEdit().setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        mCountDownInputBoxView.setSelected(hasFocus);
                        if(hintChangeListener != null){
                            hintChangeListener.onFocusChange(v,hasFocus);
                        }
                    }
                }
        );
        editPhone.setTag(true);

        if(!TextUtils.isEmpty(getIntent().getStringExtra(ConstValues.IntentKey.LoingName))){
            findViewById(R.id.textIndicator).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.textIndicator)).setText(String.format(getString(R.string.sms_verify_title),getIntent().getStringExtra(ConstValues.IntentKey.LoingName)));
        }

//        mBackRelativeLayout = (BackLinearLayout) findViewById(R.id.back_layout);
//        mBackRelativeLayout.setmSearchActivity(this);
//        mBackRelativeLayout.setTag(true);
//        mBackRelativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//
//                if((Boolean)mBackRelativeLayout.getTag()){
//
//                    View backImage = findViewById(R.id.back);
//
//                    ScreenUtil.getScrrenWidthAndHeight(LoginSuccessNeedBindDeviceActivity.this);
//                    int height = Parameters.screenHeight-Parameters.statusBarHeight-backImage.getHeight();//marginTop的大小
//                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mBackRelativeLayout.getLayoutParams();
//                    params.height = height;
//
//                    mBackRelativeLayout.setLayoutParams(params);
//
//                    View layoutBottom = findViewById(R.id.layout_bottom);
//                    LinearLayout.LayoutParams bottomParam = (LinearLayout.LayoutParams) layoutBottom.getLayoutParams();
//                    bottomParam.topMargin = Parameters.screenHeight-Parameters.statusBarHeight-backImage.getHeight()-layoutBottom.getHeight();
//                    layoutBottom.setLayoutParams(bottomParam);
//
//                    mBackRelativeLayout.setTag(false);
//                    layoutY = Parameters.statusBarHeight+backImage.getHeight()+bottomParam.topMargin;
//                }
//
//            }
//        });
    }

    @Override
    protected void getSmsCode() {
        getDeviceCheckCode();
    }

    @Override
    protected void handerNext() {
        sendDeviceCheckCode();
    }

    @Override
    protected boolean isInputValidate() {
       return validateSmscodeAndToken();
    }

    @Override
    protected void showEditTextTips(ImageView iconView, int width) {
        //手机输入框不显示，无需处理
    }

    private void initData(){
        token = getIntent().getStringExtra(ConstValues.IntentKey.BTOKEN);
        if(!TextUtils.isEmpty(token)){
            mCountDownInputBoxView.startCountdown();
        }
    }

    /**
     * 绑定设备获取授权码
     */
    private void getDeviceCheckCode(){
        LoginRequestFactory.createDeviceCheckCode(getIntent().getStringExtra(ConstValues.IntentKey.LoingName), mGetDeviceCheckCodeHandler, LoginSuccessNeedBindDeviceActivity.this).execute();
    }

    private HttpResponseHandler mGetDeviceCheckCodeHandler = new ResultDataResponseHandler( new ServiceResultCallback() {
        @Override
        public void onSuccess(ResultServices resultServices) {
            try{
                if(ResponseCode.SuccessCode.equals(resultServices.retCode)){
                    LogUtil.print(resultServices.retMsg);
                    JSONObject jsonObject = new JSONObject(resultServices.retData);
                    token = jsonObject.optString("btoken");
                    mCountDownInputBoxView.startCountdown();
                }else{
                    ToastUtil.toast(LoginSuccessNeedBindDeviceActivity.this, resultServices.retMsg);
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

    /**
     * 发送绑定设备
     */
    private void sendDeviceCheckCode(){
        String loginName = getIntent().getStringExtra(ConstValues.IntentKey.LoingName);
        String passWord = getIntent().getStringExtra(ConstValues.IntentKey.Password);//获取到的密码是jia加密后的
        LoginUtil.login(loginName, passWord, smsCode, token, LoginSuccessNeedBindDeviceActivity.this);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
