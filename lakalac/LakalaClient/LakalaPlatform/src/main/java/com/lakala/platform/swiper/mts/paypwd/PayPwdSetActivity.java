package com.lakala.platform.swiper.mts.paypwd;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lakala.core.http.HttpRequest;
import com.lakala.core.http.IHttpRequestEvents;
import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.R;
import com.lakala.platform.activity.BaseActionBarActivity;
import com.lakala.platform.common.ApplicationEx;

/**
 * 设置支付密码
 * <p/>
 * Created by jerry on 14-1-17.
 */
public class PayPwdSetActivity extends BaseActionBarActivity implements TextWatcher {

    private TextView tvNotice;
    private PayPwdInputView mPasswordEditText;
    /**
     * 支付密码:第一次,确认
     */
    private String TrsPassword,ConfirmPassword;

    private Button submitButton;

    private IHttpRequestEvents responseHandler = new IHttpRequestEvents(){

        @Override
        public void onSuccess(HttpRequest request) {
            super.onSuccess(request);
            ToastUtil.toast(PayPwdSetActivity.this, "支付密码设置成功");
            ApplicationEx.getInstance().getUser().setTrsPasswordFlag(true);
            ApplicationEx.getInstance().getUser().save();
            //如果还没有设置密保问题,跳转到设置密保问题界面去(因为修改支付密码也用的这个界面)
            if (!ApplicationEx.getInstance().getUser().isQuestionFlag()) {
                startActivity(new Intent(PayPwdSetActivity.this, PayPwdSetQuestionActivity.class));
            }
            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void onFinish(HttpRequest request) {
            super.onFinish(request);
            //调整ui
            submitButton.setVisibility(View.GONE);
            TrsPassword = ConfirmPassword = null;
            tvNotice.setText(R.string.plat_input_pay_password_prompt);
            mPasswordEditText.setText(null);
            submitButton.setText(R.string.com_next);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plat_activity_pay_pwd_set);
        navigationBar.setTitle(R.string.plat_set_pay_password);

        submitButton = (Button) findViewById(R.id.id_common_guide_button);
        submitButton.setEnabled(false);
        submitButton.setText(R.string.com_next);
        submitButton.setOnClickListener(this);
        //调整ui与ios相同
        submitButton.setVisibility(View.GONE);

        tvNotice            = (TextView) findViewById(R.id.notice);

        mPasswordEditText   = (PayPwdInputView) findViewById(R.id.id_register_input_pay_pwd);
        mPasswordEditText.addTextChangedListener(this);
        mPasswordEditText.openCKbd();

    }

    @Override
    protected void onViewClick(View view) {
        super.onViewClick(view);
        if (!isInputValid() || view.getId() != R.id.id_common_guide_button) {
            return;
        }
        String pwd = mPasswordEditText.getPassword();
        //第一次输入完成
        if (TrsPassword == null){
            TrsPassword = pwd;
            tvNotice.setText(R.string.plat_input_pay_password_prompt_again);
            submitButton.setText(R.string.com_complete);
            mPasswordEditText.clear();
        //第二次输入验证通过
        } else if (ConfirmPassword == null){
            ConfirmPassword = pwd;
            submit();
        }
    }

    /**
     * 输入是否合法
     */
    private boolean isInputValid() {
        String text = mPasswordEditText.getPassword();
        if (text == null || StringUtil.isEmpty(text)) {
            return false;
        }
        if (text.trim().length() != 6) {
            ToastUtil.toast(this, R.string.plat_input_pay_password_error);
            return false;
        }

        if (!PasswordSecurityUtil.checkPayPasswordSecurity(this, text)){
            mPasswordEditText.clear();
            return false;
        }

        //修改支付密码时，与原有支付密码比较，如果相同，则重新输入。
        if(isSameNewPassAndOldPass(text.trim())){
            mPasswordEditText.clear();
            return false;
        }
        //第二次输入验证不通过
        if (TrsPassword != null && !TrsPassword.equals(text.trim())){
            ToastUtil.toast(this, R.string.plat_password_not_same);
            mPasswordEditText.clear();
            return false;
        }
        return true;
    }

    /**
     * 当用户修改支付密码时，获取原来的支付密码
     * @return
     */
    private String getOldPassword(){
        String oldPassword = "";
        if(getIntent() != null){
            oldPassword = getIntent().hasExtra("oldPassword")?getIntent().getStringExtra("oldPassword"):"";
        }
        return oldPassword;
    }

    /**
     * 修改支付密码时，判断用户第一次输入的新的支付密码是否与原有支付密码相同，
     * 如果相同，则给予提示，让用户重新输入，如果不相同，则继续下一步。
     * @param newPassword  用户输入的新的密码明文
     * @return
     */
    private boolean isSameNewPassAndOldPass(String newPassword){
       boolean isResult = false;
       String oldPassword = getOldPassword();
       if(!StringUtil.isEmpty(oldPassword) && newPassword.equals(oldPassword)){
              ToastUtil.toast(this, this.getString(R.string.plat_password_change_password_prompt));
              isResult = true;
       }
       return  isResult;
    }

    /**
     * 提交请求验证
     */
    private void submit(){
        /* 当修改支付密码时，接口中
         * "TrsPassword" = 原有支付密码的密文；
         * "NewTrsPassword" = "ConfirmTrsPassword" = 用户两次输入的新密码的密文。
         *
         *  当第一次设置支付密码，以及找回支付密码 时，接口中
         *  "TrsPassword" = "ConfirmTrsPassword" = 用户两次输入的密码的密文；
         *  "NewTrsPassword" = ""。
         * */
        String oldPassword = "",newPassword = "" ;
        oldPassword = getOldPassword();
        if(StringUtil.isEmpty(oldPassword)){
            oldPassword = TrsPassword ;
            newPassword = "";
        }else{
            newPassword = TrsPassword ;
        }
        RegisterRequestFactory.createSetPayPwd(this, oldPassword,newPassword,ConfirmPassword).setIHttpRequestEvents(responseHandler).execute();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean enable = s != null && s.toString().trim().length() == 6;
        submitButton.setEnabled(enable);
        //调整ui与ios相同
        if (TrsPassword == null && enable){                 //第一次不显示button
            onViewClick(submitButton);
        } else if (TrsPassword != null && enable && isInputValid()){ // 第二次验证通过了才显示提交按钮
            mPasswordEditText.closeCKbd();
            submitButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

}
