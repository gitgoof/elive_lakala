package com.lakala.shoudan.activity.accountmanagement.changepwd;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.lakala.shoudan.util.HintFocusChangeListener;
import com.lakala.ui.component.LabelEditText;
import com.lakala.ui.dialog.AlertDialog;

/**
 * Created by ZhangMY on 2015/3/12.
 * 输入新密码界面
 */
public class InputNewPasswordActivity extends AppBaseActivity {

    private SecurityEditText editNewPassword;
    private SecurityEditText editNewPasswordConfirm;
    private SecurityEditText editOldPassword;
    final static String UPDATEODLD="upto";
    final static String UPDATENEW="uptn";
    final static String UPDATETWICE="uptt";
    Button btnSure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password_security);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        initUI();

    }

    protected void initUI() {
        navigationBar.setTitle("修改密码");

//        navigationBar.setBackBtnVisibility(View.GONE);

        editNewPassword = (SecurityEditText) findViewById(R.id.edit_new_password);
        editNewPasswordConfirm = (SecurityEditText) findViewById(R.id.edit_confirm_password);
        editOldPassword = (SecurityEditText) findViewById(R.id.edit_old_password);
        editOldPassword.setSecurityManager(SecurityKeyboardUtil.lklPassword(editOldPassword,UPDATEODLD));
        editNewPassword.setSecurityManager(SecurityKeyboardUtil.lklPassword(editNewPassword,UPDATENEW));
        editNewPasswordConfirm.setSecurityManager(SecurityKeyboardUtil.lklPassword(editNewPasswordConfirm,UPDATETWICE));


        btnSure=(Button)findViewById(R.id.btn_sure);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputValidate()) {
                    //
                    updateLoginPassword();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        editOldPassword.onDestroy();
        editNewPassword.onDestroy();
        editNewPasswordConfirm.onDestroy();
        super.onDestroy();
    }

    private void updateLoginPassword() {
        String oldPassword = CommonEncrypt.loginEncrypt(editOldPassword.getText(UPDATEODLD));
        String newPassword = CommonEncrypt.loginEncrypt(editNewPassword.getText(UPDATENEW));

        ResetPasswordFactory.createUpdatePasswordRequest(this, oldPassword, newPassword, new ResultDataResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (ResponseCode.SuccessCode.equals(resultServices.retCode)) {
                    handleResult();
                } else {
                    ToastUtil.toast(InputNewPasswordActivity.this, resultServices.retMsg, Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
            }
        })).execute();
    }

    protected void handleResult() {
        ShoudanStatisticManager.getInstance()
                .onEvent(ShoudanStatisticManager.Modify_Login_Pwd, context);
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
                    Intent intent = new Intent(InputNewPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        alertDialog.show(getSupportFragmentManager());

    }

    /**
     * 输入内容校验
     *
     * @return
     */
    private boolean inputValidate() {

        String oldPassword = editOldPassword.getText().toString();
        oldPassword=editOldPassword.getText(UPDATEODLD);
        if (TextUtils.isEmpty(oldPassword)) {
            ToastUtil.toast(context, getString(R.string.input_lakala_password), Toast.LENGTH_SHORT);
            return false;
        }
        int oldLen = oldPassword.length();
        if (oldLen < 6 || oldLen > getResources().getInteger(R.integer.max_password_limit)) {
            ToastUtil.toast(this, "请输入正确的6~32位旧密码", Toast.LENGTH_SHORT);
            return false;
        }


        String password = editNewPassword.getText().toString().trim();
        password=editNewPassword.getText(UPDATENEW);

        StringUtil.PasswordLvl passwordLvl = StringUtil.checkPWLevel(password, getResources().getInteger(R.integer.max_password_limit), getResources().getInteger(R.integer.min_password_limit));

        if (passwordLvl == StringUtil.PasswordLvl.EMPTY) {
            ToastUtil.toast(InputNewPasswordActivity.this, getString(R.string.input_lakala_password), Toast.LENGTH_SHORT);
            return false;
        }

        if (passwordLvl == StringUtil.PasswordLvl.LENGTH_ERROR) {
            ToastUtil.toast(this, getString(R.string.plat_plese_input_your_password_error), Toast.LENGTH_SHORT);
            return false;
        }

        if (passwordLvl == StringUtil.PasswordLvl.SIMPLE) {
            ToastUtil.toast(this, getString(R.string.password_is_too_simple), Toast.LENGTH_SHORT);
            return false;
        }

        if (passwordLvl == StringUtil.PasswordLvl.CHAR_REPEAT_4_TIMES) {
            ToastUtil.toast(this, getString(R.string.password_chars_repeat), Toast.LENGTH_SHORT);
            return false;
        }

        String passWordConfirm = editNewPasswordConfirm.getText().toString().trim();
        passWordConfirm=editNewPasswordConfirm.getText(UPDATETWICE);
        if (TextUtils.isEmpty(passWordConfirm)) {
            ToastUtil.toast(InputNewPasswordActivity.this, getString(R.string.managepwd_input_login_password_again), Toast.LENGTH_SHORT);
            return false;
        }

        if (!password.equals(passWordConfirm)) {
            ToastUtil.toast(InputNewPasswordActivity.this, getString(R.string.managepwd_input_confirm_login_password_vaild), Toast.LENGTH_SHORT);
            return false;
        }

        return true;
    }

}
