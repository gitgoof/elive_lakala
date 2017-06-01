package com.lakala.shoudan.activity.accountmanagement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.CommonEncrypt;
import com.lakala.platform.common.securitykeyboard.SecurityEditText;
import com.lakala.platform.common.securitykeyboard.SecurityKeyboardUtil;
import com.lakala.platform.request.ResetPasswordFactory;
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
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.common.ConstValues;
import com.lakala.ui.component.LabelEditText;
import com.lakala.ui.dialog.AlertDialog;

/**
 * 找回密码 重设密码，
 * Created by ZhangMY on 2015/1/14.
 */
public class ResetPasswordActivity extends AppBaseActivity {

    protected SecurityEditText editPassword;
    protected SecurityEditText editPasswordConfirm;
    private Button btnSure;
final static String RESETPWD1="resetp1";
final static String RESETPWD2="resetp2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword_security);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Modify_Login_czLoginPwd, context);
        navigationBar.setTitle("设置登录密码");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        initView();

    }

    @Override
    protected void onDestroy() {
        editPassword.onDestroy();
        editPasswordConfirm.onDestroy();
        super.onDestroy();
    }

    private void initView() {
        editPassword = (SecurityEditText) findViewById(R.id.edit_password);
        editPassword.setSecurityManager(SecurityKeyboardUtil.lklPassword(editPassword,RESETPWD1));
        editPasswordConfirm = (SecurityEditText) findViewById(R.id.edit_confirm_password);
        editPasswordConfirm.setSecurityManager(SecurityKeyboardUtil.lklPassword(editPasswordConfirm,RESETPWD2));


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
                commitPassword();
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
            resetPassword();
        }
    }

    protected void handleResult() {
        final AlertDialog alertDialog = new AlertDialog();
        alertDialog.setMessage(getString(R.string.login_again));
        alertDialog.setTitle(getString(R.string.pwd_setting_success));
        alertDialog.setButtons(new String[]{getResources().getString(R.string.sure)});
        alertDialog.setCancelable(false);
        alertDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                if (index == 0) {
                    dialog.dismiss();
                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Modify_Login_Pwd, context);
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    intent.putExtra(Constants.fromReset, "fromReset");
                    startActivity(intent);
                    finish();
                }
            }
        });
        alertDialog.show(getSupportFragmentManager());
    }


    private void resetPassword() {
        String loginName = getIntent().getStringExtra(ConstValues.IntentKey.LoingName);
        String token = getIntent().getStringExtra(ConstValues.IntentKey.BTOKEN);
        String password = CommonEncrypt.loginEncrypt(editPassword.getText(RESETPWD1));

        ResetPasswordFactory.createResetPasswordRequest(
                this,
                loginName,
                password,
                token,
                new ResultDataResponseHandler(new ServiceResultCallback() {
                    @Override
                    public void onSuccess(ResultServices resultServices) {
                        if (ResponseCode.SuccessCode.equals(resultServices.retCode)) {
                            handleResult();
                        } else {
                            ToastUtil.toast(ResetPasswordActivity.this, resultServices.retMsg, Toast.LENGTH_SHORT);
                        }
                    }

                    @Override
                    public void onEvent(HttpConnectEvent connectEvent) {
                        toastInternetError();
                    }
                })).execute();
    }

    /**
     * 输入内容校验
     *
     * @return
     */
    private boolean inputValidate() {
        String password = editPassword.getText().toString().trim();
        password=editPassword.getText(RESETPWD1);
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
                ToastUtil.toast(ResetPasswordActivity.this, getString(R.string.input_lakala_password), Toast.LENGTH_SHORT);
                break;
            case NORMAL:
                break;
        }

        if (passwordLvl != StringUtil.PasswordLvl.NORMAL) {
            return false;
        }


        String passWordConfirm = editPasswordConfirm.getText().toString().trim();
        passWordConfirm=editPasswordConfirm.getText(RESETPWD2);
        if (TextUtils.isEmpty(passWordConfirm)) {
            ToastUtil.toast(ResetPasswordActivity.this, getString(R.string.managepwd_input_login_password_again), Toast.LENGTH_SHORT);
            return false;
        }

        if (!password.equals(passWordConfirm)) {
            ToastUtil.toast(ResetPasswordActivity.this, getString(R.string.managepwd_input_confirm_login_password_vaild), Toast.LENGTH_SHORT);
            return false;
        }

        //注册 需要判断是否已经同意了服务协议
        if (!((CheckBox) findViewById(R.id.cb_agreen_pro)).isChecked()) {
            ToastUtil.toast(ResetPasswordActivity.this, getString(R.string.please_check_agreen), Toast.LENGTH_SHORT);
            return false;
        }

        return true;
    }
}
