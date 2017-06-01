package com.lakala.shoudan.activity.accountmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.core.http.HttpResponseHandler;
import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.CommonEncrypt;
import com.lakala.platform.common.securitykeyboard.SecurityEditText;
import com.lakala.platform.common.securitykeyboard.SecurityKeyboardUtil;
import com.lakala.platform.request.RegisterRequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResponseCode;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.login.LoginActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.common.ConstValues;

/**
 * Created by ZhangMY on 2015/3/11.
 * 注册设置密码
 */
public class RegisterSetPasswordActivity extends AppBaseActivity {

    protected SecurityEditText editPassword;
    protected SecurityEditText editPasswordConfirm;
    private Button btnSure;
    final static String SETPWD1="setp1";
    final static String SETPWD2="setp2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword_security);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        navigationBar.setTitle("设置登录密码");
        navigationBar.setBackBtnVisibility(View.GONE);
        initView();
    }

    @Override
    public void onBackPressed() {
        return;
    }


    private void initView() {
        editPassword = (SecurityEditText) findViewById(R.id.edit_password);
        editPassword.setSecurityManager(SecurityKeyboardUtil.lklPassword(editPassword,SETPWD1));
        editPasswordConfirm = (SecurityEditText) findViewById(R.id.edit_confirm_password);
        editPasswordConfirm.setSecurityManager(SecurityKeyboardUtil.lklPassword(editPasswordConfirm,SETPWD2));


        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() >= 8 && editPasswordConfirm.getText().toString().trim().length() >= 8) {
                    btnSure.setEnabled(true);
                } else {
                    btnSure.setEnabled(false);
                }

            }
        });
        editPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() >= 8 && editPassword.getText().toString().trim().length() >= 8) {
                    btnSure.setEnabled(true);
                } else {
                    btnSure.setEnabled(false);
                }

            }
        });
        btnSure = (Button) findViewById(R.id.btn_sure);
        btnSure.setEnabled(false);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
        TextView protoct = (TextView) findViewById(R.id.tv_protoct);
        protoct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProtol();
            }
        });

    }

    private void showProtol() {

        ProtocalActivity.open(this, ProtocalType.SERVICE_PROTOCAL);

    }

    private void commitPassword() {
        if (inputValidate()) {
            register();
        }
    }
    protected void handleResult() {
//        ToastUtil.toast(RegisterSetPasswordActivity.this, getString(R.string.register_success), Toast.LENGTH_SHORT);
        String userName = getIntent().getStringExtra(ConstValues.IntentKey.LoingName);
       // String passWord = editPassword.getText().toString().trim();

        //跳转自动登陆页面
        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Login_7,this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(ConstValues.IntentKey.LoingName, userName);
        startActivity(intent);
        ToastUtil.toast(this.getApplication(), "注册成功");
    }

    /**
     * 注册
     */
    private void register(){
        String loginName = getIntent().getStringExtra(ConstValues.IntentKey.LoingName);
        String token = getIntent().getStringExtra(ConstValues.IntentKey.BTOKEN);
        String smsCode = getIntent().getStringExtra(ConstValues.IntentKey.SMS_CODE);

        RegisterRequestFactory.createRegisterRequest(this,
                loginName,
                CommonEncrypt.loginEncrypt(editPassword.getText(SETPWD1)),
                token,
                smsCode, registerResponseHandler).execute();
    }

    @Override
    protected void onDestroy() {
        editPassword.onDestroy();
        editPasswordConfirm.onDestroy();
        super.onDestroy();
    }
    private HttpResponseHandler registerResponseHandler = new ResultDataResponseHandler(new ServiceResultCallback() {
        @Override
        public void onSuccess(ResultServices resultServices) {
            if(ResponseCode.SuccessCode.equals(resultServices.retCode)){
                handleResult();
            }else{
                ToastUtil.toast(RegisterSetPasswordActivity.this, resultServices.retMsg, Toast.LENGTH_SHORT);
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
     * 输入内容校验
     *
     * @return
     */
    private boolean inputValidate() {
        String password = editPassword.getText(SETPWD1);
        StringUtil.PasswordLvl passwordLvl = StringUtil.checkPWLevel(password, getResources().getInteger(R.integer.max_password_limit), getResources().getInteger(R.integer.min_password_limit));

        switch (passwordLvl) {

            case SIMPLE:
                ToastUtil.toast(this, getString(R.string.password_is_too_simple), Toast.LENGTH_SHORT);
                break;
            case LENGTH_ERROR:
                ToastUtil.toast(this, getString(R.string.plat_plese_input_your_password_error), Toast.LENGTH_SHORT);
                break;
            case CHAR_REPEAT_4_TIMES:
                ToastUtil.toast(this, getString(R.string.password_chars_repeat), Toast.LENGTH_SHORT);
                break;
            case EMPTY:
                ToastUtil.toast(RegisterSetPasswordActivity.this, getString(R.string.input_lakala_password), Toast.LENGTH_SHORT);
                break;
            case NORMAL:
                break;
        }

        if (passwordLvl != StringUtil.PasswordLvl.NORMAL) {
            return false;
        }


        String passWordConfirm =editPasswordConfirm.getText(SETPWD2);
        if (TextUtils.isEmpty(passWordConfirm)) {
            ToastUtil.toast(RegisterSetPasswordActivity.this, getString(R.string.managepwd_input_login_password_again), Toast.LENGTH_SHORT);
            return false;
        }

        if (!password.equals(passWordConfirm)) {
            ToastUtil.toast(RegisterSetPasswordActivity.this, getString(R.string.managepwd_input_confirm_login_password_vaild), Toast.LENGTH_SHORT);
            return false;
        }

        //注册 需要判断是否已经同意了服务协议
        if (!((CheckBox) findViewById(R.id.cb_agreen_pro)).isChecked()) {
            ToastUtil.toast(RegisterSetPasswordActivity.this, getString(R.string.please_check_agreen), Toast.LENGTH_SHORT);
            return false;
        }

        return true;
    }
}
