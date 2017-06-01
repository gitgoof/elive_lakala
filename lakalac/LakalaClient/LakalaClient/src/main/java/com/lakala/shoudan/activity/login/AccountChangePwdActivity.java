package com.lakala.shoudan.activity.login;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.CommonEncrypt;
import com.lakala.platform.common.securitykeyboard.SecurityKeyboardUtil;
import com.lakala.platform.request.ResetPasswordFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResponseCode;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.UniqueKey;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.ui.dialog.BaseDialog;

/**
 * 密码修改
 * @author More
 *
 */
public class AccountChangePwdActivity  extends AppBaseActivity{
    /**     密码    */
    private EditText oldPasswordEdit;
    private EditText passwordEdit;
    private EditText passwordTwiceEdit;
    /**    提交      */
    private TextView postBtn;

    private BaseDialog dialog2;

    private CommonServiceManager manager;
    private String phone=null;
    private String info=null;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        //标题
        navigationBar.setTitle(R.string.edit_password);

        //密码
        oldPasswordEdit=(EditText) findViewById(R.id.edit_old_psw);
        oldPasswordEdit.setHint(R.string.input_passwd);
        oldPasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        InputFilter.LengthFilter[] filters0 = { new InputFilter.LengthFilter(20) };
        oldPasswordEdit.setFilters(filters0);
        
        
        passwordEdit=(EditText) findViewById(R.id.edit_new_psw1);
        passwordEdit.setHint(R.string.please_input_new_password);
        passwordEdit.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        InputFilter.LengthFilter[] filters1 = { new InputFilter.LengthFilter(20) };
        passwordEdit.setFilters(filters1);
        
        passwordTwiceEdit=(EditText) findViewById(R.id.edit_new_psw2);
        passwordTwiceEdit.setHint(R.string.please_input_new_password_confirm);
        passwordTwiceEdit.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        InputFilter.LengthFilter[] filters2 = { new InputFilter.LengthFilter(20) };
        passwordTwiceEdit.setFilters(filters2);
        
        postBtn=(TextView) findViewById(R.id.post_btn);
        postBtn.setOnClickListener(this);
        phone=ApplicationEx.getInstance().getSession().getUser().getLoginName();

    }
    private void updateLoginPassword(){
        String oldPassword =oldPasswordEdit.getText().toString().trim();
        String newPassword = passwordTwiceEdit.getText().toString().trim();
        ResetPasswordFactory.createUpdatePasswordRequest(this, oldPassword, newPassword, new ResultDataResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    dialog2 = DialogCreator.createConfirmDialog(context, "确定", "新密码设置成功");
                    dialog2.show();
                    showNext();
                } else {
                    ToastUtil.toast(context, resultServices.retMsg, Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
//                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        })).execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_btn:
                if (isInputValid()) {
                    disableView(postBtn);
                    updateLoginPassword();
                }
                break;
        }
    }
    protected boolean isInputValid() {
        String password=passwordEdit.getText().toString();
        String passwordTwice=passwordTwiceEdit.getText().toString();
        String oldPassword = oldPasswordEdit.getText().toString();
        if (oldPassword.length()==0) {
            ToastUtil.toast(context,R.string.input_passwd);
            return false;
        }
        if ((password.length() < 6 || password.length() > 20)&& (passwordTwice.length() < 6 || passwordTwice.length() > 20)) {
            ToastUtil.toast(context, R.string.plat_plese_input_your_password_error);
            return false;
        }else if  (!password.equals(passwordTwice)) {
            ToastUtil.toast(context, R.string.password_new_not_same);
            return false;
        }else if(password.equals(oldPassword)){
            ToastUtil.toast(context,R.string.password_new_old_same);
            return false;
        }
        if (Util.checkPWLevel(password).equals("BAD")) {
            BaseDialog dialog = DialogCreator.createConfirmDialog(context, "确定", "登录密码过于简单，至少包含英文字母、数字和符号中的两种。");
            dialog.show();
            return false;
        }

        return true;
    }
    /**
     * 跳转到登录界面
     */
    private void showNext() {

        Intent intent = new Intent(this,LoginActivity.class);
        intent.putExtra(UniqueKey.phoneNumber, phone);
        startActivity(intent);
        finish();
    }
}
