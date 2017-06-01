package com.lakala.elive.user.activity;


import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.lakala.elive.R;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.UserReqInfo;
import com.lakala.elive.common.utils.EncodeUtil;
import com.lakala.elive.common.utils.UiUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.user.base.BaseActivity;


/**
 * 用户短信或者邮箱验证
 *
 * @author hongzhiliang
 */
public class UserPwdSetActivity extends BaseActivity {

    private Button pwdSetBtn;//用户认证提交按钮
    private EditText etLoginPwd,etLoginPwdTwo;  //手机号和密码重置输入框

    private LinearLayout llPwdCheckAlert;//密码强度提示

    private TextView tvPwdCheckLow;
    private TextView tvPwdCheckMiddle;
    private TextView tvPwdCheckHigh;

    private ToggleButton tbNewPwdBtn;
    private ToggleButton tbReNewPwdBtn;


    //配置信息
    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_user_pwd_set);
    }

    @Override
    protected void bindView() {
        tvTitleName = (TextView) findViewById(R.id.tv_title_name);
        tvTitleName.setText(R.string.home_passwd_set);

        iBtnBack = (ImageView) findViewById(R.id.btn_iv_back);
        iBtnBack.setVisibility(View.VISIBLE);

        pwdSetBtn = (Button) findViewById(R.id.btn_pwd_set);

        etLoginPwd = (EditText) findViewById(R.id.et_login_pwd);
        etLoginPwdTwo = (EditText) findViewById(R.id.et_login_pwd_two);

        tbNewPwdBtn = (ToggleButton) findViewById(R.id.tb_new_password);
        tbReNewPwdBtn = (ToggleButton) findViewById(R.id.tb_re_new_password);

        //密码强度提示
        llPwdCheckAlert = (LinearLayout) findViewById(R.id.ll_pwd_check_alert);
        tvPwdCheckLow = (TextView) findViewById(R.id.tv_pwd_check_low);
        tvPwdCheckMiddle = (TextView) findViewById(R.id.tv_pwd_check_middle);
        tvPwdCheckHigh = (TextView) findViewById(R.id.tv_pwd_check_high);

    }

    @Override
    protected void bindEvent() {
        pwdSetBtn.setOnClickListener(this);
        iBtnBack.setOnClickListener(this);

        //文本编辑框的处理事件
        etLoginPwd.addTextChangedListener(textWatcher);

        tbNewPwdBtn.setOnCheckedChangeListener(mOnCheckedChangeListener);
        tbReNewPwdBtn.setOnCheckedChangeListener(mOnCheckedChangeListener);

    }

    @Override
    protected void bindData() {

    }

    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.ACTION_PWD_SET_SUBMIT:
                Utils.showToast(this, "密码设置成功!" );
                UiUtils.startActivity(UserPwdSetActivity.this, UserMainActivity.class);
                finish();
                break;
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.ACTION_PWD_SET_SUBMIT:
                Utils.showToast(this, "密码设置失败:" +statusCode + "!" );
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pwd_set:
                setPwdSetSubmit();
                break;
            case R.id.btn_iv_back:
                finish();
                break;
        }
    }

    /**
     * 用户登录处理
     */
    private void setPwdSetSubmit() {
        String etLoginPwdStr = etLoginPwd.getText().toString().trim();
        String etLoginPwdTwoStr = etLoginPwdTwo.getText().toString().trim();
        if (TextUtils.isEmpty(etLoginPwdStr) || TextUtils.isEmpty(etLoginPwdTwoStr)) {
            Utils.showToast(this, "请输入修改的密码!");
            return;
        }else{
            if( Utils.passwordStrong(etLoginPwdStr).equals("弱") || etLoginPwdStr.length() < 6){
                Utils.showToast(this, "密码设置强度太弱!");
                return;
            }else if(etLoginPwdStr.equals(etLoginPwdTwoStr)){
                showProgressDialog("请求中...");
                UserReqInfo reqInfo = new UserReqInfo();
                reqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
                reqInfo.setPassword(EncodeUtil.encodeBySha(etLoginPwdStr));
                NetAPI.userPwdsetSubmit(this, this,reqInfo);
            }else{
                Utils.showToast(this, "两次输入的密码不一致!");
                return;
            }
        }

    }

    /**
     *
     * 文本编辑框的处理事件
     *
     */
    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length() >= 5){
                if(Utils.passwordStrong(s.toString()).equals("弱")){
                    llPwdCheckAlert.setVisibility(View.VISIBLE);
                    tvPwdCheckLow.setBackgroundResource(R.color.pwd_check_low);
                    tvPwdCheckMiddle.setBackgroundResource(R.color.darkgray);
                    tvPwdCheckHigh.setBackgroundResource(R.color.darkgray);
                }else if(Utils.passwordStrong(s.toString()).equals("中")){
                    llPwdCheckAlert.setVisibility(View.VISIBLE);
                    tvPwdCheckLow.setBackgroundResource(R.color.darkgray);
                    tvPwdCheckMiddle.setBackgroundResource(R.color.pwd_check_middle);
                    tvPwdCheckHigh.setBackgroundResource(R.color.darkgray);
                }else if(Utils.passwordStrong(s.toString()).equals("强")){
                    llPwdCheckAlert.setVisibility(View.VISIBLE);
                    tvPwdCheckLow.setBackgroundResource(R.color.darkgray);
                    tvPwdCheckMiddle.setBackgroundResource(R.color.darkgray);
                    tvPwdCheckHigh.setBackgroundResource(R.color.pwd_check_high);
                };
            }else {
                llPwdCheckAlert.setVisibility(View.GONE);
                tvPwdCheckLow.setBackgroundResource(R.color.darkgray);
                tvPwdCheckMiddle.setBackgroundResource(R.color.darkgray);
                tvPwdCheckHigh.setBackgroundResource(R.color.darkgray);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    };

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id = buttonView.getId();
            switch (id) {
                case R.id.tb_new_password: {
                    if (isChecked) {
                        //普通文本框
                        etLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        //密码框
                        etLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                    etLoginPwd.postInvalidate();//刷新View
                    //将光标置于文字末尾
                    CharSequence charSequence = etLoginPwd.getText();
                    if (charSequence instanceof Spannable) {
                        Spannable spanText = (Spannable) charSequence;
                        Selection.setSelection(spanText, charSequence.length());
                    }
                }
                break;

                case R.id.tb_re_new_password:{
                    if (isChecked) {
                        etLoginPwdTwo.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        etLoginPwdTwo.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                    etLoginPwdTwo.postInvalidate();
                    CharSequence charSequence = etLoginPwdTwo.getText();
                    if (charSequence instanceof Spannable) {
                        Spannable spanText = (Spannable) charSequence;
                        Selection.setSelection(spanText, charSequence.length());
                    }
                }
                break;
            }
        }
    };
}
